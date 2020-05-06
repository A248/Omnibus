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
package space.arim.universal.util.concurrent;

import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;

class ScheduledAction implements Comparable<ScheduledAction> {
	
	private final Runnable command;
	private final long when;
	
	private ScheduledAction(Runnable command, long when) {
		this.command = command;
		this.when = when;
	}
	
	void run() {
		command.run();
	}
	
	long getWhen() {
		return when;
	}
	
	long getWaitTime(long startTime) {
		return when - startTime;
	}
	
	@Override
	public int compareTo(ScheduledAction other) {
		return (int) (when - other.when);
	}
	
	private static void addDelayedTask(Runnable body, long delay) {
		if (delay > 0) {
			SchedulerThread.get().addTask(new ScheduledAction(body, System.currentTimeMillis() + delay));
		} else if (delay == 0) {
			body.run();
		}
	}
	
	static void schedule(Executor executor, Runnable cmd, long delay, BooleanSupplier cancellation) {
		addDelayedTask(() -> {
			if (!cancellation.getAsBoolean()) {
				executor.execute(cmd);
			}
		}, delay);
	}
	
	static void schedule(Executor executor, Runnable cmd, long delay, BooleanSupplier cancellation, LongUnaryOperator delayFunction) {
		addDelayedTask(() -> executeAndReschedule(executor, cmd, delay, cancellation, delayFunction), delay);
	}
	
	private static void executeAndReschedule(Executor executor, Runnable cmd, long delay, BooleanSupplier cancellation, LongUnaryOperator delayFunction) {
		if (!cancellation.getAsBoolean()) {
			executor.execute(cmd);
			if (!cancellation.getAsBoolean()) {
				schedule(executor, cmd, delayFunction.applyAsLong(delay), cancellation, delayFunction);
			}
		}
	}
	
	static void schedule(Executor executor, Runnable cmd, long delay, BooleanSupplier cancellation, LongBinaryOperator delayFunction) {
		addDelayedTask(() -> executeAndReschedule(executor, cmd, delay, cancellation, delayFunction), delay);
	}
	
	private static void executeAndReschedule(Executor executor, Runnable cmd, long delay, BooleanSupplier cancellation, LongBinaryOperator delayFunction) {
		if (!cancellation.getAsBoolean()) {
			long start = System.currentTimeMillis();
			executor.execute(cmd);
			if (!cancellation.getAsBoolean()) {
				schedule(executor, cmd, delayFunction.applyAsLong(delay, System.currentTimeMillis() - start), cancellation, delayFunction);
			}
		}
	}
	
}
