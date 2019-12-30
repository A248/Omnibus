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

import space.arim.universal.util.UniversalUtil;

/**
 * <b>UniversalEvents</b>: Main class <br>
 * <br>
 * Used for firing events and listening to them. <br>
 * <br>
 * The most common usage of this class is to fire and listen to events. <br>
 * To fire events: {@link #fireEvent(Event)} <br>
 * To listen to events: {@link EventHandler} <br>
 * <br>
 * To retrieve an instance: <br>
 * * {@link #get()} <br>
 * * {@link #getByClass(Class)} <br>
 * * {@link #threadLocal()} <br>
 * * {@link #getOrDefault(Class, Supplier)}
 * 
 * @author A248
 *
 */
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
	 * The corresponding {@link UniversalUtil} instance
	 * 
	 */
	private final UniversalUtil util;
	
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
	 * The main instance id <br>
	 * <br>
	 * Equivalent to {@link UniversalUtil#DEFAULT_ID}
	 */
	public static final String DEFAULT_ID = UniversalUtil.DEFAULT_ID;
	
	/**
	 * The thread local
	 * 
	 */
	private static final ThreadLocal<UniversalEvents> THREAD_LOCAL = ThreadLocal.withInitial(() -> byUtil(UniversalUtil.threadLocal().get()));
	
	// Control instantiation
	private UniversalEvents(String id, UniversalUtil util) {
		this.id = id;
		this.util = util;
	}
	
	private static synchronized UniversalEvents demandEvents(String id, UniversalUtil util) {
		if (!INSTANCES.containsKey(id)) {
			INSTANCES.put(id, new UniversalEvents(id, util));
		}
		return INSTANCES.get(id);
	}
	
	static UniversalEvents byUtil(UniversalUtil util) {
		return demandEvents(util.getId(), util);
	}
	
	/**
	 * UniversalEvents instances are thread-safe; however, you may wish for a thread-specific instance nonetheless.
	 * 
	 * @return ThreadLocal - a {@link ThreadLocal}
	 */
	public static ThreadLocal<UniversalEvents> threadLocal() {
		return THREAD_LOCAL;
	}
	
	/**
	 * Retrieves a UniversalEvents instance by class.
	 * If no instance for the classname exists, a new one is created.<br>
	 * <br>
	 * This is the preferred approach to using your own UniversalEvents instances.
	 * 
	 * @param clazz - the class
	 * @return UniversalEvents - the instance. If none exists, a new instance is created.
	 */
	public static UniversalEvents getByClass(Class<?> clazz) {
		return byUtil(UniversalUtil.getByClass(clazz));
	}
	
	/**
	 * Gets a UniversalEvents by class with a default value, issued by the Supplier, if it does not exist. <br>
	 * <br>
	 * This method is useful for checking for a specific instance and falling back to a default value. <br>
	 * 
	 * @param clazz - see {@link #getByClass(Class)}
	 * @param defaultSupplier - from which to return back default values.
	 * @return UniversalEvents - a registered instance if the id exists, otherwise the default value
	 */
	public static UniversalEvents getOrDefault(Class<?> clazz, Supplier<UniversalEvents> defaultSupplier) {
		UniversalEvents events = INSTANCES.get("class-" + clazz.getName());
		return events != null ? events : defaultSupplier.get();
	}
	
	/**
	 * Gets the main instance of UniversalEvents
	 * 
	 * @return UniversalEvents - the instance
	 */
	public static UniversalEvents get() {
		return byUtil(UniversalUtil.get());
	}
	
	/**
	 * Returns the id of this UniversalEvents instance. <br>
	 * <br>
	 * The current implementation: <br>
	 * * For the main instance, it is {@link #DEFAULT_ID} <br>
	 * * For classname instances retrieved with {@link #getByClass(Class)}, it is "class-" followed by the classname<br>
	 * * For thread-local instances retrieved with {@link #threadLocal()}, it is "thread-" + {@link System#currentTimeMillis()} at instantiation time of the corresponding {@link UniversalUtil} + "-" + the thread name <br>
	 * However, these values may change.
	 * 
	 * @return String - the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Gets the {@link UniversalUtil} instance corresponding to this event manager. <br>
	 * <br>
	 * The returned UniversalUtil instance is the same one used to validate the truthfulness of {@link Event#isAsynchronous()} values.
	 * 
	 * @return UniversalUtil - the accompanying utility instance
	 */
	public UniversalUtil getUtil() {
		return util;
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
		if (event.isAsynchronous() != util.isAsynchronous()) {
			throw new IllegalStateException("Event#isAsynchronous returned untruthfully!");
		}
		eventListeners.forEach((clazz, listeners) -> {
			if (clazz.isInstance(event)) {
				listeners.forEach((listener) -> {
					if (!(event instanceof Cancellable) || !listener.ignoreCancelled || !((Cancellable) event).isCancelled()) {
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
				if (!methodMap.containsKey(parameters[0])) {
					methodMap.put(parameters[0], new HashSet<ListenerMethod>());
				}
				methodMap.get(parameters[0]).add(new ListenerMethod(listener, method, annotation.priority(), annotation.ignoreCancelled()));
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
		if (methodMap.isEmpty()) {
			return;
		}
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
		if (methodMap.isEmpty()) {
			return;
		}
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

	private final Object listener;
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
