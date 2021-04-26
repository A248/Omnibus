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
import org.junit.jupiter.api.function.Executable;
import space.arim.omnibus.events.Event;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.events.EventFireController;
import space.arim.omnibus.events.ListeningMethod;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(DefaultEventsExtension.class)
public class FaultListeningMethodsTest {

	private static void assertFaulty(String containsMessage, Executable executable) {
		try {
			executable.execute();
			fail("Expected IllegalArgumentException, but none thrown");

		} catch (IllegalArgumentException ex) {
			assertTrue(ex.getMessage().contains(containsMessage),
					"Expected " + ex.getMessage() + " to contain " + containsMessage);

		} catch (Throwable ex) {
			fail("Unexpected exception", ex);
		}
	}

	private static class NonPublicListener {
		@ListeningMethod
		public void onEvent(TestEventWithInteger event) {}
	}

	@Test
	public void nonPublicListener(EventBus eventBus) {
		assertFaulty("public",
				() -> eventBus.registerListeningMethods(new NonPublicListener()));
	}

	private static class NonPublicEvent implements Event { }
	public static class PublicListenerToNonPublicEvent {
		@ListeningMethod
		public void onEvent(NonPublicEvent event) {}
	}

	@Test
	public void publicListenerToNonPublicEvent(EventBus eventBus) {
		assertFaulty("public",
				() -> eventBus.registerListeningMethods(new PublicListenerToNonPublicEvent()));
	}

	public static class NonVoidReturnType {
		@ListeningMethod
		public boolean onEvent(TestEventWithInteger event) {
			return true;
		}
	}

	@Test
	public void nonVoidReturnType(EventBus eventBus) {
		assertFaulty("void",
				() -> eventBus.registerListeningMethods(new NonVoidReturnType()));
	}

	public static class StaticMethod {
		@ListeningMethod
		public static void onEvent(TestEventWithInteger event) {}
	}

	@Test
	public void staticMethod(EventBus eventBus) {
		assertFaulty("static",
				() -> eventBus.registerListeningMethods(new StaticMethod()));
	}

	public interface DefaultMethod {
		@ListeningMethod
		default void onEvent(TestEventWithInteger event) {}
	}

	@Test
	public void defaultMethod(EventBus eventBus) {
		assertFaulty("default",
				() -> eventBus.registerListeningMethods(new DefaultMethod() {}));
	}

	public static class NoParameters {
		@ListeningMethod
		public void onEvent() {}
	}

	@Test
	public void noParameters(EventBus eventBus) {
		assertFaulty("no parameters",
				() -> eventBus.registerListeningMethods(new NoParameters()));
	}

	public static class TooManyParameters {
		@ListeningMethod
		public void onEvent(TestEventWithInteger event, String extraParam, int oneFurtherParam) {}
	}

	@Test
	public void tooManyParameters(EventBus eventBus) {
		assertFaulty("too many parameters",
				() -> eventBus.registerListeningMethods(new TooManyParameters()));
	}

	public static class NotAnEvent {
		@ListeningMethod
		public void onEvent(String notAnEvent) {}
	}

	@Test
	public void notAnEvent(EventBus eventBus) {
		assertFaulty("subclass of Event",
				() -> eventBus.registerListeningMethods(new NotAnEvent()));
	}

	public static class EventButNotAsync implements Event {}
	public static class NotAnAsyncEvent {
		@ListeningMethod
		public void onEvent(EventButNotAsync notAnEvent, EventFireController controller) {}
	}

	@Test
	public void notAnAsyncEvent(EventBus eventBus) {
		assertFaulty("subclass of AsyncEvent",
				() -> eventBus.registerListeningMethods(new NotAnAsyncEvent()));
	}

	public static class NotEventFireController {
		@ListeningMethod
		public void onEvent(AsyncTestEventWithInteger event, String notController) {}
	}

	@Test
	public void notEventFireController(EventBus eventBus) {
		assertFaulty("EventFireController",
				() -> eventBus.registerListeningMethods(new NotEventFireController()));
	}

	public static class CheckedExceptions {
		@ListeningMethod
		public void onEvent(TestEventWithInteger event) throws Exception {}
	}

	@Test
	public void checkedExceptions(EventBus eventBus) {
		assertFaulty("checked exception",
				() -> eventBus.registerListeningMethods(new CheckedExceptions()));
	}


}
