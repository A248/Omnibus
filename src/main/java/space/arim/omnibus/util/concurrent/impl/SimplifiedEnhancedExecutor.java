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
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import space.arim.omnibus.util.concurrent.DelayCalculator;
import space.arim.omnibus.util.concurrent.EnhancedExecutor;
import space.arim.omnibus.util.concurrent.ScheduledTask;

/**
 * Abstract implementation of {@link EnhancedExecutor} which handles its own scheduling and delegates
 * execution to subclasses' {@link #execute(Runnable)} methods. <br>
 * <br>
 * Uses the system wide internal scheduler ({@link CompletableFuture#delayedExecutor(long, TimeUnit, Executor)})
 * to provide scheduling.
 * 
 * @author A248
 *
 */
public abstract class SimplifiedEnhancedExecutor implements EnhancedExecutor {

	/**
	 * Creates an instance
	 */
	protected SimplifiedEnhancedExecutor() {
		
	}
	
	/**
	 * Runs the specified command. <br>
	 * <br>
	 * It may run anywhere <i>except</i> the calling thread.
	 *
	 * @param command the command
	 */
	@Override
	public abstract void execute(Runnable command);

	@Override
	public CompletableFuture<?> submit(Runnable command) {
		return CompletableFuture.runAsync(command, this);
	}

	@Override
	public <T> CompletableFuture<T> supply(Supplier<T> supplier) {
		return CompletableFuture.supplyAsync(supplier, this);
	}

	@Override
	public ScheduledTask scheduleOnce(Runnable command, Duration delay) {
		Objects.requireNonNull(command, "command");

		long nanosDelay = delay.toNanos(); // implicit null check
		if (nanosDelay < 0) {
			return new AlreadyCancelledTask(nanosDelay, false);
		}
		DelayedScheduledTaskImpl result = new DelayedScheduledTaskImpl(nanosDelay, command);
		publishTask(result, nanosDelay);
		return result;
	}

	@Override
	public ScheduledTask scheduleRepeating(Runnable command, Duration initialDelay,
			DelayCalculator delayCalculator) {
		Objects.requireNonNull(command, "command");

		return scheduleRepeating0((task) -> command.run(), initialDelay, delayCalculator);
	}

	@Override
	public ScheduledTask scheduleRepeating(Consumer<? super ScheduledTask> command, Duration initialDelay,
			DelayCalculator delayCalculator) {
		Objects.requireNonNull(command, "command");

		return scheduleRepeating0(command, initialDelay, delayCalculator);
	}

	private ScheduledTask scheduleRepeating0(Consumer<? super ScheduledTask> command,
			Duration initialDelay, DelayCalculator delayCalculator) {
		Objects.requireNonNull(delayCalculator, "delayCalculator");

		long nanosDelay = initialDelay.toNanos(); // implicit null check
		if (nanosDelay < 0) {
			return new AlreadyCancelledTask(nanosDelay, true);
		}
		RepeatingScheduledTaskImpl result = new RepeatingScheduledTaskImpl(this, command, delayCalculator);
		result.update(nanosDelay, System.nanoTime());

		publishTask(result, nanosDelay);
		return result;
	}

	void publishTask(RunnableScheduledTask publishableTask, long nanosDelay) {
		if (nanosDelay == 0L) {
			execute(publishableTask);
		} else {
			CompletableFuture.delayedExecutor(nanosDelay, TimeUnit.NANOSECONDS, this).execute(publishableTask);
		}
	}

}
