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

import java.util.function.Consumer;
import space.arim.omnibus.util.concurrent.DelayCalculator;
import space.arim.omnibus.util.concurrent.ScheduledTask;

class RepeatingScheduledTaskImpl extends RunnableScheduledTask {

	private final SimplifiedEnhancedExecutor executor;
	private final Consumer<? super ScheduledTask> command;
	private final DelayCalculator calculator;

	private volatile State state;

	RepeatingScheduledTaskImpl(SimplifiedEnhancedExecutor executor, Consumer<? super ScheduledTask> command,
			DelayCalculator calculator) {
		this.executor = executor;
		this.command = command;
		this.calculator = calculator;
	}

	private static class State {

		final long delay;
		final long runTime;

		State(long delay, long currentTime) {
			this.delay = delay;
			this.runTime = currentTime + delay;
		}
	}

	void update(long nextDelay, long currentTime) {
		state = new State(nextDelay, currentTime);
	}

	@Override
	long getRunTime() {
		return state.runTime;
	}

	@Override
	public void run() {
		if (isCancelled()) {
			return;
		}
		long startTime = (calculator.requiresExecutionTime()) ? System.nanoTime() : -1L;
		executeAndReschedule(startTime);
	}

	private void executeAndReschedule(final long startTime) {
		try {
			command.accept(this);
		} finally {
			/*
			 * Reschedule unless cancelled
			 */
			if (isCancelled()) {
				return;
			}
			reschedule(startTime);
		}
	}

	private void reschedule(final long startTime) {
		final long currentTime = System.nanoTime();
		long executionTime = (calculator.requiresExecutionTime()) ? currentTime - startTime : -1L;
		long nextDelay = calculator.calculateNextDelay(state.delay, executionTime);
		if (nextDelay < 0L) {
			// Cancelled by delay calculator
			cancel();
			return;
		}
		update(nextDelay, currentTime);
		if (nextDelay == 0L) {
			executeAndReschedule(currentTime);
			return;
		}
		if (isCancelled()) { // Recheck cancellation status
			return;
		}
		executor.publishTask(this, nextDelay);
	}

	@Override
	public boolean isRepeating() {
		return true;
	}

}
