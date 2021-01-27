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

package space.arim.omnibus.events;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * A low-level complement to a {@link EventBus}. <br>
 * <br>
 * In the driver, events need not implement {@code Event} strictly. This is done
 * to enable compatibility with other event systems. Most users should refrain
 * from using the driver and prefer the standard interface instead.
 */
public interface EventBusDriver {

	/**
	 * Fires an event, with the normal restriction that the event implement {@code Event} relaxed. <br>
	 * <br>
	 * In all other details, this is equivalent to {@link EventBus#fireEvent(Event)}, including with
	 * the requirement that the event <i>NOT</i> be a {@code AsyncEvent}
	 *
	 * @param event the event to fire
	 * @throws IllegalArgumentException if {@code event} is an {@link AsyncEvent}
	 */
	void fireEvent(Object event);

	/**
	 * Registers a listener, with the normal restriction that the event implement {@code Event} relaxed. <br>
	 * <br>
	 * In all other details, this is equivalent to {@link EventBus#registerListener(Class, byte, EventConsumer)}
	 *
	 * @param eventClass    the event class
	 * @param priority      the priority at which the listener is placed
	 * @param eventConsumer the event consumer
	 * @param <E>           the event type
	 * @return a registered listener
	 * @throws IllegalArgumentException if the event class is an array, a primitive, or {@code Object}
	 */
	<E> RegisteredListener registerListener(Class<E> eventClass, byte priority, Consumer<? super E> eventConsumer);

	/**
	 * Generates brief debug report for an event class. May yield information about
	 * listeners registered for such event, cached listeners, etc. <br>
	 * <br>
	 * The driver is free to implement this method any which way. Note that some drivers
	 * may store registered listeners using different approaches.
	 *
	 * @param eventClass the event class
	 * @return a debug report
	 */
	String debugRegisteredListeners(Class<?> eventClass);

	/**
	 * Generates brief debug report for an event class. Version of {@link #debugRegisteredListeners(Class)}
	 * which sends the output to an {@code Appendable}. See such method for more information.
	 *
	 * @param eventClass the event class
	 * @param output     the appendable output
	 * @throws IOException if {@code output} did
	 */
	void debugRegisteredListeners(Class<?> eventClass, Appendable output) throws IOException;

	/**
	 * Generates an extensive debug report for the entire driver
	 *
	 * @param output the appendable output
	 * @throws IOException if {@code output} did
	 */
	void debugEntireDriverState(Appendable output) throws IOException;
}
