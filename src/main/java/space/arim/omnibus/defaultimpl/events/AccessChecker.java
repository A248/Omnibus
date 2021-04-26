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

import java.lang.reflect.Modifier;

class AccessChecker {

	void checkClassAccess(Class<?> type) {
		if (!Modifier.isPublic(type.getModifiers())) {
			throw new IllegalArgumentException(type + " is not public");
		}
		String listenerPackage = type.getPackageName();
		boolean packageVisible = type.getModule().isExported(listenerPackage)
				|| listenerPackage.equals(getClass().getPackageName()); // whitelist own package for unit tests
		if (!packageVisible) {
			throw new IllegalArgumentException(type + " is not unconditionally exported");
		}
	}
}
