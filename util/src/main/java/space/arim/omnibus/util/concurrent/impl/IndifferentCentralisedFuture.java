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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.ReactionStage;

/**
 * {@link CentralisedFuture} implementation which regards <i>Sync</i> method calls
 * as identical to <i>Async</i> calls using the default executor.
 * 
 * @author A248
 *
 * @param <T> the result type of the future
 */
public class IndifferentCentralisedFuture<T> extends CentralisedFuture<T> {

	/**
	 * Creates a new incomplete future
	 * 
	 */
	public IndifferentCentralisedFuture() {
		
	}
	
	@Override
	public <U> CentralisedFuture<U> newIncompleteFuture() {
		return new IndifferentCentralisedFuture<>();
	}
	
	@Override
	public ReactionStage<T> minimalCompletionStage() {
		return new MinimalReactionStage<>(this);
	}

	@Override
	public <U> CentralisedFuture<U> thenApplySync(Function<? super T, ? extends U> fn) {
		return thenApplyAsync(fn);
	}

	@Override
	public CentralisedFuture<Void> thenAcceptSync(Consumer<? super T> action) {
		return thenAcceptAsync(action);
	}

	@Override
	public CentralisedFuture<Void> thenRunSync(Runnable action) {
		return thenRunAsync(action);
	}

	@Override
	public <U, V> CentralisedFuture<V> thenCombineSync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn) {
		return thenCombineAsync(other, fn);
	}

	@Override
	public <U> CentralisedFuture<Void> thenAcceptBothSync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action) {
		return thenAcceptBothAsync(other, action);
	}

	@Override
	public CentralisedFuture<Void> runAfterBothSync(CompletionStage<?> other, Runnable action) {
		return runAfterBothAsync(other, action);
	}

	@Override
	public <U> CentralisedFuture<U> applyToEitherSync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return applyToEitherAsync(other, fn);
	}

	@Override
	public CentralisedFuture<Void> acceptEitherSync(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return acceptEitherAsync(other, action);
	}

	@Override
	public CentralisedFuture<Void> runAfterEitherSync(CompletionStage<?> other, Runnable action) {
		return runAfterEitherAsync(other, action);
	}

	@Override
	public <U> CentralisedFuture<U> thenComposeSync(Function<? super T, ? extends CompletionStage<U>> fn) {
		return thenComposeAsync(fn);
	}

	@Override
	public CentralisedFuture<T> whenCompleteSync(BiConsumer<? super T, ? super Throwable> action) {
		return whenCompleteAsync(action);
	}

	@Override
	public <U> CentralisedFuture<U> handleSync(BiFunction<? super T, Throwable, ? extends U> fn) {
		return handleAsync(fn);
	}

	@Override
	public CentralisedFuture<T> completeSync(Supplier<? extends T> supplier) {
		return completeAsync(supplier);
	}

}
