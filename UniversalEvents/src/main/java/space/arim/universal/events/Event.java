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
 * A general event, which runs either synchronously or asynchronously. <br>
 * <br>
 * See {@link #isAsynchronous()}
 * 
 * @author A248
 *
 */
public abstract class Event {
	
	private final boolean async;
	
	/**
	 * Creates the event, explicitly stating whether it is asynchronous, i.e.,
	 * not on the main thread. <br>
	 * The main thread is generally taken as the thread which does the most work. <br>
	 * <br>
	 * If this value does not match whether the event is actually fired asynchronously,
	 * an unchecked exception will be thrown when the event is attempted to be fired. <br>
	 * The Events implementation will most likely use
	 * {@link space.arim.universal.util.Util#isAsynchronous Util.isAnsynchronous()}.
	 * to ensure compliance.
	 * 
	 * @param async whether the event is fired asynchronously
	 */
	protected Event(boolean async) {
		this.async = async;
	}
	
	/**
	 * Creates an event, which runs synchronously. <br>
	 * See {@link #Event(boolean)} to specify asynchronicity if required.
	 * 
	 */
	protected Event() {
		this(false);
	}
	
	/**
	 * Whether the event is firing asynchronously <br>
	 * <br>
	 * If true, event DOES NOT run on the main thread
	 * 
	 * @return true if the event is asynchronous, false otherwise
	 */
	public final boolean isAsynchronous() {
		return async;
	}
	
}
