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
package space.arim.omnibus.util.concurrent.impl;

import java.util.concurrent.CompletableFuture;

public class AlreadyCancelledWork<T> extends AbstractScheduledWork<T> {

	private final boolean repeating;
	private final long runTime;
	
	private static final CompletableFuture<?> CANCELLED_FUTURE;
	
	static {
		CompletableFuture<?> cancelledFuture = new CompletableFuture<>();
		cancelledFuture.cancel(false);
		CANCELLED_FUTURE = cancelledFuture;
	}
	
	AlreadyCancelledWork(boolean repeating, long nanosDelay) {
		this.repeating = repeating;
		runTime = System.nanoTime() + nanosDelay;
	}

	@Override
	public boolean cancel(boolean ignoreParameter) {
		return true;
	}

	@Override
	public boolean isCancelled() {
		return true;
	}

	@Override
	public boolean isRepeating() {
		return repeating;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	long getRunTime() {
		return runTime;
	}

	@SuppressWarnings("unchecked")
	@Override
	CompletableFuture<T> getCompletableFuture() {
		// Safe cast because the result will never exist
		return (CompletableFuture<T>) CANCELLED_FUTURE;
	}
	
}
