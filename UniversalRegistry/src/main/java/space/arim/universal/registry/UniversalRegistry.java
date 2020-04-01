/* 
 * UniversalRegistry, a common registry for plugin resources
 * Copyright © 2019 Anand Beh <https://www.arim.space>
 * 
 * UniversalRegistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalRegistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalRegistry. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.registry;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import space.arim.universal.events.Events;
import space.arim.universal.events.UniversalEvents;

/**
 * <b>UniversalRegistry</b>: Main class <br>
 * <br>
 * Used for retrieving {@link Registry} instances: <br>
 * * {@link #get()} <br>
 * * {@link #getByClass(Class)} <br>
 * * {@link #threadLocal()} <br>
 * * {@link #getOrDefault(Class, Supplier)}
 * 
 * @author A248
 *
 */
public final class UniversalRegistry implements Registry {
	
	/**
	 * The id of the instance
	 * 
	 */
	private final String id;
	
	/**
	 * The registry itself
	 * 
	 */
	private final ConcurrentHashMap<Class<?>, Registrable> registry = new ConcurrentHashMap<Class<?>, Registrable>();
	
	/**
	 * Unmodifiable view of the registry
	 * 
	 */
	private volatile Map<Class<?>, Registrable> registryView;
	
	/**
	 * The corresponding {@link Events} instance
	 * 
	 */
	private final Events events;
	
	/**
	 * Instances map to prevent duplicate ids
	 * 
	 */
	private static final ConcurrentHashMap<String, UniversalRegistry> INSTANCES = new ConcurrentHashMap<String, UniversalRegistry>();
	
	/**
	 * The main instance id <br>
	 * <br>
	 * Equivalent to {@link UniversalEvents#DEFAULT_ID}
	 */
	public static final String DEFAULT_ID = UniversalEvents.DEFAULT_ID;
	
	/**
	 * The thread local
	 * 
	 */
	private static final ThreadLocal<Registry> THREAD_LOCAL = ThreadLocal.withInitial(() -> byEvents(UniversalEvents.threadLocal().get()));
	
	// Control instantiation
	private UniversalRegistry(String id, Events events) {
		this.id = id;
		this.events = events;
	}
	
	static Registry demandRegistry(String id, Events events) {
		return INSTANCES.computeIfAbsent(id, (instanceId) -> new UniversalRegistry(instanceId, events));
	}
	
	static Registry byEvents(Events events) {
		return demandRegistry(((UniversalEvents) events).getId(), events);
	}
	
	/**
	 * Registry instances are thread safe; however, you may wish for a thread specific instance nonetheless.
	 * 
	 * @return ThreadLocal a {@link ThreadLocal}
	 */
	public static ThreadLocal<Registry> threadLocal() {
		return THREAD_LOCAL;
	}
	
	/**
	 * Retrieves a Registry instance by class.
	 * If no instance for the classname exists, a new one is created.<br>
	 * <br>
	 * This is the preferred approach to using your own UniversalRegistry instances.
	 * 
	 * @param clazz the class
	 * @return the instance. If none exists, a new instance is created.
	 */
	public static Registry getByClass(Class<?> clazz) {
		return byEvents(UniversalEvents.getByClass(clazz));
	}
	
	/**
	 * Gets a Registry instance by class with a default value, issued by the Supplier, if it does not exist. <br>
	 * <br>
	 * This method is useful for checking for a specific instance and falling back to a default value. <br>
	 * 
	 * @param clazz see {@link #getByClass(Class)}
	 * @param defaultSupplier from which to return back default values.
	 * @return the instance if it exists, otherwise the default value
	 */
	public static Registry getOrDefault(Class<?> clazz, Supplier<Registry> defaultSupplier) {
		Registry registry = INSTANCES.get("class-" + clazz.getName());
		return registry != null ? registry : defaultSupplier.get();
	}
	
	/**
	 * Gets the main instance of UniversalRegistry
	 * 
	 * @return the instance
	 */
	public static Registry get() {
		return byEvents(UniversalEvents.get());
	}
	
	/**
	 * Returns the id of this Registry instance. <br>
	 * <b>This method is purposefully not exposed since it is not part of the officially supported API.</b>
	 * (There may be other Registry implementations which do not use an id based system, further,
	 * UniversalRegistry may itself change its internal implementation in the future).
	 * 
	 * @return String the id
	 */
	public String getId() {
		return id;
	}
	
	@Override
	public Events getEvents() {
		return events;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Registrable> T register(Class<T> service, T provider) {
		return (provider == null) ? getRegistration(service) : (T) registry.compute(service, (serviceClass, registration) -> {
			if (registration == null || provider.getPriority() > registration.getPriority()) {
				getEvents().fireEvent(new RegistrationEvent<T>(getEvents().getUtil().isAsynchronous(), service, provider));
				return provider;
			}
			return registration;
		});
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Registrable> T compute(Class<T> service, byte priority, Supplier<T> computer) {
		return (computer == null) ? getRegistration(service) : (T) registry.compute(service, (serviceClass, registration) -> {
			if (registration == null || priority > registration.getPriority()) {
				T provider = computer.get();
				if (provider != null) {
					getEvents().fireEvent(new RegistrationEvent<T>(getEvents().getUtil().isAsynchronous(), service, provider));
					return provider;
				}
			}
			return registration;
		});
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Registrable> T computeIfAbsent(Class<T> service, Supplier<T> computer) {
		return (T) registry.computeIfAbsent(service, (serviceClass) -> computer.get());
	}
	
	@Override
	public <T extends Registrable> boolean isProvidedFor(Class<T> service) {
		return registry.containsKey(service);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Registrable> T getRegistration(Class<T> service) {
		return (T) registry.get(service);
	}
	
	@Override
	public Map<Class<?>, Registrable> getRegistrations() {
		return (registryView != null) ? registryView : (registryView = Collections.unmodifiableMap(registry));
	}
	
}