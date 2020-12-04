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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

public class AsyncEventsTest extends DefaultEventsTestingBase {

	@Test
	public void testAsynchronousResumption() {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		events().registerListener(AsyncTestEventWithInteger.class, (byte) -10, (te) -> {
			te.someValue += 1;
		});
		events().registerListener(AsyncTestEventWithInteger.class, (byte) -5, (te, controller) -> {
			executor.execute(() -> {
				te.someValue *= 10;
				controller.continueFire();
			});
		});
		events().registerListener(AsyncTestEventWithInteger.class, (byte) 0, (te, controller) -> {
			executor.execute(() -> {
				te.someValue -= 3;
				controller.continueFire();
			});
		});
		events().registerListener(AsyncTestEventWithInteger.class, (byte) 5, (te) -> {
			te.someValue = te.someValue + 15;
		});
		int startValue = ThreadLocalRandom.current().nextInt();
		AsyncTestEventWithInteger te = new AsyncTestEventWithInteger(startValue);
		fireAndWait(te);
		assertEquals(((startValue + 1) * 10) - 3 + 15, te.someValue);

		executor.shutdown();
	}
	
}
