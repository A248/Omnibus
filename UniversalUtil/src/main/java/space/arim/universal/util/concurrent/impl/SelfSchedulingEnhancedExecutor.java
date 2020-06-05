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
package space.arim.universal.util.concurrent.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import space.arim.universal.util.concurrent.AdvancedDelayCalculator;
import space.arim.universal.util.concurrent.SimpleDelayCalculator;
import space.arim.universal.util.concurrent.StoppableService;
import space.arim.universal.util.concurrent.Task;

/**
 * A nearly complete scheduling implementation for {@link EnhancedExecutor}. The only method left to subclasses to implement
 * is {@link #execute(Runnable)}. <br>
 * <br>
 * Internally the scheduling implementation uses a dedicated runnable on which delays are calculated and execution is managed.
 * Everything resolves around 1.) the dedicated runnable 2.) the <code>execute()</code> method.
 * When a task is fired from the scheduling runnable, it is run with <code>execute()</code>. Also, the runnable itself
 * is ran using <code>execute()</code> when the implementation is started. To prevent a chicken-or-the-egg problem
 * with instantiation and initiation of the runnable, subclasses must call {@link #start()} like so:
 * <pre>
 * public class EnhancedExecutorImpl extends SelfSchedulingEnhancedExecutor {
 * 
 *   public EnhancedExecutorImpl() {
 *     start(); // or super.start() if start() is already used
 *   }
 * }
 * </pre>
 * With regards to shutting down the scheduling runnable, this class implements {@link StoppableService}. (Subclasses with
 * identical methods to those in <code>StoppableService</code> should be careful to call <code>super</code> methods if necessary.)
 * 
 * 
 * @author A248
 *
 */
public abstract class SelfSchedulingEnhancedExecutor extends AbstractEnhancedExecutor implements StoppableService {

	volatile boolean isRunning = false;
	/**
	 * Main scheduler runnable
	 */
	private final SchedulerRunnable schedulerRunnable;
	/**
	 * Future representing the runnable, should never be re-assigned
	 */
	private CompletableFuture<?> schedulerFuture;
	/**
	 * The queue of future scheduled tasks
	 */
	final BlockingQueue<ScheduledTask> taskQueue = new PriorityBlockingQueue<>();
	
	protected SelfSchedulingEnhancedExecutor() {
		schedulerRunnable = new SchedulerRunnable(this);
	}
	
	/**
	 * Initiates the scheduling implementation by running the scheduling runnable. <br>
	 * Should only be called <b>ONCE</b>. Behaviour is undefined for multiple calls to this method.
	 * 
	 */
	protected void start() {
		if (schedulerFuture != null) {
			throw new IllegalStateException("SelfSchedulingEnhancedExecutor#start should only be called once");
		}
		isRunning = true;
		schedulerFuture = CompletableFuture.runAsync(schedulerRunnable, this);
	}
	
	private void checkNotShutdown() {
		if (!isRunning) {
			throw new RejectedExecutionException();
		}
	}
	
	void addTaskToQueue(ScheduledTask task) {
		taskQueue.offer(task);
		synchronized (schedulerRunnable) {
			schedulerRunnable.notify();
		}
	}
	
	/**
	 * Rewrites the units a SimpleDelayCalculator uses, to nanoseconds
	 * 
	 * @param foreignCalculator the source calculator
	 * @param foreignUnits the source units
	 * @return a nanosecond-based calculator
	 */
	private SimpleDelayCalculator transform(SimpleDelayCalculator foreignCalculator, TimeUnit foreignUnits) {
		if (foreignUnits == TimeUnit.NANOSECONDS) {
			return foreignCalculator;
		}
		return (previousDelay) -> {
			long foreignResult = foreignCalculator.calculateNext(foreignUnits.convert(previousDelay, TimeUnit.NANOSECONDS));
			return TimeUnit.NANOSECONDS.convert(foreignResult, foreignUnits);
		};
	}
	
	/**
	 * Rewrites the units an AdvancedDelayCalculator uses, to nanoseconds
	 * 
	 * @param foreignCalculator the source calculator
	 * @param foreignUnits the source units
	 * @return a nanosecond-based calculator
	 */
	private AdvancedDelayCalculator transform(AdvancedDelayCalculator foreignCalculator, TimeUnit foreignUnits) {
		if (foreignUnits == TimeUnit.NANOSECONDS) {
			return foreignCalculator;
		}
		return (previousDelay, executionTime) -> {
			long foreignDelay = foreignUnits.convert(previousDelay, TimeUnit.NANOSECONDS);
			long foreignExecutionTime = foreignUnits.convert(executionTime, TimeUnit.NANOSECONDS);
			return TimeUnit.NANOSECONDS.convert(foreignCalculator.calculateNext(foreignDelay, foreignExecutionTime), foreignUnits);
		};
	}
	
	@Override
	public Task schedule(Runnable command, long delay, TimeUnit units) {
		checkNotShutdown();

		// Instant run
		if (delay == 0L) {
			execute(command);
			return new AlreadyRunTask();
		}

		// Instant cancel
		if (delay < 0L) {
			return new CancelledTask();
		}
		ScheduledTask task = new DelayedTask(this, command, TimeUnit.NANOSECONDS.convert(delay, units));
		addTaskToQueue(task);
		return task;
	}

	private CalculatedTask makeCalculatedTask(Runnable command, long initialDelay, SimpleDelayCalculator delayFunction, TimeUnit units) {
		return new SimpleCalculatedTask(this, command, TimeUnit.NANOSECONDS.convert(initialDelay, units),
				transform(delayFunction, units));
	}
	
	private CalculatedTask makeCalculatedTask(Runnable command, long initialDelay, AdvancedDelayCalculator delayFunction, TimeUnit units) {
		return new AdvancedCalculatedTask(this, command, TimeUnit.NANOSECONDS.convert(initialDelay, units),
				transform(delayFunction, units));
	}
	
	@Override
	public Task schedule(Runnable command, long initialDelay, SimpleDelayCalculator delayFunction, TimeUnit units) {
		checkNotShutdown();

		// Instant run
		if (initialDelay == 0L) {
			CalculatedTask task = makeCalculatedTask(command, initialDelay, delayFunction, units);
			task.run();
			return task;
		}

		// Instant cancel
		if (initialDelay < 0L) {
			return new CancelledTask();
		}
		ScheduledTask task = makeCalculatedTask(command, initialDelay, delayFunction, units);
		addTaskToQueue(task);
		return task;
	}

	@Override
	public Task schedule(Consumer<Task> command, long initialDelay, SimpleDelayCalculator delayFunction,
			TimeUnit units) {
		checkNotShutdown();

		// Instant run
		if (initialDelay == 0L) {
			TaskWrapper wrapper = new TaskWrapper();
			CalculatedTask task = makeCalculatedTask(() -> command.accept(wrapper), initialDelay, delayFunction, units);
			wrapper.task = task;
			task.run();
			return task;
		}

		// Instant cancel
		if (initialDelay < 0L) {
			return new CancelledTask();
		}
		TaskWrapper wrapper = new TaskWrapper();
		ScheduledTask task = makeCalculatedTask(() -> command.accept(wrapper), initialDelay, delayFunction, units);
		wrapper.task = task;
		addTaskToQueue(task);
		return task;
	}

	@Override
	public Task schedule(Runnable command, long initialDelay, AdvancedDelayCalculator delayFunction, TimeUnit units) {
		checkNotShutdown();

		// Instant run
		if (initialDelay == 0L) {
			CalculatedTask task = makeCalculatedTask(command, initialDelay, delayFunction, units);
			task.run();
			return task;
		}

		// Instant cancel
		if (initialDelay < 0L) {
			return new CancelledTask();
		}
		ScheduledTask task = makeCalculatedTask(command, initialDelay, delayFunction, units);
		addTaskToQueue(task);
		return task;
	}

	@Override
	public Task schedule(Consumer<Task> command, long initialDelay, AdvancedDelayCalculator delayFunction,
			TimeUnit units) {
		checkNotShutdown();

		// Instant run
		if (initialDelay == 0L) {
			TaskWrapper wrapper = new TaskWrapper();
			CalculatedTask task = makeCalculatedTask(() -> command.accept(wrapper), initialDelay, delayFunction, units);
			wrapper.task = task;
			task.run();
			return task;
		}

		// Instant cancel
		if (initialDelay < 0L) {
			return new CancelledTask();
		}
		TaskWrapper wrapper = new TaskWrapper();
		ScheduledTask task = makeCalculatedTask(() -> command.accept(wrapper), initialDelay, delayFunction, units);
		wrapper.task = task;
		addTaskToQueue(task);
		return task;
	}

	@Override
	public void shutdown() {
		isRunning = false;
	}

	@Override
	public boolean isShutdown() {
		return !isRunning;
	}

	@Override
	public boolean isTerminated() {
		return schedulerFuture.isDone();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		try {
			schedulerFuture.get(timeout, unit);
			return true;
		} catch (TimeoutException ignored) {
			return false;
		} catch (ExecutionException ex) {
			throw new CompletionException(ex);
		}
	}

}
