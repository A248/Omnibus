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

/**
 * An event which may be cancelled via {@link #cancel()}. <br>
 * <br>
 * This is an interface, and not an abstract class, so that users may
 * define their own object hierarchies. Nevertheless, users must ensure the visibility
 * of cancellation in {@link #isCancelled()}. To do this, either {@link AbstractCancellable}
 * or an own memory-concise implementation may be used. <br>
 * <br>
 * The method calls in implementations of this interface are trusted by {@link EventBus}
 * implementations. They should never throw exceptions.
 * 
 * @author A248
 *
 */
public interface Cancellable extends Event {
	
	/**
	 * Marks an event as cancelled. The event may never be "uncancelled". <br>
	 * <br>
	 * The effect of cancellation is determined by the implementer of the event.
	 * Event listeners are always invoked, regardless of cancellation, except in the case
	 * {@link Listen#ignoreCancelled()} is true.
	 * 
	 */
	void cancel();
	
	/**
	 * Whether the event has been cancelled, possibly by another listener. <br>
	 * <br>
	 * To preserve the integrity of this method call in concurrent environments,
	 * implementers must guarantee memory-consistency effects. This is typically
	 * done with a volatile variable.
	 * 
	 * @return true if the event is cancelled, false otherwise
	 */
	boolean isCancelled();
	
}
