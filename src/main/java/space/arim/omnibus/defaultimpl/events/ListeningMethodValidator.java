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
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import space.arim.omnibus.events.AsyncEvent;
import space.arim.omnibus.events.Event;
import space.arim.omnibus.events.EventFireController;

class ListeningMethodValidator {

	private static final Lookup LOOKUP = MethodHandles.lookup();

	private final Method method;

	ListeningMethodValidator(Method method) {
		this.method = method;
	}

	MethodHandle validateAndUnreflect() {
		validateMethodSignature();
		detectCheckedExceptions();

		try {
			return LOOKUP.unreflect(method);
		} catch (IllegalAccessException ex) {
			throw new IllegalArgumentException("Unable to unreflect " + fullyQualifiedMethodName(), ex);
		}
	}

	private void validateMethodSignature() {
		if (method.getReturnType() != void.class) {
			throw badAnnotatedMethod("non-void return type");
		}
		int modifiers = method.getModifiers();
		if (!Modifier.isPublic(modifiers)) { // May be redundant but is future-proof
			throw badAnnotatedMethod("non-public access");
		}
		if (Modifier.isStatic(modifiers) || method.isDefault()) {
			throw badAnnotatedMethod("method is static or default");
		}
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 0) {
			throw badAnnotatedMethod("no parameters");
		}
		if (parameterTypes.length > 2) {
			throw badAnnotatedMethod("too many parameters");
		}
		if (!Event.class.isAssignableFrom(parameterTypes[0])) {
			throw badAnnotatedMethod("first parameter is not subclass of Event");
		}
		if (parameterTypes.length == 2) {
			if (parameterTypes[1] != EventFireController.class) {
				throw badAnnotatedMethod("second parameter is not EventFireController");
			}
			if (!AsyncEvent.class.isAssignableFrom(parameterTypes[0])) {
				throw badAnnotatedMethod("first parameter is not subclass of AsyncEvent");
			}
		}
	}

	private void detectCheckedExceptions() {
		for (Class<?> exception : method.getExceptionTypes()) {
			if (Error.class.isAssignableFrom(exception) || RuntimeException.class.isAssignableFrom(exception)) {
				continue;
			}
			throw badAnnotatedMethod("declares checked exception " + exception.getName());
		}
	}

	private IllegalArgumentException badAnnotatedMethod(String reason) {
		return new IllegalArgumentException(
				fullyQualifiedMethodName() + " violates @ListeningMethod requirements: " + reason);
	}

	private String fullyQualifiedMethodName() {
		return method.getDeclaringClass().getName() + "#" + method.getName();
	}

}
