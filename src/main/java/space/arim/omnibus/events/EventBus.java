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

import java.util.concurrent.CompletableFuture;

/**
 * A framework for firing events and listening to them. <br>
 * <br>
 * To listen to events, use
 * {@link #registerListener(Class, byte, EventConsumer)}. The returned
 * {@code Listener} may be unregistered later. <br>
 * <br>
 * The event bus has guarantees regarding inheritance of events. When an event
 * is fired, every listener which specified an event type which is assignment
 * compatible with the event object will be invoked. That is, the listener will
 * be called if the event is an instance of the specified event type. <br>
 * <br>
 * Note well that, unlike some other event buses, implementations are required
 * to provide consistent ordering of all listeners invoked by this method
 * according to their priorities, as opposed to merely ordering within listeners
 * specifying a certain event class. Thus, all applicable listeners to an event
 * are <i>always</i> invoked in order of priority, no matter which event nor its
 * inheritance heirarchy. <br>
 * <br>
 * To fire events, implement {@link Event} or {@link AsyncEvent} on the event
 * object and use one of the firing methods. <br>
 * <br>
 * Null parameters are not permitted on any method. {@code NullPointerException}
 * is thrown otherwise
 * 
 * @author A248
 *
 */
public interface EventBus {

	/**
	 * Fires an event, invoking all applicable listeners. <br>
	 * <br>
	 * Events capable of asynchronous listeners should not use this method.
	 * Attempting to do so will throw {@link IllegalArgumentException}
	 * 
	 * @param <E>   the event type
	 * @param event the event itself
	 * @throws IllegalArgumentException if {@code event} is an {@link AsyncEvent}
	 */
	<E extends Event> void fireEvent(E event);

	/**
	 * Fires an asynchronous capable event, invoking all applicable listeners. <br>
	 * <br>
	 * For dealing with asynchronous listeners, this method returns a future. The
	 * future will never complete exceptionally, but if an asynchronous listener
	 * somehow fails its responsibility to continue the event execution, the future
	 * may stall. Therefore callers should choose an appropriate timeout using
	 * {@link CompletableFuture#orTimeout(long, java.util.concurrent.TimeUnit)}
	 * 
	 * @param <E>   the event type
	 * @param event the event itself
	 * @return a future completed once all listeners have completed firing. The
	 *         future yields the event, for convenience.
	 */
	<E extends AsyncEvent> CompletableFuture<E> fireAsyncEvent(E event);

	/**
	 * Fires an asynchronous capable event, invoking all applicable listeners. <br>
	 * <br>
	 * This is equivalent to {@link #fireAsyncEvent(AsyncEvent)} except that any
	 * resulting future is discarded (creation of the future may be altogether
	 * avoided). This should be used when the caller wishes to proceed regardless of
	 * any listener modifications to the event.
	 * 
	 * @param <E>   the event type
	 * @param event the event itself
	 */
	<E extends AsyncEvent> void fireAsyncEventWithoutFuture(E event);

	/**
	 * Creates and registers a listener, returning the created listener. <br>
	 * The returned registered listener may be unregistered when desired. <br>
	 * <br>
	 * The same {@link EventConsumer} may be used to create a registered listener
	 * via this method multiple times without issue. It may even be used with the
	 * same priority for the same event. <br>
	 * <br>
	 * The priority determines the order in which listeners are called. Lower
	 * priorities are called first. See the class javadoc for more information on
	 * priorities. <br>
	 * <br>
	 * The event class specifies all events the listener will listen to. See the
	 * class javadoc for more information.
	 * 
	 * @param <E>           the event type
	 * @param eventClass    the event class. All instances of this class, including
	 *                      subclasses, will be listened to
	 * @param priority      the priority at which the listener is placed
	 * @param eventConsumer the logic to run when the event fires
	 * @return a listener which may be unregistered when necessary
	 */
	<E extends Event> RegisteredListener registerListener(Class<E> eventClass, byte priority,
			EventConsumer<? super E> eventConsumer);

	/**
	 * Creates and registers an asynchronous listener, returning the created
	 * listener. <br>
	 * The returned registered listener may be unregistered when desired. <br>
	 * <br>
	 * The same {@link AsynchronousEventConsumer} may be used to create a registered
	 * listener via this method multiple times without issue. It may even be used
	 * with the same priority for the same event. <br>
	 * <br>
	 * The priority determines the order in which listeners are called. Lower
	 * priorities are called first. See the class javadoc for more information on
	 * priorities. <br>
	 * <br>
	 * The event class specifies all events the listener will listen to. See the
	 * class javadoc for more information.
	 * 
	 * @param <E>                the event type
	 * @param eventClass         the event class. All instances of this class,
	 *                           including subclasses, will be listened to
	 * @param priority           the priority at which the listener is placed
	 * @param asyncEventConsumer the logic to run when the event fires
	 * @return a listener which may be unregistered when necessary
	 */
	<E extends AsyncEvent> RegisteredListener registerListener(Class<E> eventClass, byte priority,
			AsynchronousEventConsumer<? super E> asyncEventConsumer);

	/**
	 * Registers all methods on the target listener object which are annotated with
	 * {@link ListeningMethod}. Inherited methods are included. <br>
	 * <br>
	 * The runtime type of the listener object must be public. Additionally, it is
	 * in a named module, the module must be unconditionally exported.
	 * 
	 * @param annotatedListener the listener whose annotated methods to register
	 * @throws IllegalStateException    if the listener object is already registered
	 * @throws IllegalArgumentException if any method on the listener object
	 *                                  annotated with {@link ListeningMethod} does
	 *                                  not obey the requirements of it, or the
	 *                                  listener object is in an unexported package
	 */
	void registerListeningMethods(Object annotatedListener);

	/**
	 * Unregister a registered listener. <br>
	 * <br>
	 * This is the opposite of {@code #registerListener}. Unregistering a listener
	 * which is not currently registered is a no-op.
	 * 
	 * @param listener the registered listener to unregister
	 */
	void unregisterListener(RegisteredListener listener);

	/**
	 * Unregisters any methods on the target listener object which are annotated
	 * with {@link ListeningMethod} and were previously registered. <br>
	 * <br>
	 * If the listener object was never registered, this is a no-op.
	 * 
	 * @param annotatedListener the listener whose annotated methods to unregister
	 */
	void unregisterListeningMethods(Object annotatedListener);

}
