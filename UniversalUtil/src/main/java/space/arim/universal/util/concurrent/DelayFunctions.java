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

import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;

/**
 * Provides default implementations for {@link EnhancedExecutor}'s scheduling methods' delay function parameters. <br>
 * Recall that <code>Binary</code> operators take into account execution time, while <code>Unary</code> operators do not. <br>
 * <br>
 * Fixed delay: {@link #fixedDelay()} <br>
 * Fixed rate: {@link #fixedRate()} <br>
 * Linear delay: {@link #linear(long)} <br>
 * Exponential delay: {@link #exponential(long)} <br>
 * Logarithmic delay: {@link #logarithmic(long)}
 * 
 * @author A248
 *
 */
public final class DelayFunctions {

	// Prevent instantiation
	private DelayFunctions() {}
	
	/**
	 * Schedules at a constant delay <i>between</i> executions.
	 * 
	 * @return a LongUnaryOperator which yields the same delay
	 */
	public static LongUnaryOperator fixedDelay() {
		return LongUnaryOperator.identity();
	}
	
	/**
	 * Schedules at a fixed rate of delay, meaning execution initiates at constant intervals. <br>
	 * Attempts to ignore execution time by subtracting it from the next interval;
	 * the result is a fixed rate of scheduling provided execution time is not longer than the rate. <br>
	 * <br>
	 * For example, if the fixed rate is 4 seconds, an execution timeline might be as follows: <br>
	 * {@literal |--- d ----|} |<i>E</i>| {@literal |- d -|} |{@literal --}<i>E</i>{@literal --}| {@literal |- d -|} <br>
	 * {@literal |- 4 secs -|} {@literal | 4 secs |} {@literal |-- 4 secs --|} <br>
	 * where <i>E</i> represents an execution and <i>d</i> is a period of delay. <br>
	 * <br>
	 * Should execution time take longer than the fixed rate of delay, the function will: <br>
	 * 1. Return a zero (nonexistent) delay to immediately commence the next execution.
	 * 2. Temporarily accelerate future executions in order to "correct" any waverings from the fixed rate schedule.
	 * Thus, if a single execution takes a long time, it will not ruin the rate
	 * 
	 * @return a LongBinaryOperator which yields the same rate of delay
	 */
	public static LongBinaryOperator fixedRate() {
		return new LongBinaryOperator() {
			
			private long fixedRate = -1L;
			private long offset = 0;
			
			@Override
			public long applyAsLong(long previousDelay, long executionTime) {
				if (fixedRate == -1L) { // first invocation, we have to determine the fixed rate
					fixedRate = previousDelay;
				}
				long delay = fixedRate - executionTime;
				if (delay > 0) {
					if (offset == 0) { // all is well. We're up to speed
						return delay;
					} else if (delay >= offset) { // recovering from a slowdown
						offset = 0;
						return delay - offset;
					}
					offset = offset - delay; // we're recovering but it's going to take more progress next cycle.
					return 0;
				} else if (delay == 0) {
					return 0;
				}
				offset = offset - delay; // we just fell behind
				return 0;
			}
			
		};
	}
	
	/**
	 * A linearly increasing delay. <br>
	 * The delay is incremented each invocation by the slope. <br>
	 * The function may be made decreasing by specifying a negative slope. <br>
	 * 
	 * @param slope the amount to add each invocation
	 * @return a linear delay function
	 */
	public static LongUnaryOperator linear(long slope) {
		return (previousDelay) -> previousDelay + slope;
	}
	
	/**
	 * An exponentially increasing delay. <br>
	 * The delay is multiplied each invocation by the base. <br>
	 * The function may be made decreasing by specifying a negative base. <br>
	 * 
	 * @param base the amount by which to multiply each invocation
	 * @return an exponential delay function
	 */
	public static LongUnaryOperator exponential(long base) {
		return (previousDelay) -> previousDelay*base;
	}
	
	/**
	 * A logarithmically increasing delay, computed as follows: <br>
	 * Let <i>x</i> be the previous delay and <i>b</i> be the specified base.
	 * The result is <code>ln(b^x+1)/lnb</code>. <br>
	 * 
	 * @param base the logarithmic base
	 * @return a logarithmic delay function
	 */
	public static LongUnaryOperator logarithmic(long base) {
		return (previousDelay) -> (long) (Math.log(base^previousDelay + 1)/Math.log(base));
	}
	
}
