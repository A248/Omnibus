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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;

/**
 * A {@link Executor} with upgraded concurrent functionality. Focuses specifically on concurrent execution and scheduling, as such
 * there are no methods for thread pool management (such as shutdown, await termination, etc.) as in {@link java.util.concurrent.ExecutorService ExecutorService}. <br>
 * <br>
 * {@link #decorate(Executor)} may be used to enhance a <code>Executor</code> into a <code>EnhancedExecutor</code>. <br>
 * <br>
 * <b>Specifications:</b> <br>
 * * Requires {@link Executor#execute(Runnable)} <br>
 * * EnhancedExecutor provides default implementations for all of its own specifications. <br>
 * <br>
 * <b>Qualities:</b> <br>
 * <br>
 * Adds {@link #submit(Runnable)} and {@link #supply(Supplier)} for basic asynchronous work. <br>
 * <br>
 * Adds several scheduling methods to enable timed execution. Programmers may, if desired, dynamically provide
 * the time between executions based on a delay function. <br>
 * Scheduling comes in many variants. {@link #schedule(Runnable, long, TimeUnit)} may be used for a one{@literal -}off delayed task
 * (delays and then fires once, without repeating). <br>
 * <br>
 * There are several variant methods, which follow <i>{@literal schedule(Runnable/Consumer, long, LongUnaryOperator/LongBinaryOperator, TimeUnit)}</i>,
 * which allow for repeating delayed tasks. <br>
 * The <code>Runnable</code> is the execution to run, or a <code>Consumer</code> if the task must be able to access itself.
 * The <code>long</code> is the initial delay. If negative, nothing happens and all further scheduling is cancelled.
 * If zero, task eexecution begins immediately. <br>
 * The <code>LongUnaryOperator</code> or <code>LongBinaryOperator</code> is the <i>delay function</i> used to calculate the delay
 * before the next execution. All executions are guaranteed not to overlap. This is ensured in the default implementation and should
 * likewise be upheld in overriding implementations. <br>
 * If the delay function is "Unary", it takes the last delay as the input; "Unary" delay functions calculate the next delay
 * based only on the previous delay. If "Binary", the delay function takes the last delay as the left input and
 * the last execution time as the right input. Essentially, "Binary" delay functions generate the next delay
 * based on the previous delay and the execution time. For simple implementations, {@link DelayFunctions} may be used. <br>
 * The TimeUnit is the unit which the initial delay is specified in and the unit which the delay function uses.
 * However, note that while <code>TimeUnit.NANOSECONDS</code> is a possible parameter,
 * implementations are not guaranteed further precision than milliseconds time. <br>
 * The method variants are:
 * ({@link #schedule(Runnable, long, LongUnaryOperator, TimeUnit)} / {@link #schedule(Consumer, long, LongUnaryOperator, TimeUnit)} /
 * {@link #schedule(Runnable, long, LongBinaryOperator, TimeUnit)} / {@link #schedule(Consumer, long, LongBinaryOperator, TimeUnit)}).
 * 
 * @author A248
 *
 */
public interface EnhancedExecutor extends Executor {

	/**
	 * Execute an asynchronous action. <br>
	 * Differs from {@link #execute} in that the returned {@link CompletableFuture} provides additional functionality,
	 * including the ability to listen for completion or block until complete.
	 * 
	 * @param command the {@link Runnable} to run
	 * @return a <code>CompletableFuture</code> which will return <code>null</code> on {@link Future#get()}
	 */
	default CompletableFuture<Void> submit(Runnable command) {
		return HyperFuture.runAsync(command, this);
	}
	
	/**
	 * Supplies a value asynchronously.
	 * 
	 * Similar to {@link java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable) ExecutorService.submit(Callable)}. <br>
	 * However, Callable may throw an exception, while Supplier does not.
	 * 
	 * @param <T> the type of the supplier
	 * @param supplier the supplier
	 * @return a <code>CompletableFuture</code>
	 */
	default <T> CompletableFuture<T> supply(Supplier<T> supplier) {
		return HyperFuture.supplyAsync(supplier, this);
	}
	
	/**
	 * Schedules a one{@literal -}shot delayed task. <br>
	 * The returned {@link Task} may be used to cancel execution <i>if</i> it has not yet started. <br>
	 * <br>
	 * Note that there is no <code>schedule(Consumer, long, TimeUnit)</code> method.
	 * This is because the task consumer, should the task consumer cancel the task,
	 * there will be no effect, as the execution will already have begun. <br>
	 * <br>
	 * 
	 * @param cmd the <code>Runnable</code> to run
	 * @param delay the delay duration. If negative, nothing happens and all further scheduling is cancelled; if positive, run immediately.
	 * @param units the time units of the <i>delay</i>
	 * @return a <code>Task</code> which may be cancelled, never <code>null</code>
	 */
	default Task schedule(Runnable cmd, long delay, TimeUnit units) {
		Task task = new DefaultTask();
		long msDelay = TimeUnit.MILLISECONDS.convert(delay, units);
		if (msDelay > 0) {
			ScheduledAction.schedule(this, cmd, msDelay, task::isCancelled);
		} else if (msDelay == 0) {
			execute(cmd);
		}
		return task;
	}
	
	/**
	 * Schedules a repeating task where further delays are calculated after each execution by the provided function. <br>
	 * The delay function is invoked after each execution with the last delay as the input. <br>
	 * <br>
	 * Use {@link DelayFunctions} for default implementations of delay functions.
	 * 
	 * @param cmd the <code>Runnable</code> to run
	 * @param initialDelay the initial delay duration. If negative, nothing happens and all further scheduling is cancelled; if positive, run immediately.
	 * @param delayFunction the function to use when calculating the next delay
	 * @param units the time units the <i>initialDelay</i> and <i>delayFunction</i> use
	 * @return a <code>Task</code> which may then be cancelled, never <code>null</code>
	 */
	default Task schedule(Runnable cmd, long initialDelay, LongUnaryOperator delayFunction, TimeUnit units) {
		Task task = new DefaultTask();
		long msDelay = TimeUnit.MILLISECONDS.convert(initialDelay, units);
		if (msDelay >= 0) {
			LongUnaryOperator convertedDelayFunction = (previousDelay) -> TimeUnit.MILLISECONDS.convert(delayFunction.applyAsLong(units.convert(previousDelay, TimeUnit.MILLISECONDS)), units);
			ScheduledAction.schedule(this, cmd, msDelay, task::isCancelled, convertedDelayFunction);
		}
		return task;
	}
	
	/**
	 * Schedules a repeating task where further delays are calculated after each execution by the provided function. <br>
	 * <br>
	 * Differs from {@link #schedule(Runnable, long, LongUnaryOperator, TimeUnit)} in that it accepts a <code>Consumer</code>
	 * which may be used to cancel further scheduling of the task from within the execution itself.
	 * 
	 * @param cmd the <code>Consumer</code> to accept the <code>Task</code>, which may then be cancelled
	 * @param initialDelay the initial delay duration. If negative, nothing happens and all further scheduling is cancelled; if positive, run immediately.
	 * @param delayFunction the function to use when calculating the next delay
	 * @param units the time units the <i>initialDelay</i> and <i>delayFunction</i> use
	 * @return the same <code>Task</code> the <code>Consumer</code> received, never <code>null</code>
	 */
	default Task schedule(Consumer<Task> cmd, long initialDelay, LongUnaryOperator delayFunction, TimeUnit units) {
		Task task = new DefaultTask();
		long msDelay = TimeUnit.MILLISECONDS.convert(initialDelay, units);
		if (msDelay >= 0) {
			LongUnaryOperator convertedDelayFunction = (previousDelay) -> TimeUnit.MILLISECONDS.convert(delayFunction.applyAsLong(units.convert(previousDelay, TimeUnit.MILLISECONDS)), units);
			ScheduledAction.schedule(this, () -> cmd.accept(task), msDelay, task::isCancelled, convertedDelayFunction);
		}
		return task;
	}
	
	/**
	 * Schedules a repeating task where further delays are calculated after each execution by the provided function. <br>
	 * The delay function is invoked after each execution with the last delay, the left input, and the last execution time, the right input. <br>
	 * <br>
	 * Use {@link DelayFunctions} for default implementations of delay functions.
	 * 
	 * @param cmd the <code>Runnable</code> to run
	 * @param initialDelay the initial delay duration. If negative, nothing happens and all further scheduling is cancelled; if positive, run immediately.
	 * @param delayFunction the function to use when calculating the next delay
	 * @param units the time units the <i>initialDelay</i> and <i>delayFunction</i> use
	 * @return a <code>Task</code> which may then be cancelled, never <code>null</code>
	 */
	default Task schedule(Runnable cmd, long initialDelay, LongBinaryOperator delayFunction, TimeUnit units) {
		Task task = new DefaultTask();
		long msDelay = TimeUnit.MILLISECONDS.convert(initialDelay, units);
		if (msDelay >= 0) {
			LongBinaryOperator convertedDelayFunction = (previousDelay, execTime) -> TimeUnit.MILLISECONDS.convert(delayFunction.applyAsLong(units.convert(previousDelay, TimeUnit.MILLISECONDS), units.convert(execTime, TimeUnit.MILLISECONDS)), units);
			ScheduledAction.schedule(this, cmd, msDelay, task::isCancelled, convertedDelayFunction);
		}
		return task;
	}
	
	/**
	 * Schedules a repeating task where further delays are calculated after each execution by the provided function. <br>
	 * <br>
	 * Differs from {@link #schedule(Runnable, long, LongBinaryOperator, TimeUnit)} in that it accepts a <code>Consumer</code>
	 * which may be used to cancel further scheduling of the task from within the execution itself.
	 * 
	 * @param cmd the <code>Consumer</code> to accept the <code>Task</code>, which may then be cancelled
	 * @param initialDelay the initial delay duration. If negative, nothing happens and all further scheduling is cancelled; if positive, run immediately.
	 * @param delayFunction the function to use when calculating the next delay
	 * @param units the time units the <i>initialDelay</i> and <i>delayFunction</i> use
	 * @return the same <code>Task</code> the <code>Consumer</code> received, never <code>null</code>
	 */
	default Task schedule(Consumer<Task> cmd, long initialDelay, LongBinaryOperator delayFunction, TimeUnit units) {
		Task task = new DefaultTask();
		long msDelay = TimeUnit.MILLISECONDS.convert(initialDelay, units);
		if (msDelay >= 0) {
			LongBinaryOperator convertedDelayFunction = (previousDelay, execTime) -> TimeUnit.MILLISECONDS.convert(delayFunction.applyAsLong(units.convert(previousDelay, TimeUnit.MILLISECONDS), units.convert(execTime, TimeUnit.MILLISECONDS)), units);
			ScheduledAction.schedule(this, () -> cmd.accept(task), msDelay, task::isCancelled, convertedDelayFunction);
		}
		return task;
	}
	
	/**
	 * Enhances a <code>Executor</code>, converting to an EnhancedExecutor. <br>
	 * The result uses the default implementations of all but the required <code>execute()</code>.
	 * 
	 * @param executor the executor
	 * @return an EnhancedExecutor with the same {@link #execute(Runnable)} method
	 */
	static EnhancedExecutor decorate(Executor executor) {
		return executor::execute;
	}
	
}
