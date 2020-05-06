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
package space.arim.universal.util.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Utility class to apply operations to collections and arrays. <br>
 * <br>
 * <b>Designed to reduce boilerplate operations</b>.
 * 
 * @author A248
 *
 */
public final class CollectionsUtil {

	// Prevent instantiation
	private CollectionsUtil() {}
	
	/**
	 * Recursively retrieves a specified type of object from a Map of potentially nested maps. <br>
	 * Periods delineate a nested map.
	 * <br>
	 * This method is particularly useful for configuration loaded thorugh SnakeYAML. <br>
	 * Specifically, if one must retrieve the yaml value key1.subkey.value as an Integer from the map <code>configValues</code>,
	 * one should call use <code>getFromMapRecursive(configValues, "key1.subkey.value", Integer.class)</code> <br>
	 * 
	 * @param <T> the type to retrieve. If the object found is not this type, <code>null</code> is returned
	 * @param map the map from which to retrieve recursively
	 * @param key the key string
	 * @param type the type class
	 * @return the object if found, null if not
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getFromMapRecursive(Map<String, Object> map, String key, Class<T> type) {
		if (key == null) {
			return null;
		} else if (!key.contains(".")) {
			Object obj = map.get(key);
			return type.isInstance(obj) ? (T) obj : null;
		} else if (key.startsWith(".") || key.endsWith(".")) {
			throw new IllegalArgumentException("Cannot retrieve value for invalid key " + key);
		}
		return getFromMapRecursive((Map<String, Object>) map.get(key.substring(0, key.indexOf("."))), key.substring(key.indexOf(".") + 1), type);
	}
	
	/**
	 * Mutates the input array, setting each element to {@link UnaryOperator#apply(Object)}
	 * 
	 * @param <T> the type of the array
	 * @param original the input array
	 * @param wrapper the {@link UnaryOperator} to wrap each element
	 * @return the mutated array where each element has been replaced with UnaryOperator.wrap(previous element)
	 */
	public static <T> T[] wrapAll(T[] original, UnaryOperator<T> wrapper) {
		for (int n = 0; n < original.length; n++) {
			original[n] = wrapper.apply(original[n]);
		}
		return original;
	}
	
	/**
	 * Iterates across the collection, if {@link Predicate#test(Object)} for any element returns <code>true</code>, the method returns true. Otherwise, returns false.
	 * 
	 * @param <T> the type of the collection
	 * @param collection the collection across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>any</b> element
	 */
	public static <T> boolean checkForAnyMatches(Collection<T> collection, Predicate<? super T> checker) {
		for (T element : collection) {
			if (checker.test(element)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Same as {@link #checkForAnyMatches(Collection, Predicate)} but accepts an array instead.
	 * 
	 * @param <T> the type of the array
	 * @param array the array across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>any</b> element
	 */
	public static <T> boolean checkForAnyMatches(T[] array, Predicate<? super T> checker) {
		for (T element : array) {
			if (checker.test(element)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Iterates across the input array, if {@link Predicate#test(Object)} for any element returns <code>false</code>, the method returns false. Otherwise, returns true.
	 * 
	 * @param <T> the type of the collection
	 * @param collection the collection across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>every</b> element
	 */
	public static <T> boolean checkForAllMatches(Collection<T> collection, Predicate<? super T> checker) {
		for (T element : collection) {
			if (!checker.test(element)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Same as {@link #checkForAllMatches(Collection, Predicate)} but accepts an array instead.
	 * 
	 * @param <T> the type of the array
	 * @param array the array across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>every</b> element
	 */
	public static <T> boolean checkForAllMatches(T[] array, Predicate<? super T> checker) {
		for (T element : array) {
			if (!checker.test(element)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Converts an object array to a string array according to each object's {@link Object#toString()} method
	 * 
	 * @param <T> the type of the array
	 * @param original the input array
	 * @return a string array
	 * 
	 */
	public static <T> String[] convertAllToString(T[] original) {
		String[] result = new String[original.length];
		for (int n = 0; n < original.length; n++) {
			result[n] = original[n].toString();
		}
		return result;
	}
	
	/**
	 * Gets a random element from a collection. <br>
	 * If the input collection is <code>null</code> or empty, <code>null</code> is returned. <br>
	 * <br>
	 * This method is thread safe so long as the collection's iterator is thread safe.
	 * The underlying collection may be concurrently modified without compromising
	 * the integrity of this method call.
	 * 
	 * @param <T> the type of the collection
	 * @param collection the collection
	 * @return a random element from the collection, or <code>null</code> if preconditions are not met
	 */
	public static <T> T random(Collection<T> collection) {
		if (collection == null) {
			return null;
		}
		int n = 0;
		int size = collection.size();
		if (size == 0) {
			return null;
		}
		// alright, the collection is non-empty, get a random index
		int index = ThreadLocalRandom.current().nextInt(size);
		// scan the collection to find the element at the index
		for (Iterator<T> it = collection.iterator(); it.hasNext();) {
			if (n == index) {
				return it.next();
			}
			it.next();
			n++;
		}
		// huh, we must've encountered a concurrency problem, so we'll repeat
		return random(collection);
	}
	
}
