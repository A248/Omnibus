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

import java.util.function.LongUnaryOperator;

/**
 * Provides default implementations for {@link DelayCalculator}, which determines delays for {@link EnhancedExecutor}'s
 * repeated scheduling methods. <br>
 * <br>
 * Fixed delay: {@link #fixedDelay()} <br>
 * Fixed rate: {@link #fixedRate()} <br>
 * Variable rate: {@link #variableRate(LongUnaryOperator)}
 * 
 * @author A248
 *
 */
public final class DelayCalculators {
	
	// Prevent instantiation
	private DelayCalculators() {}
	
	/**
	 * Schedules at a constant delay <i>between</i> executions.
	 * 
	 * @return a delay function which yields the same delay
	 */
	public static DelayCalculator fixedDelay() {
		return (d, ignore) -> d;
	}
	
	/**
	 * Schedules at a fixed rate of delay, meaning execution initiates at constant intervals. <br>
	 * <br>
	 * Attempts to compensate for execution time by subtracting it from the next interval,
	 * in order to achieve a fixed rate of scheduling provided execution time is usually less than the rate. <br>
	 * <br>
	 * For example, if the fixed rate is 4 seconds, an execution timeline might be as follows: <br>
	 * {@literal |--- d ----|} |<i>E</i>| {@literal |- d -|} |{@literal --}<i>E</i>{@literal --}| {@literal |- d -|} <br>
	 * {@literal |- 4 secs -|} {@literal | 4 secs |} {@literal |-- 4 secs --|} <br>
	 * where <i>E</i> represents an execution and <i>d</i> is a period of delay. <br>
	 * <br>
	 * Should execution time take longer than the fixed rate of delay, the function will: <br>
	 * 1. Return a zero (nonexistent) delay to immediately commence the next execution.
	 * 2. Temporarily accelerate future executions in order to "correct" any waverings from the fixed rate schedule.
	 * Thus, if a single execution takes a long time, it will not permanently ruin the rate
	 * 
	 * @return a delay function which yields the same rate of delay
	 */
	public static DelayCalculator fixedRate() {
		return variableRate(LongUnaryOperator.identity());
	}
	
	/**
	 * A more complicated version of {@link #fixedRate()}. Execution initiates at intervals
	 * determined by the rate function, which acts on time units in nanoseconds. <br>
	 * <br>
	 * Attempts to compensate for execution time in calculating the next delay.
	 * The goal is to achieve the rate of scheduling determined by the latest application of the rate function. <br>
	 * <br>
	 * Should execution time take longer than rate calculated, the function will: <br>
	 * 1. Return a zero (nonexistent) delay to immediately commence the next execution.
	 * 2. Temporarily accelerate future executions in order to "correct" any waverings from the rates desired.
	 * Thus, if a single execution takes a long time, it will not permanently ruin the rate. <br>
	 * <br>
	 * The rate function takes the last rate, in nanoseconds, and calculates the next rate, also in
	 * nanoseconds.
	 * 
	 * @param rateFunction the rate determining function
	 * @return a delay function which yields a rate of delay as specified by the rate function
	 */
	public static DelayCalculator variableRate(LongUnaryOperator rateFunction) {
		return new VariableRateFunction(rateFunction);
	}
	
	/**
	 * Implementation of a variable rate delay function,
	 * which compensates for execution time in determing the next delay
	 * and attempts to "catch up" if it falls behind.
	 * 
	 * @author A248
	 *
	 */
	private static class VariableRateFunction implements DelayCalculator {
		
		/**
		 * The rate determining function
		 * 
		 */
		private final LongUnaryOperator rateFunction;
		
		/**
		 * The rate we want to schedule at, starts at {@literal -}1 to indicate unknown.
		 * 
		 */
		private volatile long rate = -1L;
		
		/**
		 * If we're behind, by how much are we late?
		 * Always a positive number
		 * 
		 */
		private volatile long offset = 0;
		
		VariableRateFunction(LongUnaryOperator rateFunction) {
			this.rateFunction = rateFunction;
		}
		
		@Override
		public long calculateNextDelay(long previousDelayNanos, long executionTimeNanos) {
			long rate = this.rate;
			if (rate == -1L) {
				// If this is the first invocation, determine the initial rate
				rate = previousDelayNanos;
			}
			rate = rateFunction.applyAsLong(rate);
			if (rate < 0) {
				// rate-determining function decided to cancel
				return -1L;
			}
			this.rate = rate;

			long offset = this.offset;
			long delay = rate - executionTimeNanos - offset;
			if (delay >= 0L) {
				offset = 0L;
			} else {
				offset = -delay;
				delay = 0L;
			}
			this.offset = offset;
			return delay;
		}
		
		@Override
		public boolean requiresExecutionTime() {
			return true;
		}

	}
	
}
