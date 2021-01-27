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
import space.arim.omnibus.events.Event;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.events.ListenerPriorities;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DefaultEventsExtension.class)
public class ListenerBakingTest {

	@Test
	public void decacheHierarchy(EventBus eventBus) {
		eventBus.registerListener(MainEvent.class, ListenerPriorities.NORMAL, (mainEvent) -> {
			mainEvent.value++;
		});
		eventBus.fireEvent(new MainEvent()); // The event fire causes some caching
		eventBus.registerListener(SuperEvent.class, ListenerPriorities.NORMAL, (superEvent) -> {
			superEvent.value++;
		});

		var event = new MainEvent();
		eventBus.fireEvent(event);
		assertEquals(event.value, 2);
	}

	public class SuperEvent implements Event {

		int value;
	}

	public class MainEvent extends SuperEvent { }
}
