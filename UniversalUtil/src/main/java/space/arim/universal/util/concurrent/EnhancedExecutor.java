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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import space.arim.universal.util.proxy.CompetitiveFuture;

/**
 * A {@link Executor} with upgraded concurrent functionality. Differs from {@link java.util.concurrent.ExecutorService ExecutorService} in that it contains
 * no methods for thread pool management (such as shutdown, await termination, etc.) but focuses specifically on concurrent execution. <br>
 * <br>
 * <b>Specifications:</b> <br>
 * * Requires {@link Executor#execute(Runnable)} <br>
 * * EnhancedExecutor provides default implementations for all of its own specifications. <br>
 * <br>
 * Adds {@link #submit(Runnable)} and {@link #submit(Supplier)}.
 * 
 * @author A248
 *
 */
public interface EnhancedExecutor extends Executor {

	/**
	 * Execute an asynchronous action. <br>
	 * Differs from {@link #execute} in that the returned {@link CompletableFuture} provides additional functionality,
	 * including the ability to listen for completion or cancel the task.
	 * 
	 * @param command the {@link Runnable} to run
	 * @return a CompletableFuture which will return <code>null</code> on {@link Future#get()}
	 */
	default CompletableFuture<Void> submit(Runnable command) {
		return CompetitiveFuture.of(CompletableFuture.runAsync(command, this), this);
	}
	
	/**
	 * Supplies a value asynchronously.
	 * 
	 * Similar to {@link java.util.concurrent.ExecutorService#submit(java.util.concurrent.Callable) ExecutorService.submit(Callable)}. <br>
	 * However, Callable may throw an exception, while Supplier does not.
	 * 
	 * @param <T> the type of the supplier
	 * @param supplier the supplier
	 * @return a future
	 */
	default <T> CompletableFuture<T> submit(Supplier<T> supplier) {
		return CompetitiveFuture.of(CompletableFuture.supplyAsync(supplier, this), this);
	}
	
	/**
	 * Converts a basic <code>Executor</code> to an EnhancedExecutor
	 * 
	 * @param executor the executor
	 * @return a complete EnhancedExecutor
	 */
	static EnhancedExecutor decorate(Executor executor) {
		return executor::execute;
	}
	
}
