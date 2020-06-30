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
package space.arim.universal.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

public class ArraysUtilTest {

	@Test
	public void testExpandAndInsertLiteral() {
		Integer[] source = new Integer[] {0, 5, 6, 8};
		assertArrayEquals(new Integer[] {0, 4, 5, 6, 8}, ArraysUtil.expandAndInsert(source, 4, 1));
		assertArrayEquals(new Integer[] {4, 0, 5, 6, 8}, ArraysUtil.expandAndInsert(source, 4, 0));
		assertArrayEquals(new Integer[] {0, 5, 6, 8, 4}, ArraysUtil.expandAndInsert(source, 4, 4));
	}
	
	@Test
	public void testExpandAndInsertSorted() {
		Random r = ThreadLocalRandom.current();
		Integer[] source = randomExponentialArray(3 + r.nextInt(6), 3 + r.nextInt(6));
		Integer element = source[0] + 1;
		int binarySearch = Arrays.binarySearch(source, element);
		int insertionIndex = - (binarySearch + 1);
		assertEquals(1, insertionIndex);
		assertArrayIsSorted(ArraysUtil.expandAndInsert(source, element, insertionIndex));
	}
	
	private static Integer[] randomExponentialArray(int length, Integer firstElement) {
		Integer[] source = new Integer[length];
		int currentElement = firstElement;
		for (int n = 0; n < source.length; n++) {
			source[n] = currentElement;
			currentElement *= firstElement;
		}
		return source;
	}
	
	private static <T> void assertArrayIsSorted(T[] array) {
		T[] clone = array.clone();
		Arrays.sort(clone);
		assertArrayEquals(clone, array);
	}
	
	@Test
	public void testContractAndRemoveLitera() {
		Integer[] source = new Integer[] {0, 4, 5, 6, 8};
		assertArrayEquals(new Integer[] {0, 4, 6, 8}, ArraysUtil.contractAndRemove(source, 2));
		assertArrayEquals(new Integer[] {0, 5, 6, 8}, ArraysUtil.contractAndRemove(source, 1));
		assertArrayEquals(new Integer[] {0, 4, 5, 6}, ArraysUtil.contractAndRemove(source, 4));
	}
	
	@Test
	public void testContractAndRemoveSorted() {
		Random r = ThreadLocalRandom.current();
		Integer[] source = randomExponentialArray(3 + r.nextInt(8), 3 + r.nextInt(8));
		Integer[] result = ArraysUtil.contractAndRemove(source, 1);
		assertEquals(source[2], result[1]);
		assertEquals(1, - (Arrays.binarySearch(result, source[1]) + 1));
	}
	
	@Test
	public void testMaintainArrayExpandAndInsertAndContractAndRemove() {
		Integer[] source = randomExponentialArray(8, 3);
		for (int n = 0; n < source.length; n++) {

			Integer[] expandedFirst = ArraysUtil.expandAndInsert(source, 2, n);
			assertArrayEquals(source, ArraysUtil.contractAndRemove(expandedFirst, n));

		}
	}
	
}
