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

import java.util.Set;
import java.util.function.Supplier;

/**
 * A version of {@link DynamicUnmodifiableCollection} which is specifically a set. <br>
 * <br>
 * On every call, the supplier is invoked and used to generate results.
 * 
 * @author A248
 *
 * @param <E> the type of the set
 */
public class DynamicUnmodifiableSet<E> extends DynamicUnmodifiableCollection<E> implements Set<E> {
	
	/**
	 * Creates a DynamicUnmodifiableSet based on a supplier of backing sets.
	 * 
	 * @param originalSupplier the supplier of backing sets
	 */
	public DynamicUnmodifiableSet(Supplier<Set<E>> originalSupplier) {
		super(originalSupplier);
	}
	
	@Override
	protected Set<E> getOriginal() {
		return (Set<E>) super.getOriginal();
	}
	
}
