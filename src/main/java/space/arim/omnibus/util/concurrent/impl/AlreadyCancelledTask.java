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

class AlreadyCancelledTask extends AbstractScheduledTask {

	private final long runTime;
	private final boolean repeating;
	
	AlreadyCancelledTask(long nanosDelay, boolean repeating) {
		runTime = System.nanoTime() + nanosDelay;
		this.repeating = repeating;
	}

	@Override
	public boolean cancel() {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return true;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	long getRunTime() {
		return runTime;
	}

	@Override
	public boolean isRepeating() {
		return repeating;
	}
	
}

