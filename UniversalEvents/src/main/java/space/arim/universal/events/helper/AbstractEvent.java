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
package space.arim.universal.events.helper;

import space.arim.universal.events.Cancellable;
import space.arim.universal.events.Event;
import space.arim.universal.events.Events;

/**
 * A helper class for implementing {@link Event}, ensuring its specifications are always complied with. <br>
 * <br>
 * Example usage: <br>
 * <code>
 * public class MyEvent extends AbstractEvent {
 * 
 * }
 * </code>
 * 
 * @author A248
 *
 */
public class AbstractEvent implements Event {
	
	private final Events events;
	
	/**
	 * Creates the event with
	 * 
	 * @param events
	 */
	protected AbstractEvent(Events events) {
		this.events = events;
	}
	
	/**
	 * Whether the event is fired asynchronously. <br>
	 * This value is automatically detected.
	 * 
	 */
	@Override
	public final boolean isAsynchronous() {
		return events.getUtil().isAsynchronous();
	}
	
	/**
	 * Fires the event and returns the value of {@link Events#fireEvent(Event)} applied to itself.
	 * 
	 * @return false if the event is a {@link Cancellable} and was cancelled, true otherwise
	 */
	protected boolean fire() {
		return events.fireEvent(this);
	}
	
}
