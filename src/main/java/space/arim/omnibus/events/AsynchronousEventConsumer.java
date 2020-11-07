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
 * An asynchronous event consumer, which is responsible for continuing execution of the event
 * 
 * @author A248
 *
 * @param <E> the event type
 */
public interface AsynchronousEventConsumer<E extends Event> {

	/**
	 * Acts on the event listened to, and resumes firing of event handlers in whichever context desired
	 * 
	 * @param event the event
	 * @param controller the controller to use to resume firing of the event
	 */
	void acceptAndContinue(E event, EventFireController controller);
	
	/**
	 * A controller used to resume firing of an event
	 * 
	 * @author A248
	 *
	 */
	interface EventFireController {
		
		/**
		 * Continues execution of the event fire. Should only be called once
		 * 
		 * @throws IllegalStateException if called more than once
		 */
		void continueFire();
		
	}
	
}
