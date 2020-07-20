/* 
 * Universal-resourcer
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-resourcer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-resourcer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-resourcer. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.resourcer;

import space.arim.omnibus.events.Event;

/**
 * Called once an implementation of a resource has been shutdown. In this event, existing {@link ResourceHook}s
 * may be used to re-get the new implementation.
 * 
 * @author A248
 *
 * @param <T> the type of the resource
 */
public interface ShutdownEvent<T> extends Event {

	/**
	 * Gets the class corresponding to the resource type
	 * 
	 * @return the resource type class
	 */
	Class<T> getResourceClass();
	
	/**
	 * Gets the implementation shut down
	 * 
	 * @return the shut down implementation
	 */
	T getShutImplementation();
	
}
