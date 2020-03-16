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
import space.arim.universal.events.Events;

/**
 * A helper class for implementing {@link Event} and {@link Cancellable},
 * ensuring the specifications of both are always complied with.
 * 
 * @author A248
 *
 */
public abstract class AbstractCancellable extends AbstractEvent implements Cancellable {
	
	private volatile boolean cancelled = false;
	
	/**
	 * Creates the event with a corresponding {@link Events} instance
	 * 
	 * @param events the events instance
	 */
	protected AbstractCancellable(Events events) {
		super(events);
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}
