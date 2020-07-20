/* 
 * ArimAPI-util
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * ArimAPI-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ArimAPI-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ArimAPI-util. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.util.concurrent;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An extended interface to {@link CompletionStage} providing a fourth variant of methods in the form
 * <i>do, doAsync, doAsync</i>; <i>doSync</i>, where <i>do</i> is the general stage consuming operation. <br>
 * <br>
 * The contract of the <i>Sync</i> methods is that they run according to the specifications of
 * {@link SynchronousExecutor}, that is, identically as if ran with the corresponding <i>Async</i>
 * method variant using {@link SynchronousExecutor#executeSync(Runnable)} as the functional
 * {@link Executor} argument. For example, the following should be equivalent: <br>
 * <br>
 * <pre>
 * {@code
 * stage.thenRunSync(() -> doSomething());
 * stage.thenRunAsync(() -> doSomething(), syncExecutor::executeSync);
 * }
 * </pre>
 * where <i>stage</i> is an instance of this interface and <i>syncExecutor</i> is the implementation
 * of {@code SynchronousExecutor} for the application.
 * 
 * @author A248
 *
 * @param <T> the result of the completion stage
 */
public interface ReactionStage<T> extends CompletionStage<T> {

	@Override
	public <U> ReactionStage<U> thenApply(Function<? super T, ? extends U> fn);

	@Override
	public <U> ReactionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn);

	@Override
	public <U> ReactionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor);

	public <U> ReactionStage<U> thenApplySync(Function<? super T, ? extends U> fn);

	@Override
	public ReactionStage<Void> thenAccept(Consumer<? super T> action);

	@Override
	public ReactionStage<Void> thenAcceptAsync(Consumer<? super T> action);

	@Override
	public ReactionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor);

	public ReactionStage<Void> thenAcceptSync(Consumer<? super T> action);

	@Override
	public ReactionStage<Void> thenRun(Runnable action);

	@Override
	public ReactionStage<Void> thenRunAsync(Runnable action);

	@Override
	public ReactionStage<Void> thenRunAsync(Runnable action, Executor executor);
	
	public ReactionStage<Void> thenRunSync(Runnable action);

	@Override
	public <U, V> ReactionStage<V> thenCombine(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn);

	@Override
	public <U, V> ReactionStage<V> thenCombineAsync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn);

	@Override
	public <U, V> ReactionStage<V> thenCombineAsync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn, Executor executor);
	
	public <U, V> ReactionStage<V> thenCombineSync(CompletionStage<? extends U> other,
			BiFunction<? super T, ? super U, ? extends V> fn);

	@Override
	public <U> ReactionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action);

	@Override
	public <U> ReactionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action);

	@Override
	public <U> ReactionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action, Executor executor);
	
	public <U> ReactionStage<Void> thenAcceptBothSync(CompletionStage<? extends U> other,
			BiConsumer<? super T, ? super U> action);

	@Override
	public ReactionStage<Void> runAfterBoth(CompletionStage<?> other, Runnable action);

	@Override
	public ReactionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action);

	@Override
	public ReactionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor);
	
	public ReactionStage<Void> runAfterBothSync(CompletionStage<?> other, Runnable action);

	@Override
	public <U> ReactionStage<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn);

	@Override
	public <U> ReactionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn);

	@Override
	public <U> ReactionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn,
			Executor executor);
	
	public <U> ReactionStage<U> applyToEitherSync(CompletionStage<? extends T> other, Function<? super T, U> fn);

	@Override
	public ReactionStage<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action);

	@Override
	public ReactionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action);

	@Override
	public ReactionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action,
			Executor executor);
	
	public ReactionStage<Void> acceptEitherSync(CompletionStage<? extends T> other, Consumer<? super T> action);

	@Override
	public ReactionStage<Void> runAfterEither(CompletionStage<?> other, Runnable action);

	@Override
	public ReactionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action);

	@Override
	public ReactionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor);
	
	public ReactionStage<Void> runAfterEitherSync(CompletionStage<?> other, Runnable action);

	@Override
	public <U> ReactionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn);

	@Override
	public <U> ReactionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn);

	@Override
	public <U> ReactionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn,
			Executor executor);
	
	public <U> ReactionStage<U> thenComposeSync(Function<? super T, ? extends CompletionStage<U>> fn);

	@Override
	public <U> ReactionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn);

	@Override
	public <U> ReactionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn);

	@Override
	public <U> ReactionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor);
	
	public <U> ReactionStage<U> handleSync(BiFunction<? super T, Throwable, ? extends U> fn);

	@Override
	public ReactionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action);

	@Override
	public ReactionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action);

	@Override
	public ReactionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor);
	
	public ReactionStage<T> whenCompleteSync(BiConsumer<? super T, ? super Throwable> action);

	@Override
	public ReactionStage<T> exceptionally(Function<Throwable, ? extends T> fn);

	/**
	 * Converts to an equivalent CentralisedFuture with the same completion properties as this
	 * 
	 */
	@Override
	public CentralisedFuture<T> toCompletableFuture();
	
}
