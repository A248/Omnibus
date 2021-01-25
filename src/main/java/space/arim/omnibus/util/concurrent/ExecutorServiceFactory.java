/*
 * Omnibus
 * Copyright © 2021 Anand Beh
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Factory of {@link ExecutorService}s
 *
 */
public interface ExecutorServiceFactory {

	/**
	 * Creates an executor service backed by a fixed number of threads
	 *
	 * @param threadCount the thread count
	 * @return the executor service
	 */
	ExecutorService newFixedThreadPool(int threadCount);

	/**
	 * Creates an executor service backed by a fixed number of threads
	 *
	 * @param threadCount the thread count
	 * @param threadFactory the thread factory
	 * @return the executor service
	 */
	ExecutorService newFixedThreadPool(int threadCount, ThreadFactory threadFactory);

	/**
	 * Creates an executor service backed by a cached thread pool
	 *
	 * @return the executor service
	 */
	ExecutorService newCachedThreadPool();

	/**
	 * Creates an executor service backed by a cached thread pool
	 *
	 * @param threadFactory the thread factory
	 * @return the executor service
	 */
	ExecutorService newCachedThreadPool(ThreadFactory threadFactory);

}
