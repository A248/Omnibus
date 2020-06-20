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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import space.arim.universal.util.concurrent.EnhancedExecutor;
import space.arim.universal.util.concurrent.Task;

/**
 * Base class for {@link EnhancedExecutor}, with common-sense implementations for a couple methods.
 * 
 * @author A248
 *
 */
public abstract class AbstractEnhancedExecutor implements EnhancedExecutor {
	
	@Override
	public CompletableFuture<?> submit(Runnable command) {
		return CompletableFuture.runAsync(command, this);
	}

	@Override
	public <T> CompletableFuture<T> supply(Supplier<T> supplier) {
		return CompletableFuture.supplyAsync(supplier, this);
	}
	
	@Override
	public <T> CompletableFuture<T> supplyLater(Supplier<T> supplier, long delay, TimeUnit units) {
		CompletableFutureWithTask<T> future = new CompletableFutureWithTask<>();
		future.task = schedule(() -> {
			if (!future.isDone()) {
				future.completeAsync(supplier, this);
			}
		}, delay, units);
		return future;
	}

}

class CompletableFutureWithTask<T> extends CompletableFuture<T> {
	
	volatile Task task;
	
	@Override
	public <U> CompletableFuture<U> newIncompleteFuture() {
		CompletableFutureWithTask<U> future = new CompletableFutureWithTask<>();
		future.task = task;
		return future;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		task.cancel();
		return super.cancel(mayInterruptIfRunning);
	}
	
	@Override
	public boolean isCancelled() {
		boolean result = task.isCancelled();
		assert result == super.isCancelled() : this;
		return result;
	}

	@Override
	public String toString() {
		return "CompletableFutureWithTask [task=" + task + ", toString()=" + super.toString() + "]";
	}
	
}
