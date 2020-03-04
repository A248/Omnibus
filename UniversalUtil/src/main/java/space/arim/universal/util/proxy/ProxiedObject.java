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
 * A general parent object for objects, which simply redirect, a.k.a. <i>proxy</i>, calls to another object (the backing object). <br>
 * The proxy object is itself an instance of the backing object, and thus it may be substituted in order to provide refined control.
 * 
 * @author A248
 *
 * @param <T> the type of the backing object
 */
public abstract class ProxiedObject<T> {

	private final T original;
	
	/**
	 * Creates a ProxiedObject based on a backing object. <br>
	 * <br>
	 * If the backing object is <code>null</code>, the programmer must override {@link #getOriginal()}
	 * to provide a nonnull backing object when calls are relayed to the backing object.
	 * 
	 * @param original the backing object
	 */
	protected ProxiedObject(T original) {
		this.original = original;
	}
	
	/**
	 * Gets the original, backing object, to which this ProxiedObject proxies. <br>
	 * Should never return <code>null</code>. <br>
	 * <br>
	 * Programmers may override this method to dynamically provide the backing object.
	 * 
	 * @return the backing object
	 */
	protected T getOriginal() {
		return original;
	}
	
}
