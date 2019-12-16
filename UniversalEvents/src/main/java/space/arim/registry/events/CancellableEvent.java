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
package space.arim.registry.events;

/**
 * An event which may be cancelled via {@link #setCancelled(boolean)}
 * 
 * @author A248
 *
 */
public abstract class CancellableEvent extends Event {

	private boolean cancelled = false;
	
	public CancellableEvent() {
		super();
	}
	
	/**
	 * Use this constructor for asynchronous events with parameter true <br>
	 * Calls super constructor {@link Event#Event(boolean)} with same parameter.
	 * 
	 * @param asynchronous - whether the event is asynchronous
	 */
	public CancellableEvent(boolean asynchronous) {
		super(asynchronous);
	}

	/**
	 * Whether the event has been cancelled, presumably by another listener
	 * 
	 * @return true if and only if the event is cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}
	
	/**
	 * Cancels/uncancels an event
	 * 
	 * @param cancelled - whether or not to cancel
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}
