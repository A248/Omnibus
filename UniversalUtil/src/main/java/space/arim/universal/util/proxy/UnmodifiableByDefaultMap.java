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
package space.arim.universal.util.proxy;

import java.util.Map;

/**
 * Used as a helper for creating unmodifiable maps. <br>
 * <b>Do not assume that instances of this interface are themselves immutables! Methods can be overriden!</b> <br>
 * <br>
 * Modification operations are implemented by default:
 * {@link #put(Object, Object)}, {@link #remove(Object)}, {@link #putAll(Map)}, and {@link #clear()} all
 * throw <code>UnsupportedOperationException</code>.
 * 
 * @author A248
 *
 * @param <K>
 * @param <V>
 */
public interface UnmodifiableByDefaultMap<K, V> extends Map<K, V> {
	
	@Override
	default V put(K key, V value) {throw new UnsupportedOperationException();}
	@Override
	default V remove(Object key) {throw new UnsupportedOperationException();}
	@Override
	default void putAll(Map<? extends K, ? extends V> m) {throw new UnsupportedOperationException();}
	@Override
	default void clear() {throw new UnsupportedOperationException();}
	
}
