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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import space.arim.universal.events.UniversalEvents;

/**
 * <b>UniversalRegistry</b>: Main class <br>
 * <br>
 * Used for registering all services, as well as checking/listing registrations. <br>
 * <br>
 * The most common usage of this class is to register resources and retrieve registrations. <br>
 * To register resources: {@link #register(Class, Registrable)} <br>
 * To retrieve registrations: {@link #getRegistration(Class)} <br>
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
public final class UniversalRegistry {
	
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
	 * The corresponding {@link UniversalEvents} instance
	 * 
	 */
	private final UniversalEvents events;
	
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
	private static final ThreadLocal<UniversalRegistry> THREAD_LOCAL = ThreadLocal.withInitial(() -> byEvents(UniversalEvents.threadLocal().get()));
	
	// Control instantiation
	private UniversalRegistry(String id, UniversalEvents events) {
		this.id = id;
		this.events = events;
	}
	
	private static synchronized UniversalRegistry demandRegistry(String id, UniversalEvents events) {
		if (!INSTANCES.containsKey(id)) {
			INSTANCES.put(id, new UniversalRegistry(id, events));
		}
		return INSTANCES.get(id);
	}
	
	static UniversalRegistry byEvents(UniversalEvents events) {
		return demandRegistry(events.getId(), events);
	}
	
	/**
	 * UniversalRegistry instances are thread-safe; however, you may wish for a thread-specific instance nonetheless.
	 * 
	 * @return ThreadLocal - a {@link ThreadLocal}
	 */
	public static ThreadLocal<UniversalRegistry> threadLocal() {
		return THREAD_LOCAL;
	}
	
	/**
	 * Retrieves a UniversalRegistry instance by class.
	 * If no instance for the classname exists, a new one is created.<br>
	 * <br>
	 * This is the preferred approach to using your own UniversalRegistry instances.
	 * 
	 * @param clazz - the class
	 * @return UniversalRegistry - the instance. If none exists, a new instance is created.
	 */
	public static UniversalRegistry getByClass(Class<?> clazz) {
		return byEvents(UniversalEvents.getByClass(clazz));
	}
	
	/**
	 * Gets a UniversalRegistry by class with a default value, issued by the Supplier, if it does not exist. <br>
	 * <br>
	 * This method is useful for checking for a specific instance and falling back to a default value. <br>
	 * 
	 * @param clazz - see {@link #getByClass(Class)}
	 * @param defaultSupplier - from which to return back default values.
	 * @return UniversalRegistry - a registered instance if the id exists, otherwise the default value
	 */
	public static UniversalRegistry getOrDefault(Class<?> clazz, Supplier<UniversalRegistry> defaultSupplier) {
		UniversalRegistry registry = INSTANCES.get("class-" + clazz.getName());
		return registry != null ? registry : defaultSupplier.get();
	}
	
	/**
	 * Gets the main instance of UniversalRegistry
	 * 
	 * @return UniversalRegistry - the instance
	 */
	public static UniversalRegistry get() {
		return byEvents(UniversalEvents.get());
	}
	
	/**
	 * Returns the id of this UniversalRegistry instance. <br>
	 * <br>
	 * The current implementation: <br>
	 * * For the main instance, it is {@link #DEFAULT_ID} <br>
	 * * For classname instances retrieved with {@link #getByClass(Class)}, it is "class-" followed by the classname<br>
	 * * For thread-local instances retrieved with {@link #threadLocal()}, it is "thread-" + {@link System#currentTimeMillis()} at instantiation time of the corresponding {@link UniversalEvents} + "-" + the thread name <br>
	 * However, these values may change.
	 * 
	 * @return String - the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Gets the {@link UniversalEvents} instance corresponding to this registry. <br>
	 * <br>
	 * The returned UniversalEvents instance is the same one on which RegistrationEvents are fired.
	 * 
	 * @return UniversalEvents - the accompanying events instance
	 */
	public UniversalEvents getEvents() {
		return events;
	}
	
	/**
	 * Register a resource as a specific service
	 * 
	 * @param <T> - the service
	 * @param service - the service class, e.g. Economy.class for Vault economy
	 * @param provider - the resource to register, cannot be null
	 */
	public synchronized <T extends Registrable> void register(Class<T> service, T provider) {
		Objects.requireNonNull(provider, "Provider must not be null!");
		if (!registry.containsKey(service) || provider.getPriority() > registry.get(service).getPriority()) {
			registry.put(service, provider);
			getEvents().fireEvent(new RegistrationEvent<T>(getEvents().getUtil().isAsynchronous(), service, provider));
		}
	}
	
	/**
	 * Checks whether a service has any accompanying provider <br>
	 * <br>
	 * This method should only be called in the rare case where
	 * you need to check whether a service is registered but you do not
	 * need to retrieve the registration itself. <br>
	 * <br>
	 * If you need to retrive a registration use {@link #getRegistration(Class)}
	 * 
	 * @param <T> - the service
	 * @param service - the service class
	 * @return true if the service is provided for, false if not
	 */
	public <T extends Registrable> boolean isProvidedFor(Class<T> service) {
		return registry.containsKey(service);
	}
	
	/**
	 * Retrieves the highest-priority registration for a service. <br>
	 * <br>
	 * The proper way to retrieve registrations is to call this method once,
	 * check if the returned value is non-null. If not null, proceed normally.
	 * If null, there is no registration for the service. (You could then print an explanatory error message)<br>
	 * <br>
	 * <b>Do not use {@link #isProvidedFor(Class)} and then this method, or you may experience concurrency problems.</b>
	 * 
	 * @param <T> - the service
	 * @param service - the service class
	 * @return the service asked.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Registrable> T getRegistration(Class<T> service) {
		return (T) registry.get(service);
	}
	
	/**
	 * Retrieves a the map of service types to registrations backed by the internal registry. <br>
	 * <br>
	 * <b>Changes to the internal registry are reflected in the map</b>
	 * 
	 * @return a map of service classes to registrable lists
	 */
	public Map<Class<?>, Registrable> getRegistrations() {
		return Collections.unmodifiableMap(registry);
	}
	
}