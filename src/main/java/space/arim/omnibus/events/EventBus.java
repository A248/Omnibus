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
package space.arim.omnibus.events;

/**
 * A framework for firing events and listening to them. <br>
 * <br>
 * To listen to events, use {@link #registerListener(Class, byte, EventConsumer)}. The returned {@code Listener}
 * may be unregistered later. <br>
 * <br>
 * The event bus has guarantees regarding inheritance of events. When an event is fired, every listener which
 * specified an event type which is assignment compatible with the event object will be invoked. That is,
 * the listener will be called if the event is an instance of the specified event type. <br>
 * <br>
 * Note well that, unlike some other event buses, implementations are required to provide consistent ordering
 * of all listeners invoked by this method according to their priorities, as opposed to merely ordering within
 * listeners specifying a certain event class. Thus, all applicable listeners to an event are <i>always</i>
 * invoked in order of priority, no matter which event nor its inheritance heirarchy. <br>
 * <br>
 * To fire events, implement {@link Event} on the event object. Construct such an object and call one of the
 * {@code fire} methods. <br>
 * <br>
 * Null parameters are not permitted on any method. {@code NullPointerException} is thrown otherwise
 * 
 * @author A248
 *
 */
public interface EventBus {
	
	/**
	 * Fires an event, invoking all applicable listeners. <br>
	 * <br>
	 * A listener will be considered applicable if the event fired is an instance of the listener's targeted event class.
	 * 
	 * @param <E> the event type
	 * @param event the event itself
	 */
	<E extends Event> void fireEvent(E event);
	
	/**
	 * Creates and registers a listener, returning the created listener. <br>
	 * The returned listener may be unregistered when desired. <br>
	 * <br>
	 * The same {@link EventConsumer} may be used to create a registered listener via this method multiple times without issue.
	 * It may even be used with the same priority for the same event. <br>
	 * <br>
	 * The priority determines the order in which listeners are called. Lower priorities are called first. See the class javadoc
	 * for more information on priorities. <br>
	 * <br>
	 * The event class specifies all events the listener will listen to. See the class javadoc for more information.
	 * 
	 * @param <E> the event type
	 * @param event the event class
	 * @param priority the priority at which the listener listens
	 * @param evtConsumer the logic to run when the event fires
	 * @return a listener which may be unregistered when necessary
	 */
	<E extends Event> RegisteredListener registerListener(Class<E> event, byte priority, EventConsumer<? super E> evtConsumer);
	
	/**
	 * Unregister a listener. <br>
	 * <br>
	 * This is the opposite of {@link #registerListener(Class, byte, EventConsumer)}.
	 * Unregistering a listener which is not registered is a no-op.
	 * 
	 * @param listener the object to unregister
	 */
	void unregisterListener(RegisteredListener listener);
	
}
