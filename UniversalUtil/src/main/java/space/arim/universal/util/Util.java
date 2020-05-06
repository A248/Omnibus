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
 * A framework for concurrency operations. <br>
 * <b>For an implementation, use {@link UniversalUtil}</b> <br>
 * <br>
 * <b>Usage</b>: to check synchronisation with {@link #isAsynchronous()}
 * 
 * @author A248
 *
 */
public interface Util {

	/**
	 * Returns whether program is running asynchronously. <br>
	 * <br>
	 * Note that the "main thread" is often taken to mean the thread on which the instance is initialised. <br>
	 * It is thus recommended to fetch your own instance (e.g., through {@link UniversalUtil#getByClass(Class)}) if you plan on using this method. <br>
	 * <br>
	 * <code>UniversalUtil myUtil = UniversalUtil.getByClass(MyClass.class);</code> <br>
	 * <b>By making your own instance on the main thread, you guarantee the validity of this method return</b> <br>
	 * <br>
	 * Alternatively, be sure to warn API users against bad calls.
	 * 
	 * @return true if and only if asynchronous
	 */
	boolean isAsynchronous();
	
}
