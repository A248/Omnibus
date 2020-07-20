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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.UnaryOperator;

/**
 * A {@link ProxiedCollection}, specifically a <code>List</code>. <br>
 * Methods specific to <code>List</code> refer calls back to the backing list.
 * 
 * @author A248
 *
 * @param <E> the element type
 */
@SuppressWarnings("unlikely-arg-type")
public abstract class ProxiedList<E> extends ProxiedCollection<E> implements List<E> {
	
	protected ProxiedList(List<E> original) {
		super(original);
	}
	
	/**
	 * Gets the original list upon which this ProxiedList is based.
	 * 
	 * @return the original, backing list
	 */
	@Override
	protected List<E> getOriginal() {
		return (List<E>) super.getOriginal();
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {return getOriginal().addAll(index, c);}
	@Override
	public E get(int index) {return getOriginal().get(index);}
	@Override
	public E set(int index, E element) {return getOriginal().set(index, element);}
	@Override
	public void add(int index, E element) {getOriginal().add(index, element);}
	@Override
	public E remove(int index) {return getOriginal().remove(index);}
	@Override
	public int indexOf(Object o) {return getOriginal().indexOf(o);}
	@Override
	public int lastIndexOf(Object o) {return getOriginal().lastIndexOf(o);}
	@Override
	public ListIterator<E> listIterator() {return getOriginal().listIterator();}
	@Override
	public ListIterator<E> listIterator(int index) {return getOriginal().listIterator(index);}
	@Override
	public List<E> subList(int fromIndex, int toIndex) {return getOriginal().subList(fromIndex, toIndex);}
	@Override
	public void replaceAll(UnaryOperator<E> operator) {getOriginal().replaceAll(operator);}
	@Override
	public void sort(Comparator<? super E> comparator) {getOriginal().sort(comparator);}

}
