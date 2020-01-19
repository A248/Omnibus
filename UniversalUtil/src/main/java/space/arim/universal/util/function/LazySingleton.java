/* 
 * UniversalUtil, simple utilities for Spigot and BungeeCord
 * Copyright © 2019 Anand Beh <https://www.arim.space>
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
 * A thread safe singleton supplier.
 * 
 * @author A248
 *
 * @param <T> the type of the underlying singleton
 */
public class LazySingleton<T> implements Supplier<T> {
	
	volatile T value;
	
	private final Supplier<T> instantiator;
	
	/**
	 * Creates a new LazySingleton with a given Supplier. <br>
	 * When the underlying object is created as explained in {@link #get()}, {@link Supplier#get()} is called.
	 * 
	 * @param instantiator the Supplier to use
	 */
	public LazySingleton(Supplier<T> instantiator) {
		this.instantiator = instantiator;
	}
	
	/**
	 * Gets the object. <br>
	 * If the object has not been instantiated, a new one is created.
	 * 
	 * @return the object
	 */
	@Override
	public T get() {
		if (value == null) {
			synchronized (value) {
				if (value == null) {
					value = instantiator.get();
				}
			}
		}
		return value;
	}
	
	/**
	 * Checks whether the underlying value is initialised.
	 * 
	 * @return true if and only if the value is not <code>null</code>
	 */
	public boolean has() {
		return value != null;
	}

}
