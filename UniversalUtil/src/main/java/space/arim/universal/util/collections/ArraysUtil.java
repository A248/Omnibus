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
package space.arim.universal.util.collections;

import java.lang.reflect.Array;
import java.util.function.Function;

/**
 * For manipulating arrays.
 * 
 * @author A248
 *
 */
public final class ArraysUtil {

	// Prevent instantiation
	private ArraysUtil() {}
	
	/**
	 * Removes the element from the array without mutating the original. <br>
	 * If the element is not present in the array according to <code>Object#equals</code>, the original array is returned. <br>
	 * <br>
	 * If multiple array members match the element according to <code>Object#equals</code>, then all of the matching array members are removed.
	 * 
	 * @param <T> the type of the array
	 * @param original the source array
	 * @param element to be removed
	 * @return a new array with the specified element removed
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] remove(T[] original, T element) {
		if (!CollectionsUtil.checkForAnyMatches(original, element::equals)) {
			return original;
		}
		T[] result = (T[]) Array.newInstance(original.getClass().getComponentType(), original.length - 1);
		int index = 0;
		for (int n = 0; n < original.length; n++) {
			if (!original[n].equals(element)) {
				result[index] = original[n];
				index++;
			}
		}
		return result;
	}
	
	/**
	 * Appends an element to the array without mutating the original.
	 * 
	 * @param <T> the type of the array
	 * @param original the source array
	 * @param element to be added
	 * @return a new array with the specified element added
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] add(T[] original, T element) {
		T[] result = (T[]) Array.newInstance(original.getClass().getComponentType(), original.length + 1);
		result[original.length] = element;
		return result;
	}
	
	/**
	 * Scans the array to determine if the specified element is present according to Object#equals. <br>
	 * If not, appends the element according to {@link #add(Object[], Object)}
	 * 
	 * @param <T> the type of the array
	 * @param original the source array
	 * @param element the element to add
	 * @return a new array if the element did not match
	 */
	public static <T> T[] addIfNotPresent(T[] original, T element) {
		return CollectionsUtil.checkForAnyMatches(original, element::equals) ? original : add(original, element);
	}
	
	/**
	 * Concatenates the {@link #toString()} representations of each element. <br>
	 * <br>
	 * The result will always start and end with '{' and '}'. Inside, each element is separated by a comma.
	 * 
	 * @param <T> the type of the array
	 * @param elements the element array
	 * @return a formatted String
	 */
	public static <T> String toString(T[] elements) {
		StringBuilder builder = new StringBuilder('{');
		for (int n = 0; n < elements.length; n++) {
			if (n > 0) {
				builder.append(',');
			}
			builder.append(elements[n].toString());
		}
		builder.append('}');
		return builder.toString();
	}
	
	/**
	 * Concatenates the {@link Function#apply(Object)} representations of each element. <br>
	 * <br>
	 * The result will always start and end with '{' and '}'. Inside, each element is separated by a comma.
	 * 
	 * @param <T> the type of the array
	 * @param elements the element array
	 * @param representer the function used to determine the string representation of each element
	 * @return a formatted String
	 */
	public static <T> String toString(T[] elements, Function<T, String> representer) {
		StringBuilder builder = new StringBuilder('{');
		for (int n = 0; n < elements.length; n++) {
			if (n > 0) {
				builder.append(',');
			}
			builder.append(representer.apply(elements[n]));
		}
		builder.append('}');
		return builder.toString();
	}
	
}
