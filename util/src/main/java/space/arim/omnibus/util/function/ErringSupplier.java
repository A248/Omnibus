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

/**
 * Similar to {@link java.util.function.Supplier} but throws a type of exception
 * 
 * @author A248
 *
 * @param <T> the type of the object supplied
 * @param <X> the type of the exception
 */
public interface ErringSupplier<T, X extends Throwable> {

	/**
	 * Retrieves the result, possibly throwing an exception
	 * 
	 * @return a result
	 * @throws X possibly, as parameterised
	 */
	T get() throws X;
	
}
