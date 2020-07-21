/* 
 * Omnibus-default
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-default is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-default is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-default. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.defaultimpl.events;

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
	 * Compares 2 listener methods by priority
	 * 
	 */
	@Override
	public int compareTo(ListenerMethod o) {
		int priorityDiff = priority - o.priority;
		if (priorityDiff == 0) {
			return hashCode() - o.hashCode();
		}
		return priorityDiff;
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