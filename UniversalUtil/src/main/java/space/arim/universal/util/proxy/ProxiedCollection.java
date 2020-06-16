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
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A parent class for collections which simply redirect, a.k.a. <i>proxy</i>, calls to another collection (the backing collection). <br>
 * Such "collections" do not hold any data themselves, but merely refer calls to their backing collections. <br>
 * <br>
 * However, the proxy collection's additional call layer provides fine tuned control over reads and writes to the backing collection. <br>
 * Programmers may extend this class to utilise this enhanced control. <br>
 * <br>
 * Note that a reference is retained to the backing collection. Changes to the backing collection are reflected in proxied collections.
 * 
 * @author A248
 *
 * @param <E> the element type
 */
@SuppressWarnings("unlikely-arg-type")
public abstract class ProxiedCollection<E> extends ProxiedObject<Collection<E>> implements Collection<E> {
	
	/**
	 * Creates a ProxiedCollection based on a backing collection
	 * 
	 * @param original the original, backing collection
	 */
	protected ProxiedCollection(Collection<E> original) {
		super(original);
	}
	
	// Main methods
	@Override
	public int size() {return getOriginal().size();}
	@Override
	public boolean isEmpty() {return getOriginal().isEmpty();}
	@Override
	public boolean contains(Object o) {return getOriginal().contains(o);}
	@Override
	public Iterator<E> iterator() {return getOriginal().iterator();}
	@Override
	public Object[] toArray() {return getOriginal().toArray();}
	@Override
	public <T> T[] toArray(T[] a) {return getOriginal().toArray(a);}
	@Override
	public boolean add(E e) {return getOriginal().add(e);}
	@Override
	public boolean remove(Object o) {return getOriginal().remove(o);}
	@Override
	public boolean containsAll(Collection<?> c) {return getOriginal().containsAll(c);}
	@Override
	public boolean addAll(Collection<? extends E> c) {return getOriginal().addAll(c);}
	@Override
	public boolean removeAll(Collection<?> c) {return getOriginal().removeAll(c);}
	@Override
	public boolean retainAll(Collection<?> c) {return getOriginal().retainAll(c);}
	@Override
	public void clear() {getOriginal().clear();}
	// Default methods
	@Override
	public void forEach(Consumer<? super E> action) {getOriginal().forEach(action);}
	@Override
	public <T> T[] toArray(IntFunction<T[]> generator) {return getOriginal().toArray(generator);}
	@Override
	public boolean removeIf(Predicate<? super E> filter) {return getOriginal().removeIf(filter);}
	@Override
	public boolean equals(Object o) {return getOriginal().equals(o);}
	@Override
	public int hashCode() {return getOriginal().hashCode();}
	@Override
	public Spliterator<E> spliterator() {return getOriginal().spliterator();}
	@Override
	public Stream<E> stream() {return getOriginal().stream();}
	@Override
	public Stream<E> parallelStream() {return getOriginal().parallelStream();}
	
}
