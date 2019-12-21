/* 
 * UniversalEvents, a common server event-handling api
 * Copyright © 2019 Anand Beh <https://www.arim.space>
 * 
 * UniversalEvents is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalEvents is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalEvents. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class UniversalEvents {
	
	/**
	 * The id of the instance
	 * 
	 */
	private final String id;
	
	/**
	 * All listeners
	 * 
	 */
	private final ConcurrentHashMap<Class<? extends Event>, List<EventListener>> listeners = new ConcurrentHashMap<Class<? extends Event>, List<EventListener>>();
	
	/**
	 * Used to sort listeners based on priority
	 */
	private static final Comparator<EventListener> PRIORITY_COMPARATOR = (l1, l2) -> l1.getPriority() - l2.getPriority();
	
	/**
	 * Instances map to prevent duplicate ids
	 * 
	 */
	private static final ConcurrentHashMap<String, UniversalEvents> INSTANCES = new ConcurrentHashMap<String, UniversalEvents>();
	
	/**
	 * The default instance id
	 * 
	 */
	public static final String DEFAULT_ID = "main";
	
	// Control instantiation
	private UniversalEvents(String id) {
		this.id = id;
	}
	
	public static UniversalEvents get(String id) {
		if (!INSTANCES.containsKey(id)) {
			UniversalEvents events = new UniversalEvents(id);
			INSTANCES.put(id, events);
		}
		return INSTANCES.get(id);
	}
	
	public static UniversalEvents get() {
		return get(DEFAULT_ID);
	}
	
	/**
	 * Returns the id of this UniversalEvents instance. <br>
	 * <br>
	 * For the main instance, it is {@link #DEFAULT_ID}
	 * 
	 * @return String - the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Adds/registers an event listener
	 * 
	 * @param <E> - event
	 * @param eventType - event class
	 * @param listener - the listener to register
	 */
	public synchronized <E extends Event> void addListener(Class<E> eventType, EventListener listener) {
		if (listeners.containsKey(eventType)) {
			listeners.get(eventType).add(listener);
			listeners.get(eventType).sort(PRIORITY_COMPARATOR);
		} else {
			listeners.put(eventType, new ArrayList<EventListener>(Arrays.asList(listener)));
		}
	}
	
	/**
	 * Gets all listeners for an event. <br>
	 * <br>
	 * If there are no listeners for an event, an empty list is returned.
	 * Otherwise, the returned list is backed by the internal registry.
	 * 
	 * @param <E> - event
	 * @param eventType - event class
	 * @return immutable list sorted according to priority of listenes. Empty if no listeners exist.
	 */
	public <E extends Event> List<EventListener> getListeners(Class<E> eventType) {
		return (listeners.containsKey(eventType)) ? Collections.unmodifiableList(listeners.get(eventType)) : Collections.emptyList();
	}
	
	/**
	 * Fires an event, invoking all applicable listeners
	 * 
	 * @param <E> - event
	 * @param event - the event itself
	 * @return false if the event is a CancellableEvent and was cancelled, true otherwise
	 * 
	 * @see CancellableEvent
	 */
	public <E extends Event> boolean fireEvent(E event) {
		listeners.forEach((eventType, listeners) -> {
			if (eventType.isInstance(event)) {
				listeners.forEach((listener) -> {
					try {
						listener.listen(event);
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
				});
			}
		});
		return (!(event instanceof Cancellable)) || !((Cancellable) event).isCancelled();
	}
	
}
