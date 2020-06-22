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

import space.arim.universal.util.concurrent.SimpleDelayCalculator;

class SimpleCalculatedTask extends CalculatedTask {
	
	private final SimpleDelayCalculator calculator;
	
	SimpleCalculatedTask(SelfSchedulingEnhancedExecutor executor, Runnable command, long initialDelay, SimpleDelayCalculator calculator) {
		super(executor, command, initialDelay);
		this.calculator = calculator;
	}

	@Override
	public void run() {
		executor.execute(() -> {
			try {
				command.run();
			} finally {
				long delay;
				long calculated;
				do {
					delay = currentDelay.get();
					if (delay == -1L) {
						// Cancelled
						return;
					}
					calculated = calculator.calculateNext(delay);
					if (calculated < 0L) {
						// Cancelled by calculator
						currentDelay.set(-1L);
						return;
					}
				} while (!currentDelay.compareAndSet(delay, calculated));
				runTime = System.nanoTime() + delay;
				executor.addTaskToQueue(this);
			}
		});
	}

}
