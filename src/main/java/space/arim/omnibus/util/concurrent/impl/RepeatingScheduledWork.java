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

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import space.arim.omnibus.util.concurrent.DelayCalculator;
import space.arim.omnibus.util.concurrent.ScheduledWork;

class RepeatingScheduledWork<T> extends RunnableScheduledWork<T> {

	private final SimplifiedEnhancedExecutor executor;
	private final Function<? super ScheduledWork<T>, T> command;
	private final DelayCalculator calculator;

	private volatile State<T> state;
	private volatile boolean cancelled;

	RepeatingScheduledWork(SimplifiedEnhancedExecutor executor, Function<? super ScheduledWork<T>, T> command,
			DelayCalculator calculator) {
		this.executor = executor;
		this.command = command;
		this.calculator = calculator;
	}

	private static class State<T> {

		final long delay;

		final long runTime;

		final CompletableFuture<T> future;

		State(long delay, long currentTime) {
			this.delay = delay;
			this.runTime = currentTime + delay;
			this.future = new CompletableFuture<>();
		}
	}

	void update(long nextDelay, long currentTime) {
		state = new State<>(nextDelay, currentTime);
	}

	@Override
	CompletableFuture<T> getCompletableFuture() {
		return state.future;
	}

	@Override
	long getRunTime() {
		return state.runTime;
	}

	@Override
	public void run() {
		if (cancelled) {
			return;
		}
		executeAndReschedule(System.nanoTime());
	}

	private void executeAndReschedule(final long startTime) {
		State<T> state = this.state;
		final CompletableFuture<T> currentFuture = state.future;
		if (currentFuture.isCancelled()) {
			reschedule(startTime);
			return;
		}
		try {
			currentFuture.complete(command.apply(this));
		} catch (Throwable ex) {
			currentFuture.completeExceptionally(ex);
		} finally {
			/*
			 * Reschedule unless cancelled
			 */
			if (cancelled) {
				return;
			}
			reschedule(startTime);
		}
	}

	private void reschedule(final long startTime) {
		final long currentTime = System.nanoTime();
		long executionTime = currentTime - startTime;
		long nextDelay = calculator.calculateNextDelay(state.delay, executionTime);
		if (nextDelay < 0L) {
			// Cancelled by delay calculator
			cancelled = true;
			return;
		}
		update(nextDelay, currentTime);
		if (nextDelay == 0L) {
			executeAndReschedule(currentTime);
			return;
		}
		if (cancelled) { // Recheck cancellation status
			return;
		}
		executor.publishTask(this, nextDelay);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		cancelled = true;
		return true;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean isDone() {
		// The only way for a repeating task to ever be "done" is for it to be cancelled
		// Otherwise, only the individual future snapshots may be considered "done"
		return cancelled;
	}

	@Override
	public boolean isRepeating() {
		return true;
	}

}
