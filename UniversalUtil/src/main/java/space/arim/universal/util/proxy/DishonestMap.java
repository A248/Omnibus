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

import java.util.Collections;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * A {@link ProxiedMap} which does nothing other than change the result of calls to {@link Map#get(Object)}. <br>
 * <br>
 * The backing map's <code>#get</code> is first processed by the provided {@link UnaryOperator} before being passed to the caller. <br>
 * DishonestMap is useful for modifying returned values in a Map.
 * 
 * @author A248
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class DishonestMap<K, V> extends ProxiedMap<K, V> {
	
	private final UnaryOperator<V> processor;
	
	/**
	 * Creates a DishonestMap using the given map as the backing map and the {@link UnaryOperator} to process {@link Map#get(Object)} operations. <br>
	 * <br>
	 * When {@link #get(Object)} is called, {@link UnaryOperator#apply(Object)} is invoked on the results of <code>original.get(key)</code> where <i>original</i>
	 * is the backing map and <i>key</i> is the map key passed to <code>#get</code> <br>
	 * <br>
	 * For example, a map of unmodifiable maps could be created like so: <br>
	 * <code>Map&lt;String, Map&gt; mapOfUnmodifiableMaps = new DishonestMap&lt;String, Map&gt;(new HashMap&lt;String, Map&gt;, Collections::unmodifiableMap);</code> <br>
	 * Such a map would support all normal read and write operations, such as <code>#put</code>; however, its <code>#get</code>
	 * method would return an unmodifiable collection view (per {@link Collections#unmodifiableMap}) when called.
	 * (Essentially, the map's values would be unmodifiable maps, but the map as a whole would allow modification.
	 * For complete and total immutability use this example with {@link UnmodifiableDishonestMap} instead.<br>
	 * <br>
	 * Note that a reference is retained to the backing map. Changes to the backing map are reflected in the DishonestMap.
	 * 
	 * @param original the original, backing map
	 * @param processor with which to modify <code>#get</code> results
	 */
	public DishonestMap(Map<K, V> original, UnaryOperator<V> processor) {
		super(original);
		this.processor = processor;
	}
	
	/**
	 * Gets the same UnaryOperator passed to the constructor. <br>
	 * The UnaryOperator is used to modifiy the results of <code>Map#get</code> as explained in {@link #get(Object)}.
	 * 
	 * @return the processor
	 */
	public final UnaryOperator<V> getProcessor() {
		return processor;
	}
	
	/**
	 * Takes the result of the backing map's corresponding <code>#get</code> invocation, <br>
	 * processes it according to the UnaryOperator passed to the constructor.
	 * 
	 * Equivalent to calling <code>{@link #getProcessor}.apply(originalMap.get(key))</code> where <i>originalMap</i> is the map passed to the constructor.
	 * 
	 * @return the processed result
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public V get(Object key) {
		return processor.apply(super.get(key));
	}
	
}