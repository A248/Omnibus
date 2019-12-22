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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class UniversalEvents {
	
	/**
	 * The id of the instance
	 * 
	 */
	private final String id;
	
	/**
	 * The listeners themselves
	 * 
	 */
	private final ConcurrentHashMap<Class<?>, List<ListenerMethod>> eventListeners = new ConcurrentHashMap<Class<?>, List<ListenerMethod>>();
	
	/**
	 * Used to sort all ListenerMethod wrappers by priority <br>
	 * <br>
	 * The order is swapped so that lower-priority listeners are called first
	 * 
	 */
	private static final Comparator<ListenerMethod> PRIORITY_COMPARATOR = (method1, method2) -> method2.priority - method1.priority;
	
	/**
	 * Instances map to prevent duplicate ids
	 * 
	 */
	private static final ConcurrentHashMap<String, UniversalEvents> INSTANCES = new ConcurrentHashMap<String, UniversalEvents>();
	
	/**
	 * The default instance id
	 * 
	 */
	public static final String DEFAULT_ID = "main";
	
	// Control instantiation
	private UniversalEvents(String id) {
		this.id = id;
	}
	
	static synchronized UniversalEvents demandEvents(String id) {
		if (!INSTANCES.containsKey(id)) {
			UniversalEvents events = new UniversalEvents(id);
			INSTANCES.put(id, events);
		}
		return INSTANCES.get(id);
	}
	
	/**
	 * UniversalEvents instances are thread-safe; however, you may wish for a thread-specific instance nonetheless.
	 * 
	 * @return ThreadLocal<UniversalEvents> - a {@link ThreadLocal}
	 */
	public static ThreadLocal<UniversalEvents> threadLocal() {
		return ThreadLocal.withInitial(() -> demandEvents("thread-".concat(Thread.currentThread().getName())));
	}
	
	/**
	 * Retrieves a UniversalEvents instance by classname.
	 * If no instance by the classname exists, a new one is created.<br>
	 * <br>
	 * This is the preferred approach to using your own UniversalEvents instances.
	 * 
	 * @param id - the classname. Use {@link Class#getName()}
	 * @return UniversalEvents - the instance if it exists, otherwise a new instance is created.
	 * @throws ClassNotFoundException - if the classname is invalid
	 */
	public static UniversalEvents getByClassname(String id) throws ClassNotFoundException {
		Class.forName(id);
		return demandEvents("class-".concat(id));
	}
	
	/**
	 * Gets a UniversalEvents by id. <br>
	 * <br>
	 * If no instance with the id exists, {@link Supplier#get()} is called and returned
	 * 
	 * @param id - the String-based id. See {@link #getId()}
	 * @param defaultSupplier - from which to return back default values.
	 * @return UniversalEvents - a registered instance if the id exists, otherwise the default value
	 */
	public static UniversalEvents getOrDefault(String id, Supplier<UniversalEvents> defaultSupplier) {
		UniversalEvents events = INSTANCES.get(id);
		return events != null ? events : defaultSupplier.get();
	}
	
	/**
	 * Gets the main instance of UniversalEvents
	 * 
	 * @return UniversalEvents - the instance
	 */
	public static UniversalEvents get() {
		return demandEvents(DEFAULT_ID);
	}
	
	/**
	 * Returns the id of this UniversalEvents instance. <br>
	 * <br>
	 * The current implementation: <Br>
	 * * For the main instance, it is {@link #DEFAULT_ID} <br>
	 * * For classname instances retrieved with {@link #getByClassname(String)}, it is "class-" followed by the classname<br>
	 * * For thread-local instances retrieved with {@link #threadLocal()}, it is "thread-" followed by the thread name <br>
	 * However, these values may change.
	 * 
	 * @return String - the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Fires an event, invoking all applicable listeners
	 * 
	 * @param <E> - event
	 * @param event - the event itself
	 * @return false if the event is a Cancellable and was cancelled, true otherwise
	 * 
	 * @see Cancellable
	 */
	public <E extends Event> boolean fireEvent(E event) {
		eventListeners.forEach((clazz, listeners) -> {
			if (clazz.isInstance(event)) {
				listeners.forEach((listener) -> {
					if (!(event instanceof Cancellable) || !((Cancellable) event).isCancelled() || !listener.ignoreCancelled) {
						listener.invoke(event);
					}
				});
			}
		});
		return !(event instanceof Cancellable) || !((Cancellable) event).isCancelled();
	}
	
	private static Map<Class<?>, Set<ListenerMethod>> getMethodMap(Object listener) {
		Map<Class<?>, Set<ListenerMethod>> methodMap = new HashMap<Class<?>, Set<ListenerMethod>>();
		for (Method method : listener.getClass().getDeclaredMethods()) {
			EventHandler annotation = method.getAnnotation(EventHandler.class);
			if (annotation != null) {
				Class<?>[] parameters = method.getParameterTypes();
				if (parameters.length != 1) {
					throw new IllegalArgumentException("Listening methods must have 1 parameter!");
				}
				Set<ListenerMethod> methods = methodMap.get(parameters[0]);
				if (methods == null) {
					methods = new HashSet<ListenerMethod>();
					methodMap.put(parameters[0], methods);
				}
				methods.add(new ListenerMethod(listener, method, annotation.priority(), annotation.ignoreCancelled()));
			}
		}
		return methodMap;
	}
	
	/**
	 * Registers an object to listen to events. <br>
	 * <br>
	 * In the object registered, methods must have the {@link EventHandler} annotation.
	 * 
	 * @param listener - the object to register
	 */
	public void register(Object listener) {
		Map<Class<?>, Set<ListenerMethod>> methodMap = getMethodMap(listener);
		synchronized (eventListeners) {
			methodMap.forEach((clazz, methods) -> {
				if (!eventListeners.containsKey(clazz)) {
					eventListeners.put(clazz, new ArrayList<ListenerMethod>());
				}
				List<ListenerMethod> eventMethods = eventListeners.get(clazz);
				if (eventMethods.addAll(methods)) {
					eventMethods.sort(PRIORITY_COMPARATOR);
				}
			});
		}
	}
	
	/**
	 * Unregister an object from any listening. <br>
	 * <br>
	 * Opposite of {@link #register(Object)}
	 * 
	 * @param listener - the object to unregister
	 */
	public void unregister(Object listener) {
		Map<Class<?>, Set<ListenerMethod>> methodMap = getMethodMap(listener);
		synchronized (eventListeners) {
			methodMap.forEach((clazz, methods) -> {
				if (eventListeners.containsKey(clazz) && eventListeners.get(clazz).removeAll(methods)) {
					eventListeners.get(clazz).sort(PRIORITY_COMPARATOR);
				}
			});
		}
	}
	
}

/**
 * Internal wrapper class. Implementation could change, so this is not exposed.
 * 
 * @author A248
 *
 */
class ListenerMethod {

	final Object listener;
	private final Method method;
	final byte priority;
	final boolean ignoreCancelled;
	
	ListenerMethod(Object listener, Method method, byte priority, boolean ignoreCancelled) {
		this.listener = listener;
		this.method = method;
		this.priority = priority;
		this.ignoreCancelled = ignoreCancelled;
	}
	
	void invoke(Event evt) {
		try {
			method.invoke(listener, evt);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {}
	}
	
}
