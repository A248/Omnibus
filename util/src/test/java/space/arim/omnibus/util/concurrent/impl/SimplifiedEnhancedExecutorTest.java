/* 
 * Omnibus-util
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-util. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.util.concurrent.impl;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import space.arim.omnibus.util.concurrent.DelayCalculator;
import space.arim.omnibus.util.concurrent.DelayCalculators;
import space.arim.omnibus.util.concurrent.EnhancedExecutor;
import space.arim.omnibus.util.concurrent.ScheduledWork;

public class SimplifiedEnhancedExecutorTest {
	
	private EnhancedExecutor executor;
	
	@BeforeEach
	public void setup() {
		executor = new BasicThreadedEnhancedExecutor();
	}
	
	@Test
	public void testNegativeDelaysNeverRun() {
		Runnable failureRunnable = () -> fail("This runnable should not execute");
		assertTrue(executor.scheduleOnce(failureRunnable, Duration.ofSeconds(-1L)).isCancelled(),
				"A task with a negative initial delay should already be cancelled");
		assertTrue(executor.scheduleRepeating(failureRunnable, Duration.ofSeconds(-1L), DelayCalculators.fixedDelay()).isCancelled(),
				"A task with a negative initial delay should already be cancelled");
	}
	
	@Test
	public void testDelayedExecution() {
		Object value = new Object();
		final ScheduledWork<Object> task = executor.scheduleOnce(() -> value, Duration.ofSeconds(1L));
		assertFalse(task.isCancelled(), "Task should not already be cancelled");
		Object result = task.toCompletableFuture().orTimeout(4L, TimeUnit.SECONDS).join();
		assertEquals(value, result);
	}
	
	@Test
	public void testRepeatedExecution() {
		AtomicInteger counter = new AtomicInteger();

		executor.scheduleRepeating(() -> {
			counter.incrementAndGet();
		}, Duration.ofMillis(100L), new DelayCalculator() {

			private int amount = 0;
			
			@Override
			public long calculateNextDelay(long previousDelay, long ignoredExecutionTime) {
				amount++;
				if (amount == 5) {
					return -1;
				}
				if (amount > 5) {
					fail("Delay calculator cancelled but task lives on");
				}
				return previousDelay;
			}
		});
		Integer result = executor.scheduleOnce(() -> counter.get(), Duration.ofMillis(1500L)).toCompletableFuture()
				.orTimeout(8L, TimeUnit.SECONDS).join();
		assertEquals(5, result, "Counter must have been increment five times by now");
	}
	
	// Slightly modified version using similar functionality
	@Test
	public void testRepeatedExecution2() {
		AtomicInteger counter = new AtomicInteger();

		executor.scheduleRepeating((task) -> {
			int count = counter.incrementAndGet();
			if (count == 5) {
				task.cancel();
				return;
			}
			if (count > 5) {
				fail("Consumer cancelled itself but task lives on");
			}
		}, Duration.ofMillis(100L), DelayCalculators.fixedDelay());

		Integer result = executor.scheduleOnce(() -> counter.get(), Duration.ofMillis(1500L)).toCompletableFuture()
				.orTimeout(8L, TimeUnit.SECONDS).join();
		assertEquals(5, result, "Counter must have been increment five times by now");
	}
	
}
