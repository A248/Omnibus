/* 
 * UniversalUtil, simple utilities for Spigot and BungeeCord
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * UniversalUtil is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalUtil is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalUtil. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.util.proxy;

/**
 * A simple implementation of {@link ProxiedReference} supplied with the proxied object at construction.
 * 
 * @author A248
 *
 * @param <T> the type of the proxied object
 */
public class CaptiveReference<T> extends ProxiedReference<T> {

	private final T value;
	
	/**
	 * Instantiates with the given proxied object
	 * 
	 * @param value the object to which this CaptiveReference proxies
	 */
	protected CaptiveReference(T value) {
		this.value = value;
	}
	
	@Override
	protected T getValue() {
		return value;
	}

}
