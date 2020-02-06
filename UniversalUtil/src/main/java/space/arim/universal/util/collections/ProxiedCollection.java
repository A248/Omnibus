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

import java.util.Collection;
import java.util.Iterator;

/**
 * A parent class for collections which simply redirect, a.k.a. <i>proxy</i>, calls to another collection (the backing collection). <br>
 * Such "collections" do not hold any data themselves, but merely refer calls to their backing collections. <br>
 * <br>
 * However, the proxy collection's additional call layer provides fine tuned control over reads and writes to the backing collection. <br>
 * Programmers may extend this class to utilise this enhanced control. {@link DishonestCollection} is a simple example. <br>
 * <br>
 * Note that a reference is retained to the backing collection. Changes to the backing collection are reflected in proxied collections.
 * 
 * @author A248
 *
 * @param <E> the element type
 */
@SuppressWarnings("unlikely-arg-type")
public abstract class ProxiedCollection<E> implements Collection<E> {
	
	private final Collection<E> original;
	
	protected ProxiedCollection(Collection<E> original) {
		this.original = original;
	}
	
	/**
	 * Gets the original collection upon which this ProxiedCollection is based.
	 * 
	 * @return the original, backing collection
	 */
	protected Collection<E> getOriginal() {
		return original;
	}
	
	@Override
	public int size() {return original.size();}
	@Override
	public boolean isEmpty() {return original.isEmpty();}
	@Override
	public boolean contains(Object o) {return original.contains(o);}
	@Override
	public Iterator<E> iterator() {return original.iterator();}
	@Override
	public Object[] toArray() {return original.toArray();}
	@Override
	public <T> T[] toArray(T[] a) {return original.toArray(a);}
	@Override
	public boolean add(E e) {return original.add(e);}
	@Override
	public boolean remove(Object o) {return original.remove(o);}
	@Override
	public boolean containsAll(Collection<?> c) {return original.containsAll(c);}
	@Override
	public boolean addAll(Collection<? extends E> c) {return original.addAll(c);}
	@Override
	public boolean removeAll(Collection<?> c) {return original.removeAll(c);}
	@Override
	public boolean retainAll(Collection<?> c) {return original.retainAll(c);}
	@Override
	public void clear() {original.clear();}
	
}
