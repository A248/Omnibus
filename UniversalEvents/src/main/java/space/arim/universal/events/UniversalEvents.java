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
import java.util.function.Supplier;

import space.arim.universal.util.UniversalUtil;
import space.arim.universal.util.Util;

/**
 * <b>UniversalEvents</b>: Main class <br>
 * 
 * <br>
 * Used for retrieving {@link Events} instances: <br>
 * * {@link #get()} <br>
 * * {@link #getByClass(Class)} <br>
 * * {@link #threadLocal()} <br>
 * * {@link #getOrDefault(Class, Supplier)}
 * 
 * @author A248
 *
 */
public final class UniversalEvents implements Events {
	
	/**
	 * The id of the instance
	 * 
	 */
	private final String id;
	
	/**
	 * The listeners themselves
	 * 
	 */
	private final ConcurrentHashMap<Class<?>, ListenerMethod[]> eventListeners = new ConcurrentHashMap<>();
	
	/**
	 * The corresponding {@link Util} instance
	 * 
	 */
	private final Util util;
	
	/**
	 * Instances map to prevent duplicate ids
	 * 
	 */
	private static final ConcurrentHashMap<String, UniversalEvents> INSTANCES = new ConcurrentHashMap<String, UniversalEvents>();
	
	/**
	 * The thread local
	 * 
	 */
	private static final ThreadLocal<Events> THREAD_LOCAL = ThreadLocal.withInitial(() -> byUtil(UniversalUtil.threadLocal().get()));
	
	// Control instantiation
	private UniversalEvents(String id, Util util) {
		this.id = id;
		this.util = util;
	}
	
	static Events demandEvents(String id, Util util) {
		return INSTANCES.computeIfAbsent(id, (instanceId) -> new UniversalEvents(id, util));
	}
	
	static Events byUtil(Util util) {
		return demandEvents(((UniversalUtil) util).getId(), util);
	}
	
	/**
	 * Events instances are thread safe; however, you may wish for a thread specific instance nonetheless.
	 * 
	 * @return ThreadLocal a {@link ThreadLocal}
	 */
	public static ThreadLocal<Events> threadLocal() {
		return THREAD_LOCAL;
	}
	
	/**
	 * Retrieves an Events instance by class.
	 * If no instance for the classname exists, a new one is created.<br>
	 * <br>
	 * This is the preferred approach to using your own Events instances.
	 * 
	 * @param clazz the class
	 * @return the instance. If none exists, a new instance is created.
	 */
	public static Events getByClass(Class<?> clazz) {
		return byUtil(UniversalUtil.getByClass(clazz));
	}
	
	/**
	 * Gets an Events instance by class with a default value, issued by the Supplier, if it does not exist. <br>
	 * <br>
	 * This method is useful for checking for a specific instance and falling back to a default value. <br>
	 * 
	 * @param clazz see {@link #getByClass(Class)}
	 * @param defaultSupplier from which to return back default values.
	 * @return the instance if it exists, otherwise the default value
	 */
	public static Events getOrDefault(Class<?> clazz, Supplier<Events> defaultSupplier) {
		Events events = INSTANCES.get("class-" + clazz.getName());
		return events != null ? events : defaultSupplier.get();
	}
	
	/**
	 * Gets the main Events instance
	 * 
	 * @return the instance
	 */
	public static Events get() {
		return byUtil(UniversalUtil.get());
	}
	
	/**
	 * Returns the id of this UniversalEvents instance. <br>
	 * <b>This method is purposefully not exposed since it is not part of the officially supported API.</b>
	 * (There may be other Events implementations which do not use an id based system, further,
	 * UniversalEvents may itself change its internal implementation in the future).
	 * 
	 * @return String the id
	 */
	public String getId() {
		return id;
	}
	
	@Override
	public Util getUtil() {
		return util;
	}
	
	@Override
	public <E extends Event> boolean fireEvent(E event) {
		if (event.isAsynchronous() != util.isAsynchronous()) {
			throw new IllegalStateException("Event#isAsynchronous returned untruthfully!");
		}
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
			for (ListenerMethod methodToAdd : methodsToAdd) {
				updated[startLength++] = methodToAdd;
			}
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
			// Add the method and sort
			int startLength = existingMethods.length;
			ListenerMethod[] updated = Arrays.copyOf(existingMethods, startLength + 1);
			updated[startLength] = methodToAdd;
			Arrays.sort(updated);
			return updated;
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
			removeListenerMethodIf(dynamicListener.clazz, (check) -> dynamicListener == check);
		} else {
			getMethodMap(listener).forEach((clazz, methodsToRemove) -> removeListenerMethodIf((clazz), methodsToRemove::contains));
		}
	}
	
}
