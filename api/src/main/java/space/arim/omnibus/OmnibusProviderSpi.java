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

/**
 * Service provider interface for using {@link Omnibus} instances. Methods correspond to those
 * in {@code OmnibusProvider}, which delegates to the provider of this service.
 * 
 * @author A248
 *
 */
public interface OmnibusProviderSpi {
	
	/**
	 * Gets the central {@code Omnibus} instance. This is usually a singleton. <br>
	 * Corresponds to {@code OmnibusProvider.getOmnibus()}. <br>
	 * <br>
	 * If the caller class is never used, then {@link #requiresCallerClass()}.
	 * 
	 * @param callerClass the class which called {@code OmnibusProvider.getOmnibus()}
	 * @return the central omnibus instance the caller class should be using
	 */
	Omnibus getOmnibusSingleton(Class<?> callerClass);
	
	/**
	 * Whether this provider requires that the caller class be looked up. <br>
	 * If the implementation of {@link #getOmnibusSingleton(Class)} ignores
	 * the {@code callerClass} parameter, this method should return {@code false}
	 * 
	 * @return true if the caller class lookup is required, false otherwise
	 */
	boolean requiresCallerClass();
	
}
