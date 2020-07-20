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
package space.arim.omnibus.util.concurrent;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

/**
 * A {@link Executor} with upgraded concurrent functionality. Focuses specifically on concurrent execution and scheduling,
 * and as such there are no methods for thread pool management (such as shutdown, await termination, etc.) as in
 * {@link ExecutorService}. <br>
 * <br>
 * Methods come in 3 categories. First, basic execution. Second, scheduling delayed tasks. Third, scheduling repeating
 * tasks. No method may return {@code null}. No parameters may ever be {@code null}. <br>
 * <br>
 * <b>Basic Usage</b> <br>
 * For basic asynchronous work, {@link #execute(Runnable)}, {@link #submit(Runnable)}, and {@link #supply(Supplier)}
 * may be used. <br>
 * <br>
 * <b>Delayed Scheduling</b> <br>
 * The <i>scheduleOnce</i> methods provide delayed scheduling. The delay is provided as a {@link Duration}.
 * All these methods returned {@link ScheduledWork}. Such {@code ScheduledTask}s may be used to cancel
 * the task before it begins execution. <br>
 * <br>
 * The work scheduled may be specified in 2 variants. First, it may be a simple {@code Runnable}, which is simply run.
 * Second, it may be a {@code Supplier}, whose supplied value becomes the result of the returned {@code ScheduledTask},
 * which is completed once the supplier itself completes processing. <br>
 * <br>
 * <b>Repeated Scheduling</b> <br>
 * The <i>scheduleRepeating</i> methods provide repeated scheduling. Similarly to delayed scheduling, the initial delay
 * is provided as a {@code Duration} and all methods return {@code ScheduledTask}. However, unlike delayed scheduling,
 * the {@code ScheduledTask} returned may be cancelled, or the {@code ScheduledTask} provided as an input to the functional
 * work parameter may be cancelled. Cancellation, if invoked, will prevent future executions of the task from beginning,
 * but in-progress work will not be cancelled. (Thus the {@code mayInterruptIfRunning} parameter to
 * {@link ScheduledWork#cancel(boolean)} has no effect) <br>
 * <br>
 * The work scheduled may be specified in 4 variants. First, it may be a simple {@code Runnable}, which is simply run
 * repeatedly. Second, it may be a {@code Supplier}, whose supplied value becomes the result of the {@code ScheduledTask}s
 * related to the task. Third, it may be a {@code Consumer}, which accepts the task itself, allowing the execution to cancel
 * further scheduling of itself. Fourth, it may be a {@code Function}, which accepts the task itself <i>and</i> becomes the
 * result of the {@code ScheduledTask}s in question. <br>
 * <br>
 * Each method takes a {@link DelayCalculator} whose responsibility it is to calculate the next delay, based on both the previous
 * delay and the execution time. {@link DelayCalculators} may be used for common useful implementations of such interface.
 * The {@code DelayCalculator} calculating a negative delay is equivalent to cancellation of the {@code ScheduledTask}.
 * <br>
 * No executions may ever overlap. That is, the next execution of a task will never commence while an existing execution is in
 * progress. With regards to the value of the {@code ScheduledTask}, each time the task completes the value will be set to
 * the newly completed value and dependent actions will be triggered. Awaiting completion will always await the next completion
 * of the task. {@link CompletionStage#toCompletableFuture()} will return a future representing a "snapshot" of the current
 * scheduled iteration, which is completed once the next execution completes.
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
	 * The command may execute in the calling thread, but only if the calling thread is one which is pooled
	 * or managed by the implementation, such that the command is run later. <br>
	 * For example, actions submitted from within one of the threads in <code>ForkJoinPool.commonPool</code>
	 * may run later on the same pooled thread.
	 * 
	 */
	@Override
	void execute(Runnable command);
	
	/**
	 * Execute an asynchronous action. <br>
	 * Differs from {@link #execute(Runnable)} in that the returned {@link CompletableFuture} provides additional functionality,
	 * including the ability to listen for completion or block until complete.
	 * 
	 * @param command the {@link Runnable} to run
	 * @return a completable future of the executed runnable
	 */
	CompletableFuture<?> submit(Runnable command);
	
	/**
	 * Supplies a value asynchronously using this executor as the context of execution.
	 * 
	 * @param <T> the type of the supplier
	 * @param supplier the supplier
	 * @return a future which upon completion of the supplier yields the supplied value
	 */
	<T> CompletableFuture<T> supply(Supplier<T> supplier);
	
	/*
	 * 
	 * One-off delayed tasks
	 * 
	 */
	
	/**
	 * Schedules a delayed task, where the work is specified as a {@code Runnable}. See the class javadoc
	 * 
	 * @param command the runnable to run
	 * @param delay the delay duration
	 * @return a scheduled task representing the delayed work, which may be cancelled before execution begins
	 */
	ScheduledWork<?> scheduleOnce(Runnable command, Duration delay);
	
	/**
	 * Schedules a delayed task, where the work is specified as a {@code Supplier}. See the class javadoc
	 * 
	 * @param <T> the type of the supplier
	 * @param supplier the supplier to run
	 * @param delay the delay duration
	 * @return a scheduled task representing the delayed work, which may be cancelled before execution begins
	 */
	<T> ScheduledWork<T> scheduleOnce(Supplier<T> supplier, Duration delay);
	
	/*
	 * 
	 * Repeating tasks
	 * 
	 */
	
	/**
	 * Schedules a repeating task, where the work is specified as a {@code Runnable}. See the class javadoc
	 * 
	 * @param command the runnable to run
	 * @param initialDelay the initial delay before the first execution begins
	 * @param delayCalculator the delay calculator to calculate repeated delays
	 * @return a scheduled task representing the repeating work, which may be cancelled
	 */
	ScheduledWork<?> scheduleRepeating(Runnable command, Duration initialDelay, DelayCalculator delayCalculator);
	
	/**
	 * Schedules a repeating task, where the work is specified as a {@code Supplier}. See the class javadoc
	 * 
	 * @param <T> the type of the supplier
	 * @param supplier the supplier to run
	 * @param initialDelay the initial delay before the first execution begins
	 * @param delayCalculator the delay calculator to calculate repeated delays
	 * @return a scheduled task representing the repeating work, which may be cancelled
	 */
	<T> ScheduledWork<T> scheduleRepeating(Supplier<T> supplier, Duration initialDelay, DelayCalculator delayCalculator);
	
	/**
	 * Schedules a repeating task, where the work is specified as a {@code Consumer}. See the class javadoc
	 * 
	 * @param command the consumer to run
	 * @param initialDelay the initial delay before the first execution begins
	 * @param delayCalculator the delay calculator to calculate repeated delays
	 * @return a scheduled task representing the repeating work, which may be cancelled
	 */
	ScheduledWork<?> scheduleRepeating(Consumer<? super ScheduledWork<?>> command, Duration initialDelay, DelayCalculator delayCalculator);
	
	/**
	 * Schedules a repeating task, where the work is specified as a {@code Function}. See the class javadoc
	 * 
	 * @param <T> the type of the function
	 * @param supplier the function to run
	 * @param initialDelay the initial delay before the first execution begins
	 * @param delayCalculator the delay calculator to calculate repeated delays
	 * @return a scheduled task representing the repeating work, which may be cancelled
	 */
	<T> ScheduledWork<T> scheduleRepeating(Function<? super ScheduledWork<T>, T> supplier, Duration initialDelay, DelayCalculator delayCalculator);
	
}
