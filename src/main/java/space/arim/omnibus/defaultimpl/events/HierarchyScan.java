/*
 * Omnibus
 * Copyright © 2021 Anand Beh
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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

class HierarchyScan {

	private final Class<?> subject;

	HierarchyScan(Class<?> subject) {
		this.subject = subject;
	}

	Set<Class<?>> scan() {
		Set<Class<?>> classes = new HashSet<>();

		Class<?> currentClass = subject;
		while (!currentClass.equals(Object.class)) {
			classes.add(currentClass);
			scanInterfaces(currentClass, classes::add);

			currentClass = currentClass.getSuperclass();
		}
		return Set.copyOf(classes);
	}

	private void scanInterfaces(Class<?> currentClass, Consumer<Class<?>> action) {
		for (Class<?> iface : currentClass.getInterfaces()) {
			action.accept(iface);
			scanInterfaces(iface, action);
		}
	}

}
