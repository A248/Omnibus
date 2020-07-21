/* 
 * Omnibus-default
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-default is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-default is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-default. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.defaultimpl.events;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import space.arim.omnibus.Omnibus;
import space.arim.omnibus.events.Cancellable;
import space.arim.omnibus.events.Event;
import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.Events;
import space.arim.omnibus.events.IllegalListenerException;
import space.arim.omnibus.events.Listen;
import space.arim.omnibus.events.Listener;
import space.arim.omnibus.util.ArraysUtil;

/**
 * The default implementation of {@link Events}
 * 
 * @author A248
 *
 */
public class DefaultEvents implements Events {

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	
	/**
	 * The listeners themselves, a map of event classes to listener methods
	 * 
	 */
	private final ConcurrentHashMap<Class<?>, ListenerMethod[]> eventListeners = new ConcurrentHashMap<>();
	
	/**
	 * Creates a {@code DefaultEvents}
	 * 
	 * @param omnibus the omnibus instance
	 */
	public DefaultEvents(Omnibus omnibus) {
		
	}
	
	// Testing
	DefaultEvents() {
		
	}
	
	@Override
	public <E extends Event> boolean fireEvent(E event) {
		Objects.requireNonNull(event, "Event must not be null");
		if (event instanceof Cancellable) {
			Cancellable cancellable = (Cancellable) event;
			eventListeners.forEach((clazz, listeners) -> {
				if (clazz.isInstance(cancellable)) {
					for (ListenerMethod listener : listeners) {
						if (listener.ignoreCancelled && cancellable.isCancelled()) {
							continue;
						}
						try {
							listener.invoke(cancellable);
						} catch (Throwable ex) {
							if (ex instanceof Error) {
								throw (Error) ex;
							}
							ex.printStackTrace();
						}
					}
				}
			});
			return !cancellable.isCancelled();
		} else {
			eventListeners.forEach((clazz, listeners) -> {
				if (clazz.isInstance(event)) {
					for (ListenerMethod listener : listeners) {
						try {
							listener.invoke(event);
						} catch (Throwable ex) {
							if (ex instanceof Error) {
								throw (Error) ex;
							}
							ex.printStackTrace();
						}
					}
				}
			});
			return true;
		}
	}
	
	private void addAnnotatedListeners(Class<?> clazz, Listener listener, AnnotatedListenerMethod[] methodsToAdd) {
		eventListeners.compute(clazz, (c, existingMethods) -> {
			// No existing methods
			if (existingMethods == null) {
				// Already sorted by us in #getMethodMap
				return methodsToAdd;
			}
			// Check for duplicates based on listener object equality
			for (ListenerMethod existing : existingMethods) {
				if (existing instanceof AnnotatedListenerMethod
						&& ((AnnotatedListenerMethod) existing).listener == listener) {
					return existingMethods;
				}
			}
			// Add the methods and sort
			int startLength = existingMethods.length;
			int addLength = methodsToAdd.length;
			ListenerMethod[] updated = Arrays.copyOf(existingMethods, startLength + addLength);
			System.arraycopy(methodsToAdd, 0, updated, startLength, addLength);
			Arrays.sort(updated);
			return updated;
		});
	}
	
	private void removeAnnotatedListenersFor(Class<?> clazz, Listener listener) {
		eventListeners.computeIfPresent(clazz, (c, existingMethods) -> {
			List<ListenerMethod> updated = new ArrayList<>(existingMethods.length);
			boolean changed = false;
			for (ListenerMethod method : existingMethods) {
				if (method instanceof AnnotatedListenerMethod && ((AnnotatedListenerMethod) method).listener == listener) {
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
	
	private void addDynamicListener(DynamicListener<?> methodToAdd) {
		eventListeners.compute(methodToAdd.clazz, (c, existingMethods) -> {
			// No existing methods
			if (existingMethods == null) {
				return new ListenerMethod[] {methodToAdd};
			}
			// Add the method maintaining sorting
			int insertionIndex = - (Arrays.binarySearch(existingMethods, methodToAdd) + 1);
			return ArraysUtil.expandAndInsert(existingMethods, methodToAdd, insertionIndex);
		});
	}
	
	private void removeDynamicListener(DynamicListener<?> methodToRemove) {
		eventListeners.computeIfPresent(methodToRemove.clazz, (c, existingMethods) -> {
			int removalIndex = Arrays.binarySearch(existingMethods, methodToRemove);
			if (removalIndex < 0) {
				// not present
				return existingMethods;
			}
			return ArraysUtil.contractAndRemove(existingMethods, removalIndex);
		});
	}
	
	private static Map<Class<?>, List<ListenerMethod>> getMethodMap(Listener listener) {
		Class<?> listenerClass = listener.getClass();
		if (!Modifier.isPublic(listenerClass.getModifiers())) {
			throw new IllegalListenerException("Listeners must be public");
		}
		Map<Class<?>, List<ListenerMethod>> methodMap = new HashMap<>();
		for (Method method : listenerClass.getDeclaredMethods()) {
			Listen annote = method.getAnnotation(Listen.class);
			if (annote == null) {
				continue;
			}
			int modifiers = method.getModifiers();
			if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)) {
				throw new IllegalListenerException("Listening methods must be public and non-static");
			}
			if (method.getReturnType() != void.class) {
				throw new IllegalListenerException("Listening methods must have void return type");
			}
			Class<?>[] parameters = method.getParameterTypes();
			if (parameters.length != 1) {
				throw new IllegalListenerException("Listening methods must have 1 parameter");
			}
			Class<?> evtClass = parameters[0];
			if (!Event.class.isAssignableFrom(evtClass)) {
				throw new IllegalListenerException("Listening method parameter type must be assignment-compatible with Event");
			}
			MethodHandle handle;
			try {
				handle = LOOKUP.unreflect(method);
			} catch (IllegalAccessException ex) {
				throw new IllegalListenerException("Internal exception: Cannot generate accessors to event listener", ex);
			}
			List<ListenerMethod> list = methodMap.computeIfAbsent(evtClass, (c) -> new ArrayList<>());
			list.add(new AnnotatedListenerMethod(listener, handle, annote.priority(), annote.ignoreCancelled()));
			// Presorting now helps us later during possible contention
			list.sort(null);
		}
		return methodMap;
	}
	
	private static Set<Class<?>> getListenedEventClasses(Listener listener) {
		Class<?> listenerClass = listener.getClass();
		if (!Modifier.isPublic(listenerClass.getModifiers())) {
			return Set.of();
		}
		Set<Class<?>> evtClasses = new HashSet<>();
		for (Method method : listenerClass.getDeclaredMethods()) {
			Listen annote = method.getAnnotation(Listen.class);
			if (annote == null) {
				continue;
			}
			int modifiers = method.getModifiers();
			if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers)) {
				continue;
			}
			if (method.getReturnType() != void.class) {
				continue;
			}
			Class<?>[] parameters = method.getParameterTypes();
			if (parameters.length != 1) {
				continue;
			}
			Class<?> evtClass = parameters[0];
			if (!Event.class.isAssignableFrom(evtClass)) {
				continue;
			}
			evtClasses.add(evtClass);
		}
		return evtClasses;
	}
	
	@Override
	public void registerListener(Listener listener) {
		Objects.requireNonNull(listener, "Listener must not be null");
		if (listener instanceof DynamicListener<?>) {
			// already registered
			return;
		}
		getMethodMap(listener)
				.forEach((clazz, methods) -> addAnnotatedListeners(clazz, listener, methods.toArray(new AnnotatedListenerMethod[] {})));
	}
	
	@Override
	public <E extends Event> Listener registerListener(Class<E> event, byte priority, EventConsumer<? super E> listener) {
		Objects.requireNonNull(event, "Event must not be null");
		Objects.requireNonNull(listener, "Listener must not be null");
		DynamicListener<E> dynamicListener = new DynamicListener<E>(event, listener, priority);
		addDynamicListener(dynamicListener);
		return dynamicListener;
	}
	
	@Override
	public void unregisterListener(Listener listener) {
		Objects.requireNonNull(listener, "Listener must not be null");
		if (listener instanceof DynamicListener<?>) {
			removeDynamicListener((DynamicListener<?>) listener);
		} else {
			getListenedEventClasses(listener).forEach((clazz) -> removeAnnotatedListenersFor(clazz, listener));
		}
	}
	
}