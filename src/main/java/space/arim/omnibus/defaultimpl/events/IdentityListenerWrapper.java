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

/**
 * Identity based wrapper to avoid malicious {@code equals} overrides, while not
 * requiring the locking that would be necessary for {@code IdentityHashMap}
 * 
 */
final class IdentityListenerWrapper {

	private final Object annotatedListener;

	IdentityListenerWrapper(Object annotatedListener) {
		assert annotatedListener != null : "caller checks for null";
		this.annotatedListener = annotatedListener;
	}

	@Override
	public int hashCode() {
		return 31 + System.identityHashCode(annotatedListener);
	}

	@Override
	public boolean equals(Object object) {
		return this == object || object instanceof IdentityListenerWrapper
				&& annotatedListener == ((IdentityListenerWrapper) object).annotatedListener;
	}

	@Override
	public String toString() {
		return annotatedListener.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(annotatedListener));
	}

}
