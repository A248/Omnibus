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
package space.arim.omnibus.defaultimpl.registry;

import space.arim.omnibus.registry.Registration;
import space.arim.omnibus.registry.RegistrationAddEvent;

class RegistrationRemoveEventImpl<T> extends RegistrationAddRemoveEventImpl<T> implements RegistrationAddEvent<T> {

	RegistrationRemoveEventImpl(Class<T> service, Registration<T> registration) {
		super(service, registration);
	}
	
}
