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
package space.arim.universal.util.proxy;

import java.util.Collection;
import java.util.Collections;

/**
 * A {@link ProxiedCollection} which prohibits changes to the underlying, backing collection. <br>
 * <br>
 * Attempts to add or remove elements throw {@link UnsupportedOperationException}. <br>
 * Note that the backing collection MAY change; if so, changes are reflected in this UnmodifiableCollection.
 * 
 * @author A248
 *
 * @param <E> the type of the collection
 */
public class UnmodifiableCollection<E> extends ProxiedCollection<E> {
	
	/**
	 * Creates an UnmodifiableCollection based on a backing collection
	 * 
	 * @param original the original, backing collection
	 */
	public UnmodifiableCollection(Collection<E> original) {
		super(Collections.unmodifiableCollection(original));
	}
	
}
