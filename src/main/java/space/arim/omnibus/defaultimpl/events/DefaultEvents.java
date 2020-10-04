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
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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

	/** The listeners themselves, a map of event classes to listener methods. */
	private final ConcurrentHashMap<Class<?>, ListenerImpl<?>[]> eventListeners = new ConcurrentHashMap<>();
	
	private static final int EXTRA_ARRAY_CAPACITY = 16;
	
	private static final Comparator<ListenerImpl<?>> LISTENER_COMPARATOR = Comparator.nullsLast(Comparator.naturalOrder());
	
	/**
	 * Creates an instance.
	 */
	public DefaultEvents() {
		
	}

	@SuppressWarnings("unchecked")
	private static <E extends Event> ListenerImpl<E>[] ensureCapacity(ListenerImpl<E>[] array, int requiredSize) {
		if (array == null) {
			return (ListenerImpl<E>[]) new ListenerImpl<?>[requiredSize + EXTRA_ARRAY_CAPACITY];
		}
		if (requiredSize <= array.length) {
			return array;
		}
		return Arrays.copyOf(array, requiredSize + EXTRA_ARRAY_CAPACITY, ListenerImpl[].class);
	}

	@Override
	public <E extends Event> void fireEvent(E event) {
		Objects.requireNonNull(event, "event");
		int totalSize = 0;
		ListenerImpl<E>[] toInvoke = null;
		for (Map.Entry<Class<?>, ListenerImpl<?>[]> pair : eventListeners.entrySet()) {
			if (pair.getKey().isInstance(event)) {

				ListenerImpl<?>[] fromThisEvt = pair.getValue();
				int updateSize = totalSize + fromThisEvt.length;

				toInvoke = ensureCapacity(toInvoke, updateSize);
				System.arraycopy(fromThisEvt, 0, toInvoke, totalSize, fromThisEvt.length);
				totalSize = updateSize;
			}
		}
		if (toInvoke != null) {
			assert totalSize > 0 : totalSize;
			Arrays.sort(toInvoke, LISTENER_COMPARATOR);
			for (ListenerImpl<E> listener : toInvoke) {
				if (listener == null) {
					// End of array
					break;
				}
				try {
					listener.evtConsumer.accept(event);
				} catch (RuntimeException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public <E extends Event> RegisteredListener registerListener(Class<E> event, byte priority, EventConsumer<? super E> evtConsumer) {
		Objects.requireNonNull(event, "Event class must not be null");
		Objects.requireNonNull(evtConsumer, "Event consumer must not be null");
		ListenerImpl<E> listener = new ListenerImpl<E>(event, priority, evtConsumer);

		eventListeners.compute(event, (c, existingListeners) -> {
			// No existing methods
			if (existingListeners == null) {
				return new ListenerImpl<?>[] {listener};
			}
			// Add the method maintaining sorting
			int insertionIndex = - (Arrays.binarySearch(existingListeners, listener) + 1);
			return ArraysUtil.expandAndInsert(existingListeners, listener, insertionIndex);
		});

		return listener;
	}

	@Override
	public void unregisterListener(RegisteredListener listener) {
		Objects.requireNonNull(listener, "RegisteredListener must not be null");
		if (!(listener instanceof ListenerImpl<?>)) {
			throw new IllegalArgumentException("Foreign implementation of RegisteredListener: " + listener);
		}
		eventListeners.computeIfPresent(((ListenerImpl<?>) listener).evtClass, (c, existingListeners) -> {
			int removalIndex = Arrays.binarySearch(existingListeners, listener);
			if (removalIndex < 0) {
				// not present
				return existingListeners;
			}
			ListenerImpl<?>[] updated = ArraysUtil.contractAndRemove(existingListeners, removalIndex);
			if (updated.length == 0) {
				// clean unused mappings
				return null;
			}
			return updated;
		});
	}
	
}
