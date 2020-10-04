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
package space.arim.omnibus.spi;

import space.arim.omnibus.Omnibus;

/**
 * Manager responsible for returning {@link Omnibus} instances
 * 
 * @author A248
 *
 */
public interface OmnibusDefiner {

	/**
	 * Gets an {@code Omnibus} instance for the given caller class. This is usually
	 * a singleton. <br>
	 * This method corresponds to {@code OmnibusProvider.getOmnibus()} where
	 * {@code callerClass} is its caller. <br>
	 * <br>
	 * If the caller class is never used, then {@link #requiresCallerClass()} should
	 * return false.
	 * 
	 * @param callerClass the class which called
	 *                    {@code OmnibusProvider.getOmnibus()}, or null if the
	 *                    caller class is not required
	 * @return the central omnibus instance the caller class should be using
	 */
	Omnibus getOmnibus(Class<?> callerClass);

	/**
	 * Whether this provider requires that the caller class be looked up. <br>
	 * If the implementation of {@link #getOmnibus(Class)} ignores the
	 * {@code callerClass} parameter, this method should return {@code false}
	 * 
	 * @return true if the caller class lookup is required, false otherwise
	 */
	boolean requiresCallerClass();
	
}
