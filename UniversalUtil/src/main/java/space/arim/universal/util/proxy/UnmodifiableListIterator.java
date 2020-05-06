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

import java.util.ListIterator;

/**
 * A {@link UnmodifiableIterator} which implements {@link ListIterator}, throwing
 * <code>UnsupportedOperationException</code> on mutating methods.
 * 
 * @author A248
 *
 * @param <E> the iterator type
 */
public class UnmodifiableListIterator<E> extends UnmodifiableIterator<E> implements ListIterator<E> {

	/**
	 * Creates an UnmodifiableListIterator based on a backing list iterator
	 * 
	 * @param original the original, backing iterator
	 */
	public UnmodifiableListIterator(ListIterator<E> original) {
		super(original);
	}
	
	@Override
	protected ListIterator<E> getOriginal() {
		return (ListIterator<E>) super.getOriginal();
	}
	
	@Override
	public boolean hasPrevious() {
		return getOriginal().hasPrevious();
	}
	
	@Override
	public E previous() {
		return getOriginal().previous();
	}
	
	@Override
	public int nextIndex() {
		return getOriginal().nextIndex();
	}
	
	@Override
	public int previousIndex() {
		return getOriginal().previousIndex();
	}
	
	@Override
	public void set(E e) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void add(E e) {
		throw new UnsupportedOperationException();
	}
	
}
