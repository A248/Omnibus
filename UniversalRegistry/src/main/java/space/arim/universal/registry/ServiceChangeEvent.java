/* 
 * UniversalRegistry
 * Copyright © 2020 Anand Beh <https://www.arim.space>
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
 * Indicates the prioritised provider for a specific service changed. This happens
 * whenever a new registration is added with a higher priority than the existing
 * highest priority registration, or the highest priority registration was unregistered. <br>
 * <br>
 * This event has a unique specification in that it guarantees a happens-before relationship
 * for service changes. That is, if the provider changes from A to B to C, the event for
 * the service change from A to B <i>will</i> happen before the event for that from B to C.
 * 
 * @author A248
 *
 * @param <T> the service type
 */
public interface ServiceChangeEvent<T> extends RegistryEvent<T> {

	/**
	 * Gets the previous highest priority registration. <br>
	 * If there was no previous registration when the updated registration
	 * was registered, this will be {@code null}.
	 * 
	 * @return the previous registration, or {@code null} if there was none
	 */
	Registration<T> getPrevious();
	
	/**
	 * Gets the updated highest priority registration. <br>
	 * If the current highest priority registration was unregistered
	 * and there is no other registration for the same service, this
	 * will be {@code null}.
	 * 
	 * @return the updated registration, or {@code null} if there is none
	 */
	Registration<T> getUpdated();
	
}
