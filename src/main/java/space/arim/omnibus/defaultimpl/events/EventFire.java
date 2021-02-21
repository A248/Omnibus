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

package space.arim.omnibus.defaultimpl.events;

import space.arim.omnibus.events.AsyncEvent;
import space.arim.omnibus.events.AsynchronousEventConsumer;
import space.arim.omnibus.events.EventFireController;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Responsible for invoking listeners. During the firing of an async event, an instance
 * holds state relating to the fire.
 *
 * @param <E> the async event
 */
final class EventFire<E extends AsyncEvent> {

	private final Listener<E>[] toInvoke;
	private final E event;
	private final CompletableFuture<E> future;

	@SuppressWarnings("unused")
	private int continuationIndex;
	private static final VarHandle CONTINUATION_INDEX;

	static {
		try {
			CONTINUATION_INDEX = MethodHandles.lookup().findVarHandle(EventFire.class, "continuationIndex", int.class);
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	EventFire(Listener<E>[] toInvoke, E event, CompletableFuture<E> future) {
		this.toInvoke = toInvoke;
		this.event = event;
		this.future = future;
	}

	// Plain listeners

	private static <E> void callSyncListener(SynchronousListener<E> invoke, E event) {
		Consumer<? super E> eventConsumer = invoke.getEventConsumer();
		try {
			eventConsumer.accept(event);
		} catch (Exception ex) {
			logException(eventConsumer, event, ex);
		}
	}

	static <E> void callSyncListeners(Listener<E>[] toInvoke, E event) {
		for (Listener<E> listener : toInvoke) {
			callSyncListener((SynchronousListener<E>) listener, event);
		}
	}

	// Async listeners

	void callAsyncListeners(final int startIndex) {
		for (int currentIndex = startIndex; currentIndex < toInvoke.length; currentIndex++) {
			Listener<E> listener = toInvoke[currentIndex];
			if (listener instanceof SynchronousListener) {
				callSyncListener((SynchronousListener<E>) listener, event);

			} else {
				AsynchronousListener<? super E> asyncListener = (AsynchronousListener<E>) listener;
				AsynchronousEventConsumer<? super E> asyncEventConsumer = asyncListener.getEventConsumer();
				EventFireController controller = new AsyncFireController(currentIndex);
				CONTINUATION_INDEX.setRelease(this, currentIndex);
				try {
					asyncEventConsumer.acceptAndContinue(event, controller);
					return;
				} catch (Exception ex) {
					logException(asyncEventConsumer, event, ex);
				}
			}
		}
		if (future != null) {
			future.complete(event);
		}
	}

	boolean changeIndex(int listenerIndex, int nextIndex) {
		// It's OK that the CAS uses a plain set for the exchange
		// The fail-fast behavior of EventFireController should only be used to detect bugs
		int witnessValue = (int) CONTINUATION_INDEX.compareAndExchangeAcquire(this, listenerIndex, nextIndex);
		return witnessValue == listenerIndex;
	}

	private class AsyncFireController implements EventFireController {

		private final int listenerIndex;

		AsyncFireController(int listenerIndex) {
			this.listenerIndex = listenerIndex;
		}

		@Override
		public void continueFire() {
			int nextIndex = listenerIndex + 1;
			if (!changeIndex(listenerIndex, nextIndex)) {
				throw new IllegalStateException("Already fired");
			}
			callAsyncListeners(nextIndex);
		}
	}

	// Exception logging

	private static void logException(Object eventConsumer, Object event, Exception ex) {
		LoggerHolder.LOGGER.log(System.Logger.Level.WARNING,
				"Exception while calling event " + event + " for event consumer " + eventConsumer,
				ex);
	}

	private static final class LoggerHolder {
		static final System.Logger LOGGER = System.getLogger(EventFire.class.getName());
	}

}
