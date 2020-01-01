/* 
 * UniversalRegistry, a common registry for plugin resources
 * Copyright © 2019 Anand Beh <https://www.arim.space>
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
 * Service types must implement or extend this interface.
 * 
 * @author A248
 *
 */
public interface Registrable {
	
	/**
	 * The name of this resource. It is recommended to use a user-friendly name
	 * 
	 * @return the name
	 */
	default String getName() {
		return "unknown";
	}
	
	/**
	 * The author of this resource for common reference
	 * 
	 * @return the author
	 */
	default String getAuthor() {
		return "anonymous";
	}
	
	/**
	 * The version of this resource for common reference
	 * 
	 * @return the version
	 */
	default String getVersion() {
		return "unknown";
	}
	
	/**
	 * The priority of this resource when it is registered.<br>
	 * <br>
	 * If the registered resource's priority is greater than the existing provider,
	 * the existing provider will be replaced with the new resource.
	 * 
	 * @return the priority of this resource
	 */
	byte getPriority();
	
}
