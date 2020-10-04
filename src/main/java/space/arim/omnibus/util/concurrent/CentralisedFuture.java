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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An extended {@code CompletableFuture} API implementing {@link ReactionStage}.
 * This future is centralised in that its construction is intended to be undertaken by
 * {@link FactoryOfTheFuture} implementations.
 * 
 * @author A248
 *
 * @param <T> the result type of the future
 */
public abstract class CentralisedFuture<T> extends CompletableFuture<T> implements ReactionStage<T> {

	// Must be overridden by subclasses
	
	@Override
	public abstract <U> CentralisedFuture<U> newIncompleteFuture();
	
	@Override
	public abstract ReactionStage<T> minimalCompletionStage();
	
	/*
	 * Methods not overridden as of JDK 11:
	 * 
	 * defaultExecutor() join(), get(), get(long, TimeUnit), isDone(), getNow(T),
	 * complete(T), completeExceptionally(Throwable), cancel(boolean), isCancelled(),
	 * isCompletedExceptionally(), obtrudeValue(T), obtrudeException(Throwable),
	 * getNumberOfDependents()
	 * 
	 */

	@Override
	public <U> CentralisedFuture<U> thenApply(Function<? super T, ? extends U> fn) {
		return (CentralisedFuture<U>) super.<U>thenApply(fn);
	}

	@Override
	public <U> CentralisedFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
		return (CentralisedFuture<U>) super.<U>thenApplyAsync(fn);
	}

	@Override
	public <U> CentralisedFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
		return (CentralisedFuture<U>) super.<U>thenApplyAsync(fn, executor);
	}
	
	@Override
	public abstract <U> CentralisedFuture<U> thenApplySync(Function<? super T, ? extends U> fn);

	@Override
	public CentralisedFuture<Void> thenAccept(Consumer<? super T> action) {
		return (CentralisedFuture<Void>) super.thenAccept(action);
	}

	@Override
	public CentralisedFuture<Void> thenAcceptAsync(Consumer<? super T> action) {
		return (CentralisedFuture<Void>) super.thenAcceptAsync(action);
	}

	@Override
	public CentralisedFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
		return (CentralisedFuture<Void>) super.thenAcceptAsync(action, executor);
	}
	
	@Override
	public abstract CentralisedFuture<Void> thenAcceptSync(Consumer<? super T> action);

	@Override
	public CentralisedFuture<Void> thenRun(Runnable action) {
		return (CentralisedFuture<Void>) super.thenRun(action);
	}

	@Override
	public CentralisedFuture<Void> thenRunAsync(Runnable action) {
		return (CentralisedFuture<Void>) super.thenRunAsync(action);
	}

	@Override
	public CentralisedFuture<Void> thenRunAsync(Runnable action, Executor executor) {
		return (CentralisedFuture<Void>) super.thenRunAsync(action, executor);
	}
	
	@Override
	public abstract CentralisedFuture<Void> thenRunSync(Runnable action);

	@Override
	public <U, V> CentralisedFuture<V> thenCombine(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn) {
		return (CentralisedFuture<V>) super.<U, V>thenCombine(other, fn);
	}

	@Override
	public <U, V> CentralisedFuture<V> thenCombineAsync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn) {
		return (CentralisedFuture<V>) super.<U, V>thenCombineAsync(other, fn);
	}

	@Override
	public <U, V> CentralisedFuture<V> thenCombineAsync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
		return (CentralisedFuture<V>) super.<U, V>thenCombineAsync(other, fn, executor);
	}
	
	@Override
	public abstract <U, V> CentralisedFuture<V> thenCombineSync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn);

	@Override
	public <U> CentralisedFuture<Void> thenAcceptBoth(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action) {
		return (CentralisedFuture<Void>) super.thenAcceptBoth(other, action);
	}

	@Override
	public <U> CentralisedFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action) {
		return (CentralisedFuture<Void>) super.thenAcceptBothAsync(other, action);
	}

	@Override
	public <U> CentralisedFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action, Executor executor) {
		return (CentralisedFuture<Void>) super.thenAcceptBothAsync(other, action, executor);
	}
	
	@Override
	public abstract <U> CentralisedFuture<Void> thenAcceptBothSync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action);

	@Override
	public CentralisedFuture<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
		return (CentralisedFuture<Void>) super.runAfterBoth(other, action);
	}

	@Override
	public CentralisedFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
		return (CentralisedFuture<Void>) super.runAfterBothAsync(other, action);
	}

	@Override
	public CentralisedFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
		return (CentralisedFuture<Void>) super.runAfterBothAsync(other, action, executor);
	}
	
	@Override
	public abstract CentralisedFuture<Void> runAfterBothSync(CompletionStage<?> other, Runnable action);

	@Override
	public <U> CentralisedFuture<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return (CentralisedFuture<U>) super.applyToEither(other, fn);
	}

	@Override
	public <U> CentralisedFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return (CentralisedFuture<U>) super.applyToEitherAsync(other, fn);
	}

	@Override
	public <U> CentralisedFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn,
			Executor executor) {
		return (CentralisedFuture<U>) super.applyToEitherAsync(other, fn, executor);
	}
	
	@Override
	public abstract <U> CentralisedFuture<U> applyToEitherSync(CompletionStage<? extends T> other, Function<? super T, U> fn);

	@Override
	public CentralisedFuture<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return (CentralisedFuture<Void>) super.acceptEither(other, action);
	}

	@Override
	public CentralisedFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return (CentralisedFuture<Void>) super.acceptEitherAsync(other, action);
	}

	@Override
	public CentralisedFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action,
			Executor executor) {
		return (CentralisedFuture<Void>) super.acceptEitherAsync(other, action, executor);
	}
	
	@Override
	public abstract CentralisedFuture<Void> acceptEitherSync(CompletionStage<? extends T> other, Consumer<? super T> action);

	@Override
	public CentralisedFuture<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
		return (CentralisedFuture<Void>) super.runAfterEither(other, action);
	}

	@Override
	public CentralisedFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
		return (CentralisedFuture<Void>) super.runAfterEitherAsync(other, action);
	}

	@Override
	public CentralisedFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
		return (CentralisedFuture<Void>) super.runAfterEitherAsync(other, action, executor);
	}
	
	@Override
	public abstract CentralisedFuture<Void> runAfterEitherSync(CompletionStage<?> other, Runnable action);

	@Override
	public <U> CentralisedFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
		return (CentralisedFuture<U>) super.thenCompose(fn);
	}

	@Override
	public <U> CentralisedFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
		return (CentralisedFuture<U>) super.thenComposeAsync(fn);
	}

	@Override
	public <U> CentralisedFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn,
			Executor executor) {
		return (CentralisedFuture<U>) super.thenComposeAsync(fn, executor);
	}
	
	@Override
	public abstract <U> CentralisedFuture<U> thenComposeSync(Function<? super T, ? extends CompletionStage<U>> fn);

	@Override
	public CentralisedFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
		return (CentralisedFuture<T>) super.whenComplete(action);
	}

	@Override
	public CentralisedFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
		return (CentralisedFuture<T>) super.whenCompleteAsync(action);
	}

	@Override
	public CentralisedFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
		return (CentralisedFuture<T>) super.whenCompleteAsync(action, executor);
	}
	
	@Override
	public abstract CentralisedFuture<T> whenCompleteSync(BiConsumer<? super T, ? super Throwable> action);

	@Override
	public <U> CentralisedFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
		return (CentralisedFuture<U>) super.<U>handle(fn);
	}

	@Override
	public <U> CentralisedFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
		return (CentralisedFuture<U>) super.<U>handleAsync(fn);
	}

	@Override
	public <U> CentralisedFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
		return (CentralisedFuture<U>) super.<U>handleAsync(fn, executor);
	}
	
	@Override
	public abstract <U> CentralisedFuture<U> handleSync(BiFunction<? super T, Throwable, ? extends U> fn);

	@Override
	public CentralisedFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
		return (CentralisedFuture<T>) super.exceptionally(fn);
	}

	@Override
	public CentralisedFuture<T> copy() {
		return (CentralisedFuture<T>) super.copy();
	}

	@Override
	public CentralisedFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor) {
		return (CentralisedFuture<T>) super.completeAsync(supplier, executor);
	}

	@Override
	public CentralisedFuture<T> completeAsync(Supplier<? extends T> supplier) {
		return (CentralisedFuture<T>) super.completeAsync(supplier);
	}
	
	public abstract CentralisedFuture<T> completeSync(Supplier<? extends T> supplier);

	@Override
	public CentralisedFuture<T> orTimeout(long timeout, TimeUnit unit) {
		return (CentralisedFuture<T>) super.orTimeout(timeout, unit);
	}

	@Override
	public CentralisedFuture<T> completeOnTimeout(T value, long timeout, TimeUnit unit) {
		return (CentralisedFuture<T>) super.completeOnTimeout(value, timeout, unit);
	}
	
	@Override
	public CentralisedFuture<T> toCompletableFuture() {
		return this;
	}
	
}
