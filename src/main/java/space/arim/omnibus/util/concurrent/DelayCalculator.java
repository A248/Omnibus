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

/**
 * A delay calculator which calculates the next delay of a task based on both the previous delay and the execution time. <br>
 * <br>
 * Implementations should <i>never</i> throw exceptions.
 * 
 * @author A248
 *
 */
@FunctionalInterface
public interface DelayCalculator {

	/**
	 * Calculates the next delay, based on the previous delay and execution time. <b>Must not throw exceptions.</b> <br>
	 * <br>
	 * If the returned duration is negative, the task will be cancelled. If zero, it will run immediately. <br>
	 * <br>
	 * The execution time is defined as the duration, in nanoseconds, between the time at which the task was scheduled
	 * and that at which it was completed. Therefore, it may take into the overhead of executing a task in an
	 * {@code Executor}.
	 * 
	 * @param previousDelay the previous delay, in nanoseconds
	 * @param executionTime how long it took the task to previously execute, in nanoseconds
	 * @return the next delay, negative to cancel the task, zero to run immediately
	 */
	long calculateNextDelay(long previousDelay, long executionTime);
	
}
