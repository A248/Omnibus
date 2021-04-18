/*
 * Omnibus
 * Copyright © 2021 Anand Beh
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import space.arim.omnibus.events.EventBus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(DefaultEventsExtension.class)
public class DefaultEventsTest {

	@Test
	public void nullEvent(EventBus eventBus) {
		assertThrows(NullPointerException.class, () -> eventBus.fireEvent(null));
	}

	@Test
	public void nullAsyncEvent(EventBus eventBus) {
		assertThrows(NullPointerException.class, () -> eventBus.fireAsyncEvent(null));
		assertThrows(NullPointerException.class, () -> eventBus.fireAsyncEventWithoutFuture(null));
	}

	@Test
	public void asyncEventWrongFire(EventBus eventBus) {
		assertThrows(IllegalArgumentException.class, () -> eventBus.fireEvent(new AsyncTestEventWithInteger(0)));
	}

	@Test
	public void noListeners(EventBus eventBus) {
		var event = new TestEventWithInteger(1);
		eventBus.fireEvent(event);
		assertEquals(1, event.someValue);
	}

	@Test
	public void noAsyncListeners(EventBus eventBus) {
		var event = new AsyncTestEventWithInteger(1);
		eventBus.fireAsyncEvent(event);
		assertEquals(1, event.someValue);
	}

	@Test
	public void noAsyncListenersNoFuture(EventBus eventBus) {
		var event = new AsyncTestEventWithInteger(1);
		eventBus.fireAsyncEventWithoutFuture(event);
		assertEquals(1, event.someValue);
	}

	@Test
	public void emptyListener(EventBus eventBus) {
		eventBus.registerListeningMethods(new Object());
		var event = new TestEventWithInteger(1);
		eventBus.fireEvent(event);
		assertEquals(1, event.someValue);
	}

	@Test
	public void emptyListenerAsyncEvent(EventBus eventBus) {
		eventBus.registerListeningMethods(new Object());
		var event = new AsyncTestEventWithInteger(1);
		eventBus.fireAsyncEvent(event);
		assertEquals(1, event.someValue);
	}
}
