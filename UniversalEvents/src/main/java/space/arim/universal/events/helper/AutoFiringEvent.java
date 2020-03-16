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

import space.arim.universal.events.Events;

/**
 * A helper class for implementing {@link Event} which automatically fires itself when instantiated. <br>
 * Using this class ensures the specifications are always complied with.
 * 
 * @author A248
 *
 */
public abstract class AutoFiringEvent extends AbstractEvent {
	
	/**
	 * Creates the event with a corresponding {@link Events} instance. <br>
	 * The event is immediately fired after instantiation.
	 * 
	 * @param events the events instance
	 */
	protected AutoFiringEvent(Events events) {
		super(events);
		fire();
	}
	
	/**
	 * Creates the event with a corresponding {@link Events} instance. <br>
	 * The event is immediately fired after instantiation. After the event is fired,
	 * if the <code>Runnable</code> provided is nonnull, it is run.
	 * 
	 * @param events the events instance
	 * @param postFire a runnable to execute after the event is fired
	 */
	protected AutoFiringEvent(Events events, Runnable postFire) {
		super(events);
		fire();
		if (postFire != null) {
			postFire.run();
		}
	}
	
}