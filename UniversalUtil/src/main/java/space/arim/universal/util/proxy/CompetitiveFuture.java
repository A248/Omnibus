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
package space.arim.universal.util.proxy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link ProxiedCompletableFuture} used to change the default {@link Executor} used in CompletableFuture's methods executing asynchronous code. <br>
 * Used to replace <code>ForkJoinPool.commonPool()</code> as the default executor for method calls. <br>
 * <br>
 * <b>In Java 9, there is native support for changing the default executor.</b>
 * See https://www.tutorialspoint.com/java9/java9_completablefuture_api_improvements.htm.
 * 
 * @author A248
 *
 * @param <T> the type of the object the future will yield
 */
public class CompetitiveFuture<T> extends ProxiedCompletableFuture<T> {

	private final Executor executor;
	
	/**
	 * Creates a CompetitiveFuture with a backing CompletableFuture and {@link Executor}. <br>
	 * <br>
	 * The <code>Executor</code> becomes the default Executor used for all CompletionStage methods executiing asynchronous code.
	 * In other words, <code>ForkJoinPool.commonPool()</code> is no longer used as the default executor for method calls.
	 * 
	 * @param original the original, backing CompletableFuture
	 * @param executor the executor to use for asynchronous execution
	 */
	protected CompetitiveFuture(CompletableFuture<T> original, Executor executor) {
		super(original);
		this.executor = executor;
	}
	
	/**
	 * Creates a CompetitiveFuture using a backing <code>CompletableFuture</code> and <code>Executor</code>
	 * 
	 * @param <T> the type of the object the future will yield
	 * @param original the original, backing CompletableFuture
	 * @param executor the executor used for asynchronous execution
	 * @return a CompetitiveFuture, never <code>null</code>
	 */
	public static <T> CompetitiveFuture<T> of(CompletableFuture<T> original, Executor executor) {
		return new CompetitiveFuture<T>(original, executor);
	}
	
	/**
	 * Equivalent version of {@link CompletableFuture#allOf(CompletableFuture...)} for CompetitiveFuture.
	 * 
	 * @param executor the executor
	 * @param originals the source CompletableFuture objects
	 * @return a combined CompetitiveFuture, never <code>null</code>
	 */
	public static CompetitiveFuture<Void> allOf(Executor executor, CompletableFuture<?>...originals) {
		return new CompetitiveFuture<Void>(CompletableFuture.allOf(originals), executor);
	}
	
	/**
	 * Equivalent version of {@link CompletableFuture#anyOf(CompletableFuture...)} for CompetitiveFuture.
	 * 
	 * @param executor the executor
	 * @param originals the source CompletableFuture objects
	 * @return a combined CompetitiveFuture, never <code>null</code>
	 */
	public static CompetitiveFuture<Object> anyOf(Executor executor, CompletableFuture<?>...originals) {
		return new CompetitiveFuture<Object>(CompletableFuture.anyOf(originals), executor);
	}
	
	/**
	 * Equivalent to {@link CompletableFuture#completedFuture(Object)}
	 * 
	 * @param <T> the type of the object
	 * @param value the object
	 * @param executor the executor
	 * @return a CompetitiveFuture, never <code>null</code>
	 */
	public static <T> CompetitiveFuture<T> completed(T value, Executor executor) {
		return new CompetitiveFuture<T>(CompletableFuture.completedFuture(value), executor);
	}
	
	/**
	 * Wraps an additional CompletableFuture as another CompetitiveFuture. <br>
	 * This makes everything work.
	 * 
	 * @param <U> the type of the next CompletableFuture
	 * @param another the CompletableFuture to wrap
	 * @return a rewrapped CompetitiveFuture
	 */
	@Override
	protected <U> CompetitiveFuture<U> wrapAnother(CompletableFuture<U> another) {
		return new CompetitiveFuture<U>(another, executor);
	}
	
	@Override
	public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> converter) {
		return wrapAnother(getOriginal().thenApplyAsync(converter, executor));
	}
	
	@Override
	public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action) {
		return wrapAnother(getOriginal().thenAcceptAsync(action, executor));
	}
	
	@Override
	public CompletableFuture<Void> thenRunAsync(Runnable action) {
		return wrapAnother(getOriginal().thenRunAsync(action, executor));
	}
	
	@Override
	public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
		return wrapAnother(getOriginal().thenCombineAsync(other, fn, executor));
	}
	
	@Override
	public <U> CompletableFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
		return wrapAnother(getOriginal().thenAcceptBothAsync(other, action, executor));
	}
	
	@Override
	public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
		return wrapAnother(getOriginal().runAfterBothAsync(other, action, executor));
	}
	
	@Override
	public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
		return wrapAnother(getOriginal().applyToEitherAsync(other, fn, executor));
	}
	
	@Override
	public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
		return wrapAnother(getOriginal().acceptEitherAsync(other, action, executor));
	}
	
	@Override
	public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
		return wrapAnother(getOriginal().runAfterEitherAsync(other, action, executor));
	}
	
	@Override
	public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
		return wrapAnother(getOriginal().thenComposeAsync(fn, executor));
	}
	
	@Override
	public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
		return wrapAnother(getOriginal().whenCompleteAsync(action, executor));
	}
	
	@Override
	public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
		return wrapAnother(getOriginal().handleAsync(fn, executor));
	}
	
}
