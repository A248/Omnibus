/*
 * Omnibus
 * Copyright © 2021 Anand Beh
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

package space.arim.omnibus.defaultimpl.registry;

import space.arim.omnibus.util.ArraysUtil;

import java.util.concurrent.CountDownLatch;

public class ParallelStresser {

	public void runAll(RunInstruction first, RunInstruction...extra) {
		try {
			runAll0(ArraysUtil.expandAndInsert(extra, first, 0));
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(ex);
		}
	}

	private void runAll0(RunInstruction[] instructions) throws InterruptedException {
		CountDownLatch initLatch = new CountDownLatch(instructions.length);
		CountDownLatch startLatch = new CountDownLatch(1);

		Thread[] threads = new Thread[instructions.length];
		for (int n = 0; n < instructions.length; n++) {
			threads[n] = new Thread(instructions[n].toRunnable(initLatch, startLatch));
		}
		// On your marks
		for (Thread thread : threads) {
			thread.start();
		}
		// Get set
		initLatch.await();
		// Go!
		startLatch.countDown();

		for (Thread thread : threads) {
			thread.join();
		}
	}

}
