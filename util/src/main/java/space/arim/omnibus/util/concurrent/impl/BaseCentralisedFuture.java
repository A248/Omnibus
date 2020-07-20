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

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.ReactionStage;
import space.arim.omnibus.util.concurrent.SynchronousExecutor;

/**
 * Implementation of {@link CentralisedFuture} using a {@link SynchronousExecutor}
 * for <i>Sync</i> methods.
 * 
 * @author A248
 *
 * @param <T> the result type of the future
 */
public class BaseCentralisedFuture<T> extends CentralisedFuture<T> {

	private final Executor syncExecutor;
	
	/**
	 * Creates a new incomplete future, with a {@link SynchronousExecutor} to use
	 * for executing tasks synchronously to the main thread.
	 * 
	 * @param syncExecutor the synchronous executor to use for Sync tasks
	 */
	public BaseCentralisedFuture(SynchronousExecutor syncExecutor) {
		this((Executor) syncExecutor::executeSync);
	}
	
	private BaseCentralisedFuture(Executor syncExecutor) {
		this.syncExecutor = syncExecutor;
	}
	
	@Override
	public <U> CentralisedFuture<U> newIncompleteFuture() {
		return new BaseCentralisedFuture<>(syncExecutor);
	}
	
	@Override
	public ReactionStage<T> minimalCompletionStage() {
		return new MinimalReactionStage<>(this);
	}
	
	@Override
	public <U> CentralisedFuture<U> thenApplySync(Function<? super T, ? extends U> fn) {
		return thenApplyAsync(fn, syncExecutor);
	}
	
	@Override
	public CentralisedFuture<Void> thenAcceptSync(Consumer<? super T> action) {
		return thenAcceptAsync(action, syncExecutor);
	}
	
	@Override
	public CentralisedFuture<Void> thenRunSync(Runnable action) {
		return thenRunAsync(action, syncExecutor);
	}
	
	@Override
	public <U, V> CentralisedFuture<V> thenCombineSync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn) {
		return thenCombineAsync(other, fn, syncExecutor);
	}
	
	@Override
	public <U> CentralisedFuture<Void> thenAcceptBothSync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action) {
		return thenAcceptBothAsync(other, action, syncExecutor);
	}
	
	@Override
	public CentralisedFuture<Void> runAfterBothSync(CompletionStage<?> other, Runnable action) {
		return runAfterBothAsync(other, action, syncExecutor);
	}
	
	@Override
	public <U> CentralisedFuture<U> applyToEitherSync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return applyToEitherAsync(other, fn, syncExecutor);
	}
	
	@Override
	public CentralisedFuture<Void> acceptEitherSync(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return acceptEitherAsync(other, action, syncExecutor);
	}
	
	@Override
	public CentralisedFuture<Void> runAfterEitherSync(CompletionStage<?> other, Runnable action) {
		return runAfterEitherAsync(other, action, syncExecutor);
	}
	
	@Override
	public <U> CentralisedFuture<U> thenComposeSync(Function<? super T, ? extends CompletionStage<U>> fn) {
		return thenComposeAsync(fn, syncExecutor);
	}
	
	@Override
	public CentralisedFuture<T> whenCompleteSync(BiConsumer<? super T, ? super Throwable> action) {
		return whenCompleteAsync(action, syncExecutor);
	}
	
	@Override
	public <U> CentralisedFuture<U> handleSync(BiFunction<? super T, Throwable, ? extends U> fn) {
		return handleAsync(fn, syncExecutor);
	}
	
	@Override
	public CentralisedFuture<T> completeSync(Supplier<? extends T> supplier) {
		return completeAsync(supplier, syncExecutor);
	}
	
}
