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

package space.arim.omnibus.singleprovider;

import space.arim.omnibus.Omnibus;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.registry.Registry;

public class AltOmnibus implements Omnibus {

	private final Omnibus delegate;

	public AltOmnibus(Omnibus delegate) {
		this.delegate = delegate;
	}

	@Override
	public EventBus getEventBus() {
		return delegate.getEventBus();
	}

	@Override
	public Registry getRegistry() {
		return delegate.getRegistry();
	}
}
