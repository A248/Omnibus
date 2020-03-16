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

/**
 * An event which may be cancelled via {@link #setCancelled(boolean)}. <br>
 * <br>
 * <b>Implementations MUST be thread safe!</b> <br>
 * Programmers should use a <code>volatile</code> variable to track whether
 * the event is cancelled internally; <code>volatile</code> ensures the value
 * can be read safely by multiple threads operating on the same event concurrently.
 * 
 * @author A248
 *
 */
public interface Cancellable {
	
	/**
	 * Whether the event has been cancelled, presumably by another listener. <br>
	 * <br>
	 * <b>Implementations MUST be thread safe!</b> Use a <code>volatile</code> variable.
	 * 
	 * @return true if the event is cancelled, false otherwise
	 */
	boolean isCancelled();
	
	/**
	 * Cancels (or uncancels) the event. <br>
	 * <br>
	 * <b>Implementations MUST be thread safe!</b> Use a <code>volatile</code> variable.
	 * 
	 * @param cancelled whether or not to cancel
	 */
	void setCancelled(boolean cancelled);
	
}
