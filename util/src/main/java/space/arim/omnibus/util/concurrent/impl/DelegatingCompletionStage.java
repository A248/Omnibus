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
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class DelegatingCompletionStage<T> implements CompletionStage<T> {
	
	abstract CompletableFuture<T> getCompletableFuture();
	
	@Override
	public CompletableFuture<T> toCompletableFuture() {
		return getCompletableFuture();
	}

	@Override
	public <U> CompletionStage<U> thenApply(Function<? super T, ? extends U> fn) {
		return getCompletableFuture().thenApply(fn);
	}

	@Override
	public <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
		return getCompletableFuture().thenApplyAsync(fn);
	}

	@Override
	public <U> CompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
		return getCompletableFuture().thenApplyAsync(fn, executor);
	}

	@Override
	public CompletionStage<Void> thenAccept(Consumer<? super T> action) {
		return getCompletableFuture().thenAccept(action);
	}

	@Override
	public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action) {
		return getCompletableFuture().thenAcceptAsync(action);
	}

	@Override
	public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
		return getCompletableFuture().thenAcceptAsync(action, executor);
	}

	@Override
	public CompletionStage<Void> thenRun(Runnable action) {
		return getCompletableFuture().thenRun(action);
	}

	@Override
	public CompletionStage<Void> thenRunAsync(Runnable action) {
		return getCompletableFuture().thenRunAsync(action);
	}

	@Override
	public CompletionStage<Void> thenRunAsync(Runnable action, Executor executor) {
		return getCompletableFuture().thenRunAsync(action, executor);
	}

	@Override
	public <U, V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn) {
		return getCompletableFuture().thenCombine(other, fn);
	}

	@Override
	public <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn) {
		return getCompletableFuture().thenCombineAsync(other, fn);
	}

	@Override
	public <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
		return getCompletableFuture().thenCombineAsync(other, fn, executor);
	}

	@Override
	public <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action) {
		return getCompletableFuture().thenAcceptBoth(other, action);
	}

	@Override
	public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action) {
		return getCompletableFuture().thenAcceptBothAsync(other, action);
	}

	@Override
	public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action, Executor executor) {
		return getCompletableFuture().thenAcceptBothAsync(other, action, executor);
	}

	@Override
	public CompletionStage<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
		return getCompletableFuture().runAfterBoth(other, action);
	}

	@Override
	public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
		return getCompletableFuture().runAfterBothAsync(other, action);
	}

	@Override
	public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
		return getCompletableFuture().runAfterBothAsync(other, action, executor);
	}

	@Override
	public <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return getCompletableFuture().applyToEither(other, fn);
	}

	@Override
	public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return getCompletableFuture().applyToEitherAsync(other, fn);
	}

	@Override
	public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn,
			Executor executor) {
		return getCompletableFuture().applyToEitherAsync(other, fn, executor);
	}

	@Override
	public CompletionStage<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return getCompletableFuture().acceptEither(other, action);
	}

	@Override
	public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return getCompletableFuture().acceptEitherAsync(other, action);
	}

	@Override
	public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action,
			Executor executor) {
		return getCompletableFuture().acceptEitherAsync(other, action, executor);
	}

	@Override
	public CompletionStage<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
		return getCompletableFuture().runAfterEither(other, action);
	}

	@Override
	public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
		return getCompletableFuture().runAfterEitherAsync(other, action);
	}

	@Override
	public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
		return getCompletableFuture().runAfterEitherAsync(other, action, executor);
	}

	@Override
	public <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
		return getCompletableFuture().thenCompose(fn);
	}

	@Override
	public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
		return getCompletableFuture().thenComposeAsync(fn);
	}

	@Override
	public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn,
			Executor executor) {
		return getCompletableFuture().thenComposeAsync(fn, executor);
	}

	@Override
	public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
		return getCompletableFuture().handle(fn);
	}

	@Override
	public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
		return getCompletableFuture().handleAsync(fn);
	}

	@Override
	public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
		return getCompletableFuture().handleAsync(fn, executor);
	}

	@Override
	public CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
		return getCompletableFuture().whenComplete(action);
	}

	@Override
	public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
		return getCompletableFuture().whenCompleteAsync(action);
	}

	@Override
	public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
		return getCompletableFuture().whenCompleteAsync(action, executor);
	}

	@Override
	public CompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn) {
		return getCompletableFuture().exceptionally(fn);
	}
	
}
