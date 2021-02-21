/* 
 * Omnibus
 * Copyright © 2020 Anand Beh
 * 
 * Omnibus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Omnibus. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package space.arim.omnibus.defaultimpl.events;

import space.arim.omnibus.events.RegisteredListener;

abstract class Listener<E> implements RegisteredListener, Comparable<Listener<?>> {

	private final Class<E> eventClass;
	private final byte priority;
	
	Listener(Class<E> eventClass, byte priority) {
		if (eventClass.isArray() || eventClass.isPrimitive() || eventClass.equals(Object.class)) {
			throw new IllegalArgumentException("Event class cannot be an array, a primitive, or Object");
		}
		this.eventClass = eventClass;
		this.priority = priority;
	}

	Class<E> getEventClass() {
		return eventClass;
	}

	byte priority() {
		return priority;
	}

	abstract Object getEventConsumer();

	@Override
	public int compareTo(Listener<?> other) {
		if (other == this) {
			return 0;
		}
		int priorityDiff = priority - other.priority;
		if (priorityDiff == 0) {
			// Break ties with random hash codes. Best effort
			int hashDiff = hashCode() - other.hashCode();
			if (hashDiff == 0) {
				return System.identityHashCode(getEventConsumer()) - System.identityHashCode(other.getEventConsumer());
			}
			return hashDiff;
		}
		return priorityDiff;
	}

}
