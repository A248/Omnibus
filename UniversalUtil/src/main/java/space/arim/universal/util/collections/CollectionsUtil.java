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
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class CollectionsUtil {

	private CollectionsUtil() {}
	
	public static <T> T[] wrapAll(T[] original, UnaryOperator<T> wrapper) {
		for (int n = 0; n < original.length; n++) {
			original[n] = wrapper.apply(original[n]);
		}
		return original;
	}
	
	public static <T> String[] convertAllToString(T[] original) {
		return convertAll(original, (object) -> object.toString());
	}
	
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
	
	public static <K, V> Map<K, V> valueWrappedMap(Map<K, V> original, UnaryOperator<V> wrapper) {
		return new ValueWrappedMap<K, V>(original, wrapper);
	}
	
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
		public V get(Object key) {throw new UnsupportedOperationException();}
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
