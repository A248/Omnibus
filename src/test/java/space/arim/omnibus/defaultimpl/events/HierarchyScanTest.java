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

package space.arim.omnibus.defaultimpl.events;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HierarchyScanTest {

	private Set<Class<?>> scan(Class<?> subject) {
		return new HierarchyScan(subject).scan();
	}
	@Test
	public void scanSingleClass() {
		assertEquals(Set.of(HierarchyScanTest.class), scan(HierarchyScanTest.class));
	}

	@Test
	public void scanExtends() {
		assertEquals(
				Set.of(HierarchyScanTest.class, SingleExtends.class),
				scan(SingleExtends.class));
		assertEquals(
				Set.of(HierarchyScanTest.class, SingleExtends.class, DoubleExtends.class),
				scan(DoubleExtends.class));
	}

	public static class SingleExtends extends HierarchyScanTest { }

	public static class DoubleExtends extends SingleExtends { }

	@Test
	public void scanInterfacesToo() {
		assertEquals(
				Set.of(HierarchyScanTest.class, SingleExtends.class, DoubleExtends.class,
						PlusIFaces.class, IFaceOne.class, IFaceTwo.class),
				scan(PlusIFaces.class));
	}

	public interface IFaceOne { }

	public interface IFaceTwo extends IFaceOne { }

	public static class PlusIFaces extends DoubleExtends implements IFaceTwo { }
}
