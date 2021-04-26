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
import space.arim.omnibus.events.EventBusDriver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static space.arim.omnibus.events.ListenerPriorities.NORMAL;

@ExtendWith(DefaultEventsExtension.class)
public class DefaultEventsDriverTest {

	@Test
	public void listenToForeignEvent(EventBusDriver eventBusDriver) {
		eventBusDriver.registerListener(ForeignEvent.class, NORMAL, (fe) -> {
			fe.value = 5;
		});
		ForeignEvent fe = new ForeignEvent();
		eventBusDriver.fireEvent(fe);
		assertEquals(fe.value, 5);
	}

	public static class ForeignEvent {

		int value = 1;
	}

	@Test
	public void getRegisteredListenerCount(EventBusDriver eventBusDriver) {
		assertEquals(0, eventBusDriver.getRegisteredListenerCount(ForeignEvent.class));
	}

	@Test
	public void debugRegisteredListeners(EventBusDriver eventBusDriver) throws IOException {
		try (PrintStream output = new PrintStream(OutputStream.nullOutputStream())) {
			assertDoesNotThrow(() -> {
				eventBusDriver.registerListener(ForeignEvent.class, NORMAL, (fe) -> {
				});
				eventBusDriver.registerListener(ForeignEvent.class, NORMAL, (fe) -> {
				});
				eventBusDriver.debugRegisteredListeners(ForeignEvent.class, output);
				output.println();
				eventBusDriver.fireEvent(new ForeignEvent());
				eventBusDriver.debugRegisteredListeners(ForeignEvent.class, output);
				output.println();
			});
		}
	}

	@Test
	public void debugEntireDriverState(EventBusDriver eventBusDriver) throws IOException {
		try (PrintStream output = new PrintStream(OutputStream.nullOutputStream())) {
			assertDoesNotThrow(() -> {
				eventBusDriver.debugEntireDriverState(output);
				output.println();
				eventBusDriver.registerListener(ForeignEvent.class, NORMAL, (fe) -> {
				});
				eventBusDriver.registerListener(ForeignEvent.class, NORMAL, (fe) -> {
				});
				eventBusDriver.debugEntireDriverState(output);
				output.println();
				eventBusDriver.fireEvent(new ForeignEvent());
				eventBusDriver.debugEntireDriverState(output);
				output.println();
			});
		}
	}

	@Test
	public void objectEventClass(EventBusDriver eventBusDriver) {
		assertThrows(IllegalArgumentException.class,
				() -> eventBusDriver.registerListener(Object.class, NORMAL, (e) -> {}));
	}

	@Test
	public void arrayEventClass(EventBusDriver eventBusDriver) {
		assertThrows(IllegalArgumentException.class,
				() -> eventBusDriver.registerListener(TestEventWithInteger[].class, NORMAL, (e) -> {}));
	}

	@Test
	public void primitiveEventClass(EventBusDriver eventBusDriver) {
		assertThrows(IllegalArgumentException.class,
				() -> eventBusDriver.registerListener(int.class, NORMAL, (e) -> {}));
	}

}
