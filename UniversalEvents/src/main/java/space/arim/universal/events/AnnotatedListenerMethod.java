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
 * Internal wrapper for listening methods with the {@link Listen} annotation detected 
 * 
 * @author A248
 *
 */
class AnnotatedListenerMethod extends ListenerMethod {

	private final Object listener;
	private final Method method;
	
	AnnotatedListenerMethod(Object listener, Method method, byte priority, boolean ignoreCancelled) {
		super(priority, ignoreCancelled);
		this.listener = listener;
		this.method = method;
		method.setAccessible(true);
	}
	
	@Override
	void invoke(Object evt) {
		try {
			method.invoke(listener, evt);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {}
	}
	
}
