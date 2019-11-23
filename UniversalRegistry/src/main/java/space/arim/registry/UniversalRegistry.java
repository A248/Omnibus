package space.arim.registry;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UniversalRegistry {
	
	private static final ConcurrentHashMap<Class<?>, List<Registrable>> REGISTRY = new ConcurrentHashMap<Class<?>, List<Registrable>>();
	
	private static final Comparator<Registrable> COMPARATOR = new Comparator<Registrable>() {
		@Override
		public int compare(Registrable r1, Registrable r2) {
			return r1.getPriority() - r2.getPriority();
		}
	};
	
	/**
	 * Register a resource as a specific service
	 * 
	 * @param <T> - the service type
	 * @param service - the service interface to register as
	 * @param provider - the resource to register
	 * @throws IllegalArgumentException - if the service parameter is not an interface
	 */
	public static <T extends Registrable> void register(Class<T> service, T provider) throws IllegalArgumentException {
		if (!service.isInterface()) {
			throw new IllegalArgumentException("Service must be an interface!");
		}
		if (REGISTRY.containsKey(service)) {
			REGISTRY.get(service).add(provider);
			REGISTRY.get(service).sort(COMPARATOR);
		} else {
			REGISTRY.put(service, Arrays.asList(provider));
		}
	}

	/**
	 * Checks whether a service has any accompanying provider
	 * 
	 * @param <T> - the service type
	 * @param service - the service to check for
	 * @return true if the service is provided for
	 * @throws IllegalArgumentException if the service is not an interface
	 */
	public static <T extends Registrable> boolean isProvidedFor(Class<T> service) {
		if (!service.isInterface()) {
			throw new IllegalArgumentException("Service must be an interface!");
		}
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
	public static <T extends Registrable> T getRegistration(Class<T> service) {
		if (!service.isInterface()) {
			throw new IllegalArgumentException("Service must be an interface!");
		}
		return REGISTRY.containsKey(service) ? (T) REGISTRY.get(service).get(0) : null;
	}
	
	/**
	 * Retrieves all registrations for a service
	 * 
	 * @param <T> - the service type
	 * @param service - the service to get registrations for
	 * @return List sorted according to priority of registrations
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Registrable> List<T> getRegistrations(Class<T> service) {
		if (!service.isInterface()) {
			throw new IllegalArgumentException("Service must be an interface!");
		}
		return REGISTRY.containsKey(service) ? Collections.unmodifiableList((List<T>) REGISTRY.get(service)) : null;
	}
	
}