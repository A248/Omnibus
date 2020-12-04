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
package space.arim.omnibus.registry;

import space.arim.omnibus.events.AsyncEvent;

/**
 * An event related to a registration or unregistration which happened in a {@link Registry}. <br>
 * <br>
 * Called to <i>indicate</i> some consequence, but not necessarily called <i>when</i> it happened. <br>
 * That is, <b>this event may or may not run in the same thread from which the happening itself was occurred.</b> <br>
 * <br>
 * It is guaranteed that {@code RegistryEvent}s relating to a certain service will happen in an order consistent with
 * the state of the service; i.e., that they will be fired sequentially, but not necessarily on the same thread. <br>
 * However, no such guarantee is made for events relating to different services.
 *
 * @author A248
 * @param <T> the generic type
 */
public interface RegistryEvent<T> extends AsyncEvent {

	/**
	 * Gets the service involved in this event.
	 *
	 * @return the service type
	 */
	Class<T> getService();
	
}
