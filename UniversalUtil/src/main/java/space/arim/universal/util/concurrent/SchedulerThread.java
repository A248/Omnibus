/* 
 * UniversalUtil, simple utilities for Spigot and BungeeCord
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
package space.arim.universal.util.concurrent;

import java.util.concurrent.PriorityBlockingQueue;

class SchedulerThread extends Thread {
	
	private static volatile SchedulerThread inst;
	
	private final PriorityBlockingQueue<ScheduledAction> queue = new PriorityBlockingQueue<ScheduledAction>();
	
	static SchedulerThread get() {
		if (inst == null) {
			synchronized (inst) {
				if (inst == null) {
					inst = new SchedulerThread();
					inst.start();
				}
			}
		}
		return inst;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				ScheduledAction task = queue.take();
				boolean run = false;
				while (!run) {
					long startTime = System.currentTimeMillis();
					long fullWait = task.getWaitTime(startTime);
					if (fullWait > 0) {
						synchronized (this) {
							wait(fullWait);
						}
						if (System.currentTimeMillis() - startTime >= fullWait) {
							task.run();
							run = true;
						} else {
							queue.offer(task);
							task = queue.take();
						}
					} else {
						task.run();
						run = true;
					}
				}
			} catch (InterruptedException ex) {}
		}
	}
	
	void addTask(ScheduledAction task) {
		queue.offer(task);
		synchronized (this) {
			notify();
		}
	}
	
}
