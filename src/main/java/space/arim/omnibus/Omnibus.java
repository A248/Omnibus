/* 
 * Omnibus
 * Copyright © 2020 Anand Beh
 * 
 * Omnibus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Omnibus. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package space.arim.omnibus;

import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.registry.Registry;

/**
 * Central provider of the event bus and service registry.
 *
 * @author A248
 */
public interface Omnibus {

	/**
	 * Gets the event bus used.
	 *
	 * @return the event bus
	 */
	EventBus getEventBus();
	
	/**
	 * Gets the service registry.
	 *
	 * @return the service registry
	 */
	Registry getRegistry();
	
}
