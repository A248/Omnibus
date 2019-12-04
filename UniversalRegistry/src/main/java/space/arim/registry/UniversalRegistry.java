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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

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
 * @author A248
 *
 */
public class UniversalRegistry {
	
	/**
	 * The registry itself
	 * 
	 */
	private static final ConcurrentHashMap<Class<?>, List<Registrable>> REGISTRY = new ConcurrentHashMap<Class<?>, List<Registrable>>();
	
	/**
	 * Used to sort the registry based on priority
	 * 
	 */
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
	 * @param <T> - the service
	 * @param service - the service class, e.g. Economy.class for Vault economy
	 * @param provider - the resource to register
	 */
	public static synchronized <T extends Registrable> void register(Class<T> service, @NonNull T provider) {
		Objects.requireNonNull(provider, "Provider must not be null!");
		if (REGISTRY.containsKey(service)) {
			REGISTRY.get(service).add(provider);
			REGISTRY.get(service).sort(COMPARATOR);
		} else {
			REGISTRY.put(service, new ArrayList<Registrable>(Arrays.asList(provider)));
		}
	}
	
	/**
	 * Unregister a resource
	 * 
	 * @param <T> - the service
	 * @param service - the service class
	 * @param provider - the resource to unregister
	 */
	public static synchronized <T extends Registrable> void unregister(Class<T> service, @NonNull T provider) {
		if (REGISTRY.containsKey(service)) {
			if (REGISTRY.get(service).remove(provider)) {
				REGISTRY.get(service).sort(COMPARATOR);
			}
		}
	}
	
	/**
	 * Checks whether a service has any accompanying provider
	 * 
	 * @param <T> - the service
	 * @param service - the service class
	 * @return true if the service is provided for, false if not
	 */
	public static <T extends Registrable> boolean isProvidedFor(Class<T> service) {
		return REGISTRY.containsKey(service);
	}
	
	/**
	 * Retrieves the highest-priority registration for a service
	 * 
	 * @param <T> - the service
	 * @param service - the service class
	 * @return the service asked. Use {@link #isProvidedFor(Class)} to avoid null values.
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public static synchronized <T extends Registrable> T getRegistration(Class<T> service) {
		return REGISTRY.containsKey(service) ? (T) REGISTRY.get(service).get(0) : null;
	}
	
	/**
	 * Retrieves all registrations for a service
	 * 
	 * @param <T> - the service
	 * @param service - the service class
	 * @return immutable list sorted according to priority of registrations. Empty if no registrations exist.
	 */
	@SuppressWarnings({ "unchecked", "null" })
	@NonNull
	public static synchronized <T extends Registrable> List<T> getRegistrations(Class<T> service) {
		return REGISTRY.containsKey(service) ?  Collections.unmodifiableList((List<T>) REGISTRY.get(service)) : Collections.emptyList();
	}
	
}