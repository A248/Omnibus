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

/**
 * Service provider interface allowing replacement of the default {@link OmnibusDefiner}
 * 
 * @author A248
 *
 */
public interface OmnibusProviderSpi {
	
	/**
	 * Creates the definer instance. <br>
	 * <br>
	 * This method will only be called if this provider is selected assuming its priority ({@link #priority()})
	 * is higher than all other providers.
	 * 
	 * @return the definer instance
	 */
	OmnibusDefiner createDefiner();
	
	/**
	 * The priority of this provider. If multiple providers are found, the one with the highest
	 * priority is used
	 * 
	 * @return the priority of this provider
	 */
	byte priority();
	
}
