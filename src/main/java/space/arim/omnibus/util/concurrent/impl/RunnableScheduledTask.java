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
package space.arim.omnibus.util.concurrent.impl;

import java.util.concurrent.atomic.AtomicInteger;

abstract class RunnableScheduledTask extends AbstractScheduledTask implements Runnable {

	private final AtomicInteger state = new AtomicInteger();
	
	/*
	 * The multiple states this task may be in.
	 * 
	 * Delayed tasks start scheduled, and can be cancelled before they begin.
	 * Once in progress, they cannot be cancelled.
	 * 
	 * Repeating tasks are always considered scheduled until they are cancelled.
	 * Therefore IN_PROGRESS and DONE are not used for repeating tasks.
	 */
	
	private static final int SCHEDULED = 0;
	private static final int IN_PROGRESS = 1;
	private static final int CANCELLED = 2;
	private static final int DONE = 3;

	@Override
	public boolean cancel() {
		return state.compareAndSet(SCHEDULED, CANCELLED);
	}

	@Override
	public boolean isCancelled() {
		return state.get() == CANCELLED;
	}

	@Override
	public boolean isDone() {
		int state = this.state.get();
		return state == DONE || state == CANCELLED;
	}
	
	/*
	 * Called by delayed tasks
	 */
	
	boolean start() {
		return state.compareAndSet(SCHEDULED, IN_PROGRESS);
	}
	
	void finish() {
		assert state.get() == IN_PROGRESS;
		state.set(DONE);
	}
	
}
