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
package space.arim.universal.util;

/**
 * Simple utility to get one's own literal class, including in a static context. <br>
 * <br>
 * The most common use case is where one requires a statically constructed {@code Logger}: <br>
 * <code>private static final Logger logger = LoggerFactory.getLogger(LoggingClass.class);</code> <br>
 * <br>
 * where <i>LoggingClass</i> is the class in which the logger is placed. This approach is prone to particular
 * error due to the class literal passed to <code>LoggerFactory.getLogger</code>. If improperly copied
 * and pasted, the wrong logger might be retrieved. <br>
 * <br>
 * The solution involves using this utility class to determine the class: <br>
 * <code>private static final Logger logger = LoggerFactory.getLogger(ThisClass.get());</code> <br>
 * <br>
 * The improved code uses <code>ThisClass.get()</code> instead of the class literal, and thus may be copied
 * and pasted at will with no fear of error. <br>
 * <br>
 * Moreover, <code>ThisClass.get()</code> is more readable than the alternative <code>MethodHandles.lookup().lookupClass()</code>.
 * 
 * @author A248
 *
 */
public final class ThisClass {

	private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
	
	private ThisClass() {}
	
	/**
	 * Gets the literal class which called this method
	 * 
	 * @return the literal class in which this method is called
	 */
	public static Class<?> get() {
		return WALKER.getCallerClass();
	}
	
}
