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
package space.arim.omnibus.util.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * A service which may be shut down, at which point it stops accepting service-related method calls. <br>
 * The methods defined in this interface, however, should remain functional. <br>
 * <br>
 * Although this interface is similar to some methods in {@link java.util.concurrent.ExecutorService ExecutorService},
 * implementers need not be specifically designed to be a thread pool.
 * 
 * @author A248
 *
 */
public interface StoppableService {

    /**
     * Initiates an orderly shutdown in which previously submitted work
     * is executed, but no service-related method calls will be accepted.
     * Invocation has no additional effect if already shutdown or shutting down.
     *
     * <p>This method does not wait for shutdown to complete.
     * Use {@link #awaitTermination awaitTermination} to do that.
     */
    void shutdown();

    /**
     * Returns {@code true} if this service has started or finished shutting down.
     *
     * @return {@code true} if this service has started or finished shutting down
     */
    boolean isShutdown();

    /**
     * Returns {@code true} if the service has completed shutting down.
     * Note that {@code isTerminated} is never {@code true} unless
     * {@code shutdown} was called first.
     *
     * @return {@code true} if the service has completed shutting down
     */
    boolean isTerminated();

    /**
     * Blocks until the service has finished shutting down after a shutdown
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return {@code true} if this service terminated and
     *         {@code false} if the timeout elapsed before termination
     * @throws InterruptedException if interrupted while waiting
     */
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;
	
}
