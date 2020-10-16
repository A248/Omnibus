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

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.ReactionStage;

/**
 * Implementation of {@link CentralisedFuture#minimalCompletionStage()}.
 *
 * @param <T> the generic type
 */
class MinimalReactionStage<T> implements ReactionStage<T> {

	private final CentralisedFuture<T> centralisedFuture;

	MinimalReactionStage(CentralisedFuture<T> centralisedFuture) {
		this.centralisedFuture = centralisedFuture;
	}

	@Override
	public CentralisedFuture<T> toCompletableFuture() {
		return centralisedFuture.copy();
	}

	private <U> ReactionStage<U> wrap(CentralisedFuture<U> centralisedFuture) {
		return new MinimalReactionStage<>(centralisedFuture);
	}

	@Override
	public <U> ReactionStage<U> thenApply(Function<? super T, ? extends U> fn) {
		return wrap(centralisedFuture.thenApply(fn));
	}

	@Override
	public <U> ReactionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
		return wrap(centralisedFuture.thenApplyAsync(fn));
	}

	@Override
	public <U> ReactionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
		return wrap(centralisedFuture.thenApplyAsync(fn, executor));
	}

	@Override
	public <U> ReactionStage<U> thenApplySync(Function<? super T, ? extends U> fn) {
		return wrap(centralisedFuture.thenApplySync(fn));
	}

	@Override
	public ReactionStage<Void> thenAccept(Consumer<? super T> action) {
		return wrap(centralisedFuture.thenAccept(action));
	}

	@Override
	public ReactionStage<Void> thenAcceptAsync(Consumer<? super T> action) {
		return wrap(centralisedFuture.thenAcceptAsync(action));
	}

	@Override
	public ReactionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
		return wrap(centralisedFuture.thenAcceptAsync(action, executor));
	}

	@Override
	public ReactionStage<Void> thenAcceptSync(Consumer<? super T> action) {
		return wrap(centralisedFuture.thenAcceptSync(action));
	}

	@Override
	public ReactionStage<Void> thenRun(Runnable action) {
		return wrap(centralisedFuture.thenRun(action));
	}

	@Override
	public ReactionStage<Void> thenRunAsync(Runnable action) {
		return wrap(centralisedFuture.thenRunAsync(action));
	}

	@Override
	public ReactionStage<Void> thenRunAsync(Runnable action, Executor executor) {
		return wrap(centralisedFuture.thenRunAsync(action, executor));
	}

	@Override
	public ReactionStage<Void> thenRunSync(Runnable action) {
		return wrap(centralisedFuture.thenRunSync(action));
	}

	@Override
	public <U, V> ReactionStage<V> thenCombine(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn) {
		return wrap(centralisedFuture.thenCombine(other, fn));
	}

	@Override
	public <U, V> ReactionStage<V> thenCombineAsync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn) {
		return wrap(centralisedFuture.thenCombineAsync(other, fn));
	}

	@Override
	public <U, V> ReactionStage<V> thenCombineAsync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
		return wrap(centralisedFuture.thenCombineAsync(other, fn, executor));
	}

	@Override
	public <U, V> ReactionStage<V> thenCombineSync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn) {
		return wrap(centralisedFuture.thenCombineSync(other, fn));
	}

	@Override
	public <U> ReactionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action) {
		return wrap(centralisedFuture.thenAcceptBoth(other, action));
	}

	@Override
	public <U> ReactionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action) {
		return wrap(centralisedFuture.thenAcceptBothAsync(other, action));
	}

	@Override
	public <U> ReactionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action, Executor executor) {
		return wrap(centralisedFuture.thenAcceptBothAsync(other, action, executor));
	}

	@Override
	public <U> ReactionStage<Void> thenAcceptBothSync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action) {
		return wrap(centralisedFuture.thenAcceptBothSync(other, action));
	}

	@Override
	public ReactionStage<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
		return wrap(centralisedFuture.runAfterBoth(other, action));
	}

	@Override
	public ReactionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
		return wrap(centralisedFuture.runAfterBothAsync(other, action));
	}

	@Override
	public ReactionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
		return wrap(centralisedFuture.runAfterBothAsync(other, action, executor));
	}

	@Override
	public ReactionStage<Void> runAfterBothSync(CompletionStage<?> other, Runnable action) {
		return wrap(centralisedFuture.runAfterBothSync(other, action));
	}

	@Override
	public <U> ReactionStage<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return wrap(centralisedFuture.applyToEither(other, fn));
	}

	@Override
	public <U> ReactionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return wrap(centralisedFuture.applyToEitherAsync(other, fn));
	}

	@Override
	public <U> ReactionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn,
			Executor executor) {
		return wrap(centralisedFuture.applyToEitherAsync(other, fn, executor));
	}

	@Override
	public <U> ReactionStage<U> applyToEitherSync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return wrap(centralisedFuture.applyToEitherSync(other, fn));
	}

	@Override
	public ReactionStage<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return wrap(centralisedFuture.acceptEither(other, action));
	}

	@Override
	public ReactionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return wrap(centralisedFuture.acceptEitherAsync(other, action));
	}

	@Override
	public ReactionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action,
			Executor executor) {
		return wrap(centralisedFuture.acceptEitherAsync(other, action, executor));
	}

	@Override
	public ReactionStage<Void> acceptEitherSync(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return wrap(centralisedFuture.acceptEitherSync(other, action));
	}

	@Override
	public ReactionStage<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
		return wrap(centralisedFuture.runAfterEither(other, action));
	}

	@Override
	public ReactionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
		return wrap(centralisedFuture.runAfterEitherAsync(other, action));
	}

	@Override
	public ReactionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
		return wrap(centralisedFuture.runAfterEitherAsync(other, action, executor));
	}

	@Override
	public ReactionStage<Void> runAfterEitherSync(CompletionStage<?> other, Runnable action) {
		return wrap(centralisedFuture.runAfterEitherSync(other, action));
	}

	@Override
	public <U> ReactionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
		return wrap(centralisedFuture.thenCompose(fn));
	}

	@Override
	public <U> ReactionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
		return wrap(centralisedFuture.thenComposeAsync(fn));
	}

	@Override
	public <U> ReactionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn,
			Executor executor) {
		return wrap(centralisedFuture.thenComposeAsync(fn, executor));
	}

	@Override
	public <U> ReactionStage<U> thenComposeSync(Function<? super T, ? extends CompletionStage<U>> fn) {
		return wrap(centralisedFuture.thenComposeSync(fn));
	}

	@Override
	public <U> ReactionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
		return wrap(centralisedFuture.handle(fn));
	}

	@Override
	public <U> ReactionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
		return wrap(centralisedFuture.handleAsync(fn));
	}

	@Override
	public <U> ReactionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
		return wrap(centralisedFuture.handleAsync(fn, executor));
	}

	@Override
	public <U> ReactionStage<U> handleSync(BiFunction<? super T, Throwable, ? extends U> fn) {
		return wrap(centralisedFuture.handleSync(fn));
	}

	@Override
	public ReactionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
		return wrap(centralisedFuture.whenComplete(action));
	}

	@Override
	public ReactionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
		return wrap(centralisedFuture.whenCompleteAsync(action));
	}

	@Override
	public ReactionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
		return wrap(centralisedFuture.whenCompleteAsync(action, executor));
	}

	@Override
	public ReactionStage<T> whenCompleteSync(BiConsumer<? super T, ? super Throwable> action) {
		return wrap(centralisedFuture.whenCompleteSync(action));
	}

	@Override
	public ReactionStage<T> exceptionally(Function<Throwable, ? extends T> fn) {
		return wrap(centralisedFuture.exceptionally(fn));
	}
}
