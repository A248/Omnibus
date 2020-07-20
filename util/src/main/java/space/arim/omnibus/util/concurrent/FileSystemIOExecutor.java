/* 
 * Omnibus-util
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-util. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.util.concurrent;

import java.util.concurrent.Executor;

/**
 * An executor whose details are tuned for specialising in handling work related to filesystem IO. <br>
 * <Br>
 * Such executors typically manage more threads than a Fork/Join pool but less than a
 * {@link NetworkIOExecutor}. <br>
 * <br>
 * This interface is intended as a marker for filesystem IO executors, e.g. as a service in some kind
 * of registry.
 * 
 * @author A248
 *
 */
public interface FileSystemIOExecutor extends Executor {

	/**
	 * Executes an operation involving considerable filesystem IO
	 * 
	 */
	@Override
	void execute(Runnable command);
	
}
