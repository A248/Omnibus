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

package space.arim.omnibus.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class UUIDUtilTest {

	private final UUID uuid = UUID.randomUUID();

	@Test
	public void toFromByteArray() {
		assertEquals(uuid, UUIDUtil.fromByteArray(UUIDUtil.toByteArray(uuid)));
	}

	@Test
	public void toBytesWrittenUsingByteBuffer() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(16);
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());
		assumeTrue(byteBuffer.hasArray());
		assertArrayEquals(byteBuffer.array(), UUIDUtil.toByteArray(uuid));
	}
	
	@Test
	public void toBytesRereadUsingByteBuffer() {
		int offset = ThreadLocalRandom.current().nextInt(0, 64);
		byte[] bytes = new byte[offset + 16];
		UUIDUtil.toByteArray(uuid, bytes, offset);
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes, offset, 16);
		long msb = byteBuffer.getLong();
		long lsb = byteBuffer.getLong();
		assertEquals(uuid, new UUID(msb, lsb));
	}

	@Test
	public void toBytesLength() {
		assertEquals(16, UUIDUtil.toByteArray(uuid).length);
	}

	@Test
	public void toBytesOffset() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int bufferSize = random.nextInt(16, 128);
		byte[] bytes = new byte[bufferSize];
		int offset = (bufferSize == 16) ? 0 : random.nextInt(bufferSize - 16);

		UUIDUtil.toByteArray(uuid, bytes, offset);
		assertEquals(uuid, UUIDUtil.fromByteArray(bytes, offset));
	}

	@Test
	public void toBytesOffsetUsingByteBuffer() {
		int offset = ThreadLocalRandom.current().nextInt(0, 64);

		ByteBuffer byteBuffer = ByteBuffer.allocate(16 + offset);
		byteBuffer.position(offset);
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());
		assumeTrue(byteBuffer.hasArray());
		byte[] byteBufferBytes = byteBuffer.array();

		byte[] verifyBytes = new byte[16 + offset];
		UUIDUtil.toByteArray(uuid, verifyBytes, offset);

		assertArrayEquals(byteBufferBytes, verifyBytes);
	}

	@RepeatedTest(5)
	public void toFromShortString() {
		assertEquals(uuid, UUIDUtil.fromShortString(UUIDUtil.toShortString(uuid)));
	}

	@RepeatedTest(5)
	public void shortStringLength() {
		assertEquals(32, UUIDUtil.toShortString(uuid).length());
	}

	@Test
	public void contractExpandShortString() {
		String fullUuid = uuid.toString();
		String shortUuid = UUIDUtil.contractFullString(fullUuid);

		assertEquals(fullUuid.replace("-", ""), shortUuid);
		assertEquals(fullUuid, UUIDUtil.expandShortString(shortUuid));
		assertEquals(shortUuid, UUIDUtil.toShortString(uuid));
		assertEquals(32, shortUuid.length());
		assertEquals(uuid, UUIDUtil.fromShortString(shortUuid));
	}

}
