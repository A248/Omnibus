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
	public void shouldModifyEventValue() {
		events.registerListener(new IntOperatorTestListener());

		callTestEventAndCheck(events);
	}
	
	@Test
	public void shouldModifyEventValueDynamic() {
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
	public void shouldMaintainProperOrder() {
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
	
	@Test
	public void shouldSupportSubclassedEvents() {
		events.registerListener(new IntOperatorTestListener());

		callTestEventAndCheck(events, new TestEventWithIntegerAndBoolean(ThreadLocalRandom.current().nextInt(), false));
	}
	
	@Test
	public void shouldSupportSubclassedEventsDynamic() {
		events.registerListener(TestEventWithInteger.class, EventPriority.LOW, (te) -> {
			te.someValue = IntOperatorTestListener.OPERATOR.applyAsInt(te.someValue);
		});

		callTestEventAndCheck(events, new TestEventWithIntegerAndBoolean(ThreadLocalRandom.current().nextInt(), false));
	}
	
}
