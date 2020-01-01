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
package space.arim.universal.util.function.erring;

/**
 * An erring version of {@link java.util.Comparator}
 * 
 * @author A248
 * 
 * @param <T> the type of the compared objects
 * @param <X> the type of the exception
 * 
 */
public interface ErringComparator<T, X extends Throwable> {

	/**
	 * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.
	 * 
	 * @param obj1 the first object
	 * @param obj2 the second object
	 * @return the comparison value
	 * @throws X possibly, as parameterised
	 */
	int compare(T obj1, T obj2) throws X;
	
}
