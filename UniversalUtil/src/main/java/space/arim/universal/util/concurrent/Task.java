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

/**
 * Represents scheduled work which may be cancelled via {@link #cancel()}.
 * 
 * @author A248
 *
 */
public interface Task {

	/**
	 * Cancels the task, preventing further scheduling, rescheduling, or upcoming execution. <br>
	 * <br>
	 * A task which is currently executing will not be interrupted; merely, it will not be rescheduled.
	 * <br>
	 * Calling <code>cancel()</code> on a cancelled task is a no-op.
	 * 
	 */
	void cancel();
	
	/**
	 * Checks whether the task was cancelled using {@link #cancel()}
	 * 
	 * @return true if the task is cancelled, false otherwise
	 */
	boolean isCancelled();
	
	/**
	 * Whether this object is equal to another. <br>
	 * <br>
	 * Two task objects are considered equal if they represent the same scheduled work.
	 * Thus, if <code>task1.equals(task2)</code>, then <code>task1.cancel()</code> is
	 * equivalent to <code>task2.cancel()</code>.
	 * 
	 * @param object the object to evaluate equality with
	 * @return true if equal, false otherwise
	 */
	@Override
	boolean equals(Object object);
	
}
