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

import java.util.Set;

/**
 * A {@link ProxiedCollection}, specifically a <code>Set</code>. <br>
 * If there were any methods specific to <code>Set</code>, ProxiedSet would refer calls back to the backing set. However, there are none.
 * 
 * @author A248
 *
 * @param <E> the element type
 */
public abstract class ProxiedSet<E> extends ProxiedCollection<E> implements Set<E> {

	protected ProxiedSet(Set<E> original) {
		super(original);
	}
	
	/**
	 * Gets the original set upon which this ProxiedList is based.
	 * 
	 * @return the original, backing set
	 */
	@Override
	protected final Set<E> getOriginal() {
		return (Set<E>) super.getOriginal();
	}

}
