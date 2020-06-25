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

import java.util.function.Consumer;

/**
 * Internal wrapper for dynamic listeners
 * 
 * @author A248
 *
 * @param <E> the type of the event
 */
class DynamicListener<E extends Event> extends ListenerMethod implements Listener {

	final Class<E> clazz;
	private final Consumer<E> listener;
	
	DynamicListener(Class<E> clazz, Consumer<E> listener, byte priority) {
		super(priority, false);
		this.clazz = clazz;
		this.listener = listener;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	void invoke(Object evt) {
		listener.accept((E) evt);
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object object) {
		return this == object;
	}
	
}
