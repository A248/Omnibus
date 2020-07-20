/* 
 * Universal-util
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-util. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.util.concurrent.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

class DelayedScheduledWork<T> extends RunnableScheduledWork<T> {

	private final Executor executor;
	private final long runTime;
	private final Supplier<T> supplier;
	
	private final CompletableFuture<T> future = new CompletableFuture<>();
	
	DelayedScheduledWork(Executor executor, long delay, Supplier<T> supplier) {
		this.executor = executor;
		runTime = System.nanoTime() + delay;
		this.supplier = supplier;
	}
	
	@Override
	CompletableFuture<T> getCompletableFuture() {
		return future;
	}

	@Override
	long getRunTime() {
		return runTime;
	}

	@Override
	public void run() {
		executor.execute(() -> {
			if (isCancelled()) {
				// Cancellation possible as execution has not yet commenced
				return;
			}
			try {
				future.complete(supplier.get());
			} catch (Throwable ex) {
				future.completeExceptionally(ex);
			}
		});
	}
	
	@Override
	public boolean isCancelled() {
		return getCompletableFuture().isCancelled();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return getCompletableFuture().cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isDone() {
		return getCompletableFuture().isDone();
	}

	@Override
	public boolean isRepeating() {
		return false;
	}
	
}
