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

class EventClassDebug {

	private final Appendable output;
	private final boolean verbose;
	private final CharSequence indentPrefix;

	private boolean wroteAnything;

	EventClassDebug(Appendable output, boolean verbose, CharSequence indentPrefix) {
		this.output = output;
		this.verbose = verbose;
		this.indentPrefix = indentPrefix;
	}

	void debugEventClass(Class<?> eventClass, Listener<?>[] listeners, BakedListenerGroup listenerGroup)
		throws IOException {
		output.append('\n').append(indentPrefix).append("-- Event class ").append(eventClass.getName());
		appendListeners(listeners);
		appendListenerGroup(listenerGroup);

		wroteAnything = true;
	}

	private void appendListeners(Listener<?>[] listeners) throws IOException {
		output.append('\n').append(indentPrefix);
		if (listeners == null) {
			output.append("  No directly specified listeners.");
			if (verbose) {
				output.append(" (This will necessarily be the case for encapsulated event implementations)");
			}
		} else {
			output.append("  Has directly specified listeners:");
			for (Listener<?> listener : listeners) {
				output.append('\n').append(indentPrefix).append("    - ").append(listener.toString());
			}
		}
	}

	private void appendListenerGroup(BakedListenerGroup listenerGroup) throws IOException {
		output.append('\n').append(indentPrefix);
		if (listenerGroup == null) {
			output.append("  No cached listeners.");
			if (verbose) {
				output.append(" (This will necessarily be the case for abstract event classes)");
			}
		} else {
			output.append("  Has cached listeners with the following information:");
			listenerGroup.debugTo(indentPrefix + "    ", output);
		}
	}

	boolean wroteAnything() {
		return wroteAnything;
	}
}
