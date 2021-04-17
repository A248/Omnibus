/* 
 * Omnibus
 * Copyright © 2020 Anand Beh
 * 
 * Omnibus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Omnibus. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package space.arim.omnibus.util;

import java.lang.reflect.Array;

/**
 * Utility class for manipulating arrays, particularly sorted arrays, reducing the need to write
 * fast, error-prone array operations.
 * 
 * @author A248
 *
 */
public final class ArraysUtil {

	// Prevent instantiation
	private ArraysUtil() {}
	
	/**
	 * Creates a new array of the source array's length plus 1, then copies all the elements before the insertion index
	 * from the source array to the new array, and copies all elements after the insertion index to the new array.
	 * Then the element at the insertion index is set to the specified element.
	 * 
	 * @param <T> the array type
	 * @param sourceArray the source array
	 * @param element the element to insert
	 * @param insertionIndex the index at which to insert the element
	 * @return the expanded array
	 */
	public static <T> T[] expandAndInsert(T[] sourceArray, T element, int insertionIndex) {
		@SuppressWarnings("unchecked")
		T[] updated = (T[]) Array.newInstance(sourceArray.getClass().getComponentType(), sourceArray.length + 1);
		updated[insertionIndex] = element;
		System.arraycopy(sourceArray, 0, updated, 0, insertionIndex++);
		System.arraycopy(sourceArray, insertionIndex - 1, updated, insertionIndex, updated.length - insertionIndex);
		return updated;
	}
	
	/**
	 * Copies all the elements in the target array, except the element at <code>removalIndex</code>,
	 * to a new array, and returns the resulting array. In this way all the elements before the removalIndex are unaffected,
	 * and all those after the index are shifted up one, such that the order of existing elements is maintained.
	 * 
	 * @param <T> the array type
	 * @param sourceArray the source array
	 * @param removalIndex the index to skip
	 * @return the contracted array
	 */
	public static <T> T[] contractAndRemove(T[] sourceArray, int removalIndex) {
		@SuppressWarnings("unchecked")
		T[] updated = (T[]) Array.newInstance(sourceArray.getClass().getComponentType(), sourceArray.length - 1);
		System.arraycopy(sourceArray, 0, updated, 0, removalIndex++);
		System.arraycopy(sourceArray, removalIndex, updated, removalIndex - 1, sourceArray.length - removalIndex);
		return updated;
	}
	
}
