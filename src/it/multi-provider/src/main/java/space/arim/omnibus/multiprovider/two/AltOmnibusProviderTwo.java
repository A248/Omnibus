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

package space.arim.omnibus.multiprovider.two;

import space.arim.omnibus.DefaultOmnibus;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.spi.OmnibusDefiner;
import space.arim.omnibus.spi.OmnibusProviderSpi;

public class AltOmnibusProviderTwo implements OmnibusProviderSpi {
	@Override
	public OmnibusDefiner createDefiner() {
		return new OmnibusDefiner() {

			@Override
			public Omnibus getOmnibus(Class<?> callerClass) {
				return new AltOmnibusTwo(new DefaultOmnibus());
			}

			@Override
			public boolean requiresCallerClass() {
				return true;
			}
		};
	}

	@Override
	public byte priority() {
		return 32;
	}
}
