/* 
 * UniversalUtil, simple utilities for Spigot and BungeeCord
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

import java.util.function.Consumer;

/**
 * A scheduler designed for creating delayed and timed tasks.
 * <br>
 * <b>Specifications:</b> <br>
 * * Requires {@link #runTaskLater(Runnable, long)} <br>
 * * Requires {@link #runTaskTimerLater(Runnable, long, long)} <br>
 * * Default implementations: See below <br>
 * <br>
 * <b>Contract:</b> <br>
 * * Each method returns a {@link Task} representing the scheduled task.
 * Such returned Task objects' {@link Task#cancel()} method should succeed in ceasing further scheduling of the task. <br>
 * * All time units are in <i>milliseconds</i> to provide compatibility with implementing platforms which do not support other units. <br>
 * <br>
 * <b>Default implementations:</b> <br>
 * * {@link #runTaskTimer(Runnable, long)} calls {@link #runTaskTimerLater} with <code>delay</code> parameter as <code>0</code>.
 * This may be overriden if doing so increases efficiency. <br>
 * * All methods accept either a basic {@link Runnable} or a {@link Consumer} as the task to schedule.
 * Callers may use the method accepting a consumer parameter so that the executing task can cancel its own scheduling.
 * Default implementations need never be overriden. They derive from the corresponding methods accepting Runnable parameters.
 * 
 * @author A248
 *
 */
public interface Scheduler {
	
	/**
	 * Runs a delayed task
	 * 
	 * @param command the execution
	 * @param delay the delay
	 * @return a cancellable task
	 */
	Task runTaskLater(Runnable command, long delay);
	
	/**
	 * Same as {@link #runTaskLater(Runnable, long)} but with the ability to access the task
	 * (and thus cancel further scheduling of it) from within the execution itself.
	 * 
	 * @param command the execution
	 * @param delay the delay
	 * @return a cancellable task
	 */
	default Task runTaskLater(Consumer<? super Task> command, long delay) {
		PreTask pre = new PreTask();
		Task result = runTaskLater(() -> command.accept(pre), delay);
		pre.fill(result);
		return result;
	}
	
	/**
	 * Runs a timed repeating task. The task may be cancelled. <br>
	 * <br>
	 * Default implementation: returns {@link #runTaskTimerLater(Runnable, long, long)} with the <code>delay</code> parameter (2nd param) set to zero.
	 * 
	 * @param command the execution
	 * @param period the timespan between executions
	 * @return a cancellable task
	 */
	default Task runTaskTimer(Runnable command, long period) {
		return runTaskTimerLater(command, 0L, period);
	}
	
	/**
	 * Same as {@link #runTaskTimer(Runnable, long)} but with the ability to access the task
	 * (and thus cancel further scheduling of it) from within the execution itself.
	 * 
	 * @param command the execution
	 * @param period the timespan between executions
	 * @return a cancellable task
	 */
	default Task runTaskTimer(Consumer<? super Task> command, long period) {
		PreTask pre = new PreTask();
		Task result = runTaskTimer(() -> command.accept(pre), period);
		pre.fill(result);
		return result;
	}
	
	/**
	 * Runs a timed repeating task with an additional initial delay. The task may be cancelled to stop further scheduling.
	 * 
	 * @param command the execution
	 * @param period the timespan between executions
	 * @param delay the initial delay
	 * @return a cancellable task
	 */
	Task runTaskTimerLater(Runnable command, long delay, long period);
	
	/**
	 * Same as {@link #runTaskTimerLater(Runnable, long, long)} but with the ability to access the task
	 * (and thus cancel further scheduling of it) from within the execution itself.
	 * 
	 * @param command the execution
	 * @param period the timespan between executions
	 * @param delay the initial delay
	 * @return a cancellable task
	 */
	default Task runTaskTimerLater(Consumer<? super Task> command, long delay, long period) {
		PreTask pre = new PreTask();
		Task result = runTaskTimerLater(() -> command.accept(pre), delay, period);
		pre.fill(result);
		return result;
	}
	
}
