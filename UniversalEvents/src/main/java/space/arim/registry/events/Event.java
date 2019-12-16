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

public abstract class Event {
	
	private final boolean asynchronous;
	
	public Event() {
		this(false);
	}
	
	/**
	 * Use this constructor for asynchronous events with parameter true
	 * 
	 * @param asynchronous - whether the event is asynchronous
	 */
	public Event(boolean asynchronous) {
		this.asynchronous = asynchronous;
	}
	
	/**
	 * Checks whether an event is running asynchronously <br>
	 * <br>
	 * The invoker of {@link Event#Event(boolean)} or {@link CancellableEvent#CancellableEvent(boolean)} is trusted with setting this value.
	 * 
	 * @return
	 */
	public boolean isAsynchronous() {
		return asynchronous;
	}
	
}
