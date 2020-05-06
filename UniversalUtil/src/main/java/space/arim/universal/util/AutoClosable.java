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
 * A subinterface of {@link AutoCloseable}. Advantages: See {@link #close()}
 * 
 * @author A248
 *
 */
public interface AutoClosable extends AutoCloseable {

	/**
	 * Replaces {@link AutoCloseable#close()}. <br>
	 * <br>
	 * <b>Differences</b>: <br>
	 * * Does not throw <code>Exception</code>, so implementations are forced to conduct their own exception handling. <br>
	 * * Has a default implementation, which is an empty method block. This prevents an explosion of empty <code>close</code> declarations in implementing objects.
	 * 
	 */
	@Override
	default void close() {
		
		// do nothing by default
		
	}
	
}
