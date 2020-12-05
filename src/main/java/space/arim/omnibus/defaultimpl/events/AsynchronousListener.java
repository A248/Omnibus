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

import java.util.Objects;

import space.arim.omnibus.events.AsynchronousEventConsumer;
import space.arim.omnibus.events.Event;

final class AsynchronousListener<E extends Event> extends Listener<E> {

	private final AsynchronousEventConsumer<? super E> asyncEventConsumer;

	AsynchronousListener(Class<E> eventClass, byte priority, AsynchronousEventConsumer<? super E> asyncEventConsumer) {
		super(eventClass, priority);
		this.asyncEventConsumer = Objects.requireNonNull(asyncEventConsumer, "asyncEventConsumer");
	}
	
	AsynchronousEventConsumer<? super E> getAsyncEventConsumer() {
		return asyncEventConsumer;
	}

}
