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
package space.arim.universal.util.concurrent.impl;

import space.arim.universal.util.concurrent.Task;

/**
 * Main implementation of Task, placed into and withdrawn from the task queue as necessary
 * 
 * @author A248
 *
 */
abstract class ScheduledTask implements Comparable<ScheduledTask>, Runnable, Task {

	volatile long runTime;
	
	final SelfSchedulingEnhancedExecutor executor;
	final Runnable command;
	
	ScheduledTask(SelfSchedulingEnhancedExecutor executor, Runnable command) {
		this.executor = executor;
		this.command = command;
	}
	
	@Override
	public int compareTo(ScheduledTask o) {
		// Need to prevent overflow
		long diff = runTime - o.runTime;
		return (diff > 0) ? 1 : (diff < 0) ? -1 : 0;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object object) {
		return this == object || object instanceof TaskWrapper && this == ((TaskWrapper) object).task;
	}
	
}
