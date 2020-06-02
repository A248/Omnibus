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

import java.util.function.LongBinaryOperator;

/**
 * A slightly more advanced delay calculator which calculates the next delay
 * based on both the previous delay and the execution time. <br>
 * Since this is similar in type arguments to LongBinaryOperator, {@link #decorate(LongBinaryOperator)}
 * may be used for conversion.
 * 
 * @author A248
 *
 */
@FunctionalInterface
public interface AdvancedDelayCalculator {

	/**
	 * Calculates the next delay
	 * 
	 * @param previousDelay the previous delay
	 * @param executionTime how long it took the task to previously execute
	 * @return the next delay
	 */
	long calculateNext(long previousDelay, long executionTime);
	
	/**
	 * Converts a LongBinaryOperator to a delay calculator
	 * 
	 * @param rawFunction the long binary operator
	 * @return the operator as a delay calculator
	 */
	static AdvancedDelayCalculator decorate(LongBinaryOperator rawFunction) {
		return rawFunction::applyAsLong;
	}
	
}
