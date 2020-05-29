/* 
 * UniversalEvents
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * UniversalEvents is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalEvents is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalEvents. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.events;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class UniversalEventsTest {
	
	@RepeatedTest(20)
	public void shouldNotCompletelyBreak() {
		assertNotNull(UniversalEvents.get());
		assertNotNull(UniversalEvents.getByClass(UniversalEventsTest.class));
		assertNotNull(UniversalEvents.threadLocal().get());
	}
	
	@Test
	public void shouldModifyEventValue() {
		Events events = UniversalEvents.get();
		assertNotNull(events);
		events.registerListener(new IntOperatorTestListener());

		callTestEventAndCheck(events);
	}
	
	@Test
	public void shouldModifyEventValueDynamic() {
		Events events = UniversalEvents.getByClass(UniversalEventsTest.class);
		events.registerListener(TestEventWithInteger.class, EventPriority.LOW, (te) -> {
			te.someValue = IntOperatorTestListener.OPERATOR.applyAsInt(te.someValue);
		});

		callTestEventAndCheck(events);
	}
	
	private void callTestEventAndCheck(Events events) {
		int start = ThreadLocalRandom.current().nextInt();
		TestEventWithInteger te = new TestEventWithInteger(start);
		events.fireEvent(te);
		int result = te.someValue;
		int expected = IntOperatorTestListener.OPERATOR.applyAsInt(start);
		assertEquals(result, expected);
	}
	
	@Test
	public void shouldMaintainProperOrder() {
		Events events = UniversalEvents.threadLocal().get();
		events.registerListener(TestEventWithInteger.class, EventPriority.LOW, (te) -> {
			te.someValue = te.someValue + 1;
		});
		events.registerListener(TestEventWithInteger.class, EventPriority.HIGH, (te) -> {
			te.someValue = te.someValue * 2;
		});
		TestEventWithInteger te = new TestEventWithInteger(0);
		events.fireEvent(te);
		assertEquals(te.someValue, 2); // (0 + 1) * 2 = 2
	}
	
	@Test
	public void shouldProperlyUnregister() {
		StringOperatorTestListener sotl = new StringOperatorTestListener();
		Events events = UniversalEvents.get();
		String initial = "spacey spaces";
		String result = StringOperatorTestListener.OPERATOR.apply(initial);

		events.registerListener(sotl);
		TestEventWithString tews1 = new TestEventWithString(initial);
		events.fireEvent(tews1);
		assertEquals(tews1.str, result);

		events.unregisterListener(sotl);
		TestEventWithString tews2 = new TestEventWithString(initial);
		events.fireEvent(tews2);
		assertEquals(tews2.str, initial);
	}
	
	@Test
	public void shouldProperlyUnregisterDynamic() {
		Events events = UniversalEvents.getByClass(UniversalEventsTest.class);
		String initial = "spacey spaces";
		String result = StringOperatorTestListener.OPERATOR.apply(initial);

		Listener listener = events.registerListener(TestEventWithString.class, EventPriority.HIGHEST, (evt) -> {
			evt.str = StringOperatorTestListener.OPERATOR.apply(evt.str);
		});
		TestEventWithString tews1 = new TestEventWithString(initial);
		events.fireEvent(tews1);
		assertEquals(tews1.str, result);

		events.unregisterListener(listener);
		TestEventWithString tews2 = new TestEventWithString(initial);
		events.fireEvent(tews2);
		assertEquals(tews2.str, initial);
	}
	
}
