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
 * A workaround for overriding {@link Object#toString()} in interfaces. <br>
 * <br>
 * <b>Usage:</b> <br>
 * Programmers should let an interface extend Stringable, then create a default implementation of {@link #toStringMe()} in the interface. <br>
 * Then, classes implementing that interface may use a simple <code>toString()</code> override, as follows: <br>
 * <code>
 * public String toString() {
 *   return toStringMe();
 * }
 * </code>
 * 
 * @author A248
 *
 * @deprecated Promotes subpar API design. Interfaces should not be used this way. The purpose of an interface is to
 * describe capabilities, including among disparate classes. <code>toString()</code> implementations, on the other hand
 * are strictly specific to the concrete class (or in some cases an abstract class, but never an interface).
 */
@Deprecated
public interface Stringable {

	/**
	 * Should be equivalent to {@link Object#toString()}
	 * 
	 * @return the string representation
	 */
	String toStringMe();
	
}
