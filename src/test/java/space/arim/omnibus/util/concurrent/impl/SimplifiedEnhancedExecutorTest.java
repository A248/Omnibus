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
package space.arim.omnibus.util.concurrent.impl;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import space.arim.omnibus.util.concurrent.DelayCalculator;
import space.arim.omnibus.util.concurrent.DelayCalculators;
import space.arim.omnibus.util.concurrent.EnhancedExecutor;
import space.arim.omnibus.util.concurrent.ScheduledTask;

public class SimplifiedEnhancedExecutorTest {
	
	private EnhancedExecutor executor;
	
	@BeforeEach
	public void setup() {
		executor = new BasicThreadedEnhancedExecutor();
	}
	
	private static class BasicThreadedEnhancedExecutor extends SimplifiedEnhancedExecutor {
		
		@Override
		public void execute(Runnable command) {
			Objects.requireNonNull(command, "command");
			new Thread(command).start();
		}

	}
	
	@Test
	public void testNegativeDelaysNeverRun() {
		Runnable failureRunnable = () -> fail("This runnable should not execute");

		ScheduledTask delayedTask = executor.scheduleOnce(failureRunnable, Duration.ofSeconds(-1L));
		assertFalse(delayedTask.isRepeating(), "Not a repeating task");
		assertTrue(delayedTask.isCancelled(),
				"A delayed task with a negative initial delay should already be cancelled");

		ScheduledTask repeatingTask = executor.scheduleRepeating(failureRunnable, Duration.ofSeconds(-1L),
				DelayCalculators.fixedDelay());
		assertTrue(repeatingTask.isRepeating(), "Is a repeating task");
		assertTrue(repeatingTask.isCancelled(),
				"A repeating task with a negative initial delay should already be cancelled");
	}
	
	@Test
	public void testDelayedExecution() {
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		Boolean value = Boolean.TRUE;
		ScheduledTask task = executor.scheduleOnce(() -> future.complete(value), Duration.ofMillis(100L));

		assertFalse(task.isCancelled(), "Task should not already be cancelled");
		assertFalse(task.isRepeating(), "Not a repeating task");
		assertEquals(value, future.orTimeout(1L, TimeUnit.SECONDS).join());
	}
	
	@Test
	public void testRepeatedExecution() {
		try {
			testRepeatedExecution1();
			testRepeatedExecution2();
		} catch (CompletionException ex) {
			fail(ex);
		}
	}
	
	private void testRepeatedExecution1() {
		AtomicInteger counter = new AtomicInteger();
		Awaiter awaiter = new Awaiter();

		ScheduledTask repeatingTask = executor.scheduleRepeating(() -> {
			counter.incrementAndGet();
		}, Duration.ofMillis(100L), new DelayCalculator() {

			private int amount = 0;
			
			@Override
			public long calculateNextDelay(long previousDelay, long ignoredExecutionTime) {
				amount++;
				if (amount == 5) {
					awaiter.complete();
					return -1;
				}
				if (amount > 5) {
					fail("Delay calculator cancelled but task lives on");
				}
				return previousDelay;
			}
		});
		assertRepeatingTask(counter, awaiter, repeatingTask);
	}
	
	// Slightly modified version using similar functionality
	private void testRepeatedExecution2() {
		AtomicInteger counter = new AtomicInteger();
		Awaiter awaiter = new Awaiter();

		ScheduledTask repeatingTask = executor.scheduleRepeating((task) -> {
			int count = counter.incrementAndGet();
			if (count == 5) {
				task.cancel();
				awaiter.complete();
				return;
			}
			if (count > 5) {
				fail("Consumer cancelled itself but task lives on");
			}
		}, Duration.ofMillis(100L), DelayCalculators.fixedDelay());

		assertRepeatingTask(counter, awaiter, repeatingTask);
	}
	
	private static void assertRepeatingTask(AtomicInteger counter, Awaiter awaiter, ScheduledTask repeatingTask) {
		assertTrue(repeatingTask.isRepeating(), "Is a repeating task");
		assertFalse(repeatingTask.isCancelled(), "Cannot be cancelled yet. Was just scheduled");
		assertFalse(repeatingTask.isDone(), "Only way for repeating task to be done is to be cancelled");
		awaiter.await(Duration.ofSeconds(1L));

		assertTrue(repeatingTask.isCancelled(), "Must be cancelled by now");
		assertTrue(repeatingTask.isDone(), "Cancelled is the same as done");
		assertEquals(5, counter.get(), "Counter must have been incremented five times by now");
	}
	
}
