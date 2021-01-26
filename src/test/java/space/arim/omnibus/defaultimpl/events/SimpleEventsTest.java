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

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.ListenerPriorities;
import space.arim.omnibus.events.RegisteredListener;

@ExtendWith(DefaultEventsExtension.class)
public class SimpleEventsTest {
	
	private static final IntUnaryOperator PLUS_15_OPERATOR = (val) -> val + 15;
	
	private static final UnaryOperator<String> UNSPACING_OPERATOR = (str) -> str.replace(" ", "");
	
	@Test
	public void modifyEventValue(EventBus eventBus) {
		eventBus.registerListener(TestEventWithInteger.class, ListenerPriorities.LOW,
				(te) -> te.someValue += 15);

		int beginValue = ThreadLocalRandom.current().nextInt();
		TestEventWithInteger te = new TestEventWithInteger(beginValue);
		eventBus.fireEvent(te);
		assertEquals(beginValue + 15, te.someValue);
	}
	
	@Test
	public void maintainOrder(EventBus eventBus) {
		eventBus.registerListener(TestEventWithInteger.class, ListenerPriorities.NORMAL,
				(te) -> te.someValue = te.someValue + 1);
		eventBus.registerListener(TestEventWithInteger.class, ListenerPriorities.HIGH,
				(te) -> te.someValue = te.someValue * 2);
		TestEventWithInteger te = new TestEventWithInteger(0);
		eventBus.fireEvent(te);
		assertEquals(2, te.someValue, "(0 + 1) * 2 = 2");
	}
	
	@Test
	public void unregister(EventBus eventBus) {
		String initial = "spacey spaces";
		String result = UNSPACING_OPERATOR.apply(initial);

		RegisteredListener registeredListener = eventBus.registerListener(TestEventWithString.class,
				ListenerPriorities.HIGHEST, (evt) -> evt.str = UNSPACING_OPERATOR.apply(evt.str));
		TestEventWithString tews1 = new TestEventWithString(initial);
		eventBus.fireEvent(tews1);
		assertEquals(result, tews1.str);

		eventBus.unregisterListener(registeredListener);
		TestEventWithString tews2 = new TestEventWithString(initial);
		eventBus.fireEvent(tews2);
		assertEquals(initial, tews2.str);
	}
	
	@Test
	public void identicalConsumers(EventBus eventBus) {
		EventConsumer<TestEventWithInteger> consumer = (te) -> te.someValue *= 2;
		eventBus.registerListener(TestEventWithInteger.class, ListenerPriorities.NORMAL, consumer);
		eventBus.registerListener(TestEventWithInteger.class, ListenerPriorities.NORMAL, consumer);
		eventBus.registerListener(TestEventWithInteger.class, ListenerPriorities.NORMAL, consumer);
		TestEventWithInteger te = new TestEventWithInteger(1);
		eventBus.fireEvent(te);
		assertEquals(8, te.someValue, "1 * 2 * 2 * 2 = 8");
	}
	
	@Test
	public void multiUnregister(EventBus eventBus) {
		RegisteredListener listener = eventBus.registerListener(TestEventWithInteger.class, ListenerPriorities.NORMAL, (te) -> {
			te.someValue = PLUS_15_OPERATOR.applyAsInt(te.someValue);
		});
		eventBus.unregisterListener(listener);
		eventBus.unregisterListener(listener); // should be no-op

		int startValue = ThreadLocalRandom.current().nextInt();
		TestEventWithInteger te = new TestEventWithInteger(startValue);
		eventBus.fireEvent(te);
		assertEquals(startValue, te.someValue, "No change should occur");
	}
	
}
