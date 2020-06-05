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

import java.util.concurrent.atomic.AtomicLong;

abstract class CalculatedTask extends ScheduledTask {

	final AtomicLong currentDelay;
	
	CalculatedTask(SelfSchedulingEnhancedExecutor executor, Runnable command, long initialDelay) {
		super(executor, command);
		currentDelay = new AtomicLong(initialDelay);
		runTime = System.nanoTime() + initialDelay;
	}

	@Override
	public void cancel() {
		if (currentDelay.getAndSet(-1L) != -1L) {
			executor.taskQueue.remove(this);
		}
	}

	@Override
	public boolean isCancelled() {
		return currentDelay.get() == -1L;
	}

}
