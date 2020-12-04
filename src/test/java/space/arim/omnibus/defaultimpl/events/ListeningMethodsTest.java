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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import space.arim.omnibus.events.EventFireController;
import space.arim.omnibus.events.ListenerPriorities;
import space.arim.omnibus.events.ListeningMethod;

public class ListeningMethodsTest extends DefaultEventsTestingBase {

	@Test
	public void testAnnotatedListeners() {
		events().registerListeningMethods(new AnnotatedListener());

		int startValue = ThreadLocalRandom.current().nextInt();
		TestEventWithInteger te = new TestEventWithInteger(startValue);
		events().fireEvent(te);
		assertEquals(((startValue + 1) * 10) - 3, te.someValue);
	}

	@Test
	public void testUnregisterAnnotated() {
		AnnotatedListener listener = new AnnotatedListener();
		events().registerListeningMethods(listener);
		events().unregisterListeningMethods(listener);

		int startValue = ThreadLocalRandom.current().nextInt();
		TestEventWithInteger te = new TestEventWithInteger(startValue);
		events().fireEvent(te);
		assertEquals(startValue, te.someValue, "No change should occur");
	}

	@Test
	public void testAsyncAnnotated() {
		try (AsyncAnnotatedListener asyncListener = new AsyncAnnotatedListener()) {
			events().registerListeningMethods(asyncListener);

			int startValue = ThreadLocalRandom.current().nextInt();
			AsyncTestEventWithInteger te = new AsyncTestEventWithInteger(startValue);
			fireAndWait(te);
			assertEquals(((startValue + 1) * 10) - 3, te.someValue);
		}
	}

	@Test
	public void testDuplicateRegister() {
		AnnotatedListener listener = new AnnotatedListener();
		events().registerListeningMethods(listener);
		assertThrows(IllegalStateException.class, () -> events().registerListeningMethods(listener));
	}

	@Test
	public void testDuplicateUnregister() {
		AnnotatedListener listener = new AnnotatedListener();
		events().registerListeningMethods(listener);
		events().unregisterListeningMethods(listener);
		events().unregisterListeningMethods(listener); // No-op
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
