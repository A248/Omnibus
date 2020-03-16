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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
	private final ConcurrentHashMap<Class<?>, List<ListenerMethod>> eventListeners = new ConcurrentHashMap<Class<?>, List<ListenerMethod>>();
	
	/**
	 * The corresponding {@link Util} instance
	 * 
	 */
	private final Util util;
	
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
				listeners.forEach((listener) -> {
					if (!(event instanceof Cancellable && listener.ignoreCancelled && ((Cancellable) event).isCancelled())) {
						listener.invoke(event);
					}
				});
			}
		});
		return !(event instanceof Cancellable) || !((Cancellable) event).isCancelled();
	}
	
	private static Map<Class<?>, Set<ListenerMethod>> getMethodMap(Listener listener) {
		Map<Class<?>, Set<ListenerMethod>> methodMap = new HashMap<Class<?>, Set<ListenerMethod>>();
		for (Method method : listener.getClass().getDeclaredMethods()) {
			Listen annotation = method.getAnnotation(Listen.class);
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
	
	@Override
	public void register(Listener listener) {
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
	
	@Override
	public void unregister(Listener listener) {
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
