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
package space.arim.omnibus.registry;

import space.arim.omnibus.events.Event;

/**
 * An event related to a registration or unregistration which happened in a {@link Registry}. <br>
 * <br>
 * Called to <i>indicate</i> some consequence, but not necessarily called <i>when</i> it happened. <br>
 * That is, <b>this event may or may not run in the same thread from which the happening itself was occurred.</b> It may run
 * completely asynchronously regardless. <br>
 * <br>
 * It is up to the discretion of {@link Registry} implementations to decide when this event and its subclasses may fire.
 * Do not assume this event will fire immediately after things literally occur.
 * 
 * @author A248
 *
 */
public interface RegistryEvent<T> extends Event {

	/**
	 * Gets the service involved in this event
	 * 
	 * @return the service type
	 */
	Class<T> getService();
	
}
