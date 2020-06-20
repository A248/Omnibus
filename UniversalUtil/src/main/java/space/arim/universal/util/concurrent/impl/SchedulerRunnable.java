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

class SchedulerRunnable implements Runnable {

	private final SelfSchedulingEnhancedExecutor executor;
	volatile ScheduledTask currentAwaitee;
	
	SchedulerRunnable(SelfSchedulingEnhancedExecutor executor) {
		this.executor = executor;
	}

	@Override
	public void run() {
		try {
			synchronized (this) {
				mainLoop:
				while (executor.isRunning) {
					ScheduledTask task;
					while ((task = executor.taskQueue.poll()) == null) {
						wait();
						if (!executor.isRunning) {
							// shutdown
							break mainLoop;
						}
					}
					long startTime = System.nanoTime();
					long timeToWait = task.runTime - startTime;
					if (timeToWait > 0) {

						wait(timeToWait / 1_000_000L, (int) (timeToWait % 1_000_000L));
						if (!executor.isRunning) {
							// shutdown
							break mainLoop;
						}

						if (!task.isCancelled()) {
							if (System.nanoTime() - startTime >= timeToWait) {
								task.run();
							} else {
								executor.taskQueue.offer(task);
							}
						}
					} else {
						task.run();
					}
				}
			}
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
	
}
