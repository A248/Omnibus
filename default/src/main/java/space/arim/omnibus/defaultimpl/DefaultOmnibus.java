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
package space.arim.omnibus.defaultimpl;

import space.arim.omnibus.Omnibus;
import space.arim.omnibus.defaultimpl.events.DefaultEvents;
import space.arim.omnibus.defaultimpl.registry.DefaultRegistry;
import space.arim.omnibus.defaultimpl.resourcer.DefaultResourcer;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.registry.Registry;
import space.arim.omnibus.resourcer.Resourcer;

/**
 * The default implementation of {@link Omnibus}
 * 
 * @author A248
 *
 */
public class DefaultOmnibus implements Omnibus {

	private final EventBus events;
	private final Registry registry;
	private final Resourcer resourcer;
	
	/**
	 * Creates a {@code DefaultOmnibus}
	 * 
	 */
	public DefaultOmnibus() {
		events = new DefaultEvents(this);
		registry = new DefaultRegistry(this);
		resourcer = new DefaultResourcer(this);
	}
	
	@Override
	public EventBus getEvents() {
		return events;
	}

	@Override
	public Registry getRegistry() {
		return registry;
	}

	@Override
	public Resourcer getResourcer() {
		return resourcer;
	}

	@Override
	public String toString() {
		return "DefaultOmnibus [events=" + events + ", registry=" + registry + ", resourcer=" + resourcer + "]";
	}
	
}
