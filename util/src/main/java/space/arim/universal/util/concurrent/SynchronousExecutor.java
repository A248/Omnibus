/* 
 * ArimAPI-util
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * ArimAPI-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArimAPI-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArimAPI-util. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.util.concurrent;

import java.util.concurrent.Executor;

/**
 * Similar to {@link Executor}, in that it accepts and runs {@link Runnable}s. However,
 * instead of executing in an arbitrary thread, the {@code Runnable} must run on the
 * application's main thread if it has a main thread. <br>
 * <br>
 * According to the concept of a <i>main thread</i>, an application has a main thread if
 * it has a single thread which is responsible for the majority of work done, in terms of
 * necessary CPU resources. <br>
 * <br>
 * For applications without a main thread, this interface, if used at all, should
 * behave as if scheduling computational work deemed suitable for <code>ForkJoinPool.commonPool()</code>.
 * 
 * @author A248
 *
 */
public interface SynchronousExecutor {

	/**
	 * Runs the Runnable on the main thread, if the application has a main thread,
	 * else executes it in a thread pool suitable for computational work such as
	 * <code>ForkJoinPool.commonPool()</code>
	 * 
	 * @param command the runnable to run synchronously with the main thread
	 */
	void executeSync(Runnable command);
	
}
