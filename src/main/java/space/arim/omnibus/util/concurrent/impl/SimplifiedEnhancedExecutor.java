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
import java.util.function.Function;
import java.util.function.Supplier;

import space.arim.omnibus.util.concurrent.DelayCalculator;
import space.arim.omnibus.util.concurrent.EnhancedExecutor;
import space.arim.omnibus.util.concurrent.ScheduledWork;

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
	public ScheduledWork<Void> scheduleOnce(Runnable command, Duration delay) {
		Objects.requireNonNull(command, "command");
		Objects.requireNonNull(delay, "delay");

		return scheduleOnce0(() -> {
			command.run();
			return null;
		}, delay);
	}

	@Override
	public <T> ScheduledWork<T> scheduleOnce(Supplier<T> supplier, Duration delay) {
		Objects.requireNonNull(supplier, "supplier");
		Objects.requireNonNull(delay, "delay");

		return scheduleOnce0(supplier, delay);
	}

	private <T> ScheduledWork<T> scheduleOnce0(Supplier<T> supplier, Duration delay) {
		long nanosDelay = delay.toNanos();
		if (nanosDelay < 0) {
			return new AlreadyCancelledWork<>(false, nanosDelay);
		}
		DelayedScheduledWork<T> result = new DelayedScheduledWork<>(nanosDelay, supplier);
		publishTask(result, nanosDelay);
		return result;
	}

	@Override
	public ScheduledWork<?> scheduleRepeating(Runnable command, Duration initialDelay,
			DelayCalculator delayCalculator) {
		Objects.requireNonNull(command, "command");
		Objects.requireNonNull(initialDelay, "initialDelay");
		Objects.requireNonNull(delayCalculator, "delayCalculator");

		return scheduleRepeating0((task) -> {
			command.run();
			return null;
		}, initialDelay, delayCalculator);
	}

	@Override
	public <T> ScheduledWork<T> scheduleRepeating(Supplier<T> supplier, Duration initialDelay,
			DelayCalculator delayCalculator) {
		Objects.requireNonNull(supplier, "supplier");
		Objects.requireNonNull(initialDelay, "initialDelay");
		Objects.requireNonNull(delayCalculator, "delayCalculator");

		return scheduleRepeating0((task) -> supplier.get(), initialDelay, delayCalculator);
	}

	@Override
	public ScheduledWork<?> scheduleRepeating(Consumer<? super ScheduledWork<?>> command, Duration initialDelay,
			DelayCalculator delayCalculator) {
		Objects.requireNonNull(command, "command");
		Objects.requireNonNull(initialDelay, "initialDelay");
		Objects.requireNonNull(delayCalculator, "delayCalculator");

		return scheduleRepeating0((task) -> {
			command.accept(task);
			return null;
		}, initialDelay, delayCalculator);
	}

	@Override
	public <T> ScheduledWork<T> scheduleRepeating(Function<? super ScheduledWork<T>, T> supplier, Duration initialDelay,
			DelayCalculator delayCalculator) {
		Objects.requireNonNull(supplier, "supplier");
		Objects.requireNonNull(initialDelay, "initialDelay");
		Objects.requireNonNull(delayCalculator, "delayCalculator");

		return scheduleRepeating0(supplier, initialDelay, delayCalculator);
	}

	private <T> ScheduledWork<T> scheduleRepeating0(Function<? super ScheduledWork<T>, T> supplier, Duration initialDelay,
			DelayCalculator delayCalculator) {
		long nanosDelay = initialDelay.toNanos();
		if (nanosDelay < 0) {
			return new AlreadyCancelledWork<>(true, nanosDelay);
		}
		RepeatingScheduledWork<T> result = new RepeatingScheduledWork<>(this, supplier, delayCalculator);
		result.update(nanosDelay, System.nanoTime());

		publishTask(result, nanosDelay);
		return result;
	}

	void publishTask(RunnableScheduledWork<?> publishableTask, long nanosDelay) {
		if (nanosDelay == 0L) {
			execute(publishableTask);
		} else {
			CompletableFuture.delayedExecutor(nanosDelay, TimeUnit.NANOSECONDS, this).execute(publishableTask);
		}
	}

}
