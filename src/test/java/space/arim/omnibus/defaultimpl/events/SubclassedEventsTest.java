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

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import space.arim.omnibus.events.ListenerPriorities;

public class SubclassedEventsTest extends DefaultEventsTestingBase {
	
	@Test
	public void testSubclassedEventsListenedTo() {
		events().registerListener(TestEventWithInteger.class, ListenerPriorities.LOW, (te) -> te.someValue += 15);

		ThreadLocalRandom random = ThreadLocalRandom.current();
		int beginValue = random.nextInt();
		TestEventWithInteger te = new TestEventWithIntegerAndBoolean(beginValue, random.nextBoolean());
		events().fireEvent(te);
		assertEquals(beginValue + 15, te.someValue);
	}

	@Test
	public void testSubclassedPriorityMaintained() {
		events().registerListener(TestEventWithIntegerAndBoolean.class, ListenerPriorities.LOWEST,
				(te) -> te.someValue += 1);
		events().registerListener(TestEventWithInteger.class, ListenerPriorities.LOWER,
				(te) -> te.someValue *= 10);
		events().registerListener(TestEventWithIntegerAndBoolean.class, ListenerPriorities.NORMAL,
				(te) -> te.someValue -= 3);

		int startValue = ThreadLocalRandom.current().nextInt();
		TestEventWithInteger te = new TestEventWithIntegerAndBoolean(startValue, false);
		events().fireEvent(te);
		assertEquals(((startValue + 1) * 10) - 3, te.someValue);
	}
	
}
