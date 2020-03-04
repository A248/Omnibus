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

import java.util.Collection;
import java.util.function.Supplier;

/**
 * A {@link UnmodifiableCollection} whose original, backing collection is dynamically provided by a {@link Supplier}. <br>
 * <br>
 * On every call, the supplier is invoked and used to generate results.
 * 
 * @author A248
 *
 * @param <E> the type of the collection
 */
public class DynamicUnmodifiableCollection<E> extends UnmodifiableCollection<E> {

	private final Supplier<Collection<E>> originalSupplier;
	
	/**
	 * Creates a DynamicUnmodifiableCollection based on a supplier of backing collections.
	 * 
	 * @param originalSupplier the supplier of backing collections
	 */
	public DynamicUnmodifiableCollection(Supplier<Collection<E>> originalSupplier) {
		super(null);
		this.originalSupplier = originalSupplier;
	}
	
	@Override
	protected Collection<E> getOriginal() {
		return originalSupplier.get();
	}

}
