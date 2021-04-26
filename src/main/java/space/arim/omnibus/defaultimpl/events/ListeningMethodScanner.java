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
package space.arim.omnibus.defaultimpl.events;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import space.arim.omnibus.events.AsyncEvent;
import space.arim.omnibus.events.Event;
import space.arim.omnibus.events.ListeningMethod;

class ListeningMethodScanner {

	private final Object listener;
	private final Class<?> listenerClass;

	private final AccessChecker accessChecker = new AccessChecker();
	
	ListeningMethodScanner(Object listener) {
		this.listener = listener;
		listenerClass = listener.getClass();
	}
	
	Set<Listener<?>> scanAndTransformAnnotatedMethods() {
		accessChecker.checkClassAccess(listenerClass);

		Set<Listener<?>> transformedListeners = new HashSet<>();
		for (Method method : listenerClass.getMethods()) {
			ListeningMethod annotation = method.getAnnotation(ListeningMethod.class);
			if (annotation == null) {
				continue;
			}
			Listener<?> transformedListener = transformAnnotatedMethod(method, annotation);
			transformedListeners.add(transformedListener);
		}
		return transformedListeners;
	}
	
	private Listener<?> transformAnnotatedMethod(Method method, ListeningMethod annotation) {
		MethodHandle methodHandle = new ListeningMethodValidator(accessChecker, method).validateAndUnreflect();

		Class<?>[] parameterTypes = method.getParameterTypes();
		Class<? extends Event> eventClass = parameterTypes[0].asSubclass(Event.class);
		byte priority = annotation.priority();

		Listener<?> transformedListener;
		if (parameterTypes.length == 1) {
			transformedListener = new SynchronousListener<>(eventClass, priority,
					new InvokingEventConsumer<>(listener, methodHandle));
		} else {
			Class<? extends AsyncEvent> asyncEventClass = eventClass.asSubclass(AsyncEvent.class);
			transformedListener = new AsynchronousListener<>(asyncEventClass, priority,
					new InvokingAsynchronousEventConsumer<>(listener, methodHandle));
		}
		return transformedListener;
	}
	
}
