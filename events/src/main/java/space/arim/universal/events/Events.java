/* 
 * UniversalEvents, a common server event-handling api
 * Copyright © 2020 Anand Beh <https://www.arim.space>
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

import java.util.function.Consumer;

/**
 * A framework for firing events and listening to them. <b>For an implementation, use {@link UniversalEvents}</b> <br>
 * <br>
 * To listen to events, first create an object implementing {@link Listener} with listening methods. All listening methods
 * must be annotated with {@link Listen}. Then, register the listener using {@link #registerListener(Listener)}. <br>
 * <br>
 * To listen to events dynamically, use {@link #registerListener(Class, byte, Consumer)}. <br>
 * <br>
 * To fire events, implement {@link Event} on the event object. Construct such an object and call {@link #fireEvent(Event)}.
 * 
 * @author A248
 *
 */
public interface Events {
	
	/**
	 * Fires an event, invoking all applicable listeners. <br>
	 * <br>
	 * For any listener, if the event fired is an instance of the listener's targeted event class,
	 * as specified in either the parameter type of {@link Listen} or the event class
	 * passed to {@link #registerListener(Class, byte, Consumer)}, the listener will be invoked. <br>
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
	<E extends Event> boolean fireEvent(E event);
	
	/**
	 * Registers an object to listen to events. <br>
	 * <br>
	 * In the object registered, listening methods must have the {@link Listen} annotation. <br>
	 * Registering a listener already registered is a no-op.
	 * 
	 * @param listener the object to register
	 * @throws NullPointerException if the listener is null
	 */
	void registerListener(Listener listener);
	
	/**
	 * Creates and registers a dynamic listener, returning the created listener. <br>
	 * The returned listener may be unregistered when desired. <br>
	 * <br>
	 * The same Consumer may be registered multiple times without issue. It may even be
	 * registered with the same priority for the same event.
	 * 
	 * @param <E> the type of the event
	 * @param event the event class
	 * @param priority the priority at which the listener listens, same as in {@link Listen}
	 * @param listener the logic to run when the event fires
	 * @return a listener which may be unregistered when necessary
	 * @throws NullPointerException if the event class or listener is null
	 */
	<E extends Event> Listener registerListener(Class<E> event, byte priority, Consumer<? super E> listener);
	
	/**
	 * Unregister an object from any listening. <br>
	 * <br>
	 * This is the opposite of {@link #registerListener(Listener)} and {@link #registerListener(Class, byte, Consumer)}.
	 * Unregistering a listener which is not registered is a no-op.
	 * 
	 * @param listener the object to unregister
	 * @throws NullPointerException if the listener is null
	 */
	void unregisterListener(Listener listener);
	
}
