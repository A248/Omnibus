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
package space.arim.universal.util.function;

import java.util.function.Supplier;

/**
 * Subclass of {@link LazySingleton} with {@link #set(Object)} added.
 * 
 * @author A248
 *
 * @param <T> the kind of the underlying object
 */
public class LazyReference<T> extends LazySingleton<T> {

	/**
	 * Same as {@link LazySingleton#LazySingleton(Supplier)}
	 * 
	 * @param instantiator the Supplier to use
	 */
	public LazyReference(Supplier<T> instantiator) {
		super(instantiator);
	}
	
	/**
	 * Sets the object.
	 * 
	 * @param value the new value
	 */
	public void set(T value) {
		this.value = value;
	}

}
