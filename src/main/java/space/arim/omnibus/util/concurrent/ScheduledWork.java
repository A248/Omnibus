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
import java.util.concurrent.ScheduledFuture;

/**
 * A task, either delayed or repeating, scheduled with a {@link EnhancedExecutor}. <br>
 * <br>
 * Some of the specifications of this interface depend on whether it represents delayed or repeating work. 
 * 
 * @author A248
 *
 * @param <T> the type of the result
 */
public interface ScheduledWork<T> extends ScheduledFuture<T>, CompletionStage<T> {

	/**
	 * Cancels this scheduled work. <br>
	 * <br>
	 * If this is a delayed task, the work will not run unless it has already begun execution. Else, if
	 * it is a repeating task, further scheduling of the work will cease. If any execution is in process,
	 * it will complete first. <br>
	 * <br>
	 * The boolean parameter is specified by the {@code Future} interface. However, it is not used,
	 * because interrupts are not used to control processing. Should any work be in progress
	 * when this method (or its alias {@link #cancel()}) is called, it will be allowed to complete.
	 *
	 * @param ignoreParameter the ignore parameter
	 * @return {@code false} if this is a delayed task and has already run, {@code true} otherwise
	 */
	@Override
	boolean cancel(boolean ignoreParameter);
	
	/**
	 * Cancels this scheduled work. <br>
	 * <br>
	 * If this is a delayed task, the work will not run unless it has already begun execution. Else, if
	 * it is a repeating task, further scheduling of the work will cease. If any execution is in process,
	 * it will complete first. <br>
	 * <br>
	 * This is an alias for {@link #cancel(boolean)} which improves readability since it does not require
	 * the unused boolean parameter.
	 * 
	 * @return {@code false} if this is a delayed task and has already run, {@code true} otherwise
	 */
	default boolean cancel() {
		return cancel(false);
	}
	
	/**
	 * Whether this task was cancelled via {@link #cancel(boolean)}. <br>
	 * In the case of delayed work, cancellation may also occur through {@link #toCompletableFuture()}
	 *
	 * @return true, if is cancelled
	 */
	@Override
	boolean isCancelled();
	
	/**
	 * Gets a completable future which is completed by the result of this work when this
	 * task runs. <br>
	 * <br>
	 * If this instance represents delayed work, cancelling the returned future will prevent the work
	 * from executing unless execution has already begun. <br>
	 * <br>
	 * Else, if this instance represents repeating work, the returned future represents a snapshot of
	 * the next execution, which is completed when this task next completes. Cancelling it prevents
	 * the next execution, but does not prevent further scheduling. To cancel the task entirely,
	 * {@link #cancel()} must be used.
	 *
	 * @return the completable future
	 */
	@Override
	CompletableFuture<T> toCompletableFuture();
	
	/**
	 * Whether this task is a repeating task, or a delayed task. {@code true}
	 * for repeating, false for delayed.
	 * 
	 * @return true if the task is repeating, false if it is delayed
	 */
	boolean isRepeating();
	
}
