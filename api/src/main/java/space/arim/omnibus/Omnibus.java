/* 
 * Omnibus-api
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-api. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus;

import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.registry.Registry;
import space.arim.omnibus.resourcer.ResourceManager;

/**
 * Central provider of the event bus, service registry, and resource manager.
 * 
 * @author A248
 *
 */
public interface Omnibus {

	/**
	 * Gets the event bus used by this {@code Omnibus}
	 * 
	 * @return the event bus
	 */
	EventBus getEvents();
	
	/**
	 * Gets the service registry used by this {@code Omnibus}
	 * 
	 * @return the service registry
	 */
	Registry getRegistry();
	
	/**
	 * Gets the resource manager used by this {@code Omnibus}
	 * 
	 * @return the resource manager
	 */
	ResourceManager getResourceManager();
	
}
