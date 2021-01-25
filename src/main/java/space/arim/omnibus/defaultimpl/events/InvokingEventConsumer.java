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

import java.lang.invoke.MethodHandle;

import space.arim.omnibus.events.Event;
import space.arim.omnibus.events.EventConsumer;

class InvokingEventConsumer<E extends Event> implements EventConsumer<E> {

	private final MethodHandle methodHandle;

	InvokingEventConsumer(Object listener, MethodHandle methodHandle) {
		this.methodHandle = methodHandle.bindTo(listener);
	}

	@Override
	public void accept(E event) {
		try {
			methodHandle.invoke(event);

		} catch (Error | RuntimeException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String toString() {
		return "InvokingEventConsumer{" +
				"methodHandle=" + methodHandle +
				'}';
	}
}
