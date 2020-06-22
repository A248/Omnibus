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

import java.util.function.LongUnaryOperator;

/**
 * A basic functional interface which calculates the next delay of a repeating task
 * based on the previous delay. <br>
 * Since this is similar in type arguments to LongUnaryOperator, {@link #decorate(LongUnaryOperator)}
 * may be used for conversion.
 * 
 * @author A248
 *
 */
public interface SimpleDelayCalculator {

	/**
	 * Calculates the next delay
	 * 
	 * @param previousDelay the previous delay
	 * @return the next delay
	 */
	long calculateNext(long previousDelay);
	
	/**
	 * Converts a LongUnaryOperator to a delay calculator
	 * 
	 * @param rawFunction the long unary operator
	 * @return the operator as a delay calculator
	 */
	static SimpleDelayCalculator decorate(LongUnaryOperator rawFunction) {
		return rawFunction::applyAsLong;
	}
	
}
