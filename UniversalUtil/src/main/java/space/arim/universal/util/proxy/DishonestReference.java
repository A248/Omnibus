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
package space.arim.universal.util.proxy;

import java.util.function.UnaryOperator;

/**
 * A {@link CaptiveReference} which does nothing other than change the result of calls to {@link #getValue()}.
 * 
 * @author A248
 *
 * @param <T> the type of the proxied object
 */
public class DishonestReference<T> extends CaptiveReference<T> {

	private final UnaryOperator<T> processor;
	
	/**
	 * Instantiates with the given proxied object and a processor, which will be applied when {@link #getValue()} is called.
	 * 
	 * @param value the proxied object
	 * @param processor the operator function
	 */
	protected DishonestReference(T value, UnaryOperator<T> processor) {
		super(value);
		this.processor = processor;
	}
	
	/**
	 * Takes the proxied object and runs it through the processor supplied at construction.
	 * 
	 * @return the processed result
	 */
	@Override
	protected T getValue() {
		return processor.apply(super.getValue());
	}

}
