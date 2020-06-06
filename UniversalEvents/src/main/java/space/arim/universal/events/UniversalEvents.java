/* 
 * UniversalEvents, a common server event-handling api
 * Copyright © 2019 Anand Beh <https://www.arim.space>
 * 
 * UniversalEvents is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalEvents is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalEvents. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import space.arim.universal.util.ArraysUtil;

/**
 * The main implementation of {@link Events}. <br>
 * <br>
 * To retrieve the central instance, use {@link #get()}. Instances may also be constructed as desired.
 * 
 * @author A248
 *
 */
public class UniversalEvents implements Events {

	/**
	 * The listeners themselves, a map of event classes to listener methods
	 * 
	 */
	private final ConcurrentHashMap<Class<?>, ListenerMethod[]> eventListeners = new ConcurrentHashMap<>();
	
	/**
	 * The main instance
	 * 
	 */
	private static final UniversalEvents DEFAULT_EVENTS = new UniversalEvents();
	
	/**
	 * Creates a UniversalEvents. <br>
	 * This may be useful for creating one's own instances. An event fired in one instance
	 * has no relation to other instances.
	 * 
	 */
	public UniversalEvents() {
		
	}
	
	/**
	 * Gets the main Events instance
	 * 
	 * @return the central instance
	 */
	public static Events get() {
		return DEFAULT_EVENTS;
	}
	
	@Override
	public <E extends Event> boolean fireEvent(E event) {
		eventListeners.forEach((clazz, listeners) -> {
			if (clazz.isInstance(event)) {
				for (ListenerMethod listener : listeners) {
					if (!listener.ignoreCancelled || !(event instanceof Cancellable) || !((Cancellable) event).isCancelled()) {
						try {
							listener.invoke(event);
						} catch (Throwable ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
		return !(event instanceof Cancellable) || !((Cancellable) event).isCancelled();
	}
	
	private void addMethods(Class<?> clazz, List<ListenerMethod> methodsToAdd) {
		eventListeners.compute(clazz, (c, existingMethods) -> {
			// No existing methods
			if (existingMethods == null) {
				// This list is already sorted by us in #getMethodMap
				return methodsToAdd.toArray(new ListenerMethod[] {});
			}
			// Check for duplicates
			for (ListenerMethod existing : existingMethods) {
				// Once we find a single duplicate, we know the listener object is already registered
				if (methodsToAdd.contains(existing)) {
					return existingMethods;
				}
			}
			// Add the methods and sort
			int startLength = existingMethods.length;
			ListenerMethod[] updated = Arrays.copyOf(existingMethods, startLength + methodsToAdd.size());
			System.arraycopy(methodsToAdd, 0, updated, startLength, methodsToAdd.size());
			Arrays.sort(updated);
			return updated;
		});
	}
	
	private void addSingleMethod(Class<?> clazz, ListenerMethod methodToAdd) {
		eventListeners.compute(clazz, (c, existingMethods) -> {
			// No existing methods
			if (existingMethods == null) {
				return new ListenerMethod[] {methodToAdd};
			}
			// Check for duplicates
			for (ListenerMethod existing : existingMethods) {
				if (methodToAdd.equals(existing)) {
					return existingMethods;
				}
			}
			// Add the method maintaining sorting
			int insertionIndex = - (Arrays.binarySearch(existingMethods, methodToAdd) + 1);
			return ArraysUtil.expandAndInsert(existingMethods, methodToAdd, insertionIndex);
		});
	}
	
	private void removeSingleMethod(Class<?> clazz, ListenerMethod methodToRemove) {
		eventListeners.computeIfPresent(clazz, (c, existingMethods) -> {
			int removalIndex = Arrays.binarySearch(existingMethods, methodToRemove);
			if (removalIndex < 0) {
				return existingMethods;
			}
			return ArraysUtil.contractAndRemove(existingMethods, removalIndex);
		});
	}
	
	private void removeListenerMethodIf(Class<?> clazz, Predicate<ListenerMethod> checker) {
		eventListeners.computeIfPresent(clazz, (c, existingMethods) -> {
			List<ListenerMethod> updated = new ArrayList<>(existingMethods.length);
			boolean changed = false;
			for (ListenerMethod method : existingMethods) {
				if (checker.test(method)) {
					changed = true;
				} else {
					updated.add(method);
				}
			}
			if (!changed) {
				return existingMethods;
			}
			if (updated.isEmpty()) {
				return null;
			}
			return updated.toArray(new ListenerMethod[] {});
		});
	}
	
	private static Map<Class<?>, List<ListenerMethod>> getMethodMap(Listener listener) {
		Map<Class<?>, List<ListenerMethod>> methodMap = new HashMap<>();
		for (Method method : listener.getClass().getDeclaredMethods()) {
			Listen annote = method.getAnnotation(Listen.class);
			if (annote == null) {
				continue;
			}
			Class<?>[] parameters = method.getParameterTypes();
			if (parameters.length != 1) {
				throw new IllegalArgumentException("Listening methods must have 1 parameter!");
			}
			List<ListenerMethod> list = methodMap.computeIfAbsent(parameters[0], (clazz) -> new ArrayList<>());
			list.add(new AnnotatedListenerMethod(listener, method, annote.priority(), annote.ignoreCancelled()));
			// Presorting now helps us later during possible contention
			list.sort(null);
		}
		return methodMap;
	}
	
	@Override
	public void registerListener(Listener listener) {
		if (!(listener instanceof DynamicListener<?>)) {
			getMethodMap(listener).forEach(this::addMethods);
		}
	}
	
	@Override
	public <E extends Event> Listener registerListener(Class<E> event, byte priority, Consumer<E> listener) {
		DynamicListener<E> dynamicListener = new DynamicListener<E>(event, listener, priority);
		addSingleMethod(event, dynamicListener);
		return dynamicListener;
	}
	
	@Override
	public void unregisterListener(Listener listener) {
		if (listener instanceof DynamicListener<?>) {
			DynamicListener<?> dynamicListener = (DynamicListener<?>) listener;
			removeSingleMethod(dynamicListener.clazz, dynamicListener);
		} else {
			getMethodMap(listener).forEach((clazz, methodsToRemove) -> removeListenerMethodIf((clazz), methodsToRemove::contains));
		}
	}
	
}
