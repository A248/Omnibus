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

package space.arim.omnibus.defaultimpl.registry;

import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.registry.RegistryEvent;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Event queue which allows implementing deadlock-free, sequentially-consistent,
 * and mutually exclusive event firing: <br>
 * 1. Registry events will be fired in the same order with respect to each other
 *   as if all registrations and unregistrations occurred sequentially.
 * 2. No deadlocks between firing of events are possible, no matter whether
 *   user code takes locks while listening to such events.
 * 3. Events will be fired one at a time, such that the next event will only be
 *   fired once the previous event has finished firing.
 *
 */
class FifoEventQueue {

	private final Queue<RegistryEvent<?>> queue = new ConcurrentLinkedQueue<>();
	/**
	 * Maintains a soft spin-lock with three states. Can be NOTHING, TRANSPOSING, or FIRING. <br>
	 * <br>
	 * Only 1 thread holds the lock at any time. Others may observe the state.
	 */
	private volatile int queueState;
	private static final VarHandle QUEUE_STATE;

	private static final int UNHELD = 0;
	private static final int TRANSPOSING = 1;
	private static final int FIRING = 2;

	static {
		try {
			QUEUE_STATE = MethodHandles.lookup().findVarHandle(FifoEventQueue.class, "queueState", int.class);
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	void offer(RegistryEvent<?> event) {
		queue.offer(event);
	}

	void fireEvents(EventBus eventBus) {
		spinLoop:
		while (true) {
			int witnessValue = (int) QUEUE_STATE.compareAndExchange(this, UNHELD, TRANSPOSING);
			switch (witnessValue) {
			case UNHELD:
				// Successful CAS. We now own the lock
				break spinLoop;

			case TRANSPOSING:
				// Spin while waiting for something to happen
				Thread.onSpinWait();
				continue spinLoop;

			case FIRING:
				/*
				 * OK. Another thread is firing events. That thread will re-poll
				 * the queue and detect any of our events. Our events may be fired
				 * after this method returns.
				 */
				return;
			default:
				throw new IllegalStateException("Unknown state " + witnessValue);
			}
		}
		RegistryEvent<?> event;
		while ((event = queue.poll()) != null) {
			queueState = FIRING;
			eventBus.fireAsyncEventWithoutFuture(event);
			queueState = TRANSPOSING;
		}
		queueState = UNHELD;
	}

}
