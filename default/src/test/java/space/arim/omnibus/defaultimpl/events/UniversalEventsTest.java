/* 
 * Omnibus-default
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-default is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-default is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-default. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.defaultimpl.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.ListenerPriorities;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.events.RegisteredListener;

public class UniversalEventsTest {
	
	private EventBus events;
	
	private static final IntUnaryOperator PLUS_15_OPERATOR = (val) -> val + 15;
	
	private static final UnaryOperator<String> UNSPACING_OPERATOR = (str) -> str.replace(" ", "");
	
	@BeforeEach
	public void setup() {
		events = new TestableEvents();
	}
	
	@Test
	public void testModifyEventValue() {
		events.registerListener(TestEventWithInteger.class, ListenerPriorities.LOW,
				(te) -> te.someValue = PLUS_15_OPERATOR.applyAsInt(te.someValue));

		int beginValue = ThreadLocalRandom.current().nextInt();
		callTestEventAssuming1Listener(new TestEventWithInteger(beginValue));
	}
	
	private void callTestEventAssuming1Listener(TestEventWithInteger te) {
		int beginValue = te.someValue;
		events.fireEvent(te);
		int result = te.someValue;
		int expected = PLUS_15_OPERATOR.applyAsInt(beginValue);
		assertEquals(expected, result);
	}
	
	@Test
	public void testMaintainOrder() {
		events.registerListener(TestEventWithInteger.class, ListenerPriorities.NORMAL,
				(te) -> te.someValue = te.someValue + 1);
		events.registerListener(TestEventWithInteger.class, ListenerPriorities.HIGH,
				(te) -> te.someValue = te.someValue * 2);
		TestEventWithInteger te = new TestEventWithInteger(0);
		events.fireEvent(te);
		assertEquals(2, te.someValue); // (0 + 1) * 2 = 2
	}
	
	@Test
	public void testUnregister() {
		String initial = "spacey spaces";
		String result = UNSPACING_OPERATOR.apply(initial);

		RegisteredListener registeredListener = events.registerListener(TestEventWithString.class,
				ListenerPriorities.HIGHEST, (evt) -> evt.str = UNSPACING_OPERATOR.apply(evt.str));
		TestEventWithString tews1 = new TestEventWithString(initial);
		events.fireEvent(tews1);
		assertEquals(result, tews1.str);

		events.unregisterListener(registeredListener);
		TestEventWithString tews2 = new TestEventWithString(initial);
		events.fireEvent(tews2);
		assertEquals(initial, tews2.str);
	}
	
	@Test
	public void testSubclassedEventsListenedTo() {
		events.registerListener(TestEventWithInteger.class, ListenerPriorities.LOW, (te) -> {
			te.someValue = PLUS_15_OPERATOR.applyAsInt(te.someValue);
		});

		ThreadLocalRandom r = ThreadLocalRandom.current();
		callTestEventAssuming1Listener(new TestEventWithIntegerAndBoolean(r.nextInt(), r.nextBoolean()));
	}
	
	@Test
	public void testIdenticalConsumers() {
		EventConsumer<TestEventWithInteger> consumer = (te) -> {
			te.someValue = te.someValue + 1;
		};
		events.registerListener(TestEventWithInteger.class, ListenerPriorities.NORMAL, consumer);
		events.registerListener(TestEventWithInteger.class, ListenerPriorities.NORMAL, consumer);
		TestEventWithInteger te = new TestEventWithInteger(1);
		events.fireEvent(te);
		assertEquals(3, te.someValue); // 1 + 1 + 1 = 3
	}
	
	@Test
	public void testMultiUnregister() {
		RegisteredListener listener = events.registerListener(TestEventWithInteger.class, ListenerPriorities.NORMAL, (te) -> {
			te.someValue = PLUS_15_OPERATOR.applyAsInt(te.someValue);
		});
		events.unregisterListener(listener);
		events.unregisterListener(listener); // should be no-op

		int startValue = ThreadLocalRandom.current().nextInt();
		TestEventWithInteger te = new TestEventWithInteger(startValue);
		events.fireEvent(te);
		assertEquals(startValue, te.someValue); // no change
	}
	
	@Test
	public void testSubclassedPriorityMaintained() {
		events.registerListener(TestEventWithIntegerAndBoolean.class, (byte) -10, (te) -> {
			te.someValue = te.someValue + 1;
		});
		events.registerListener(TestEventWithInteger.class, (byte) -5, (te) -> {
			te.someValue = te.someValue * 10;
		});
		events.registerListener(TestEventWithIntegerAndBoolean.class, (byte) 0, (te) -> {
			te.someValue = te.someValue - 3;
		});
		events.registerListener(TestEventWithIntegerAndBoolean.class, (byte) 5, (te) -> {
			te.someValue = te.someValue + 15;
		});
		events.registerListener(TestEventWithInteger.class, (byte) 10, (te) -> {
			te.someValue = te.someValue * 2;
		});
		int startValue = ThreadLocalRandom.current().nextInt();
		TestEventWithInteger te = new TestEventWithIntegerAndBoolean(startValue, false);
		events.fireEvent(te);
		assertEquals((((startValue + 1) * 10) - 3 + 15) * 2, te.someValue);
	}
	
}
