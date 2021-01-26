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

import space.arim.omnibus.events.AsyncEvent;
import space.arim.omnibus.events.AsynchronousEventConsumer;
import space.arim.omnibus.events.Event;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.events.EventBusDriver;
import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.RegisteredListener;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The default implementation of {@link EventBus}.
 *
 * @author A248
 */
public class DefaultEvents implements EventBus {

	private final DefaultEventsDriver driver = new DefaultEventsDriver();

	/**
	 * Creates an instance.
	 */
	public DefaultEvents() {
	}

	@Override
	public EventBusDriver getDriver() {
		return driver;
	}

	/*
	 *
	 * Event firing
	 *
	 */

	@Override
	public <E extends Event> void fireEvent(E event) {
		driver.fireEvent(event);
	}

	private <E extends AsyncEvent> void fireAsyncEventCommon(E event, CompletableFuture<E> future) {
		Listener<E>[] toInvoke = driver.getListenersTo(event);
		DefaultEventsDriver.callAsyncListeners(toInvoke, 0, event, future);
	}

	@Override
	public <E extends AsyncEvent> CompletableFuture<E> fireAsyncEvent(E event) {
		if (event == null) {
			throw new NullPointerException("event");
		}
		CompletableFuture<E> future = new CompletableFuture<>();
		fireAsyncEventCommon(event, future);
		return future;
	}

	@Override
	public <E extends AsyncEvent> void fireAsyncEventWithoutFuture(E event) {
		if (event == null) {
			throw new NullPointerException("event");
		}
		fireAsyncEventCommon(event, null);
	}

	/*
	 * Listener registration
	 */

	@Override
	public <E extends Event> RegisteredListener registerListener(Class<E> eventClass, byte priority,
			EventConsumer<? super E> eventConsumer) {
		return driver.registerListener(eventClass, priority, eventConsumer);
	}

	@Override
	public <E extends AsyncEvent> RegisteredListener registerListener(Class<E> eventClass, byte priority,
			AsynchronousEventConsumer<? super E> asyncEventConsumer) {
		Listener<E> listener = new AsynchronousListener<>(eventClass, priority, asyncEventConsumer);
		driver.registerListener(listener);
		return listener;
	}

	@Override
	public void unregisterListener(RegisteredListener listener) {
		Objects.requireNonNull(listener, "listener");
		if (!(listener instanceof Listener<?>)) {
			return;
		}
		driver.unregisterListener((Listener<?>) listener);
	}

	/*
	 * Annotated listeners
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
				driver.registerListener(transformedListener);
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
				driver.unregisterListener(transformedListener);
			}
			return null;
		});
	}

}
