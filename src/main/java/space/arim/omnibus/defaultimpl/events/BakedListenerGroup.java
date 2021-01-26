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

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

class BakedListenerGroup {

	private final Set<Class<?>> eventClasses;
	private final Listener<?>[] listeners;

	BakedListenerGroup(Set<Class<?>> eventClasses, Listener<?>[] listeners) {
		this.eventClasses = Set.copyOf(eventClasses);
		this.listeners = listeners;
	}

	Set<Class<?>> eventClasses() {
		return eventClasses;
	}

	Listener<?>[] listeners() {
		return listeners;
	}

	@Override
	public String toString() {
		return "BakedListenerGroup{" +
				"eventClasses=" + eventClasses +
				", listeners=" + Arrays.toString(listeners) +
				'}';
	}

	void debugTo(CharSequence indentPrefix, Appendable output) throws IOException {
		output.append('\n').append(indentPrefix).append("Event classes: \n");
		for (Class<?> eventClass : eventClasses) {
			output.append('\n').append(indentPrefix).append("  - ").append(eventClass.getName());
		}
		output.append('\n').append(indentPrefix).append("Event listeners: \n");
		for (Listener<?> listener : listeners) {
			output.append('\n').append(indentPrefix).append("  - ").append(listener.toString());
		}
	}
}
