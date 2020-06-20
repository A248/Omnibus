/* 
 * UniversalEvents
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
package space.arim.universal.events;

/**
 * An implementation of {@link Cancellable} using a volatile variable
 * to guarantee memory consistency effects.
 * 
 * @author A248
 * 
 * @see Cancellable
 */
public class AbstractCancellable implements Cancellable {

	private volatile boolean cancelled = false;
	
	/**
	 * Creates a cancellable event. By default events are not cancelled.
	 * 
	 */
	protected AbstractCancellable() {
		
	}

	@Override
	public void cancel() {
		cancelled = true;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
}
