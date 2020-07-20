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
package space.arim.omnibus.util.function;

/**
 * An erring version of {@link java.util.function.Predicate}
 * 
 * @author A248
 *
 * @param <T> the type of the input object
 * @param <X> the type of the exception
 */
public interface ErringPredicate<T, X extends Throwable> {

	/**
     * Evaluates the predicate on the given argument.
     *
     * @param object the input object
     * @return true if the input argument matches the predicate, false otherwise
     * @throws X possibly, as parameterised
     */
    boolean test(T object) throws X;
	
}
