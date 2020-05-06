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
package space.arim.universal.util.concurrent;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

/**
 * A CompletableFuture with a specified default executor,
 * as opposed to <code>ForkJoinPool.commonPool()</code>
 * 
 * @author A248
 *
 * @param <T> the type of the future
 */
public class HyperFuture<T> extends CompletableFuture<T> {

	private final Executor defaultExecutor;
	
	private HyperFuture(Executor defaultExecutor) {
		this.defaultExecutor = (defaultExecutor != null) ? defaultExecutor : ForkJoinPool.commonPool();
	}
	
	@Override
	public <U> CompletableFuture<U> newIncompleteFuture() {
		return new HyperFuture<>(defaultExecutor);
	}
	
	@Override
	public Executor defaultExecutor() {
		return defaultExecutor;
	}
	
	/**
	 * Runs an action asynchronously. See {@link CompletableFuture#runAsync(Runnable)}. <br>
	 * If the specified default executor is null, <code>ForkJoinPool.commonPool()</code> is used.
	 * 
	 * @param command what to do
	 * @param defaultExecutor the default executor, used for async methods not specifying an executor
	 * @return a completable future corresponding to the progress of the action
	 */
	public static HyperFuture<Void> runAsync(Runnable command, Executor defaultExecutor) {
		Objects.requireNonNull(command, "Runnable must not be null");
		return supplyAsync(() -> {
			command.run();
			return null;
		}, defaultExecutor);
	}
	
	/**
	 * Supplies a value asynchronously. See {@link CompletableFuture#supplyAsync(Supplier)}. <br>
	 * If the specified default executor is null, <code>ForkJoinPool.commonPool()</code> is used.
	 * 
	 * @param <T> the type of the future
	 * @param supplier the supplier from which to get the value
	 * @param defaultExecutor the default executor, used for async methods not specifying an executor
	 * @return a completable future corresponding to the progress of the action
	 */
	public static <T> HyperFuture<T> supplyAsync(Supplier<T> supplier, Executor defaultExecutor) {
		return (HyperFuture<T>) new HyperFuture<T>(defaultExecutor).completeAsync(supplier);
	}
	
}
