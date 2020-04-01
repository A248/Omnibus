/* 
 * UniversalRegistry, a common registry for plugin resources
 * Copyright © 2019 Anand Beh <https://www.arim.space
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

import space.arim.universal.events.Event;

/**
 * Called for the registration of a resource. <br>
 * The event will run async if the registration occured async.
 * 
 * @author A248
 *
 * @param <T> the service for which the resource was registered
 */
public class RegistrationEvent<T> extends Event {

	private final Class<T> service;
	private final Registration<T> registration;
	
	/**
	 * Constructs a RegistrationEvent for a service and provider <br>
	 * <br>
	 * The event will run async if the registration occured async.
	 * 
	 * @param async whether the event is running async
	 * @param service the service type registered
	 * @param provider the provider of the service
	 */
	RegistrationEvent(boolean async, Class<T> service, Registration<T> registration) {
		super(async);
		this.service = service;
		this.registration = registration;
	}
	
	/**
	 * Returns the service for which the registration/unregistration occured.
	 * The provider is an instance of this class.
	 * 
	 * @return the service class
	 */
	public Class<T> getService() {
		return service;
	}
	
	/**
	 * Returns the registration. This includes the provider,
	 * the priority, and a name for the provider.
	 * 
	 * @return the registration
	 */
	public Registration<T> getRegistration() {
		return registration;
	}
	
}
