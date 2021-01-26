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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static space.arim.omnibus.defaultimpl.events.DefaultEventsTesting.fireAndWait;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.events.EventFireController;
import space.arim.omnibus.events.ListenerPriorities;
import space.arim.omnibus.events.ListeningMethod;

@ExtendWith(DefaultEventsExtension.class)
public class ListeningMethodsTest {

	@Test
	public void annotatedListeners(EventBus eventBus) {
		eventBus.registerListeningMethods(new AnnotatedListener());

		int startValue = ThreadLocalRandom.current().nextInt();
		TestEventWithInteger te = new TestEventWithInteger(startValue);
		eventBus.fireEvent(te);
		assertEquals(((startValue + 1) * 10) - 3, te.someValue);
	}

	@Test
	public void unregisterAnnotated(EventBus eventBus) {
		AnnotatedListener listener = new AnnotatedListener();
		eventBus.registerListeningMethods(listener);
		eventBus.unregisterListeningMethods(listener);

		int startValue = ThreadLocalRandom.current().nextInt();
		TestEventWithInteger te = new TestEventWithInteger(startValue);
		eventBus.fireEvent(te);
		assertEquals(startValue, te.someValue, "No change should occur");
	}

	@Test
	public void asyncAnnotated(EventBus eventBus) {
		try (AsyncAnnotatedListener asyncListener = new AsyncAnnotatedListener()) {
			eventBus.registerListeningMethods(asyncListener);

			int startValue = ThreadLocalRandom.current().nextInt();
			AsyncTestEventWithInteger te = new AsyncTestEventWithInteger(startValue);
			fireAndWait(eventBus, te);
			assertEquals(((startValue + 1) * 10) - 3, te.someValue);
		}
	}

	@Test
	public void duplicateRegister(EventBus eventBus) {
		AnnotatedListener listener = new AnnotatedListener();
		eventBus.registerListeningMethods(listener);
		assertThrows(IllegalStateException.class, () -> eventBus.registerListeningMethods(listener));
	}

	@Test
	public void duplicateUnregister(EventBus eventBus) {
		AnnotatedListener listener = new AnnotatedListener();
		eventBus.registerListeningMethods(listener);
		eventBus.unregisterListeningMethods(listener);
		eventBus.unregisterListeningMethods(listener); // No-op
	}

	public static class AnnotatedListener {

		@ListeningMethod(priority = ListenerPriorities.LOW)
		public void plusOne(TestEventWithInteger te) {
			te.someValue += 1;
		}

		@ListeningMethod
		public void timesTen(TestEventWithInteger te) {
			te.someValue *= 10;
		}

		@ListeningMethod(priority = ListenerPriorities.HIGH)
		public void minus3(TestEventWithInteger te) {
			te.someValue -= 3;
		}

	}

	public static class AsyncAnnotatedListener implements AutoCloseable {

		private final ExecutorService executor = Executors.newFixedThreadPool(1);

		@ListeningMethod(priority = ListenerPriorities.LOW)
		public void plusOne(AsyncTestEventWithInteger te, EventFireController controller) {
			executor.execute(() -> {
				te.someValue += 1;
				controller.continueFire();
			});
		}

		@ListeningMethod
		public void timesTen(AsyncTestEventWithInteger te, EventFireController controller) {
			executor.execute(() -> {
				te.someValue *= 10;
				controller.continueFire();
			});
		}

		@ListeningMethod(priority = ListenerPriorities.HIGH)
		public void minus3(AsyncTestEventWithInteger te, EventFireController controller) {
			te.someValue -= 3;
			controller.continueFire();
		}

		@Override
		public void close() {
			executor.shutdown();
		}

	}

}
