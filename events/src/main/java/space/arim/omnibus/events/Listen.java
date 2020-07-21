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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks methods for listening to events. Though this approach is less efficient and unidiomatic java,
 * it may be more convenient at times. <br>
 * <br>
 * The method must be and public, non-static, and in a public class. The method must have {@code void} return type.
 * Also, it must have a single parameter which is assignment-compatible with <code>Event.class</code>. If any of
 * these requirements are not met, an unchecked exception is thrown (usually {@link IllegalListenerException}). <br>
 * <br>
 * The single parameter of the listening method determines the event which will be listened to. More formally,
 * for any event <code>evt</code>, if the <code>evt</code> is an instance of the parameter type,
 * the listening method will be invoked when the method is thrown. Listening to superclasses is, therefore,
 * supported. <br>
 * <br>
 * <b>Inheritance</b>
 * Listening methods, unlike normal methods, cannot be inherited. If inheritance is required, then it will be better
 * to switch to normal event listening.
 * 
 * @author A248
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Listen {
	
	/**
	 * The priority of the listening. <b>Higher priorities are called last</b>
	 * 
	 * @return byte the priority
	 */
	byte priority() default 0;
	
	/**
	 * Specifies whether this listener should be called for cancelled events. <br>
	 * If <code>true</code>, the method will not be invoked if the event is cancellable
	 * and was cancelled.
	 * 
	 * @return true to ignore cancelled events, false otherwise
	 */
	boolean ignoreCancelled() default false;
	
}
