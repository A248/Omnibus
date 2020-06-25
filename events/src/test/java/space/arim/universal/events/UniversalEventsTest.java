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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UniversalEventsTest {
	
	private Events events;
	
	@BeforeEach
	public void setup() {
		events = new UniversalEvents();
	}
	
	@Test
	public void testModifyEventValue() {
		events.registerListener(new IntOperatorTestListener());

		callTestEventAndCheck(events);
	}
	
	@Test
	public void testModifyEventValueDynamic() {
		events.registerListener(TestEventWithInteger.class, EventPriority.LOW, (te) -> {
			te.someValue = IntOperatorTestListener.OPERATOR.applyAsInt(te.someValue);
		});

		callTestEventAndCheck(events);
	}
	
	private void callTestEventAndCheck(Events events) {
		int beginValue = ThreadLocalRandom.current().nextInt();
		callTestEventAndCheck(events, new TestEventWithInteger(beginValue));
	}
	
	private void callTestEventAndCheck(Events events, TestEventWithInteger te) {
		int beginValue = te.someValue;
		events.fireEvent(te);
		int result = te.someValue;
		int expected = IntOperatorTestListener.OPERATOR.applyAsInt(beginValue);
		assertEquals(expected, result);
	}
	
	@Test
	public void testMaintainOrder() {
		events.registerListener(TestEventWithInteger.class, EventPriority.LOW, (te) -> {
			te.someValue = te.someValue + 1;
		});
		events.registerListener(TestEventWithInteger.class, EventPriority.HIGH, (te) -> {
			te.someValue = te.someValue * 2;
		});
		TestEventWithInteger te = new TestEventWithInteger(0);
		events.fireEvent(te);
		assertEquals(2, te.someValue); // (0 + 1) * 2 = 2
	}
	
	@Test
	public void testUnregister() {
		StringOperatorTestListener sotl = new StringOperatorTestListener();
		String initial = "spacey spaces";
		String result = StringOperatorTestListener.OPERATOR.apply(initial);

		events.registerListener(sotl);
		TestEventWithString tews1 = new TestEventWithString(initial);
		events.fireEvent(tews1);
		assertEquals(result, tews1.str);

		events.unregisterListener(sotl);
		TestEventWithString tews2 = new TestEventWithString(initial);
		events.fireEvent(tews2);
		assertEquals(initial, tews2.str);
	}
	
	@Test
	public void testUnregisterDynamic() {
		String initial = "spacey spaces";
		String result = StringOperatorTestListener.OPERATOR.apply(initial);

		Listener listener = events.registerListener(TestEventWithString.class, EventPriority.HIGHEST, (evt) -> {
			evt.str = StringOperatorTestListener.OPERATOR.apply(evt.str);
		});
		TestEventWithString tews1 = new TestEventWithString(initial);
		events.fireEvent(tews1);
		assertEquals(result, tews1.str);

		events.unregisterListener(listener);
		TestEventWithString tews2 = new TestEventWithString(initial);
		events.fireEvent(tews2);
		assertEquals(initial, tews2.str);
	}
	
	@Test
	public void testSubclassedEvents() {
		events.registerListener(new IntOperatorTestListener());

		callTestEventAndCheck(events, new TestEventWithIntegerAndBoolean(ThreadLocalRandom.current().nextInt(), false));
	}
	
	@Test
	public void testSubclassedEventsDynamic() {
		events.registerListener(TestEventWithInteger.class, EventPriority.LOW, (te) -> {
			te.someValue = IntOperatorTestListener.OPERATOR.applyAsInt(te.someValue);
		});

		callTestEventAndCheck(events, new TestEventWithIntegerAndBoolean(ThreadLocalRandom.current().nextInt(), false));
	}
	
	@Test
	public void testIdenticalPriorities() {
		events.registerListener(TestEventWithInteger.class, EventPriority.NORMAL, (te) -> {
			te.someValue = te.someValue + 1;
		});
		events.registerListener(TestEventWithInteger.class, EventPriority.NORMAL, (te) -> {
			te.someValue = te.someValue + 1;
		});
		TestEventWithInteger te = new TestEventWithInteger(1);
		events.fireEvent(te);
		assertEquals(3, te.someValue);
	}
	
}
