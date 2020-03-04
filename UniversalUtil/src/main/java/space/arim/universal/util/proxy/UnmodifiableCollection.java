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
import java.util.Iterator;

/**
 * A {@link ProxiedCollection} which prohibits changes to the underlying, backing collection. <br>
 * <br>
 * Attempts to add or remove elements throw {@link UnsupportedOperationException}. <br>
 * Note that the backing collection MAY change; if so, changes are reflected in this UnmodifiableCollection.
 * 
 * @author A248
 *
 * @param <E>
 */
public class UnmodifiableCollection<E> extends ProxiedCollection<E> {
	
	/**
	 * Creates an UnmodifiableCollection based on a backing collection
	 * 
	 * @param original the original, backing collection
	 */
	public UnmodifiableCollection(Collection<E> original) {
		super(original);
	}
	
	@Override
	public Iterator<E> iterator() {
		return new UnmodifiableIterator<E>(super.iterator());
	}
	
	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	
}
