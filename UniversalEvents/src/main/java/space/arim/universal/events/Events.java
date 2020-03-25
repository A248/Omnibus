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

import space.arim.universal.util.Util;

/**
 * A framework for firing events and listening to them. <br>
 * <b>For an implementation, use {@link UniversalEvents}</b> <br>
 * <br>
 * <b>Usage</b>: to fire and listen to events. <br>
 * To fire events: {@link #fireEvent(Event)} <br>
 * To listen to events: {@link Listen} <br>
 * 
 * @author A248
 *
 */
public interface Events {

	/**
	 * Gets the {@link Util} instance corresponding to this event manager. <br>
	 * <br>
	 * The returned Util instance is the same one used to validate the truthfulness of {@link Event#isAsynchronous()} values.
	 * 
	 * @return Util the accompanying utility instance
	 */
	Util getUtil();
	
	/**
	 * Fires an event, invoking all applicable listeners. <br>
	 * If {@link Event#isAsynchronous()} returns untruthfully, an unchecked exception is thrown.
	 * 
	 * @param <E> event
	 * @param event the event itself
	 * @return false if the event is a Cancellable and was cancelled, true otherwise
	 * 
	 * @see Cancellable
	 */
	<E extends Event> boolean fireEvent(E event);
	
	/**
	 * Registers an object to listen to events. <br>
	 * <br>
	 * In the object registered, listening methods must have the {@link Listen} annotation.
	 * 
	 * @param listener the object to register
	 */
	void register(Listener listener);
	
	/**
	 * Dynamically registers a listener.
	 * 
	 * @param <E> the type of the event
	 * @param event the event class
	 * @param priority the priority at which the listener listens, same as in {@link Listen}
	 * @param listener the logic to run when the event fires
	 * @return a listener which may be unregistered when necessary
	 */
	<E extends Event> Listener register(Class<E> event, byte priority, Consumer<E> listener);
	
	/**
	 * Unregister an object from any listening. <br>
	 * <br>
	 * Opposite of {@link #register(Listener)}
	 * 
	 * @param listener the object to unregister
	 */
	void unregister(Listener listener);
	
}
