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

package space.arim.omnibus.eventbususage.test;

import org.junit.jupiter.api.Test;
import space.arim.omnibus.DefaultOmnibus;
import space.arim.omnibus.eventbususage.ExampleEvent;
import space.arim.omnibus.eventbususage.ExampleEventListenerExported;
import space.arim.omnibus.eventbususage.UnexportedEventListenerExported;
import space.arim.omnibus.eventbususage.impl.ExampleEventImpl;
import space.arim.omnibus.eventbususage.unexported.ExampleEventListenerUnexported;
import space.arim.omnibus.eventbususage.unexported.UnexportedEvent;
import space.arim.omnibus.eventbususage.unexported.UnexportedEventListenerUnexported;
import space.arim.omnibus.events.EventBus;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventBusUsageTest {

	private final EventBus eventBus = new DefaultOmnibus().getEventBus();

	private void registerDummyListener(Class<? extends ExampleEvent> evtClass) {
		eventBus.registerListener(evtClass, (byte) 0, new DummyEventConsumer());
	}

	private void assertSingleListener(Supplier<? extends ExampleEvent> evtCreator) {
		ExampleEvent evt = evtCreator.get();
		eventBus.fireEvent(evt);
		assertEquals(1, evt.listenedCount());
	}

	@Test
	public void exampleEventListenerExported() {
		var listener = new ExampleEventListenerExported();
		eventBus.registerListeningMethods(listener);

		assertSingleListener(ExampleEventImpl::new);
	}

	@Test
	public void unexportedEventListenerExported() {
		var listener = new UnexportedEventListenerExported();
		assertThrows(IllegalArgumentException.class, () -> eventBus.registerListeningMethods(listener));

		registerDummyListener(UnexportedEvent.class);

		assertSingleListener(UnexportedEvent::new);
	}

	@Test
	public void exampleEventListenerUnexported() {
		var listener = new ExampleEventListenerUnexported();
		assertThrows(IllegalArgumentException.class, () -> eventBus.registerListeningMethods(listener));

		registerDummyListener(ExampleEvent.class);

		assertSingleListener(ExampleEventImpl::new);
	}

	@Test
	public void unexportedEventListenerUnexported() {
		var listener = new UnexportedEventListenerUnexported();
		assertThrows(IllegalArgumentException.class, () -> eventBus.registerListeningMethods(listener));

		registerDummyListener(UnexportedEvent.class);

		assertSingleListener(UnexportedEvent::new);
	}
}
