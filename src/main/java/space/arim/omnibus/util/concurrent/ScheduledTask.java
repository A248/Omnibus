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

import java.util.concurrent.Delayed;

/**
 * A delayed or repeating task scheduled with a {@link EnhancedExecutor}.
 * 
 * @author A248
 *
 */
public interface ScheduledTask extends Delayed {

	/**
	 * Cancels this scheduled task. <br>
	 * <br>
	 * If this is a delayed task, the work will not run unless it has already begun execution. Else, if
	 * it is a repeating task, further scheduling of the work will cease. If any execution is in process,
	 * it will complete first.
	 *
	 * @return true if cancelled, false if already cancelled, or this is a delayed task and already run
	 */
	boolean cancel();
	
	/**
	 * Whether this task is cancelled
	 *
	 * @return true if cancelled, false otherwise
	 */
	boolean isCancelled();
	
	/**
	 * Whether this task is cancelled, or is a delayed task and has been run
	 * 
	 * @return true if cancelled or this is a delayed task which has been run
	 */
	boolean isDone();
	
	/**
	 * Whether this is a repeating task or a one time delayed task
	 * 
	 * @return true if repeating, false if a one off delayed task
	 */
	boolean isRepeating();
	
}
