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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
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
	private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<ListenerMethod>> eventListeners = new ConcurrentHashMap<>();
	
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
				listeners.forEach((listener) -> {
					if (!listener.ignoreCancelled || !(event instanceof Cancellable) || !((Cancellable) event).isCancelled()) {
						try {
							listener.invoke(clazz.cast(event));
						} catch (Throwable ex) {
							ex.printStackTrace();
						}
					}
				});
			}
		});
		return !(event instanceof Cancellable) || !((Cancellable) event).isCancelled();
	}
	
	private void addMethods(Class<?> clazz, Set<? extends ListenerMethod> methods) {
		List<ListenerMethod> existingMethods = eventListeners.computeIfAbsent(clazz, (c) -> new CopyOnWriteArrayList<ListenerMethod>());
		synchronized (existingMethods) {
			if (existingMethods.addAll(methods)) {
				existingMethods.sort(null);
			}
		}
	}
	
	private void addSingleMethod(Class<?> clazz, ListenerMethod method) {
		List<ListenerMethod> existingMethods = eventListeners.computeIfAbsent(clazz, (c) -> new CopyOnWriteArrayList<ListenerMethod>());
		synchronized (existingMethods) {
			if (existingMethods.add(method)) {
				existingMethods.sort(null);
			}
		}
	}
	
	private void removeMethods(Class<?> clazz, Set<? extends ListenerMethod> methods) {
		List<ListenerMethod> existingMethods = eventListeners.get(clazz);
		if (existingMethods != null) {
			existingMethods.removeAll(methods);
		}
	}
	
	private void removeSingleMethod(Class<?> clazz, ListenerMethod method) {
		List<ListenerMethod> existingMethods = eventListeners.get(clazz);
		if (existingMethods != null) {
			existingMethods.remove(method);
		}
	}
	
	private static Map<Class<?>, Set<AnnotatedListenerMethod>> getMethodMap(Listener listener) {
		Map<Class<?>, Set<AnnotatedListenerMethod>> methodMap = new HashMap<Class<?>, Set<AnnotatedListenerMethod>>();
		for (Method method : listener.getClass().getDeclaredMethods()) {
			Listen annotation = method.getAnnotation(Listen.class);
			if (annotation != null) {
				Class<?>[] parameters = method.getParameterTypes();
				if (parameters.length != 1) {
					throw new IllegalArgumentException("Listening methods must have 1 parameter!");
				}
				methodMap.computeIfAbsent(parameters[0], (clazz) -> new HashSet<AnnotatedListenerMethod>())
						.add(new AnnotatedListenerMethod(listener, method, annotation.priority(),
								annotation.ignoreCancelled()));
			}
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
			getMethodMap(listener).forEach(this::removeMethods);
		}
	}
	
}
