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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * An unmodifiable variant of {@link DishonestMap}. <br>
 * <br>
 * Attempts to add or remove key mappings throw {@link UnsupportedOperationException}. <br>
 * Note that the backing map MAY change; if so, changes are reflected in this UnmodifiableDishonestMap.
 * 
 * @author A248
 *
 * @param <K> the key tpye
 * @param <V> the value type
 */
public class UnmodifiableDishonestMap<K, V> extends DishonestMap<K, V> {
	
    private transient Set<K> keySetView;
    private transient Set<Map.Entry<K,V>> entrySetView;
    private transient Collection<V> valuesView;
	
    /**
	 * Creates an UnmodifiableDishonestMap using the given map as the backing map and the {@link UnaryOperator} to process {@link Map#get(Object)} operations. <br>
	 * <br>
	 * See {@link DishonestMap#DishonestMap(Map, UnaryOperator)} for more information. <br>
	 * <br>
	 * Example: creating an unmodifiable map of unmodifiable submaps.
	 * <code>Map&lt;String, Map&gt; totallyUnmodifiableMegaMap = new UnmodifiableDishonestMap&lt;String, Map&gt;(existingMap, Collections::unmodifiableMap);</code> where <i>existingMap</i> is the original map.
	 * 
     * @param original the original, backing map
	 * @param processor with which to modify <code>#get</code> results
	 */
	public UnmodifiableDishonestMap(Map<K, V> original, UnaryOperator<V> processor) {
		super(original, processor);
	}
	
	@Override
	public V put(K key, V value) {throw new UnsupportedOperationException();}
	@Override
	public V remove(Object key) {throw new UnsupportedOperationException();}
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {throw new UnsupportedOperationException();}
	@Override
	public void clear() {throw new UnsupportedOperationException();}
	
	/**
	 * Returns an unmodifiable view of the backing map's key set.
	 * 
	 */
	@Override
	public Set<K> keySet() {
		return (keySetView != null) ? keySetView : (keySetView = Collections.unmodifiableSet(super.keySet()));
	}
	
	/**
	 * Returns an unmodifiable view of the backing map's values.
	 * 
	 */
	@Override
	public Collection<V> values() {
		return (valuesView != null) ? valuesView : (valuesView = Collections.unmodifiableCollection(super.values()));
	}
	
	/**
	 * Returns an unmodifiable view of the backing map's entry set.
	 * 
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		return (entrySetView != null) ? entrySetView : (entrySetView = Collections.unmodifiableSet(super.entrySet()));
	}
	
}
