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
package space.arim.universal.util.proxy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A parent class for completable futures which simply redirect, a.k.a. <i>proxy</i>, calls to another such future (the backing future). <br>
 * Such "futures" do no work themselves, but merely refer calls to their backing futures. <br>
 * <br>
 * However, the proxy future's additional call layer provides fine tuned control over access to the backing future. <br>
 * Programmers may extend this class to utilise this enhanced control.
 * <br>
 * Note that a reference is retained to the backing future. State changes in the backing future are reflected in proxied futures.
 * 
 * @author A248
 *
 * @param <T> the type of the object the future will yield
 * 
 */
public abstract class ProxiedCompletableFuture<T> extends CompletableFuture<T> {

	private final CompletableFuture<T> original;
	
	/**
	 * Creates a ProxiedCompletableFuture based on a backing CompletableFuture
	 * 
	 * @param original the original, backing future
	 */
	protected ProxiedCompletableFuture(CompletableFuture<T> original) {
		this.original = original;
	}
	
	/**
	 * Since CompletableFuture utilises chaining, returning a new CompletableFuture each time,
	 * we must continue wrapping each CompletableFuture returned in a ProxiedCompletableFuture. <br>
	 * <br>
	 * Otherwise, ProxiedCompletableFuture implementations would lose all traits after a single call.
	 * 
	 * @param <U> the type of the next CompletableFuture
	 * @param another the CompletableFuture to wrap
	 * @return a rewrapped ProxiedCompletableFuture
	 */
	protected abstract <U> ProxiedCompletableFuture<U> wrapAnother(CompletableFuture<U> another);
	
	/**
	 * Gets the original future upon which this ProxiedCompletableFuture is based
	 * 
	 * @return the original, backing future
	 */
	protected CompletableFuture<T> getOriginal() {
		return original;
	}
	
	@Override
	public boolean isDone() {
		return original.isDone();
	}
	
	@Override
	public T get() throws InterruptedException, ExecutionException {
		return original.get();
	}
	
	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return original.get(timeout, unit);
	}
	
	@Override
	public T join() {
		return original.join();
	}
	
	@Override
	public T getNow(T valueIfAbsent) {
        return original.getNow(valueIfAbsent);
    }
	
	@Override
	public boolean complete(T value) {
		return original.complete(value);
	}
	
	@Override
	public boolean completeExceptionally(Throwable ex) {
		return original.completeExceptionally(ex);
	}
	
	// begin CompletionStage implementation
	
	@Override
	public <U> CompletableFuture<U> thenApply(Function<? super T,? extends U> converter) {
		return wrapAnother(original.thenApply(converter));
	}
	
	@Override
	public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> converter) {
		return wrapAnother(original.thenApplyAsync(converter));
	}
	
	@Override
	public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> converter, Executor executor) {
		return wrapAnother(original.thenApplyAsync(converter, executor));
	}
	
	@Override
	public CompletableFuture<Void> thenAccept(Consumer<? super T> action) {
		return wrapAnother(original.thenAccept(action));
	}
	
	@Override
	public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action) {
		return wrapAnother(original.thenAcceptAsync(action));
	}
	
	@Override
	public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
		return wrapAnother(original.thenAcceptAsync(action, executor));
	}
	
	@Override
	public CompletableFuture<Void> thenRun(Runnable action) {
		return wrapAnother(original.thenRun(action));
	}
	
	@Override
	public CompletableFuture<Void> thenRunAsync(Runnable action) {
		return wrapAnother(original.thenRunAsync(action));
	}
	
	@Override
	public CompletableFuture<Void> thenRunAsync(Runnable action, Executor executor) {
		return wrapAnother(original.thenRunAsync(action, executor));
	}
	
	@Override
	public <U, V> CompletableFuture<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
		return wrapAnother(original.thenCombine(other, fn));
	}
	
	@Override
	public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
		return wrapAnother(original.thenCombineAsync(other, fn));
	}
	
	@Override
	public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
		return wrapAnother(original.thenCombineAsync(other, fn, executor));
	}
	
	@Override
	public <U> CompletableFuture<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
		return wrapAnother(original.thenAcceptBoth(other, action));
	}
	
	@Override
	public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
		return wrapAnother(original.thenAcceptBothAsync(other, action));
	}
	
	@Override
	public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor) {
		return wrapAnother(original.thenAcceptBothAsync(other, action, executor));
	}
	
	@Override
	public CompletableFuture<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
		return wrapAnother(original.runAfterBoth(other, action));
	}
	
	@Override
	public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
		return wrapAnother(original.runAfterBothAsync(other, action));
	}
	
	@Override
	public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
		return wrapAnother(original.runAfterBothAsync(other, action, executor));
	}
	
	@Override
	public <U> CompletableFuture<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return wrapAnother(original.applyToEither(other, fn));
	}
	
	@Override
	public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return wrapAnother(original.applyToEitherAsync(other, fn));
	}
	
	@Override
	public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {
		return wrapAnother(original.applyToEitherAsync(other, fn, executor));
	}
	
	@Override
	public CompletableFuture<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return wrapAnother(original.acceptEither(other, action));
	}
	
	@Override
	public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return wrapAnother(original.acceptEitherAsync(other, action));
	}
	
	@Override
	public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action,Executor executor) {
		return wrapAnother(original.acceptEitherAsync(other, action, executor));
	}
	
	@Override
	public CompletableFuture<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
		return wrapAnother(original.runAfterEither(other, action));
	}
	
	@Override
	public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
		return wrapAnother(original.runAfterEitherAsync(other, action));
	}
	
	@Override
	public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
		return wrapAnother(original.runAfterEitherAsync(other, action, executor));
	}
	
	@Override
	public <U> CompletableFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
		return wrapAnother(original.thenCompose(fn));
	}
	
	@Override
	public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
		return wrapAnother(original.thenComposeAsync(fn));
	}
	
	@Override
	public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
		return wrapAnother(original.thenComposeAsync(fn, executor));
	}
	
	@Override
	public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
		return wrapAnother(original.whenComplete(action));
	}
	
	@Override
	public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
		return wrapAnother(original.whenCompleteAsync(action));
	}
	
	@Override
	public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
		return wrapAnother(original.whenCompleteAsync(action, executor));
	}
	
	@Override
	public <U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
		return wrapAnother(original.handle(fn));
	}
	
	@Override
	public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
		return wrapAnother(original.handleAsync(fn));
	}
	
	@Override
	public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
		return wrapAnother(original.handleAsync(fn, executor));
	}
	
	// end CompletionStage implementation
	
	@Override
	public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
		return wrapAnother(original.exceptionally(fn));
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return original.cancel(mayInterruptIfRunning);
	}
	
	@Override
	public boolean isCancelled() {
		return original.isCancelled();
	}
	
	@Override
	public boolean isCompletedExceptionally() {
		return original.isCompletedExceptionally();
	}
	
	@Override
	public void obtrudeValue(T value) {
		original.obtrudeValue(value);
	}
	
	@Override
	public void obtrudeException(Throwable ex) {
		original.obtrudeException(ex);
	}
	
	@Override
	public int getNumberOfDependents() {
		return original.getNumberOfDependents();
	}
	
	@Override
	public String toString() {
		return original.toString();
	}
	
}
