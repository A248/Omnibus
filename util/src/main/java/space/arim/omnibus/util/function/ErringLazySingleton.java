/* 
 * Omnibus-util
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-util. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.util.function;

import java.util.Objects;

/**
 * A thread-safe singleton supplier which may throw an error upon instantiation.
 * 
 * @author A248
 *
 * @param <T> the type of the singleton
 * @param <X> the type of the exception
 */
public class ErringLazySingleton<T, X extends Throwable> implements ErringSupplier<T, X> {

	private volatile T value;
	
	private final ErringSupplier<T, X> instantiator;
	
	public ErringLazySingleton(ErringSupplier<T, X> instantiator) {
		this.instantiator = Objects.requireNonNull(instantiator, "Instantiator must not be null!");
	}
	
	@Override
	public T get() throws X {
		if (value == null) {
			synchronized (instantiator) {
				if (value == null) {
					value = instantiator.get();
				}
			}
		}
		return value;
	}
	
}
