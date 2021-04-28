/*
 * Omnibus
 * Copyright © 2021 Anand Beh
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

package space.arim.omnibus.defaultimpl.registry;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FifoEventQueueIT {

	private static final int ITERATIONS = 3000;

	private final FifoEventQueue eventQueue = new FifoEventQueue();
	private final ParallelStresser stresser = new ParallelStresser();

	@RepeatedTest(ITERATIONS)
	public void dispatchEventsUnordered() {
		TestingEventBus eventBus = new TestingEventBus();
		ShellEvent event1 = new ShellEvent();
		ShellEvent event2 = new ShellEvent();
		stresser.runAll(new RunInstruction(() -> {
			eventQueue.offer(event1);
			eventQueue.fireEvents(eventBus);
		}), new RunInstruction(() -> {
			eventQueue.offer(event2);
			eventQueue.fireEvents(eventBus);
		}));
		assertEquals(eventBus.eventsUnordered(), Set.of(event1, event2));
	}

	@RepeatedTest(ITERATIONS)
	public void dispatchEventsInOrder() {
		TestingEventBus eventBus = new TestingEventBus();
		ShellEvent event1 = new ShellEvent();
		ShellEvent event2 = new ShellEvent();
		var run1 = new RunInstruction(
				() -> eventQueue.offer(event1),
				() -> {});
		var run2 = new RunInstruction(() -> {
			eventQueue.offer(event2);
			eventQueue.fireEvents(eventBus);
		});
		stresser.runAll(run1, run2);
		assertEquals(eventBus.events(), List.of(event1, event2));
	}

	@Test
	public void dispatchEventsInOrderWithMultithreadedReentrancy() {
		ShellEvent event1 = new ShellEvent();
		ShellEvent event2 = new ShellEvent();
		TestingEventBus eventBus = new TestingEventBus();
		var run = new RunInstruction(() -> {
			// Thread A begins
			eventQueue.offer(event1);
			eventQueue.fireEvents(eventBus);
		});
		AtomicBoolean called = new AtomicBoolean();
		eventBus.setListener((event) -> {
			if (!called.compareAndSet(false, true)) {
				// Make this listener only work once, avoids infinite multithreaded recursion
				return;
			}
			// Thread A calls the listener which creates a child thread B
			CompletableFuture.runAsync(() -> {
				// Thread B attempts to fire events. No deadlock should occur
				eventQueue.offer(event2);
				eventQueue.fireEvents(eventBus);
			})
					// Thread A awaits B
					.orTimeout(1L, TimeUnit.SECONDS).join();
		});
		stresser.runAll(run);
		assertEquals(eventBus.events(), List.of(event1, event2));
	}

	@Test
	public void dispatchEventsInOrderWithLocks() {
		ShellEvent event1 = new ShellEvent();
		ShellEvent event2 = new ShellEvent();
		ReentrantLock lock = new ReentrantLock();
		TestingEventBus eventBus = new TestingEventBus();
		var run1 = new RunInstruction(
				() -> eventQueue.offer(event1),
				() -> {
					// Thread A begins
					eventQueue.fireEvents(eventBus);
				});
		var run2 = new RunInstruction(
				// Thread B starts out holding the lock
				() -> { lock.lock(); },
				() -> {
					// Thread B attempts to fire events. No deadlock should occur
					eventQueue.offer(event2);
					eventQueue.fireEvents(eventBus);
					// Thread B unlocks, allowing Thread A to continue
					lock.unlock();
				});
		eventBus.setListener((event) -> {
			// Thread A calls the listener which requires the lock Thread B owns
			// No deadlock should occur. Thread B should relinquish its lock
			lock.lock();
			lock.unlock();
		});
		stresser.runAll(run1, run2);
		assertEquals(eventBus.events(), List.of(event1, event2));
	}

}
