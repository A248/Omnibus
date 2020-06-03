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
package space.arim.universal.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link Executor} with upgraded concurrent functionality. Focuses specifically on concurrent execution and scheduling,
 * as such there are no methods for thread pool management (such as shutdown, await termination, etc.) as in
 * {@link java.util.concurrent.ExecutorService ExecutorService}. <br>
 * <br>
 * <b>Basic Usage</b> <br>
 * For basic asynchronous work, {@link #execute(Runnable)}, {@link #submit(Runnable)}, and {@link #supply(Supplier)} may be used. <br>
 * <br>
 * <b>Scheduling</b> <br>
 * Adds several scheduling methods to enable timed execution. Programmers may, if desired, dynamically provide
 * the time between executions based on a delay function. <br>
 * Scheduling comes in many variants. First, {@link #schedule(Runnable, long, TimeUnit)} may be used
 * for a one{@literal -}off delayed task (delays and then fires once, without repeating). <br>
 * <br>
 * There variant methods follow the pattern
 * <i>{@literal schedule(Runnable/Consumer, long, SimpleDelayCalculator/AdvancedDelayCalculator, TimeUnit)}</i>,
 * which allow for powerful usage of repeating delayed tasks. <br>
 * The <code>Runnable</code> is the execution to run, or a <code>Consumer</code> if the task must be able to access itself.
 * The <code>long</code> is the initial delay. If negative, nothing happens and all further scheduling is cancelled.
 * If zero, task execution begins immediately. <br>
 * <br>
 * The <code>SimpleDelayCalculator</code> or <code>AdvancedDelayCalculator</code> is the <i>delay function</i>
 * used to calculate the delay before the next execution. No executions will overlap. <br>
 * If the delay function is "Simple", it takes the last delay as the input and calculates the next delay accordingly.
 * If "Advanced", the delay function takes the last delay and the last execution time to calculate the next delay. <br>
 * For useful implementations of delay functions, {@link DelayFunctions} may be used. <br>
 * <br>
 * The TimeUnit is the unit which the initial delay is specified in and the unit which the delay function uses.
 * 
 * @author A248
 *
 */
public interface EnhancedExecutor extends Executor {

	/**
	 * Execute an asynchronous action. <br>
	 * Differs from {@link #execute(Runnable)} in that the returned {@link CompletableFuture} provides additional functionality,
	 * including the ability to listen for completion or block until complete.
	 * 
	 * @param command the {@link Runnable} to run
	 * @return a completable future of the executed runnable, never null
	 */
	CompletableFuture<?> submit(Runnable command);
	
	/**
	 * Supplies a value asynchronously using this executor to decide the thread context of execution.
	 * 
	 * @param <T> the type of the supplier
	 * @param supplier the supplier
	 * @return a completable future of the supplied value, never null
	 */
	<T> CompletableFuture<T> supply(Supplier<T> supplier);
	
	/**
	 * Schedules a one{@literal -}shot delayed task. <br>
	 * The returned {@link Task} may be used to cancel execution <i>if</i> it has not yet started.
	 * 
	 * @param command the <code>Runnable</code> to run
	 * @param delay the delay duration. If negative, nothing happens and all further scheduling is cancelled; if positive, run immediately.
	 * @param units the time units of the <i>delay</i>
	 * @return a task which may be cancelled, never <code>null</code>
	 */
	Task schedule(Runnable command, long delay, TimeUnit units);
	
	/**
	 * Schedules a repeating task where further delays are calculated after each execution by the provided function. <br>
	 * The delay function is invoked after each execution with the last delay as the input. <br>
	 * <br>
	 * Use {@link DelayFunctions} for default implementations of delay functions.
	 * 
	 * @param command the <code>Runnable</code> to run
	 * @param initialDelay the initial delay duration. If negative, nothing happens and all further scheduling is cancelled; if positive, run immediately.
	 * @param delayFunction the function to use when calculating the next delay
	 * @param units the time units the <i>initialDelay</i> and <i>delayFunction</i> use
	 * @return a task which may be cancelled, never <code>null</code>
	 */
	Task schedule(Runnable command, long initialDelay, SimpleDelayCalculator delayFunction, TimeUnit units);
	
	/**
	 * Schedules a repeating task where further delays are calculated after each execution by the provided function. <br>
	 * <br>
	 * Differs from {@link #schedule(Runnable, long, SimpleDelayCalculator, TimeUnit)} in that it accepts a <code>Consumer</code>
	 * which may be used to cancel further scheduling of the task from within the execution itself.
	 * 
	 * @param command the consumer to accept the task
	 * @param initialDelay the initial delay duration. If negative, nothing happens and all further scheduling is cancelled; if positive, run immediately.
	 * @param delayFunction the function to use when calculating the next delay
	 * @param units the time units the <i>initialDelay</i> and <i>delayFunction</i> use
	 * @return the same task the consumer received, never <code>null</code>
	 */
	Task schedule(Consumer<Task> command, long initialDelay, SimpleDelayCalculator delayFunction, TimeUnit units);
	
	/**
	 * Schedules a repeating task where further delays are calculated after each execution by the provided function. <br>
	 * The delay function is invoked after each execution with the last delay, the left input, and the last execution time, the right input. <br>
	 * <br>
	 * Use {@link DelayFunctions} for default implementations of delay functions.
	 * 
	 * @param command the runnable to run
	 * @param initialDelay the initial delay duration. If negative, nothing happens and all further scheduling is cancelled; if positive, run immediately.
	 * @param delayFunction the function to use when calculating the next delay
	 * @param units the time units the <i>initialDelay</i> and <i>delayFunction</i> use
	 * @return a task which may be cancelled, never <code>null</code>
	 */
	Task schedule(Runnable command, long initialDelay, AdvancedDelayCalculator delayFunction, TimeUnit units);
	
	/**
	 * Schedules a repeating task where further delays are calculated after each execution by the provided function. <br>
	 * <br>
	 * Differs from {@link #schedule(Runnable, long, AdvancedDelayCalculator, TimeUnit)} in that it accepts a <code>Consumer</code>
	 * which may be used to cancel further scheduling of the task from within the execution itself.
	 * 
	 * @param command the consumer to accept the task
	 * @param initialDelay the initial delay duration. If negative, nothing happens and all further scheduling is cancelled; if positive, run immediately.
	 * @param delayFunction the function to use when calculating the next delay
	 * @param units the time units the <i>initialDelay</i> and <i>delayFunction</i> use
	 * @return the same task the consumer received, never <code>null</code>
	 */
	Task schedule(Consumer<Task> command, long initialDelay, AdvancedDelayCalculator delayFunction, TimeUnit units);
	
}
