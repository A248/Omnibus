/* 
 * UniversalUtil
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
 * A helper for proxying final objects. <br>
 * <br>
 * A "true" or perfect proxied object is an instance of the type it proxies to. E.g., {@link ProxiedMap} implements <code>Map</code>. <br>
 * However, in some cases, perfect proxying may not be possible. There can be no truly proxied <code>String</code>,
 * since <code>String</code> is <i>final</i>. <br>
 * <br>
 * This class represents an object which returns a proxied value. {@link CaptiveReference} may be used for a default implementation.
 * 
 * @author A248
 *
 * @param <T> the type of the proxied object
 */
public abstract class ProxiedReference<T> {

	/**
	 * Gets the object which this ProxiedReference proxies to.
	 * 
	 * @return the proxied object
	 */
	protected abstract T getValue();
	
}
