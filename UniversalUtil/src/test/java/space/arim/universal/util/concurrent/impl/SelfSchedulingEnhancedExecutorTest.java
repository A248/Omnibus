/* 
 * UniversalUtil
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * UniversalUtil is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalUtil is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalUtil. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.util.concurrent.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import space.arim.universal.util.concurrent.DelayFunctions;
import space.arim.universal.util.concurrent.EnhancedExecutor;
import space.arim.universal.util.concurrent.SimpleDelayCalculator;
import space.arim.universal.util.concurrent.Task;

public class SelfSchedulingEnhancedExecutorTest {
	
	@Test
	public void testNegativeDelaysNeverRun() {
		EnhancedExecutor executor = new BasicThreadedEnhancedExecutor();
		Runnable failureRunnable = () -> fail("This runnable should not execute");
		Consumer<Task> failureConsumer = (task) -> fail("This consumer should not execute");
		assertTrue(executor.schedule(failureRunnable, -1L, DelayFunctions.fixedDelay(), TimeUnit.SECONDS).isCancelled(),
				"A task with a negative initial delay should already be cancelled");
		assertTrue(executor.schedule(failureRunnable, -1L, DelayFunctions.fixedRate(), TimeUnit.SECONDS).isCancelled(),
				"A task with a negative initial delay should already be cancelled");
		assertTrue(executor.schedule(failureConsumer, -1L, DelayFunctions.fixedDelay(), TimeUnit.SECONDS).isCancelled(),
				"A task with a negative initial delay should already be cancelled");
		assertTrue(executor.schedule(failureConsumer, -1L, DelayFunctions.fixedRate(), TimeUnit.SECONDS).isCancelled(),
				"A task with a negative initial delay should already be cancelled");
	}
	
	@Test
	public void testDelayedExecution() {
		EnhancedExecutor executor = new BasicThreadedEnhancedExecutor();
		CompletableFuture<Object> future = new CompletableFuture<>();
		Object result = new Object();
		Task task = executor.schedule(() -> {
			future.complete(result);
			return;
		}, 1L, TimeUnit.SECONDS);
		assertFalse(task.isCancelled(), "Task should not already be cancelled");
		try {
			assertEquals(result, future.get(5L, TimeUnit.SECONDS));
		} catch (InterruptedException | ExecutionException | TimeoutException ex) {
			fail(ex);
		}
	}
	
	@Test
	public void testRepeatedExecution() {
		EnhancedExecutor executor = new BasicThreadedEnhancedExecutor();

		CompletableFuture<Object> firstFuture = new CompletableFuture<>();
		Object firstResult = new Object();
		AtomicInteger firstCounter = new AtomicInteger();
		executor.schedule(() -> {
			firstCounter.incrementAndGet();
		}, 500L, new SimpleDelayCalculator() {

			private int amount = 0;
			
			@Override
			public long calculateNext(long previousDelay) {
				amount++;
				if (amount == 5) {
					firstFuture.complete(firstResult);
					return -1;
				}
				if (amount > 5) {
					fail("Delay calculator cancelled but task lives on");
				}
				return previousDelay;
			}
			
		}, TimeUnit.MILLISECONDS);
		executor.schedule(() -> {
			assertEquals(5, firstCounter.get(), "Counter must have been increment five times by now");
		}, 5L, TimeUnit.SECONDS);
		assertWithTimeout(firstResult, firstFuture, 8L, TimeUnit.SECONDS);

		// Slightly modified version using similar functionality

		CompletableFuture<Object> secondFuture = new CompletableFuture<>();
		Object secondResult = new Object();
		AtomicInteger secondCounter = new AtomicInteger();
		executor.schedule((task) -> {
			int counter = secondCounter.getAndIncrement();
			if (counter == 6) {
				task.cancel();
				secondFuture.complete(secondResult);
				return;
			}
			if (counter > 6) {
				fail("Consumer cancelled itself but task lives on");
			}
			return;
		}, 500L, DelayFunctions.fixedDelay(), TimeUnit.MILLISECONDS);
		executor.schedule(() -> {
			assertEquals(5, secondCounter.get(), "Counter must have been increment five times by now");
		}, 5L, TimeUnit.SECONDS);
		assertWithTimeout(secondResult, secondFuture, 8L, TimeUnit.SECONDS);
	}
	
	private static <T> void assertWithTimeout(T result, CompletableFuture<T> future, long timeout, TimeUnit unit) {
		try {
			assertEquals(result, future.get(timeout, unit));
		} catch (InterruptedException | ExecutionException | TimeoutException ex) {
			fail(ex);
		}
	}
	
}
