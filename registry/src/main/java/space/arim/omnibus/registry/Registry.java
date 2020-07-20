/* 
 * Omnibus-registry
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-registry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-registry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-registry. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.registry;

import java.util.List;
import java.util.function.Supplier;

import space.arim.omnibus.events.Events;

/**
 * A framework for registering and loading services. <br>
 * <br>
 * Used to register providers of services which will be utilised
 * by dependents according to the priority of registration. <br>
 * <br>
 * A <i>service</i> is a type, usually an interface or abstract class, which
 * multiple programs may decide to implement. An implementation of a service
 * is considered a <i>provider</i>. <br>
 * <br>
 * Providers may be registered with a priority and an optional user friendly name.
 * These details comprise a <i>registration</i> (which is really just a wrapper
 * class for the provider, priority, and optional name). See {@link Registration}. <br>
 * <br>
 * The service class is the class corresponding to the service type.
 * 
 * @author A248
 * 
 * @see UniversalRegistry
 *
 */
public interface Registry {

	/**
	 * Gets the {@link Events} instance corresponding to this registry. <br>
	 * <br>
	 * The returned Events instance is the same one on which {@link RegistryEvent}s are fired.
	 * 
	 * @return the accompanying events instance
	 */
	Events getEvents();
	
	/**
	 * Registers a resource as a specific service and generates a {@link Registration}
	 * to represent the registration which was just added. <br>
	 * <br>
	 * The provider must be nonnull. The name may be null or empty. <br>
	 * Higher priority registrations will be preferred for {@link #getProvider(Class)}. <br>
	 * <br>
	 * Services should be registered under all intended service types, thus: <br>
	 * <br>
	 * <code>
	 * SpecificType myProvider = new SpecificTypeImpl(); <br>
	 * assert myProvider instanceof BroadType;
	 * <br> // Both of these should happen <br>
	 * UniversalRegistry.get().register(BroadType.class, myProvider); <br>
	 * UniversalRegistry.get().register(SpecificType.class, myProvider); <br>
	 * </code> <br>
	 * This way, programs which require the broader type will seek the registration
	 * and find it. Simultaneously, programs requiring the specific type will seek
	 * the specific type registration. <br>
	 * This framework permits the possibility that two providers may be registered
	 * for a broad type, but only one of these is a specific type. <br>
	 * <br>
	 * (More formally, for two interfaces <code>BroadType</code> and <code>SpecificType</code>,
	 * where <code>SpecificType extends BroadType</code>, providers  should be registered
	 * under <code>BroadType.class</code> AND <code>SpecificType.class</code>)
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @param priority the registration priority
	 * @param provider the resource to register, cannot be null
	 * @param name a user friendly name for the implementation
	 * @return the registration which was added to the registry, formed from the parameters
	 * @throws DuplicateRegistrationException if the provider is already registered for the service type
	 */
	<T> Registration<T> register(Class<T> service, byte priority, T provider, String name);
	
	/**
	 * Registers a resource as a specific service and returns the highest priority
	 * registration for the service. <br>
	 * This is similar to {@link #register(Class, byte, Object, String)}, but instead of returning
	 * a {@link Registration} based on the resource registered, this returns the highest priority
	 * registration <i>after</i> computations. <br>
	 * <br>
	 * The provider must be nonnull. The name may be null or empty.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @param priority the registration priority
	 * @param provider the resource to register, cannot be null
	 * @param name a user friendly name for the implementation
	 * @return the highest priority registration after the resource is registered
	 * @throws DuplicateRegistrationException if the provider is already registered for the service type
	 */
	<T> Registration<T> registerAndGet(Class<T> service, byte priority, T provider, String name);
	
	/**
	 * Finds the highest priority registration for a service and returns its provider. <br>
	 * If no registration for the service is found, <code>null</code> is returned. <br>
	 * <br>
	 * The proper way to retrieve registrations is to call this method once,
	 * and check if the returned value is nonnull. If nonnull, proceed normally.
	 * If null, there is no registration for the service. <br>
	 * <br>
	 * To handle unregistrations, <b>the return value of this method should not
	 * be cached in a field</b> unless the caller is prepared to update the field
	 * by listening to {@link ServiceChangeEvent}. As a convenience to avoid
	 * listening to said event, {@link #getProviderSupplier(Class)} may be used
	 * and the resulting supplier invoked whenever the provider is needed.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @return the highest priority provider or <code>null</code> if not found
	 */
	<T> T getProvider(Class<T> service);
	
	/**
	 * Gets a supplier which, when invoked, will retrieve the current highest priority
	 * provider for the given service, using {@link #getProvider(Class)}. <br>
	 * <br>
	 * This method is provided as a convenience so as to encourage decoupling of dependent
	 * projects from the {@code Registry}. For example, instead of passing a reference to
	 * the {@code Registry}, and calling {@link #getProvider(Class)} multiple times,
	 * the returned {@code Supplier} may rather be passed.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @return a supplier returning the current highest priority provider for the service at any given time
	 */
	default <T> Supplier<T> getProviderSupplier(Class<T> service) {
		return () -> getProvider(service);
	}
	
	/**
	 * Retrieves the highest priority registration for a service. <br>
	 * <br>
	 * If no registration for the service is found, <code>null</code> is returned.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @return the highest priority registration
	 */
	<T> Registration<T> getRegistration(Class<T> service);
	
	/**
	 * Gets all registrations for a specific service, as an immutable copy. <br>
	 * The list is sorted in ascending priority. The last element has the highest priority. <br>
	 * <br>
	 * If no registrations for the service are found, an empty list is returned.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @return an unmodifiable copy of all registrations for the service, never null
	 */
	<T> List<Registration<T>> getAllRegistrations(Class<T> service);
	
	/**
	 * Checks whether a service has any accompanying provider <br>
	 * <br>
	 * This method should only be used in the rare case where one needs to check
	 * whether a service is registered but does not need to retrieve the registration itself.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @return true if the service is provided for, false if not
	 */
	<T> boolean isProvidedFor(Class<T> service);
	
	/**
	 * Adds a registration if there is no existing registration for the service type. <br>
	 * <br>
	 * If there are registration for the service, the highest priority one is returned. <br>
	 * Otherwise, the {@link Registration} generated by the Supplier is registered, and then returned.
	 * If the supplied {@code Registration} is {@code null}, nothing happens, and {@code null} is returned.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @param computer if there is no registration for the service, the registration is demanded from this Supplier
	 * @return the highest priority registration for the service once the operation is complete
	 */
	<T> Registration<T> registerIfAbsent(Class<T> service, Supplier<Registration<T>> computer);
	
	/**
	 * Unregisters the specified {@link Registration}, and returns the updated highest priority
	 * registration after the specified {@code Registration} is unregistered. <br>
	 * <br>
	 * If the specified {@code Registration} was not registered, this is a no-op.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @param registration the registration to unregister
	 * @return the updated highest priority registration for the service, or null if there is none
	 */
	<T> Registration<T> unregister(Class<T> service, Registration<T> registration);
	
}
