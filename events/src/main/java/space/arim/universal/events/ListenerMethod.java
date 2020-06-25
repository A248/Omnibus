/* 
 * UniversalEvents, a common server event-handling api
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * UniversalEvents is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalEvents is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalEvents. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.events;

/**
 * General internal listener
 * 
 * @author A248
 *
 */
abstract class ListenerMethod implements Comparable<ListenerMethod> {

	final byte priority;
	final boolean ignoreCancelled;
	
	ListenerMethod(byte priority, boolean ignoreCancelled) {
		this.priority = priority;
		this.ignoreCancelled = ignoreCancelled;
	}
	
	abstract void invoke(Object object) throws Throwable;
	
	/**
	 * Compares 2 listener methods.
	 * 
	 */
	@Override
	public int compareTo(ListenerMethod o) {
		return priority - o.priority;
	}
	
	@Override
	public abstract int hashCode();
	
	/**
	 * Used to determine whether there are duplicate listeners.
	 * This method may not necessarily evaluate strict equality.
	 * 
	 */
	@Override
	public abstract boolean equals(Object object);
	
}
