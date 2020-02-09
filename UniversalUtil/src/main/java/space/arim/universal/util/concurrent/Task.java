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

/**
 * A scheduled task which may be cancelled via {@link #cancel()}. <br>
 * <br>
 * See {@link #cancel()} for specifications.
 * 
 * @author A248
 *
 */
public interface Task {

	/**
	 * Cancels the task, preventing further scheduling or rescheduling. <br>
	 * Cancellation should not stop a single pending execution of a task if execution has initiated (no interruption required). <br>
	 * <br>
	 * <b>Specifications</b>: <br>
	 * * A {@link EnhancedExecutor} or {@link Scheduler} MUST cease further scheduling of the task in timed or delayed executions. <br>
	 * * Method MUST be idempotent for repeated calls.
	 * 
	 */
	void cancel();
	
	/**
	 * Checks whether the task was cancelled using {@link #cancel()}
	 * 
	 * @return true if the task is cancelled, false otherwise
	 */
	boolean isCancelled();
	
}
