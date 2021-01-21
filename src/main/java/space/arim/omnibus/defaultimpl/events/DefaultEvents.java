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
package space.arim.omnibus.defaultimpl.events;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import space.arim.omnibus.events.AsyncEvent;
import space.arim.omnibus.events.AsynchronousEventConsumer;
import space.arim.omnibus.events.EventFireController;
import space.arim.omnibus.events.Event;
import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.events.RegisteredListener;
import space.arim.omnibus.util.ArraysUtil;

/**
 * The default implementation of {@link EventBus}.
 *
 * @author A248
 */
public class DefaultEvents implements EventBus {

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

	private static class BakedListenerGroup {

		final Set<Class<?>> eventClasses;
		final Listener<?>[] listeners;

		BakedListenerGroup(Set<Class<?>> eventClasses, Listener<?>[] listeners) {
			this.eventClasses = Set.copyOf(eventClasses);
			this.listeners = listeners;
		}
	}

	/**
	 * Creates an instance.
	 */
	public DefaultEvents() {
	}

	@SuppressWarnings("unchecked")
	private static <E extends Event> Listener<E>[] createGenericArray(int size) {
		return (Listener<E>[]) new Listener<?>[size];
	}

	private static <E extends Event> Listener<E>[] combineListeners(Listener<E>[] existingListeners,
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

	private <E extends Event> BakedListenerGroup computeListenersFor(Class<?> eventClass) {
		Listener<E>[] listeners = createGenericArray(0);
		Set<Class<?>> eventClasses = new HashSet<>();

		for (Entry<Class<?>, Listener<?>[]> entry : eventListeners.entrySet()) {

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
		bakedListeners.values().removeIf((listenerGroup) -> listenerGroup.eventClasses.contains(eventClass));
	}

	private <E extends Event> void callSyncListener(SynchronousListener<E> invoke, E event) {
		EventConsumer<? super E> eventConsumer = invoke.getEventConsumer();
		try {
			eventConsumer.accept(event);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
	}

	private <E extends Event> void callSyncListeners(Listener<E>[] toInvoke, E event) {
		for (Listener<E> listener : toInvoke) {
			callSyncListener((SynchronousListener<E>) listener, event);
		}
	}

	private <E extends AsyncEvent> void callAsyncListeners(Listener<E>[] toInvoke, int invokeIndex, E event,
			final CompletableFuture<E> future) {
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
				} catch (RuntimeException ex) {
					ex.printStackTrace();
				}
			}
		}
		if (future != null) {
			future.complete(event);
		}
	}

	private <E extends Event> Listener<E>[] getListenersTo(E event) {
		BakedListenerGroup listenerGroup = bakedListeners.computeIfAbsent(event.getClass(), this::computeListenersFor);
		@SuppressWarnings("unchecked")
		Listener<E>[] toInvoke = (Listener<E>[]) listenerGroup.listeners;
		return toInvoke;
	}

	@Override
	public <E extends Event> void fireEvent(E event) {
		Objects.requireNonNull(event, "event");
		if (event instanceof AsyncEvent) {
			throw new IllegalArgumentException("Cannot use EventBus#fireEvent with asynchronous capable events");
		}

		callSyncListeners(getListenersTo(event), event);
	}

	private <E extends AsyncEvent> void fireAsyncEventCommon(E event, CompletableFuture<E> future) {
		Listener<E>[] toInvoke = getListenersTo(event);
		callAsyncListeners(toInvoke, 0, event, future);
	}

	@Override
	public <E extends AsyncEvent> CompletableFuture<E> fireAsyncEvent(E event) {
		Objects.requireNonNull(event, "event");

		CompletableFuture<E> future = new CompletableFuture<>();
		fireAsyncEventCommon(event, future);
		return future;
	}

	@Override
	public <E extends AsyncEvent> void fireAsyncEventWithoutFuture(E event) {
		Objects.requireNonNull(event, "event");

		fireAsyncEventCommon(event, null);
	}

	@Override
	public <E extends Event> RegisteredListener registerListener(Class<E> eventClass, byte priority,
			EventConsumer<? super E> eventConsumer) {
		Listener<E> listener = new SynchronousListener<>(eventClass, priority, eventConsumer);
		registerListener0(listener);
		return listener;
	}

	@Override
	public <E extends AsyncEvent> RegisteredListener registerListener(Class<E> eventClass, byte priority,
			AsynchronousEventConsumer<? super E> asyncEventConsumer) {
		Listener<E> listener = new AsynchronousListener<>(eventClass, priority, asyncEventConsumer);
		registerListener0(listener);
		return listener;
	}

	private <E extends Event> void registerListener0(Listener<E> listener) {
		Class<E> eventClass = listener.getEventClass();
		eventListeners.compute(eventClass, (c, existingListeners) -> {
			// No existing listeners
			if (existingListeners == null) {
				return new Listener<?>[] { listener };
			}
			// Add the listener maintaining sorting
			int insertionIndex = -(Arrays.binarySearch(existingListeners, listener) + 1);
			return ArraysUtil.expandAndInsert(existingListeners, listener, insertionIndex);
		});
		uncacheBaked(eventClass);
	}

	@Override
	public void unregisterListener(RegisteredListener listener) {
		Objects.requireNonNull(listener, "listener");
		if (!(listener instanceof Listener<?>)) {
			return;
		}
		unregisterListener0((Listener<?>) listener);
	}

	private void unregisterListener0(Listener<?> listener) {
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
	 * 
	 * Annotated listeners
	 * 
	 */

	/**
	 * Map of annotated listener objects to listener methods transformed into event
	 * consumers
	 */
	private final ConcurrentMap<IdentityListenerWrapper, Set<Listener<?>>> annotatedListenerObjects = new ConcurrentHashMap<>();

	@Override
	public void registerListeningMethods(Object annotatedListener) {
		Objects.requireNonNull(annotatedListener, "annotatedListener");
		Set<Listener<?>> transformedListeners = new ListeningMethodScanner(annotatedListener).scanAndTransformAnnotatedMethods();
		if (transformedListeners.isEmpty()) {
			// No-op
			return;
		}
		var wrapper = new IdentityListenerWrapper(annotatedListener);
		annotatedListenerObjects.compute(wrapper, (w, previousListeners) -> {
			if (previousListeners != null) {
				throw new IllegalStateException("Listener " + wrapper + " is already registered");
			}
			for (Listener<?> transformedListener : transformedListeners) {
				registerListener0(transformedListener);
			}
			return transformedListeners;
		});
	}

	@Override
	public void unregisterListeningMethods(Object annotatedListener) {
		Objects.requireNonNull(annotatedListener, "annotatedListener");
		var wrapper = new IdentityListenerWrapper(annotatedListener);
		annotatedListenerObjects.computeIfPresent(wrapper, (w, transformedListeners) -> {
			for (Listener<?> transformedListener : transformedListeners) {
				unregisterListener0(transformedListener);
			}
			return null;
		});
	}

}
