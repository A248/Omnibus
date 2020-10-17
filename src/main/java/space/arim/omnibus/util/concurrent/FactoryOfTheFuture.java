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
package space.arim.omnibus.util.concurrent;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Concurrent executor and producer of {@link CentralisedFuture}. Intended
 * to manage both asynchronous execution and synchronisation to the main thread.
 * 
 * @author A248
 *
 */
public interface FactoryOfTheFuture extends Executor, SynchronousExecutor {

	/**
	 * Executes a {@link Runnable} asynchronously.
	 *
	 * @param command the command
	 */
	@Override
	void execute(Runnable command);
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	void executeSync(Runnable command);
	
	/**
	 * Runs a {@link Runnable} asynchronously and returns a future indicating its progress.
	 * 
	 * @param command the runnable to run
	 * @return a centralised future representing the runnable's progress
	 * @throws NullPointerException if {@code command} is null
	 */
	CentralisedFuture<?> runAsync(Runnable command);
	
	/**
	 * Runs a {@link Runnable} asynchronously using the provided {@link Executor} and returns
	 * a future indicating its progress.
	 * 
	 * @param command the runnable to run
	 * @param executor the executor on which to run the runnable
	 * @return a centralised future representing the runnable's progress
	 * @throws NullPointerException if {@code command} or {@code executor} is null
	 */
	CentralisedFuture<?> runAsync(Runnable command, Executor executor);
	
	/**
	 * Runs a {@link Runnable} on the main thread if a main thread exists, else appropriately,
	 * per the specifications of {@link SynchronousExecutor#executeSync(Runnable)}.
	 * 
	 * @param command the runnable to run
	 * @return a centralised future representing the runnable's progress
	 * @throws NullPointerException if {@code command} is null
	 */
	CentralisedFuture<?> runSync(Runnable command);
	
	/**
	 * Supplies a value asynchronously and returns a future indicating its progress.
	 * 
	 * @param <T> the return type of the supplier
	 * @param supplier the supplier to run
	 * @return a centralised future representing the supplier's progress
	 * @throws NullPointerException if {@code supplier} is null
	 */
	<T> CentralisedFuture<T> supplyAsync(Supplier<T> supplier);
	
	/**
	 * Supplies a value asynchronously using the provided {@link Executor} and returns
	 * a future indicating its progress.
	 * 
	 * @param <T> the return type of the supplier
	 * @param supplier the supplier to run
	 * @param executor the executor on which to run the supplier
	 * @return a centralised future representing the supplier's progress
	 * @throws NullPointerException if {@code supplier} or {@code executor} is null
	 */
	<T> CentralisedFuture<T> supplyAsync(Supplier<T> supplier, Executor executor);
	
	/**
	 * Supplies a value on the main thread if a main thread exists, else appropriately,
	 * per the specifications of {@link SynchronousExecutor#executeSync(Runnable)}.
	 *
	 * @param <T> the return type of the supplier
	 * @param supplier the supplier to run
	 * @return a centralised future representing the supplier's progress
	 * @throws NullPointerException if {@code supplier} is null
	 */
	<T> CentralisedFuture<T> supplySync(Supplier<T> supplier);
	
	/**
	 * Creates a precompleted future which has completed normally with the given value.
	 * 
	 * @param <T> the result type of the future
	 * @param value the value with which to complete the future
	 * @return a future completed normally with the given value
	 */
	<T> CentralisedFuture<T> completedFuture(T value);
	
	/**
	 * Creates a precompleted stage which has completed normally with the given value.
	 * 
	 * @param <T> the result type of the stage
	 * @param value the value with which to complete the stage
	 * @return a stage completed normally with the given value
	 */
	<T> ReactionStage<T> completedStage(T value);
	
	/**
	 * Creates a precompleted future which has completed exceptionally with the given exception.
	 * 
	 * @param <T> the result type of the future
	 * @param ex the exception with which to complete the future
	 * @return a future completeld exceptionally with the given exception
	 */
	<T> CentralisedFuture<T> failedFuture(Throwable ex);
	
	/**
	 * Creates a precompleted stage which has completed exceptionally with the given exception.
	 * 
	 * @param <T> the result type of the stage
	 * @param ex the exception with which to complete the stage
	 * @return a stage completeld exceptionally with the given exception
	 */
	<T> ReactionStage<T> failedStage(Throwable ex);
	
	/**
	 * Creates an incomplete future.
	 *
	 * @param <T> the result type of the future
	 * @return an incomplete future
	 */
	<T> CentralisedFuture<T> newIncompleteFuture();
	
	/**
	 * Copies a {@code CompletableFuture} to a {@code CentralisedFuture}. <br>
	 * When the completable future completes, if it does so normally, the centralised future
	 * is also completed normally with the same result. Else, if the former completes exceptionally,
	 * the latter is also completed exceptionally with the same exception.
	 * 
	 * @param <T> the result type of the future
	 * @param completableFuture the completable future
	 * @return a centralised future completed in the same way as the original completable future
	 */
	<T> CentralisedFuture<T> copyFuture(CompletableFuture<T> completableFuture);
	
	/**
	 * Copies a {@code CompletionStage} to a {@code ReactionStage}. <br>
	 * When the completable future completes, if it does so normally, the reaction stage
	 * is also completed normally with the same result. Else, if the former completes exceptionally,
	 * the latter is also completed exceptionally with the same exception.
	 * 
	 * @param <T> the result type of the stage
	 * @param completionStage the completion stage
	 * @return a reaction stage completed in the same way as the original completion stage
	 */
	<T> ReactionStage<T> copyStage(CompletionStage<T> completionStage);
	
	/**
	 * Creates a future which completes when all of the specified futures complete. If all do so normally,
	 * the combined future completes normally, else exceptionally with the exception. <br>
	 * <br>
	 * If no futures are provided, returns an already completed future. <br>
	 * <br>
	 * Note that the completed value of the combined future, if it completes normally, is not meaningful.
	 * 
	 * @param futures the futures to combine
	 * @return a future completed combining the specified futures
	 * @throws NullPointerException if {@code futures} or an element in it is null
	 */
	CentralisedFuture<?> allOf(CentralisedFuture<?>...futures);
	
	/**
	 * Creates a future which completes when all of the specified futures complete. If all do so normally,
	 * the combined future completes normally, else exceptionally with the exception. <br>
	 * <br>
	 * If no futures are provided, returns an already completed future. <br>
	 * <br>
	 * Note that the completed value of the combined future, if it completes normally, is not meaningful.
	 * 
	 * @param futures the futures to combine
	 * @return a future completed combining the specified futures
	 * @throws NullPointerException if {@code futures} or an element in it is null
	 */
	CentralisedFuture<?> allOf(Collection<CentralisedFuture<?>> futures);

}
