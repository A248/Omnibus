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

package space.arim.omnibus.fjponeparallelism;

import org.junit.jupiter.api.Test;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;
import space.arim.omnibus.util.concurrent.impl.IndifferentFactoryOfTheFuture;

import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 * Verifies null-checking in the presence of JDK-8254973
 */
public class AbstractFactoryOfTheFutureTest {

	@Test
	public void nullCheckExecute() {
		assertEquals(1, ForkJoinPool.getCommonPoolParallelism(), "Test environment misprepared");
		FactoryOfTheFuture futuresFactory = new IndifferentFactoryOfTheFuture();
		assertThrows(NullPointerException.class, () -> futuresFactory.execute(null));
	}
}
