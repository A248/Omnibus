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

package space.arim.omnibus.multiprovider.test;

import org.junit.jupiter.api.Test;
import space.arim.omnibus.OmnibusProvider;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.multiprovider.two.AltOmnibusTwo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OmnibusProviderTest {

	@Test
	public void getOmnibus() {
		Omnibus omnibus = assertDoesNotThrow(() -> OmnibusProvider.getOmnibus());
		assertEquals(AltOmnibusTwo.class, omnibus.getClass());
	}


}
