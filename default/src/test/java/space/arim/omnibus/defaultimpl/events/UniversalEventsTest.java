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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.EventPriority;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.events.Listener;

public class UniversalEventsTest {
	
	private EventBus events;
	
	@BeforeEach
	public void setup() {
		events = new TestableEvents();
	}
	
	@Test
	public void testModifyEventValue() {
		events.registerListener(new IntOperatorTestListener());

		callTestEventAssuming1Listener(events);
	}
	
	@Test
	public void testModifyEventValueDynamic() {
		events.registerListener(TestEventWithInteger.class, EventPriority.LOW, (te) -> {
			te.someValue = IntOperatorTestListener.OPERATOR.applyAsInt(te.someValue);
		});

		callTestEventAssuming1Listener(events);
	}
	
	private static void callTestEventAssuming1Listener(EventBus events) {
		int beginValue = ThreadLocalRandom.current().nextInt();
		callTestEventAssuming1Listener(events, new TestEventWithInteger(beginValue));
	}
	
	private static void callTestEventAssuming1Listener(EventBus events, TestEventWithInteger te) {
		int beginValue = te.someValue;
		events.fireEvent(te);
		int result = te.someValue;
		int expected = IntOperatorTestListener.OPERATOR.applyAsInt(beginValue);
		assertEquals(expected, result);
	}
	
	@Test
	public void testMaintainOrder() {
		events.registerListener(new IncrementingTestListener()); // NORMAL priority
		events.registerListener(new DoublingIncrementingTestListener()); // HIGH priority
		TestEventWithInteger te = new TestEventWithInteger(0);
		events.fireEvent(te);
		assertEquals(2, te.someValue); // (0 + 1) * 2 = 2
	}
	
	@Test
	public void testMaintainOrderDynamic() {
		events.registerListener(TestEventWithInteger.class, EventPriority.NORMAL, (te) -> {
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

		ThreadLocalRandom r = ThreadLocalRandom.current();
		callTestEventAssuming1Listener(events, new TestEventWithIntegerAndBoolean(r.nextInt(), r.nextBoolean()));
	}
	
	@Test
	public void testSubclassedEventsDynamic() {
		events.registerListener(TestEventWithInteger.class, EventPriority.LOW, (te) -> {
			te.someValue = IntOperatorTestListener.OPERATOR.applyAsInt(te.someValue);
		});

		ThreadLocalRandom r = ThreadLocalRandom.current();
		callTestEventAssuming1Listener(events, new TestEventWithIntegerAndBoolean(r.nextInt(), r.nextBoolean()));
	}
	
	@Test
	public void testIdenticalPriorities() {
		events.registerListener(new IncrementingTestListener());
		events.registerListener(new IncrementingTestListener());
		TestEventWithInteger te = new TestEventWithInteger(1);
		events.fireEvent(te);
		assertEquals(3, te.someValue); // 1 + 1 + 1 = 3
	}
	
	@Test
	public void testIdenticalDynamicConsumers() {
		EventConsumer<TestEventWithInteger> consumer = (te) -> {
			te.someValue = te.someValue + 1;
		};
		events.registerListener(TestEventWithInteger.class, EventPriority.NORMAL, consumer);
		events.registerListener(TestEventWithInteger.class, EventPriority.NORMAL, consumer);
		TestEventWithInteger te = new TestEventWithInteger(1);
		events.fireEvent(te);
		assertEquals(3, te.someValue); // 1 + 1 + 1 = 3
	}
	
	@Test
	public void testDuplicateRegister() {
		Listener listener = new IntOperatorTestListener();
		events.registerListener(listener);
		events.registerListener(listener); // should be no-op

		callTestEventAssuming1Listener(events);
	}
	
	@Test
	public void testDuplicateRegisterDynamic() {
		Listener listener = events.registerListener(TestEventWithInteger.class, EventPriority.NORMAL, (te) -> {
			te.someValue = IntOperatorTestListener.OPERATOR.applyAsInt(te.someValue);
		});
		events.registerListener(listener); // should be no-op

		callTestEventAssuming1Listener(events);
	}
	
	private static void callTestEventAssumingNoListeners(EventBus events) {
		int startValue = ThreadLocalRandom.current().nextInt();
		TestEventWithInteger te = new TestEventWithInteger(startValue);
		events.fireEvent(te);
		assertEquals(startValue, te.someValue); // no change
	}
	
	@Test
	public void testAlreadyUnregister() {
		Listener listener = new IntOperatorTestListener();
		events.unregisterListener(listener); // should be no-op
		events.registerListener(listener);
		events.unregisterListener(listener);
		events.unregisterListener(listener); // should be no-op
		callTestEventAssumingNoListeners(events);
	}
	
	@Test
	public void testAlreadyUnregisterDynamic() {
		Listener listener = events.registerListener(TestEventWithInteger.class, EventPriority.NORMAL, (te) -> {
			te.someValue = IntOperatorTestListener.OPERATOR.applyAsInt(te.someValue);
		});
		events.unregisterListener(listener);
		events.unregisterListener(listener); // should be no-op
		callTestEventAssumingNoListeners(events);
	}
	
	@Test
	public void testNonInheritableAnnotatedListeners() {
		events.registerListener(new SubclassedIntOperatorTestListener());
		callTestEventAssumingNoListeners(events);
	}
	
}
