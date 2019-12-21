/* 
 * UniversalRegistry, a common registry for plugin resources
 * Copyright © 2019 Anand Beh <https://www.arim.space
 * 
 * UniversalRegistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalRegistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalRegistry. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.registry;

/**
 * Called for the unregistration of a resource
 * 
 * @author A248
 *
 * @param <T> - the service for which the resource was registered
 */
public class UnregistrationEvent<T extends Registrable> extends AbstractRegistrationEvent<T> {

	UnregistrationEvent(Class<T> service, T provider) {
		super(service, provider);
	}

}
