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
package space.arim.universal.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Internal wrapper class. Implementation could change, so this is not exposed.
 * 
 * @author A248
 *
 */
class ListenerMethod {

	private final Object listener;
	private final Method method;
	final byte priority;
	final boolean ignoreCancelled;
	
	ListenerMethod(Object listener, Method method, byte priority, boolean ignoreCancelled) {
		this.listener = listener;
		this.method = method;
		this.priority = priority;
		this.ignoreCancelled = ignoreCancelled;
		method.setAccessible(true);
	}
	
	void invoke(Event evt) {
		try {
			method.invoke(listener, evt);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {}
	}
	
}
