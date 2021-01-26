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
import space.arim.omnibus.events.EventBusDriver;
import space.arim.omnibus.events.EventFireController;
import space.arim.omnibus.events.RegisteredListener;
import space.arim.omnibus.util.ArraysUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

class DefaultEventsDriver implements EventBusDriver {

	/**
	 * The listeners themselves, a map of usually abstract event classes to event
	 * listeners
	 */
	private final ConcurrentMap<Class<?>, Listener<?>[]> eventListeners = new ConcurrentHashMap<>();

	/**
	 * The baked listeners, a map of concrete event classes to baked groups of
	 * listeners
	 */
	private final ConcurrentMap<Class<?>, BakedListenerGroup> bakedListeners = new ConcurrentHashMap<>();

	/*
	 * Listener order computation and caching
	 */

	@SuppressWarnings("unchecked")
	private static <E> Listener<E>[] createGenericArray(int size) {
		return (Listener<E>[]) new Listener<?>[size];
	}

	private static <E> Listener<E>[] combineListeners(Listener<E>[] existingListeners,
													  Listener<E>[] appendListeners) {
		int existingSize = existingListeners.length;
		if (existingSize == 0) {
			return appendListeners;
		}
		int requiredSize = existingSize + appendListeners.length;
		Listener<E>[] expanded = createGenericArray(requiredSize);
		System.arraycopy(existingListeners, 0, expanded, 0, existingSize);
		System.arraycopy(appendListeners, 0, expanded, existingSize, appendListeners.length);
		return expanded;
	}

	private <E> BakedListenerGroup computeListenersFor(Class<?> eventClass) {
		Listener<E>[] listeners = createGenericArray(0);
		Set<Class<?>> eventClasses = new HashSet<>();

		for (Map.Entry<Class<?>, Listener<?>[]> entry : eventListeners.entrySet()) {

			Class<?> thisEventClass = entry.getKey();
			if (!thisEventClass.isAssignableFrom(eventClass)) {
				continue;
			}
			eventClasses.add(thisEventClass);

			@SuppressWarnings("unchecked")
			Listener<E>[] fromThisEvt = (Listener<E>[]) entry.getValue();
			listeners = combineListeners(listeners, fromThisEvt);
		}
		Arrays.sort(listeners);
		return new BakedListenerGroup(eventClasses, listeners);
	}

	private void uncacheBaked(Class<?> eventClass) {
		bakedListeners.values().removeIf((listenerGroup) -> listenerGroup.eventClasses().contains(eventClass));
	}

	<E> Listener<E>[] getListenersTo(E event) {
		BakedListenerGroup listenerGroup = bakedListeners.computeIfAbsent(event.getClass(), this::computeListenersFor);
		@SuppressWarnings("unchecked")
		Listener<E>[] toInvoke = (Listener<E>[]) listenerGroup.listeners();
		return toInvoke;
	}

	/*
	 * Listener invocation
	 */

	private static <E> void callSyncListener(SynchronousListener<E> invoke, E event) {
		Consumer<? super E> eventConsumer = invoke.getEventConsumer();
		try {
			eventConsumer.accept(event);
		} catch (Exception ex) {
			logException(eventConsumer, event, ex);
		}
	}

	private static <E> void callSyncListeners(Listener<E>[] toInvoke, E event) {
		for (Listener<E> listener : toInvoke) {
			callSyncListener((SynchronousListener<E>) listener, event);
		}
	}

	static <E extends AsyncEvent> void callAsyncListeners(final Listener<E>[] toInvoke, final int invokeIndex,
														  final E event, final CompletableFuture<E> future) {
		for (int n = invokeIndex; n < toInvoke.length; n++) {
			Listener<E> listener = toInvoke[n];
			if (listener instanceof SynchronousListener) {
				callSyncListener((SynchronousListener<E>) listener, event);

			} else {
				int nextIndex = n + 1;
				AsynchronousListener<? super E> asyncListener = (AsynchronousListener<E>) listener;
				AsynchronousEventConsumer<? super E> asyncEventConsumer = asyncListener.getEventConsumer();
				EventFireController controller = new EventFireController() {

					private final AtomicBoolean fired = new AtomicBoolean();

					@Override
					public void continueFire() {
						if (!fired.compareAndSet(false, true)) {
							throw new IllegalStateException("Already fired");
						}
						callAsyncListeners(toInvoke, nextIndex, event, future);
					}
				};
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

	// Exception logging

	private static void logException(Object eventConsumer, Object event, Exception ex) {
		LoggerHolder.LOGGER.log(System.Logger.Level.WARNING,
				"Exception while calling event " + event + " for event consumer " + eventConsumer,
				ex);
	}

	private static final class LoggerHolder {
		static final System.Logger LOGGER = System.getLogger(DefaultEventsDriver.class.getName());
	}

	/*
	 * Listener registration
	 */

	<E> void registerListener(Listener<E> listener) {
		Class<E> eventClass = listener.getEventClass();
		eventListeners.compute(eventClass, (c, existingListeners) -> {
			// No existing listeners
			if (existingListeners == null) {
				return new Listener<?>[]{listener};
			}
			// Add the listener maintaining sorting
			int insertionIndex = -(Arrays.binarySearch(existingListeners, listener) + 1);
			return ArraysUtil.expandAndInsert(existingListeners, listener, insertionIndex);
		});
		uncacheBaked(eventClass);
	}

	void unregisterListener(Listener<?> listener) {
		Class<?> eventClass = listener.getEventClass();
		eventListeners.computeIfPresent(eventClass, (c, existingListeners) -> {
			int removalIndex = Arrays.binarySearch(existingListeners, listener);
			if (removalIndex < 0) {
				// Not present
				return existingListeners;
			}
			Listener<?>[] updated = ArraysUtil.contractAndRemove(existingListeners, removalIndex);
			if (updated.length == 0) {
				// Clean unused mappings
				return null;
			}
			return updated;
		});
		uncacheBaked(eventClass);
	}

	/*
	 * EventBusDriver API
	 */

	@Override
	public void fireEvent(Object event) {
		if (event == null) {
			throw new NullPointerException("event");
		}
		if (event instanceof AsyncEvent) {
			throw new IllegalArgumentException("Cannot use #fireEvent with asynchronous capable events");
		}
		callSyncListeners(getListenersTo(event), event);
	}

	@Override
	public <E> RegisteredListener registerListener(Class<E> eventClass, byte priority,
												   Consumer<? super E> eventConsumer) {
		Listener<E> listener = new SynchronousListener<>(eventClass, priority, eventConsumer);
		registerListener(listener);
		return listener;
	}

	@Override
	public String debugRegisteredListeners(Class<?> eventClass) {
		StringWriter output = new StringWriter();
		try {
			debugRegisteredListeners(eventClass, output);
		} catch (IOException ex) {
			throw new UncheckedIOException("StringWriter should not throw IOException", ex);
		}
		return output.toString();
	}

	@Override
	public void debugRegisteredListeners(Class<?> eventClass, Appendable output) throws IOException {
		new EventClassDebug(output, true, "").debugEventClass(
				eventClass, eventListeners.get(eventClass), bakedListeners.get(eventClass));
	}

	@Override
	public void debugEntireDriverState(Appendable output) throws IOException {
		output.append("Entire state of ").append(this.toString());
		EventClassDebug eventClassDebug = new EventClassDebug(output, false, "  ");
		for (Map.Entry<Class<?>, Listener<?>[]> entry : eventListeners.entrySet()) {
			Class<?> eventClass = entry.getKey();
			eventClassDebug.debugEventClass(
					eventClass, entry.getValue(), bakedListeners.get(eventClass));
		}
		if (!eventClassDebug.wroteAnything()) {
			output.append('\n').append("(Empty)");
		}
	}

}
