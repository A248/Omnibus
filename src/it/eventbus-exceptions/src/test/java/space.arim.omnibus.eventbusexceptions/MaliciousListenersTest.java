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

package space.arim.omnibus.eventbusexceptions;

import org.junit.jupiter.api.Test;
import space.arim.omnibus.DefaultOmnibus;
import space.arim.omnibus.events.EventBus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static space.arim.omnibus.events.ListenerPriorities.LOWEST;

public class MaliciousListenersTest {

	private final EventBus eventBus = new DefaultOmnibus().getEventBus();

	private int fireAndGetCount() {
		SomeEvent event = new SomeEvent(0);
		ToggleableLoggerFinder loggerFinder = ToggleableLoggerFinder.getLoggerFinder();
		CompletableFuture<SomeEvent> eventFuture = loggerFinder.runSilently(() -> {
			return eventBus.fireAsyncEvent(event);
		});
		eventFuture.orTimeout(1L, TimeUnit.SECONDS).join();
		return event.getCount();
	}

	@Test
	public void attemptDoubleContinueTwiceCalled() {
		eventBus.registerListener(SomeEvent.class, LOWEST, (event, controller) -> {
			controller.continueFire();
			controller.continueFire();
		});
		eventBus.registerListener(SomeEvent.class, LOWEST, SomeEvent::increment);
		assertEquals(1, fireAndGetCount());
	}

	@Test
	public void attemptDoubleContinueException() {
		eventBus.registerListener(SomeEvent.class, LOWEST, (event, controller) -> {
			controller.continueFire();
			throw new RuntimeException();
		});
		eventBus.registerListener(SomeEvent.class, LOWEST, SomeEvent::increment);
		assertEquals(1, fireAndGetCount());
	}
}
