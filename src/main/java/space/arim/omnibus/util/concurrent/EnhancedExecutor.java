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
package space.arim.omnibus.util.concurrent;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.concurrent.ExecutorService;

/**
 * A {@link Executor} with upgraded concurrent functionality. Focuses
 * specifically on concurrent execution and scheduling, and as such there are no
 * methods for thread pool management (such as shutdown, await termination,
 * etc.) as in {@link ExecutorService}. <br>
 * <br>
 * Methods come in 3 categories: basic execution, one time delayed tasks, and
 * repeating tasks. Delays are specified in {@link Duration}. <br>
 * <br>
 * No method may return {@code null}. No parameters may ever be {@code null},
 * and {@code NullPointerException} will be thrown otherwise. <br>
 * <br>
 * <b>Basic Usage</b> <br>
 * For basic asynchronous work, {@link #execute(Runnable)},
 * {@link #submit(Runnable)}, and {@link #supply(Supplier)} may be used. <br>
 * <br>
 * <b>Delayed Scheduling</b> <br>
 * {@link #scheduleOnce(Runnable, Duration)} provides delayed scheduling. The
 * delay is provided as a {@link Duration}. Returned {@link ScheduledTask}s may
 * be used to cancel the task before it begins execution. <br>
 * <br>
 * <b>Repeated Scheduling</b> <br>
 * The <i>scheduleRepeating</i> methods provide repeated scheduling.
 * Cancellation will prevent future executions of the task from beginning, but
 * in-progress work will not be cancelled. <br>
 * <br>
 * Each method takes a {@link DelayCalculator} whose responsibility it is to
 * calculate the next delay, based on both the previous delay and the execution
 * time. {@link DelayCalculators} may be used for common useful implementations
 * of such interface. The {@code DelayCalculator} calculating a negative delay
 * is equivalent to cancellation of the {@code ScheduledTask}. <br>
 * No executions may ever overlap. That is, the next execution of a task will
 * never commence while an existing execution is in progress.
 * 
 * @author A248
 *
 */
public interface EnhancedExecutor extends Executor {

	/*
	 * 
	 * Basic methods
	 * 
	 */

	/**
	 * Executes an asynchronous action. <br>
	 * <br>
	 * The command may execute in the calling thread, but not synchronously. For
	 * example, actions submitted from within one of the threads in
	 * <code>ForkJoinPool.commonPool</code> may run later on the same pooled thread.
	 *
	 * @param command the command
	 */
	@Override
	void execute(Runnable command);

	/**
	 * Execute an asynchronous action using this executor. <br>
	 * <br>
	 * The returned future provides the ability to listen for completion or block
	 * until complete.
	 * 
	 * @param command the {@link Runnable} to run
	 * @return a future completed when the command is run
	 */
	CompletableFuture<?> submit(Runnable command);

	/**
	 * Supplies a value asynchronously using this executor.
	 * 
	 * @param <T>      the type of the supplier
	 * @param supplier the supplier
	 * @return a future which yields the result of the supplier
	 */
	<T> CompletableFuture<T> supply(Supplier<T> supplier);

	/*
	 * 
	 * One-off delayed tasks
	 * 
	 */

	/**
	 * Schedules a delayed task
	 * 
	 * @param command the runnable to run
	 * @param delay   the duration after which to commence execution
	 * @return a scheduled task for the delayed work, which may be cancelled before
	 *         execution begins
	 */
	ScheduledTask scheduleOnce(Runnable command, Duration delay);

	/*
	 * 
	 * Repeating tasks
	 * 
	 */

	/**
	 * Schedules a repeating task
	 * 
	 * @param command         the runnable to run
	 * @param initialDelay    the initial delay before the first execution begins
	 * @param delayCalculator the delay calculator to calculate repeated delays
	 * @return a scheduled task for the repeating work, which may be cancelled
	 */
	ScheduledTask scheduleRepeating(Runnable command, Duration initialDelay, DelayCalculator delayCalculator);

	/**
	 * Schedules a repeating task
	 * 
	 * @param command         the consumer to run
	 * @param initialDelay    the initial delay before the first execution begins
	 * @param delayCalculator the delay calculator to calculate repeated delays
	 * @return a scheduled task representing the repeating work, which may be
	 *         cancelled
	 */
	ScheduledTask scheduleRepeating(Consumer<? super ScheduledTask> command, Duration initialDelay,
			DelayCalculator delayCalculator);

}
