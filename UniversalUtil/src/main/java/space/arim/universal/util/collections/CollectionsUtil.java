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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import space.arim.universal.util.function.ErringFunction;

/**
 * Utility class to apply operations to collections and arrays, as well as better unmodifiable collections support. <br>
 * <br>
 * <b>Designed to reduce boilerplate operations</b>.
 * 
 * @author A248
 *
 */
public final class CollectionsUtil {

	private CollectionsUtil() {}
	
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
	 * Applies an action to all elements of a collection where checker.apply(element) returns <code>true</code>
	 * 
	 * @param <T> the type of the collection
	 * @param collection the collection
	 * @param checker the checker function
	 * @param action the action to apply if checker.apply(element) returns <code>true</code>
	 */
	public static <T> void forEachApplicable(Collection<T> collection, Function<T, Boolean> checker, Consumer<T> action) {
		collection.forEach((element) -> {
			if (checker.apply(element)) {
				action.accept(element);
			}
		});
	}
	
	/**
	 * 
	 * Iterates across the collection, if {@link Function#apply(Object)} for any element returns <code>true</code>, the method returns true. Otherwise, returns false.
	 * 
	 * @param <T> the type of the collection
	 * @param collection the collection across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>any</b> element
	 */
	public static <T> boolean checkForAnyMatches(Collection<T> collection, Function<T, Boolean> checker) {
		for (T element : collection) {
			if (checker.apply(element)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Same as {@link #checkForAnyMatches(Collection, Function)} but accepts an array instead.
	 * 
	 * @param <T> the type of the array
	 * @param array the array across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>any</b> element
	 */
	public static <T> boolean checkForAnyMatches(T[] array, Function<T, Boolean> checker) {
		for (T element : array) {
			if (checker.apply(element)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Same as {@link #checkForAnyMatches(Collection, Function)} but possibly throws an exception.
	 * 
	 * @param <T> the type of the collection
	 * @param <X> the type of the exception
	 * @param collection the collection across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>any</b> element
	 * @throws X according to {@link ErringFunction#apply(Object)}
	 */
	public static <T, X extends Throwable> boolean checkForAnyMatchesErring(Collection<T> collection, ErringFunction<T, Boolean, X> checker) throws X {
		for (T element : collection) {
			if (checker.apply(element)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Same as {@link #checkForAnyMatches(Object[], ErringFunction)} but possibly throws an exception.
	 * 
	 * @param <T> the type of the array
	 * @param <X> the type of the exception
	 * @param array the array across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>any</b> element
	 * @throws X according to {@link ErringFunction#apply(Object)}
	 */
	public static <T, X extends Throwable> boolean checkForAnyMatchesErring(T[] array, ErringFunction<T, Boolean, X> checker) throws X {
		for (T element : array) {
			if (checker.apply(element)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Iterates across the input array, if {@link Function#apply(Object)} for any element returns <code>false</code>, the method returns false. Otherwise, returns true.
	 * 
	 * @param <T> the type of the collection
	 * @param collection the collection across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>every</b> element
	 */
	public static <T> boolean checkForAllMatches(Collection<T> collection, Function<T, Boolean> checker) {
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
	 * @param array the array across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>every</b> element
	 */
	public static <T> boolean checkForAllMatches(T[] array, Function<T, Boolean> checker) {
		for (T element : array) {
			if (!checker.apply(element)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Same as {@link #checkForAllMatches(Collection, Function)} but possibly throws an exception.
	 * 
	 * @param <T> the type of the collection
	 * @param <X> the type of the exception
	 * @param collection the collection across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>every</b> element
	 * @throws X according to {@link ErringFunction#apply(Object)}
	 */
	public static <T, X extends Throwable> boolean checkForAllMatchesErring(Collection<T> collection, ErringFunction<T, Boolean, X> checker) throws X {
		for (T element : collection) {
			if (!checker.apply(element)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Same as {@link #checkForAllMatches(Object[], Function)} but possibly throws an exception.
	 * 
	 * @param <T> the type of the array
	 * @param <X> the type of the exception
	 * @param array the array across which to iterate
	 * @param checker used when checking an element
	 * @return true if and only if checker.apply(element) returns true for <b>every</b> element
	 * @throws X according to {@link ErringFunction#apply(Object)}
	 */
	public static <T, X extends Throwable> boolean checkForAllMatchesErring(T[] array, ErringFunction<T, Boolean, X> checker) throws X {
		for (T element : array) {
			if (!checker.apply(element)) {
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
	 */
	public static <T> String[] convertAllToString(T[] original) {
		return convertAll(original, (object) -> object.toString());
	}
	
	/**
	 * Converts an array of type T to type R according to the given mapping function.
	 * 
	 * @param <T> the type of the original array
	 * @param <R> the typoe of the target array
	 * @param original the original array
	 * @param mapper the mapping function
	 * @param doNotPassThisVariable ignore this parameter and do not pass it. It is used for internal mechanics.
	 * @return a converted array
	 */
	@SuppressWarnings("unchecked")
	public static <T, R> R[] convertAll(T[] original, Function<T, R> mapper, R...doNotPassThisVariable) {
		if (doNotPassThisVariable.length > 0) {
			throw new IllegalArgumentException("Do not pass the last parameter!");
		}
		R[] results = (R[]) Array.newInstance(doNotPassThisVariable.getClass().getComponentType(), original.length);
		for (int n = 0; n < original.length; n++) {
			results[n] = mapper.apply(original[n]);
		}
		return results;
	}
	
	/**
	 * Creates a map whose {@link Map#get(Object)} is first wrapped according to the given wrapping function before being passed to the caller. <br>
	 * <br>
	 * This is useful for modifying returned values in an API if needed. Calling {@link Map#get(Object)} on a map created with this method is equivalent to calling <code>wrapper.apply(original.get(key))</code>.
	 * 
	 * @param <K> the type of a map key
	 * @param <V> the type of a map value
	 * @param original the original, backing map
	 * @param wrapper the {@link UnaryOperator} to apply for retrievals
	 * @return a map whose retrievals are wrapped accordingly
	 */
	public static <K, V> Map<K, V> valueWrappedMap(Map<K, V> original, UnaryOperator<V> wrapper) {
		return new ValueWrappedMap<K, V>(original, wrapper);
	}
	
	/**
	 * Similar to {@link #valueWrappedMap(Map, UnaryOperator)} in that it wraps all the values of the given map according to the wrapping function. <br>
	 * This method differs in that the mutating methods (put, remove, putAll, clear) of the wrapped map throw {@link UnsupportedOperationException} if accessed. <br>
	 * <br>
	 * <b>Useful for creating unmodifiable maps with nested unmodifiable collections</b>. <br>
	 * E.g., it is possible to return a map to a caller whose values are nested maps which are in themselves unmodifiable: <br>
	 * <code>Map nestedUnmodifiable = CollectionsUtil.unmodifiableValueWrappedMap(original, new UnaryOperator() {
	 * 
	 * public void apply(Map map) {
	 *   return Collections.unmodifiableMap(map);
	 * }
	 * }</code> <br>
	 * 
	 * @param <K> the type of a map key
	 * @param <V> the type of a map value
	 * @param original the original, backing map
	 * @param wrapper the {@link UnaryOperator} to apply for retrievals
	 * @return an unmodifiable map whose retrievals are wrapped accordingly.
	 */
	public static <K, V> Map<K, V> unmodifiableValueWrappedMap(Map<K, V> original, UnaryOperator<V> wrapper) {
		return new UnmodifiableValueWrappedMap<K, V>(original, wrapper);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	private static class WrappedMap<K, V> implements Map<K, V>, Serializable {
		
		/**
		 * Serial version id
		 */
		private static final long serialVersionUID = 9173816626493816021L;

        private final Map<K, V> original;
		
		WrappedMap(Map<K, V> original) {
			this.original = Objects.requireNonNull(original);
		}
		
		@Override
		public int size() {return original.size();}
		@Override
		public boolean isEmpty() {return original.isEmpty();}
		@Override
		public boolean containsKey(Object key) {return original.containsKey(key);}
		@Override
		public boolean containsValue(Object value) {return original.containsValue(value);}
		@Override
		public V get(Object key) {return original.get(key);}
		@Override
		public V put(K key, V value) {return original.put(key, value);}
		@Override
		public V remove(Object key) {return original.remove(key);}
		@Override
		public void putAll(Map<? extends K, ? extends V> m) {original.putAll(m);}
		@Override
		public void clear() {original.clear();}
		@Override
		public Set<K> keySet() {return original.keySet();}
		@Override
		public Collection<V> values() {return original.values();}
		@Override
		public Set<Entry<K, V>> entrySet() {return original.entrySet();}
		
	}
	
	private static class ValueWrappedMap<K, V> extends WrappedMap<K, V> {
		
		/**
		 * Serial version id
		 */
		private static final long serialVersionUID = 7086908298319638652L;
		
		private final UnaryOperator<V> wrapper;
		
		ValueWrappedMap(Map<K, V> original, UnaryOperator<V> wrapper) {
			super(original);
			this.wrapper = wrapper;
		}
		
		@SuppressWarnings("unlikely-arg-type")
		@Override
		public V get(Object key) {
			return wrapper.apply(super.get(key));
		}
		
	}
	
	private static class UnmodifiableValueWrappedMap<K, V> extends ValueWrappedMap<K, V> {

		/**
		 * Serial version id
		 */
		private static final long serialVersionUID = -3678280252223253513L;

        private transient Set<K> keySet;
        private transient Set<Map.Entry<K,V>> entrySet;
        private transient Collection<V> values;
		
		UnmodifiableValueWrappedMap(Map<K, V> original, UnaryOperator<V> wrapper) {
			super(original, wrapper);
		}
		
		@Override
		public V put(K key, V value) {throw new UnsupportedOperationException();}
		@Override
		public V remove(Object key) {throw new UnsupportedOperationException();}
		@Override
		public void putAll(Map<? extends K, ? extends V> m) {throw new UnsupportedOperationException();}
		@Override
		public void clear() {throw new UnsupportedOperationException();}
		
		@Override
		public Set<K> keySet() {
			if (keySet == null) {
				keySet = Collections.unmodifiableSet(super.keySet());
			}
			return keySet;
		}
		@Override
		public Collection<V> values() {
			if (values == null) {
				values = Collections.unmodifiableCollection(super.values());
			}
			return values;
		}
		@Override
		public Set<Entry<K, V>> entrySet() {
			if (entrySet == null) {
				entrySet = Collections.unmodifiableSet(super.entrySet());
			}
			return entrySet;
		}        
		
	}
	
}
