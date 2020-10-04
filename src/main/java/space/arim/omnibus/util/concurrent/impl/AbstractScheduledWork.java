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

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import space.arim.omnibus.util.concurrent.ScheduledWork;

abstract class AbstractScheduledWork<T> extends DelegatingCompletionStage<T> implements ScheduledWork<T> {

	abstract long getRunTime();

	private long getNanosDelay() {
		return getRunTime() - System.nanoTime();
	}
	
	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(getNanosDelay(), TimeUnit.NANOSECONDS);
	}

	@Override
	public int compareTo(Delayed o) {
		if (o == this) {
			return 0;
		}
		if (o instanceof AbstractScheduledWork<?>) {
			AbstractScheduledWork<?> other = (AbstractScheduledWork<?>) o;
			long diff = getRunTime() - other.getRunTime();
			return Long.signum(diff);
		}
		long diff = getNanosDelay() - o.getDelay(TimeUnit.NANOSECONDS);
		return Long.signum(diff);
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return getCompletableFuture().get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return getCompletableFuture().get(timeout, unit);
	}
	
}
