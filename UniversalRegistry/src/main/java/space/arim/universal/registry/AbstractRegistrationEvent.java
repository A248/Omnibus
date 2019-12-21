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

public abstract class AbstractRegistrationEvent<T extends Registrable> extends Event {

	private final String id;
	private final Class<T> service;
	private final T provider;
	
	AbstractRegistrationEvent(String id, Class<T> service, T provider) {
		this.id = id;
		this.service = service;
		this.provider = provider;
	}
	
	/**
	 * Gets the ID of the Registry for this event. <br>
	 * <br>
	 * For the main registry, it is simply <code>main</code>
	 * 
	 * @return String - the registry id
	 */
	public String getRegistryId() {
		return id;
	}
	
	/**
	 * Returns the service for which the registration/unregistration occured.
	 * 
	 * @return Class - the service class
	 */
	public Class<T> getService() {
		return service;
	}
	
	/**
	 * Returns the resource which was registered/unregistered
	 * 
	 * @return the resource
	 */
	public T getResource() {
		return provider;
	}
	
}
