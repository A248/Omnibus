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

/**
 * A partially complete {@link RegistrationEvent}, containing information
 * about all but whether the event runs async.
 * 
 * @author A248
 *
 */
class PartialRegistrationEvent<T> {

	private final Class<T> service;
	private final Registration<T> registration;
	
	/**
	 * Constructs from a service and registration
	 * 
	 * @param service the service
	 * @param registration the registration
	 */
	PartialRegistrationEvent(Class<T> service, Registration<T> registration) {
		this.service = service;
		this.registration = registration;
	}
	
	/**
	 * Creates a full registration event
	 * 
	 * @param async whether the event runs async
	 * @return the registration event
	 */
	RegistrationEvent<T> toFullEvent(boolean async) {
		return new RegistrationEvent<>(async, service, registration);
	}
	
}
