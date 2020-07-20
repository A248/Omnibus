/* 
 * Omnibus-util
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-util. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.util.proxy;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * A version of {@link DynamicUnmodifiableCollection} which is specifically a list. <br>
 * <br>
 * On every call, the supplier is invoked and used to generate results.
 * 
 * @author A248
 *
 * @param <E> the type of the list
 */
public class DynamicUnmodifiableList<E> extends ProxiedList<E> {
	
	/**
	 * Creates a DynamicUnmodifiableList based on a supplier of backing lists.
	 * 
	 * @param originalSupplier the supplier of backing lists
	 */
	public DynamicUnmodifiableList(Supplier<? extends List<E>> originalSupplier) {
		super(Collections.unmodifiableList(new ProxiedList<E>(null) {
			@Override
			protected List<E> getOriginal() {
				return originalSupplier.get();
			}
		}));
	}
	
}
