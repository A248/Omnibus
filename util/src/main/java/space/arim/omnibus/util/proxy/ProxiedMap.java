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
package space.arim.omnibus.util.proxy;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A parent class for maps which simply redirect, a.k.a. <i>proxy</i>, calls to another map (the backing map). <br>
 * Such "maps" do not hold any data themselves, but merely refer calls to their backing maps. <br>
 * <br>
 * However, the proxy map's additional call layer provides fine tuned control over reads and writes to the backing map. <br>
 * Programmers may extend this class to utilise this enhanced control. {@link DishonestMap} is a simple example. <br>
 * <br>
 * Note that a reference is retained to the backing map. Changes to the backing map are reflected in proxied maps.
 * 
 * @author A248
 *
 * @param <K> the key type
 * @param <V> the value type
 */
@SuppressWarnings("unlikely-arg-type")
public abstract class ProxiedMap<K, V> extends ProxiedObject<Map<K, V>> implements Map<K, V> {

	/**
	 * Creates a ProxiedMap based on a backing map
	 * 
	 * @param original the original, backing map
	 */
	protected ProxiedMap(Map<K, V> original) {
		super(original);
	}
	
	// Main methods
	@Override
	public int size() {return getOriginal().size();}
	@Override
	public boolean isEmpty() {return getOriginal().isEmpty();}
	@Override
	public boolean containsKey(Object key) {return getOriginal().containsKey(key);}
	@Override
	public boolean containsValue(Object value) {return getOriginal().containsValue(value);}
	@Override
	public V get(Object key) {return getOriginal().get(key);}
	@Override
	public V put(K key, V value) {return getOriginal().put(key, value);}
	@Override
	public V remove(Object key) {return getOriginal().remove(key);}
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {getOriginal().putAll(m);}
	@Override
	public void clear() {getOriginal().clear();}
	@Override
	public Set<K> keySet() {return getOriginal().keySet();}
	@Override
	public Collection<V> values() {return getOriginal().values();}
	@Override
	public Set<Entry<K, V>> entrySet() {return getOriginal().entrySet();}
	// Default methods
	@Override
	public V getOrDefault(Object key, V defaultValue) {return getOriginal().getOrDefault(key, defaultValue);}
	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {getOriginal().forEach(action);}
	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {getOriginal().replaceAll(function);}
	@Override
	public V putIfAbsent(K key, V value) {return getOriginal().putIfAbsent(key, value);}
	@Override
	public boolean remove(Object key, Object value) {return getOriginal().remove(key, value);}
	@Override
	public boolean replace(K key, V oldValue, V newValue) {return getOriginal().replace(key, oldValue, newValue);}
	@Override
	public V replace(K key, V value) {return getOriginal().replace(key, value);}
	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {return getOriginal().computeIfAbsent(key, mappingFunction);}
	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {return getOriginal().computeIfPresent(key, remappingFunction);}
	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {return getOriginal().compute(key, remappingFunction);}
	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {return getOriginal().merge(key, value, remappingFunction);}
	// toString
	@Override
	public String toString() {
		return "ProxiedMap [getOriginal()=" + getOriginal() + "]";}
	// equals and hashCode
	@Override
	public boolean equals(Object o) {return getOriginal().equals(o);}
	@Override
	public int hashCode() {return getOriginal().hashCode();}
	
}
