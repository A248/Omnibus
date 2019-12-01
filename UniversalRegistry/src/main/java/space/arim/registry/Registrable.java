/*
 * UniversalRegistry, a Bukkit/BungeeCord bridge service registration API
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
package space.arim.registry;

public interface Registrable {
	
	String getName();
	
	default String getAuthor() {
		return "anonymous";
	}
	
	String getVersion();
	
	/**
	 * The priority of this resource when it is registered.<br>
	 * <br>
	 * If multiple resources are registered for one service,
	 * 
	 * @return
	 */
	byte getPriority();
	
}
