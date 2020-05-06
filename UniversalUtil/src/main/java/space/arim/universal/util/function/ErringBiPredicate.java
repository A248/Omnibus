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
package space.arim.universal.util.function;

/**
 * An erring version of {@link java.util.function.BiPredicate}
 * 
 * @author A248
 *
 * @param <T> the type of the first object
 * @param <U> the type of the second object
 * @param <X> the type of the exception
 */
public interface ErringBiPredicate<T, U, X extends Throwable> {
	
	/**
	 * Evaluates this predicate based on the given arguments
	 * 
	 * @param obj1 the first object
	 * @param obj2 the second object
	 * @return true or false
	 * @throws X possibly, as parameterised
	 */
	boolean test(T obj1, U obj2) throws X;
	
}
