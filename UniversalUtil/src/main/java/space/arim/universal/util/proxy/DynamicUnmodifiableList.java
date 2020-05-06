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
import java.util.List;
import java.util.ListIterator;
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
public class DynamicUnmodifiableList<E> extends DynamicUnmodifiableCollection<E> implements List<E> {

	/**
	 * Creates a DynamicUnmodifiableList based on a supplier of backing lists.
	 * 
	 * @param originalSupplier the supplier of backing lists
	 */
	public DynamicUnmodifiableList(Supplier<? extends List<E>> originalSupplier) {
		super(originalSupplier);
	}
	
	@Override
	protected List<E> getOriginal() {
		return (List<E>) super.getOriginal();
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public E get(int index) {
		return getOriginal().get(index);
	}
	
	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException();
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public int indexOf(Object o) {
		return getOriginal().indexOf(o);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public int lastIndexOf(Object o) {
		return getOriginal().lastIndexOf(o);
	}
	
	@Override
	public ListIterator<E> listIterator() {
		return new UnmodifiableListIterator<E>(getOriginal().listIterator());
	}
	
	@Override
	public ListIterator<E> listIterator(int index) {
		return new UnmodifiableListIterator<E>(getOriginal().listIterator(index));
	}
	
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return new DynamicUnmodifiableList<E>(() -> getOriginal().subList(fromIndex, toIndex));
	}
	
}
