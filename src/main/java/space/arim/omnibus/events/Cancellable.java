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
 * An event which may be cancelled via {@link #cancel()}. <br>
 * <br>
 * This is an interface, and not an abstract class, so that users may
 * define their own object hierarchies. Nevertheless, users should ensure the visibility
 * of cancellation in {@link #isCancelled()}.
 * 
 * @author A248
 *
 */
public interface Cancellable extends Event {
	
	/**
	 * Marks an event as cancelled. The event may never be "uncancelled". <br>
	 * <br>
	 * The effect of cancellation is determined by the implementer of the event.
	 * Event listeners are always invoked, regardless of cancellation.
	 * 
	 */
	void cancel();
	
	/**
	 * Whether the event has been cancelled, possibly by another listener. <br>
	 * <br>
	 * To preserve the integrity of this method call in concurrent environments,
	 * implementers should guarantee memory-consistency effects.
	 * 
	 * @return true if the event is cancelled, false otherwise
	 */
	boolean isCancelled();
	
}
