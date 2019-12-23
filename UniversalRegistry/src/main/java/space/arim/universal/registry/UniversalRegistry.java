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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import space.arim.universal.events.Event;
import space.arim.universal.events.UniversalEvents;

/**
 * <b>UniversalRegistry</b>: Main class <br>
 * <br>
 * Used for registering all services, as well as checking/listing registrations. <br>
 * <br>
 * The most common usage of this class is to register resources and retrieve registrations. <br>
 * To register resources: {@link #register(Class, Registrable)} <br>
 * To retrieve registrations: {@link #getRegistration(Class)}
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
	private final ConcurrentHashMap<Class<?>, List<Registrable>> registry = new ConcurrentHashMap<Class<?>, List<Registrable>>();
	
	/**
	 * The corresponding {@link UniversalEvents} instance
	 * 
	 */
	private final UniversalEvents events;
	
	/**
	 * Used to sort the registry based on priority
	 */
	private static final Comparator<Registrable> PRIORITY_COMPARATOR = (r1, r2) -> r1.getPriority() - r2.getPriority();
	
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
	 * @return ThreadLocal<UniversalRegistry> - a {@link ThreadLocal}
	 */
	public static ThreadLocal<UniversalRegistry> threadLocal() {
		return ThreadLocal.withInitial(() -> byEvents(UniversalEvents.threadLocal().get()));
	}
	
	/**
	 * Retrieves a UniversalRegistry instance by classname.
	 * If no instance by the classname exists, a new one is created.<br>
	 * <br>
	 * This is the preferred approach to using your own UniversalRegistry instances.
	 * 
	 * @param classname - the classname. Use {@link Class#getName()}
	 * @return UniversalRegistry - the instance if it exists, otherwise a new instance is created.
	 * @throws ClassNotFoundException - if the classname is invalid
	 */
	public static UniversalRegistry getByClassname(String classname) throws ClassNotFoundException {
		return byEvents(UniversalEvents.getByClassname(classname));
	}
	
	/**
	 * Gets a UniversalRegistry by id. <br>
	 * <br>
	 * If no instance with the id exists, {@link Supplier#get()} is called and returned
	 * 
	 * @param id - the String-based id. See {@link #getId()}
	 * @param defaultSupplier - from which to return back default values.
	 * @return UniversalRegistry - a registered instance if the id exists, otherwise the default value
	 */
	public static UniversalRegistry getOrDefault(String id, Supplier<UniversalRegistry> defaultSupplier) {
		UniversalRegistry registry = INSTANCES.get(id);
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
	 * Register a resource as a specific service
	 * 
	 * @param <T> - the service
	 * @param service - the service class, e.g. Economy.class for Vault economy
	 * @param provider - the resource to register, cannot be null
	 */
	public synchronized <T extends Registrable> void register(Class<T> service, T provider) {
		Objects.requireNonNull(provider, "Provider must not be null!");
		if (registry.containsKey(service)) {
			if (registry.get(service).add(provider)) {
				registry.get(service).sort(PRIORITY_COMPARATOR);
				fireRegistrationEvent(service, provider);
			}
		} else {
			registry.put(service, new ArrayList<Registrable>(Arrays.asList(provider)));
			fireRegistrationEvent(service, provider);
		}
	}
	
	private <T extends Registrable> void fireRegistrationEvent(Class<T> service, T provider) {
		fireEvent(new RegistrationEvent<T>(service, provider));
	}
	
	/**
	 * Unregister a resource
	 * 
	 * @param <T> - the service
	 * @param service - the service class
	 * @param provider - the resource to unregister
	 */
	public synchronized <T extends Registrable> void unregister(Class<T> service, T provider) {
		if (registry.containsKey(service) && registry.get(service).remove(provider)) {
			registry.get(service).sort(PRIORITY_COMPARATOR);
			fireUnregistrationEvent(service, provider);
		}
	}
	
	private <T extends Registrable> void fireUnregistrationEvent(Class<T> service, T provider) {
		fireEvent(new UnregistrationEvent<T>(service, provider));
	}
	
	private void fireEvent(Event evt) {
		events.fireEvent(evt);
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
	public synchronized <T extends Registrable> T getRegistration(Class<T> service) {
		return registry.containsKey(service) ? (T) registry.get(service).get(0) : null;
	}
	
	/**
	 * Retrieves all registrations for a service. <br>
	 * <br>
	 * If there are no registrations for the service parameter, an empty list is returned.
	 * Otherwise, the returned list is backed by the internal registry.
	 * 
	 * @param <T> - the service
	 * @param service - the service class
	 * @return immutable list sorted according to priority of registrations. Empty if no registrations exist.
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends Registrable> List<T> getRegistrations(Class<T> service) {
		return registry.containsKey(service) ?  Collections.unmodifiableList((List<T>) registry.get(service)) : Collections.emptyList();
	}
	
	/**
	 * Retrieves a map of service types to registrations backed by the internal registry. <br>
	 * <br>
	 * Changes to the registry itself are reflected in the returned map.
	 * 
	 * @return the map of service classes to registrable lists
	 */
	public Map<Class<?>, List<Registrable>> getRegistrations() {
		return Collections.unmodifiableMap(registry);
	}
	
}