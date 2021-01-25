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

package space.arim.omnibus.util.concurrent.impl;

import space.arim.omnibus.util.concurrent.ExecutorServiceFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * An {@link ExecutorServiceFactory} which uses the JDK executor implementations
 * returned from {@link Executors}
 *
 */
public final class JDKExecutorServiceFactory implements ExecutorServiceFactory {

	@Override
	public ExecutorService newFixedThreadPool(int threadCount) {
		return Executors.newFixedThreadPool(threadCount);
	}

	@Override
	public ExecutorService newFixedThreadPool(int threadCount, ThreadFactory threadFactory) {
		return Executors.newFixedThreadPool(threadCount, threadFactory);
	}

	@Override
	public ExecutorService newCachedThreadPool() {
		return Executors.newCachedThreadPool();
	}

	@Override
	public ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
		return Executors.newCachedThreadPool(threadFactory);
	}

}
