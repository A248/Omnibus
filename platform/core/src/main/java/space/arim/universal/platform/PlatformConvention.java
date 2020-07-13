/* 
 * Universal-platform-core
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-platform-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-platform-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-platform-core. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.platform;

import space.arim.universal.registry.Registry;

/**
 * An interface to be implemented by definers of platform-specific conventions for obtaining
 * central {@link Registry} instances.
 * 
 * @author A248
 *
 */
public interface PlatformConvention {

	/**
	 * Gets or initialises the {@link Registry} for the specific platform
	 * 
	 * @return the registry, never {@code null}
	 */
	Registry getRegistry();
	
}
