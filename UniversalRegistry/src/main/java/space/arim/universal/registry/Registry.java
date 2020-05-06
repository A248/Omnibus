/* 
 * UniversalRegistry
 * Copyright © 2020 Anand Beh <https://www.arim.space>
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

import java.util.Map;
import java.util.function.Supplier;

import space.arim.universal.events.Events;

/**
 * A framework for registering and loading services. <br>
 * <b>For an implementation, use {@link UniversalRegistry}</b> <br>
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
 * The service class is just the class corresponding to the service type. <br>
 * <br>
 * Note that while implementations should be thread safe, there is no requirement
 * for happens{@literal -}before relationships.
 * 
 * @author A248
 *
 */
public interface Registry {

	/**
	 * Gets the {@link Events} instance corresponding to this registry. <br>
	 * <br>
	 * The returned Events instance is the same one on which RegistrationEvents are fired.
	 * 
	 * @return Events the accompanying events instance
	 */
	Events getEvents();
	
	/**
	 * Registers a resource as a specific service and generates a {@link Registration}
	 * to represent the registration which was just added. <br>
	 * <br>
	 * The provider must be nonnull. If the name is null, an empty string is used. <br>
	 * Higher priority registrations will be preferred for {@link #load(Class)}. <br>
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
	 */
	<T> Registration<T> register(Class<T> service, byte priority, T provider, String name);
	
	/**
	 * Registers a resource as a specific service and returns the highest priority
	 * registration for the service. <br>
	 * This is similar to {@link #register(Class, byte, Object, String)}, but instead
	 * of returning a {@link Registration} based on the resource registered,
	 * this returns the highest priority registration after computations. <br>
	 * <br>
	 * The provider must be nonnull. If the name is null, an empty string is used.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @param priority the registration priority
	 * @param provider the resource to register, cannot be null
	 * @param name a user friendly name for the implementation
	 * @return the highest priority registration after the resource is registered
	 */
	<T> Registration<T> registerAndGet(Class<T> service, byte priority, T provider, String name);
	
	/**
	 * Registers a resource as a specific service and automatically finds
	 * the highest priority registration for the service after computations are
	 * applied. <br>
	 * This is essentially a combination of {@link #register(Class, byte, Object, String)}
	 * and {@link #load(Class)}, completed in a thread safe manner.
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @param priority the registration priority
	 * @param provider the resource to register, cannot be null
	 * @param name a user friendly name for the implementation
	 * @return the highest priority provider
	 */
	default <T> T registerAndLoad(Class<T> service, byte priority, T provider, String name) {
		return registerAndGet(service, priority, provider, name).getProvider();
	}
	
	/**
	 * Automatically finds the highest priority registration for a service
	 * and returns its provider. <br>
	 * If no registration for the service is found, <code>null</code> is returned. <br>
	 * <br>
	 * The proper way to retrieve registrations is to call this method once,
	 * and check if the returned value is nonnull. If nonnull, proceed normally.
	 * If null, there is no registration for the service. (You could then print an explanatory error message)
	 * 
	 * @param <T> the service type
	 * @param service the service class
	 * @return the provider or <code>null</code> if not found
	 */
	default <T> T load(Class<T> service) {
		Registration<T> registration = getRegistration(service);
		return (registration != null) ? registration.getProvider() : null;
	}
	
	/**
	 * Retrieves the highest priority registration for a service. <br>
	 * <br>
	 * If no registration for the service is found, <code>null</code> is returned.
	 * 
	 * @param <T> the service
	 * @param service the service class
	 * @return the registration
	 */
	<T> Registration<T> getRegistration(Class<T> service);
	
	/**
	 * Checks whether a service has any accompanying provider <br>
	 * <br>
	 * This method should only be called in the rare case where
	 * you need to check whether a service is registered but you do not
	 * need to retrieve the registration itself. <br>
	 * <br>
	 * <b>Do not use this method to determine if a registration exists. </b>
	 * Else, you may experience concurrency problems if providers are
	 * unregistered.
	 * 
	 * @param <T> the service
	 * @param service the service class
	 * @return true if the service is provided for, false if not
	 */
	<T> boolean isProvidedFor(Class<T> service);
	
	/**
	 * Adds a registration if there is no existing registration
	 * for the service type. <br>
	 * <br>
	 * If there is a registration for the service, it is returned. <br>
	 * <br>
	 * Otherwise, the provider generated by the Supplier is registered, and then returned.
	 * If the generated provider is <code>null</code>, it is not registered, since the registry
	 * does not permit null providers, and then <code>null</code> is returned. <br>
	 * <br>
	 * Functions similarly to {@link Map#computeIfAbsent(Object, java.util.function.Function)}
	 * in that it returns the updated, current value after calculations are applied.
	 * 
	 * @param <T> the service
	 * @param service the service class
	 * @param computer if there is no registration for the service, the registration is demanded from this Supplier
	 * @return the registration for the service once the operation is complete
	 */
	<T> Registration<T> registerIfAbsent(Class<T> service, Supplier<Registration<T>> computer);
	
	/**
	 * Unregisters the specified Registration.
	 * 
	 * @param <T> the service
	 * @param service the service class
	 * @param registration the registration to unregister
	 */
	<T> void unregister(Class<T> service, Registration<T> registration);
	
}
