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
package space.arim.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import space.arim.registry.events.UniversalEvents;

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
	 * The registry itself
	 * 
	 */
	private static final ConcurrentHashMap<Class<?>, List<Registrable>> REGISTRY = new ConcurrentHashMap<Class<?>, List<Registrable>>();
	
	/**
	 * Used to sort the registry based on priority
	 */
	private static final Comparator<Registrable> PRIORITY_COMPARATOR = (r1, r2) -> r1.getPriority() - r2.getPriority();
	
	// Prevent instantiation
	private UniversalRegistry() {}
	
	/**
	 * Register a resource as a specific service
	 * 
	 * @param <T> - the service
	 * @param service - the service class, e.g. Economy.class for Vault economy
	 * @param provider - the resource to register, cannot be null
	 */
	public static synchronized <T extends Registrable> void register(Class<T> service, T provider) {
		Objects.requireNonNull(provider, "Provider must not be null!");
		if (REGISTRY.containsKey(service)) {
			if (REGISTRY.get(service).add(provider)) {
				REGISTRY.get(service).sort(PRIORITY_COMPARATOR);
				fireRegistrationEvent(service, provider);
			}
		} else {
			REGISTRY.put(service, new ArrayList<Registrable>(Arrays.asList(provider)));
			fireRegistrationEvent(service, provider);
		}
	}
	
	private static <T extends Registrable> void fireRegistrationEvent(Class<T> service, T provider) {
		UniversalEvents.fireEvent(new RegistrationEvent<T>(service, provider));
	}
	
	/**
	 * Unregister a resource
	 * 
	 * @param <T> - the service
	 * @param service - the service class
	 * @param provider - the resource to unregister
	 */
	public static synchronized <T extends Registrable> void unregister(Class<T> service, T provider) {
		if (REGISTRY.containsKey(service)) {
			if (REGISTRY.get(service).remove(provider)) {
				REGISTRY.get(service).sort(PRIORITY_COMPARATOR);
				fireUnregistrationEvent(service, provider);
			}
		}
	}
	
	private static <T extends Registrable> void fireUnregistrationEvent(Class<T> service, T provider) {
		UniversalEvents.fireEvent(new UnregistrationEvent<T>(service, provider));
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
	public static <T extends Registrable> boolean isProvidedFor(Class<T> service) {
		return REGISTRY.containsKey(service);
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
	public static synchronized <T extends Registrable> T getRegistration(Class<T> service) {
		return REGISTRY.containsKey(service) ? (T) REGISTRY.get(service).get(0) : null;
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
	public static synchronized <T extends Registrable> List<T> getRegistrations(Class<T> service) {
		return REGISTRY.containsKey(service) ?  Collections.unmodifiableList((List<T>) REGISTRY.get(service)) : Collections.emptyList();
	}
	
	/**
	 * Retrieves a map of service types to registrations backed by the internal registry. <br>
	 * <br>
	 * Changes to the registry itself are reflected in the returned map.
	 * 
	 * @return the map of service classes to registrable lists
	 */
	public static Map<Class<?>, List<Registrable>> getRegistrations() {
		return Collections.unmodifiableMap(REGISTRY);
	}
	
}