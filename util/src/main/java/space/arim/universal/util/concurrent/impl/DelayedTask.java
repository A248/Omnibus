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

import java.util.concurrent.atomic.AtomicBoolean;

class DelayedTask extends ScheduledTask {

	private final AtomicBoolean cancelled;
	
	DelayedTask(SelfSchedulingEnhancedExecutor executor, Runnable command, long initialDelay) {
		super(executor, command);
		cancelled = new AtomicBoolean(false);
		runTime = System.nanoTime() + initialDelay;
	}
	
	@Override
	public void run() {
		executor.execute(command);
	}

	@Override
	public void cancel() {
		if (cancelled.compareAndSet(false, true)) {
			executor.taskQueue.remove(this);
		}
	}

	@Override
	public boolean isCancelled() {
		return cancelled.get();
	}

}
