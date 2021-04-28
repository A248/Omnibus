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

import space.arim.omnibus.events.AsyncEvent;
import space.arim.omnibus.events.AsynchronousEventConsumer;
import space.arim.omnibus.events.Event;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.events.EventBusDriver;
import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.RegisteredListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

class TestingEventBus implements EventBus {

	private volatile Consumer<Event> eventListener;
	private final List<Event> events = Collections.synchronizedList(new ArrayList<>());

	TestingEventBus(Consumer<Event> eventListener) {
		this.eventListener = eventListener;
	}

	TestingEventBus() {
		this((e) -> {});
	}

	void setListener(Consumer<Event> eventListener) {
		this.eventListener = Objects.requireNonNull(eventListener);
	}

	public List<Event> events() {
		return events;
	}

	public Collection<Event> eventsUnordered() {
		return Set.copyOf(events);
	}

	@Override
	public <E extends Event> void fireEvent(E event) {
		eventListener.accept(event);
		events.add(event);
	}

	@Override
	public <E extends AsyncEvent> CompletableFuture<E> fireAsyncEvent(E event) {
		eventListener.accept(event);
		events.add(event);
		return CompletableFuture.completedFuture(event);
	}

	@Override
	public <E extends AsyncEvent> void fireAsyncEventWithoutFuture(E event) {
		eventListener.accept(event);
		events.add(event);
	}

	@Override
	public <E extends Event> RegisteredListener registerListener(Class<E> eventClass, byte priority, EventConsumer<? super E> eventConsumer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends AsyncEvent> RegisteredListener registerListener(Class<E> eventClass, byte priority, AsynchronousEventConsumer<? super E> asyncEventConsumer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void registerListeningMethods(Object annotatedListener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unregisterListener(RegisteredListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unregisterListeningMethods(Object annotatedListener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EventBusDriver getDriver() {
		throw new UnsupportedOperationException();
	}
}
