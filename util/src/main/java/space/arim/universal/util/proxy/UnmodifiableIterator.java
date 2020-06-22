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

import java.util.Iterator;

/**
 * A {@link ProxiedIterator} which does nothing other than throw <code>UnsupportedOperationException</code> on calls to {@link #remove()}.
 * 
 * @author A248
 *
 * @param <E> the iterator type
 */
public class UnmodifiableIterator<E> extends ProxiedIterator<E> {
	
	/**
	 * Creates an UnmodifiableIterator based on a backing iterator
	 * 
	 * @param original the original, backing iterator
	 */
	public UnmodifiableIterator(Iterator<E> original) {
		super(original);
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
