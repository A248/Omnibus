/* 
 * Omnibus-default
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-default is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-default is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-default. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.defaultimpl.events;

import java.lang.invoke.MethodHandle;

import space.arim.omnibus.events.Listen;

/**
 * Internal wrapper for listening methods with the {@link Listen} annotation detected 
 * 
 * @author A248
 *
 */
class AnnotatedListenerMethod extends ListenerMethod {

	final Object listener;
	private final MethodHandle handle;
	
	AnnotatedListenerMethod(Object listener, MethodHandle handle, byte priority, boolean ignoreCancelled) {
		super(priority, ignoreCancelled);
		this.listener = listener;
		this.handle = handle;
	}
	
	@Override
	void invoke(Object evt) throws Throwable {
		handle.invoke(listener, evt);
	}
	
	/*
	 * We only use equals and hashCode to check for duplicate listeners
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + listener.hashCode();
		//result = prime * result + handle.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof AnnotatedListenerMethod)) {
			return false;
		}
		AnnotatedListenerMethod other = (AnnotatedListenerMethod) object;
		return listener == other.listener;// && handle.equals(other.handle);
	}
	
}
