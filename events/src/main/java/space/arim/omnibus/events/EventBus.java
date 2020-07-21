/* 
 * Omnibus-events
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-events is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-events is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-events. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.events;

/**
 * A framework for firing events and listening to them. <br>
 * <br>
 * To listen to events, use {@link #registerListener(Class, byte, EventConsumer)}. The returned {@code Listener}
 * may be unregistered later. <br>
 * <br>
 * Alternatively, create an object implementing {@link RegisteredListener} with listening methods. All listening methods
 * must be annotated with {@link Listen}. Then, register the listener using {@link #registerListener(RegisteredListener)}. <br>
 * <br>
 * To fire events, implement {@link Event} on the event object. Construct such an object and call {@link #fireEvent(Event)}.
 * 
 * @author A248
 *
 */
public interface EventBus {
	
	/**
	 * Fires an event, invoking all applicable listeners. <br>
	 * <br>
	 * For any listener, if the event fired is an instance of the listener's targeted event class,
	 * as specified in either the parameter type of {@link Listen} or the event class
	 * passed to {@link #registerListener(Class, byte, EventConsumer)}, the listener will be invoked. <br>
	 * <br>
	 * If the event is a {@link Cancellable} and was cancelled, this method will return <code>false</code>.
	 * Otherwise, it will return <code>true</code>
	 * 
	 * @param <E> the event type
	 * @param event the event itself
	 * @return false if the event is a Cancellable and was cancelled, true otherwise
	 * @throws NullPointerException if the event is null
	 * 
	 * @see Cancellable
	 */
	<E extends Event> void fireEvent(E event);
	
	/**
	 * Creates and registers a listener, returning the created listener. <br>
	 * The returned listener may be unregistered when desired. <br>
	 * <br>
	 * The same {@link EventConsumer} may be used to create a registered listener via this method multiple times without issue.
	 * It may even be used with the same priority for the same event.
	 * 
	 * @param <E> the event type
	 * @param event the event class
	 * @param priority the priority at which the listener listens, same as in {@link Listen}
	 * @param evtConsumer the logic to run when the event fires
	 * @return a listener which may be unregistered when necessary
	 * @throws NullPointerException if the event class or listener is null
	 */
	<E extends Event> RegisteredListener registerListener(Class<E> event, byte priority, EventConsumer<? super E> evtConsumer);
	
	/**
	 * Unregister a listener. <br>
	 * <br>
	 * This is the opposite of {@link #registerListener(Class, byte, EventConsumer)}.
	 * Unregistering a listener which is already unregistered is a no-op.
	 * 
	 * @param listener the object to unregister
	 * @throws NullPointerException if the listener is null
	 * @throws IllegalArgumentException if the {@code RegisteredListener} is a foreign implementation
	 */
	void unregisterListener(RegisteredListener listener);
	
}
