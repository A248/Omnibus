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
 * A delay calculator which calculates the next delay of a task based on both
 * the previous delay and the execution time.
 * 
 * @author A248
 *
 */
@FunctionalInterface
public interface DelayCalculator {

	/**
	 * Calculates the next delay, based on the previous delay and execution time.
	 * <br>
	 * <br>
	 * If the returned duration is negative, the task will be cancelled. If zero, it
	 * will run immediately. <br>
	 * <br>
	 * The execution time is not computed by default. If the execution time is used
	 * by this calculator, {@link #requiresExecutionTime()} should return
	 * {@code true}
	 * 
	 * @param previousDelayNanos the previous delay, in nanoseconds
	 * @param executionTimeNanos how long it took the task to previously execute, in
	 *                           nanoseconds. If {@code requiresExecutionTime} is
	 *                           false, this will be {@literal 0}
	 * @return the next delay in nanoseconds, negative to cancel the task, zero to
	 *         run immediately
	 */
	long calculateNextDelay(long previousDelayNanos, long executionTimeNanos);

	/**
	 * Whether the execution time passed to {@link #calculateNextDelay(long, long)}
	 * is used.
	 * 
	 * @return true if the execution time is used, false otherwise
	 */
	default boolean requiresExecutionTime() {
		return false;
	}

}
