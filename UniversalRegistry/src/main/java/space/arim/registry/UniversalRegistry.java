/*
 * UniversalRegistry, a Bukkit/BungeeCord bridge service registration API
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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>UniversalRegistry</b>: Main class
 * 
 * <br><br>Used for registering all services, as well as checking/listing registrations.
 * 
 * @author anandbeh
 *
 */
public class UniversalRegistry {
	
	private static final ConcurrentHashMap<Class<?>, List<Registrable>> REGISTRY = new ConcurrentHashMap<Class<?>, List<Registrable>>();
	private static final Comparator<Registrable> COMPARATOR = new Comparator<Registrable>() {
		@Override
		public int compare(Registrable r1, Registrable r2) {
			return r1.getPriority() - r2.getPriority();
		}
	};
	
	// Prevent instantiation
	private UniversalRegistry() {}
	
	/**
	 * Register a resource as a specific service
	 * 
	 * @param <T> - the service type
	 * @param service - the service interface to register as
	 * @param provider - the resource to register
	 */
	public static synchronized <T extends Registrable> void register(Class<T> service, T provider) throws IllegalArgumentException {
		Objects.requireNonNull(provider, "Provider must not be null!");
		if (REGISTRY.containsKey(service)) {
			REGISTRY.get(service).add(provider);
			REGISTRY.get(service).sort(COMPARATOR);
		} else {
			REGISTRY.put(service, Arrays.asList(provider));
		}
	}
	
	/**
	 * Unregister a resource
	 * 
	 * @param <T> - the service type
	 * @param service - the service interface registered
	 * @param provider - the resource to unregister
	 */
	public static synchronized <T extends Registrable> void unregister(Class<T> service, T provider) throws IllegalArgumentException {
		if (REGISTRY.containsKey(service)) {
			REGISTRY.get(service).remove(provider);
			REGISTRY.get(service).sort(COMPARATOR);
		}
	}
	
	/**
	 * Checks whether a service has any accompanying provider
	 * 
	 * @param <T> - the service type
	 * @param service - the service to check for
	 * @return true if the service is provided for
	 */
	public static <T extends Registrable> boolean isProvidedFor(Class<T> service) {
		return REGISTRY.containsKey(service);
	}
	
	/**
	 * Retrieves the highest-priority registration for a service
	 * 
	 * @param <T> - the service type
	 * @param service - the service to get the registration for
	 * @return the service asked. Use {@link #isProvidedFor(Class)} to avoid null values.
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T extends Registrable> T getRegistration(Class<T> service) {
		return REGISTRY.containsKey(service) ? (T) REGISTRY.get(service).get(0) : null;
	}
	
	/**
	 * Retrieves all registrations for a service
	 * 
	 * @param <T> - the service type
	 * @param service - the service to get registrations for
	 * @return Unmodifiable list sorted according to priority of registrations. Empty if no registrations exist.
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T extends Registrable> List<T> getRegistrations(Class<T> service) {
		return Collections.unmodifiableList(REGISTRY.containsKey(service) ?  (List<T>) REGISTRY.get(service) : new ArrayList<T>());
	}
	
}