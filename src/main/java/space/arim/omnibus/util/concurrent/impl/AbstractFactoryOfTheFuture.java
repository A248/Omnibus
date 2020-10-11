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

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;
import space.arim.omnibus.util.concurrent.ReactionStage;

/**
 * Abstract base class for {@link FactoryOfTheFuture} using protected method
 * {@link #newIncompleteFuture()} to generate futures as determined by subclasses. <br>
 * <br>
 * Requirements to be implemented by subclasses: <br>
 * {@link #newIncompleteFuture()} <br>
 * {@link #executeSync(Runnable)} <br>
 * {@link #execute(Runnable)} may be optionally overridden to change the default executor for
 * asynchronous work.
 * 
 * @author A248
 *
 */
public abstract class AbstractFactoryOfTheFuture implements FactoryOfTheFuture {

	/**
	 * Creates a new, incomplete future. The result of this method
	 * is used in implementing various {@link FactoryOfTheFuture} specifications.
	 * 
	 * @param <U> the result type of the future
	 * @return a new incomplete future
	 */
	@Override
	public abstract <U> CentralisedFuture<U> newIncompleteFuture();

	@Override
	public void execute(Runnable command) {
		ForkJoinPool.commonPool().execute(command);
	}
	
	@Override
	public CentralisedFuture<?> runAsync(Runnable command) {
		Objects.requireNonNull(command, "Runnable must not be null");
		return supplyAsync(() -> {
			command.run();
			return null;
		});
	}

	@Override
	public CentralisedFuture<?> runAsync(Runnable command, Executor executor) {
		Objects.requireNonNull(command, "Runnable must not be null");
		return supplyAsync(() -> {
			command.run();
			return null;
		}, executor);
	}

	@Override
	public CentralisedFuture<?> runSync(Runnable command) {
		Objects.requireNonNull(command, "Runnable must not be null");
		return supplySync(() -> {
			command.run();
			return null;
		});
	}

	@Override
	public <T> CentralisedFuture<T> supplyAsync(Supplier<T> supplier) {
		return this.<T>newIncompleteFuture().completeAsync(supplier);
	}

	@Override
	public <T> CentralisedFuture<T> supplyAsync(Supplier<T> supplier, Executor executor) {
		return this.<T>newIncompleteFuture().completeAsync(supplier, executor);
	}

	@Override
	public <T> CentralisedFuture<T> supplySync(Supplier<T> supplier) {
		return this.<T>newIncompleteFuture().completeSync(supplier);
	}

	@Override
	public <T> CentralisedFuture<T> completedFuture(T value) {
		CentralisedFuture<T> result = newIncompleteFuture();
		result.complete(value);
		return result;
	}

	@Override
	public <T> ReactionStage<T> completedStage(T value) {
		return new MinimalReactionStage<>(completedFuture(value));
	}

	@Override
	public <T> CentralisedFuture<T> failedFuture(Throwable ex) {
		CentralisedFuture<T> result = newIncompleteFuture();
		result.completeExceptionally(ex);
		return result;
	}

	@Override
	public <T> ReactionStage<T> failedStage(Throwable ex) {
		return new MinimalReactionStage<>(failedFuture(ex));
	}

	@Override
	public <T> CentralisedFuture<T> copyFuture(CompletableFuture<T> completableFuture) {
		CentralisedFuture<T> result = newIncompleteFuture();
		completableFuture.whenComplete((val, ex) -> {
			if (ex == null) {
				result.complete(val);
			} else {
				result.completeExceptionally(ex);
			}
		});
		return result;
	}

	@Override
	public <T> ReactionStage<T> copyStage(CompletionStage<T> completionStage) {
		CentralisedFuture<T> result = newIncompleteFuture();
		completionStage.whenComplete((val, ex) -> {
			if (ex == null) {
				result.complete(val);
			} else {
				result.completeExceptionally(ex);
			}
		});
		return new MinimalReactionStage<>(result);
	}
	
	@Override
	public CentralisedFuture<?> allOf(CentralisedFuture<?>...futures) {
		return copyFuture(CompletableFuture.allOf(futures));
	}
	
	@Override
	public CentralisedFuture<?> allOf(Collection<CentralisedFuture<?>> futures) {
		return allOf(futures.toArray(CentralisedFuture[]::new));
	}

}
