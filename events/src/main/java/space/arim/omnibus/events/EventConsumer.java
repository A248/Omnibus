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

import java.util.function.Consumer;

/**
 * Functional interface for event listeners, to be passed to {@link EventBus#registerListener(Class, byte, EventConsumer)}.
 * 
 * @author A248
 *
 * @param <E> the event type
 */
public interface EventConsumer<E extends Event> extends Consumer<E> {

	/**
	 * Acts on the event listened to
	 * 
	 */
	@Override
	void accept(E event);
	
	/**
	 * Turns a plain {@link Consumer} into an {@code EventConsumer}
	 * 
	 * @param <E> the event type
	 * @param consumer the consumer
	 * @return the consumer as an event consumer
	 */
	static <E extends Event> EventConsumer<E> decorate(Consumer<E> consumer) {
		return consumer::accept;
	}
	
}
