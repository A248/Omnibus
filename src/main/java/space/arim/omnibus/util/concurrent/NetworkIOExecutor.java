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

import java.util.concurrent.Executor;

/**
 * An executor whose details are tuned for specialising in handling work related to networking IO. <br>
 * <br>
 * Usually, implementations are thread pools handling a large number of threads, certainly more than
 * the amount of cores available, since the threads would presumably spend considerable time on blocking calls. <br>
 * A good rule of thumb is that the amount of threads be at most ten times the core count. <br>
 * <br>
 *  This interface is intended as a marker for networking IO executors, e.g. as a service in some kind
 * of registry.
 * 
 * @author A248
 *
 */
public interface NetworkIOExecutor extends Executor {

	/**
	 * Executes an operation involving considerable networking IO.
	 *
	 * @param command the command
	 */
	@Override
	void execute(Runnable command);
	
}
