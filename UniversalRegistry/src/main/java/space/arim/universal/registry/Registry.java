/* 
 * UniversalRegistry, a common registry for plugin resources
 * Copyright © 2020 Anand Beh <https://www.arim.space
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
 * A framework for registering all services, as well as checking/listing registrations. <br>
 * <b>For an implementation, use {@link UniversalRegistry}</b> <br>
 * <br>
 * <b>Usage</b>: to register resources and retrieve registrations. <br>
 * To register resources: {@link #register(Class, Registrable)} <br>
 * To retrieve registrations: {@link #getRegistration(Class)} <br>
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
	 * Attempts to register a resource as a specific service. <br>
	 * * If there is a registration for the service which has a higher priority, nothing happens. <br>
	 * * Otherwise, the resource is registered for the service.
	 * 
	 * @param <T> the service
	 * @param service the service class, e.g. Economy.class for Vault economy
	 * @param provider the resource to register, cannot be null
	 * @return the resulting registration (after a possible update)
	 */
	<T extends Registrable> T register(Class<T> service, T provider);
	
	/**
	 * Attempts to register a resource as a specific service. <br>
	 * Differs from {@link #register(Class, Registrable)} in that the other method <i>always</i> requires a preconstructed object.
	 * Since the object may be discarded, it is a potential misuse of resources to create and object only to discard it.
	 * Thus, this method is provided as a solution. This way, creating the resource is only necessary if the existing registration
	 * has a lower priority than the priority parameter.
	 * 
	 * @param <T> the service
	 * @param service the service class, e.g. Economy.class for Vault economy
	 * @param priority the priority of the resource which will be registered
	 * @param computer the {@link Supplier} which, if queried for its object, will provide the resource to register
	 * @return the resulting registration (after a possible update)
	 */
	<T extends Registrable> T compute(Class<T> service, byte priority, Supplier<T> computer);
	
	/**
	 * Registers a resource conditionally: <br>
	 * * If there is a registration for the service, it is returned. <br>
	 * * Otherwise, the resource provided by the Supplier is registered and returned. <br>
	 * <br>
	 * Functions similarly to {@link Map#computeIfAbsent(Object, java.util.function.Function)}.
	 * 
	 * @param <T> the service
	 * @param service the service class
	 * @param computer if there is no registration for the service, the resource is demanded from this Supplier
	 * @return the registration for the service once the operation is complete
	 */
	<T extends Registrable> T computeIfAbsent(Class<T> service, Supplier<T> computer);
	
	/**
	 * Checks whether a service has any accompanying provider <br>
	 * <br>
	 * This method should only be called in the rare case where
	 * you need to check whether a service is registered but you do not
	 * need to retrieve the registration itself. <br>
	 * <br>
	 * If you need to retrive a registration use {@link #getRegistration(Class)}
	 * 
	 * @param <T> the service
	 * @param service the service class
	 * @return true if the service is provided for, false if not
	 */
	<T extends Registrable> boolean isProvidedFor(Class<T> service);
	
	/**
	 * Retrieves the highest-priority registration for a service. <br>
	 * <br>
	 * The proper way to retrieve registrations is to call this method once,
	 * check if the returned value is non-null. If not null, proceed normally.
	 * If null, there is no registration for the service. (You could then print an explanatory error message)<br>
	 * <br>
	 * <b>Do not use {@link #isProvidedFor(Class)} and then this method, or you may experience concurrency problems.</b>
	 * 
	 * @param <T> the service
	 * @param service the service class
	 * @return the service asked.
	 */
	<T extends Registrable> T getRegistration(Class<T> service);
	
	/**
	 * Retrieves a the map of service types to registrations backed by the internal registry. <br>
	 * <br>
	 * <b>Changes to the internal registry are reflected in the map</b>
	 * 
	 * @return a map of service classes to registrable lists
	 */
	Map<Class<?>, Registrable> getRegistrations();
	
}
