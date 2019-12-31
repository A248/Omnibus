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
package space.arim.universal.util.collections;

import java.util.Collection;
import java.util.function.Function;
import space.arim.universal.util.function.erring.ErringFunction;
import space.arim.universal.util.function.erring.ErringUnaryOperator;

/**
 * Utility class similar to {@link CollectionsUtil} but whose methods throw exceptions and accept erring functions. <br>
 * <br>
 * <b>Designed to reduce boilerplate operations</b>.
 * 
 * @author A248
 *
 */
public final class ErringCollectionsUtil {

	private ErringCollectionsUtil() {}
	
	/**
	 * Mutates the input array, setting each element to {@link ErringUnaryOperator#apply(Object)}
	 * 
	 * @param <T> the type of the array
	 * @param original the input array
	 * @param wrapper the {@link ErringUnaryOperator} to wrap each element
	 * @return the mutated array where each element has been replaced with UnaryOperator.wrap(previous element)
	 * @throws X according to {@link ErringUnaryOperator#apply}
	 */
	public static <T, X extends Throwable> T[] wrapAll(T[] original, ErringUnaryOperator<T, X> wrapper) throws X {
		for (int n = 0; n < original.length; n++) {
			original[n] = wrapper.apply(original[n]);
		}
		return original;
	}
	
	/**
	 * Same as {@link CollectionsUtil#checkForAnyMatches(Collection, Function)} but uses an {@link ErringFunction} instead.
	 * 
	 * @param <T> the type of the collection
	 * @param <X> the type of the exception
	 * @param collection the collection across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>any</b> element
	 * @throws X according to {@link ErringFunction#apply(Object)}
	 */
	public static <T, X extends Throwable> boolean checkForAnyMatches(Collection<T> collection, ErringFunction<T, Boolean, X> checker) throws X {
		for (T element : collection) {
			if (checker.apply(element)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Same as {@link #checkForAnyMatches(Collection, ErringFunction)} but accepts an array instead.
	 * 
	 * @param <T> the type of the array
	 * @param <X> the type of the exception
	 * @param array the array across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>any</b> element
	 * @throws X according to {@link ErringFunction#apply(Object)}
	 */
	public static <T, X extends Throwable> boolean checkForAnyMatches(T[] array, ErringFunction<T, Boolean, X> checker) throws X {
		for (T element : array) {
			if (checker.apply(element)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Same as {@link CollectionsUtil#checkForAllMatches(Collection, Function)} but uses an {@link ErringFunction} instead.
	 * 
	 * @param <T> the type of the collection
	 * @param <X> the type of the exception
	 * @param collection the collection across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>every</b> element
	 * @throws X according to {@link ErringFunction#apply(Object)}
	 */
	public static <T, X extends Throwable> boolean checkForAllMatches(Collection<T> collection, ErringFunction<T, Boolean, X> checker) throws X {
		for (T element : collection) {
			if (!checker.apply(element)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Same as {@link #checkForAllMatches(Collection, Function)} but accepts an array instead.
	 * 
	 * @param <T> the type of the array
	 * @param <X> the type of the exception
	 * @param array the array across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>every</b> element
	 * @throws X according to {@link ErringFunction#apply(Object)}
	 */
	public static <T, X extends Throwable> boolean checkForAllMatches(T[] array, ErringFunction<T, Boolean, X> checker) throws X {
		for (T element : array) {
			if (!checker.apply(element)) {
				return false;
			}
		}
		return true;
	}
	
}
