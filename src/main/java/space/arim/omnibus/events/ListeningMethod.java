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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Placed on methods to indicate it is intended they listen to an event. <br>
 * <br>
 * The target method must be public and return {@code void}. The method cannot
 * declare checked exceptions, it cannot be static, and it cannot be an abstract
 * or default method. <br>
 * <br>
 * A listening method must ordinarily have a single parameter whose type is that
 * of the event listened to. If it is desired to be an asynchronous listener, it
 * should accept two parameters; first, the event, second, a
 * {@link EventFireController}. <br>
 * <br>
 * Event classes must not be array types, primitive types, or {@code Object}.
 * The event type declared by the method must be public and unconditionally exported.
 * 
 * @author A248
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ListeningMethod {

	/**
	 * The priority at which to listen. Lower priorities are called first. See
	 * {@link EventBus} for more information
	 * 
	 * @return the listening priority
	 */
	byte priority() default ListenerPriorities.NORMAL;
	
}
