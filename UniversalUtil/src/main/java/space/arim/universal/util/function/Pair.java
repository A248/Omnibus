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
package space.arim.universal.util.function;

/**
 * A thread safe pair with immutable components
 * 
 * @author A248
 *
 * @param <M> the type of the first value
 * @param <N> the type of the second value
 */
public class Pair<M, N> {

	private final M firstValue;
	private final N secondValue;
	
	public Pair(M firstValue, N secondValue) {
		this.firstValue = firstValue;
		this.secondValue = secondValue;
	}
	
	/**
	 * Retrieves the first object in the pair
	 * 
	 * @return the first object
	 */
	public M firstValue() {
		return firstValue;
	}
	
	/**
	 * Retrieves the second object in the pair
	 * 
	 * @return the second object
	 */
	public N secondValue() {
		return secondValue;
	}
	
	/**
	 * Flips the ordering of the pair.
	 * 
	 * @return a new Pair with the first and second values swapped.
	 */
	public Pair<N, M> swapped() {
		return new Pair<N, M>(secondValue, firstValue);
	}
	
}
