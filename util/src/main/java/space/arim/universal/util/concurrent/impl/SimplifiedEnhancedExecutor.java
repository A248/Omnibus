/* 
 * Universal-util
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-util. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.util.concurrent.impl;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import space.arim.universal.util.concurrent.DelayCalculator;
import space.arim.universal.util.concurrent.EnhancedExecutor;
import space.arim.universal.util.concurrent.ScheduledWork;

/**
 * Abstract implementation of {@link EnhancedExecutor} which handles its own scheduling and delegates
 * execution to subclasses' {@link #execute(Runnable)} methods. <br>
 * <br>
 * An internal scheduling thread is maintained which is responsible for all scheduling of tasks. From
 * within this internal scheduler, execution of the work itself is delegated to the {@code execute} method.
 * The {@code Runnable} is then executed somewhere else, typically in a thread pool associated with the subclass.
 * 
 * @author A248
 *
 */
public abstract class SimplifiedEnhancedExecutor implements EnhancedExecutor {

	private final ScheduledExecutorService scheduler;
	
	/**
	 * Creates using the default thread factory to instantiate the scheduling thread
	 * 
	 */
	protected SimplifiedEnhancedExecutor() {
		scheduler = Executors.newScheduledThreadPool(1);
	}
	
	/**
	 * Creates using the specified thread factory to instantiate the scheduling thread
	 * 
	 * @param threadFactory the thread factory from which to derive the scheduling thread
	 */
	protected SimplifiedEnhancedExecutor(ThreadFactory threadFactory) {
		scheduler = Executors.newScheduledThreadPool(1, threadFactory);
	}
	
	/**
	 * Runs the specified command. <br>
	 * <br>
	 * It may run anywhere <i>except</i> the calling thread. This implementation uses a single dedicated
	 * scheduling thread whose only job it is to schedule execution of tasks. Executing work in the calling
	 * thread would therefore be inappropriate, and could delay further tasks.
	 * 
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
		Objects.requireNonNull(command, "Command must not be null");
		Objects.requireNonNull(delay, "Delay must not be null");

		return scheduleOnce0(() -> {
			command.run();
			return null;
		}, delay);
	}

	@Override
	public <T> ScheduledWork<T> scheduleOnce(Supplier<T> supplier, Duration delay) {
		Objects.requireNonNull(supplier, "Supplier must not be null");
		Objects.requireNonNull(delay, "Delay must not be null");

		return scheduleOnce0(supplier, delay);
	}
	
	private <T> ScheduledWork<T> scheduleOnce0(Supplier<T> supplier, Duration delay) {
		long nanosDelay = delay.toNanos();
		if (nanosDelay < 0) {
			return new AlreadyCancelledWork<>(false, nanosDelay);
		}
		DelayedScheduledWork<T> result = new DelayedScheduledWork<>(this, nanosDelay, supplier);
		scheduleAndLink(result, nanosDelay);
		return result;
	}

	@Override
	public ScheduledWork<?> scheduleRepeating(Runnable command, Duration initialDelay,
			DelayCalculator delayCalculator) {
		Objects.requireNonNull(command, "Command must not be null");
		Objects.requireNonNull(initialDelay, "Initial delay must not be null");
		Objects.requireNonNull(delayCalculator, "Delay calculator must not be null");

		return scheduleRepeating0((task) -> {
			command.run();
			return null;
		}, initialDelay, delayCalculator);
	}

	@Override
	public <T> ScheduledWork<T> scheduleRepeating(Supplier<T> supplier, Duration initialDelay,
			DelayCalculator delayCalculator) {
		Objects.requireNonNull(supplier, "Supplier must not be null");
		Objects.requireNonNull(initialDelay, "Initial delay must not be null");
		Objects.requireNonNull(delayCalculator, "Delay calculator must not be null");

		return scheduleRepeating0((task) -> supplier.get(), initialDelay, delayCalculator);
	}

	@Override
	public ScheduledWork<?> scheduleRepeating(Consumer<? super ScheduledWork<?>> command, Duration initialDelay,
			DelayCalculator delayCalculator) {
		Objects.requireNonNull(command, "Command must not be null");
		Objects.requireNonNull(initialDelay, "Initial delay must not be null");
		Objects.requireNonNull(delayCalculator, "Delay calculator must not be null");

		return scheduleRepeating0((task) -> {
			command.accept(task);
			return null;
		}, initialDelay, delayCalculator);
	}

	@Override
	public <T> ScheduledWork<T> scheduleRepeating(Function<? super ScheduledWork<T>, T> supplier, Duration initialDelay,
			DelayCalculator delayCalculator) {
		Objects.requireNonNull(supplier, "Supplier must not be null");
		Objects.requireNonNull(initialDelay, "Initial delay must not be null");
		Objects.requireNonNull(delayCalculator, "Delay calculator must not be null");

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

		scheduleAndLink(result, nanosDelay);
		return result;
	}
	
	void scheduleAndLink(RunnableScheduledWork<?> publishableTask, long nanosDelay) {
		if (nanosDelay == 0L) {
			scheduler.execute(publishableTask);
		} else {
			ScheduledFuture<?> internalFuture = scheduler.schedule(publishableTask, nanosDelay, TimeUnit.NANOSECONDS);
			publishableTask.whenComplete((val, ex) -> {
				if (ex instanceof CancellationException) {
					internalFuture.cancel(false);
				}
			});
		}
	}

}
