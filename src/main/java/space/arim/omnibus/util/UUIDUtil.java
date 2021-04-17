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

import java.util.UUID;

/**
 * Utility class for manipulating valid UUIDs. Allows conversions between
 * {@link UUID}s, full UUID strings, short UUID strings, and byte arrays. <br>
 * <br>
 * All methods do not permit null, and may throw {@code NullPointerException}
 * otherwise. <br>
 * <br>
 * <b>A Note on Preconditions</b> <br>
 * So that it may be used to operate on large volumes of data, this class does
 * not check comprehensive preconditions. It assumes mostly valid representations.
 * Callers are encouraged to validate their own input if necessary. <br>
 * <br>
 * <b>UUID Forms</b> <br>
 * This class recognises 3 forms of UUIDs besides {@code java.util.UUID}: <br>
 * 1. Full UUID strings. This the common string based representation of a UUID
 * as defined by {@link UUID#toString()}. UUIDs in this form may be converted
 * back to a {@code java.util.UUID} via the JDK's
 * {@link UUID#fromString(String)}. <br>
 * 2. Short UUID strings. These are the same as full UUID strings except that
 * they are not hyphenated. <br>
 * 3. Byte arrays. These must be 16 bytes in length. They convey the same data
 * as a pair of {@code long} values. <br>
 * <br>
 * Methods are provided for efficient conversion between: <br>
 * 1. Full UUID strings and short UUID strings. <br>
 * 2. Short UUID strings and {@code java.util.UUID}. <br>
 * 3. Byte arrays and {@code java.util.UUID}. <br>
 * <br>
 * Where applicable, conversion methods are designed to be at least as
 * performant as more roundabout approaches. For example,
 * {@link #fromShortString(String)} should be faster than combining
 * {@link #expandShortString(String)} and {@link UUID#fromString(String)}.
 * 
 * @author A248
 *
 */
public final class UUIDUtil {

	private UUIDUtil() {}

	/*
	 * 
	 * UUID String Conversions
	 * 
	 */

	/**
	 * Expands a shortened version of a UUID to the full string form. Inverse
	 * operation of {@link #contractFullString(String)}
	 * 
	 * @param shortUuid the short uuid string
	 * @return the full uuid string
	 * @throws IllegalArgumentException if {@code shortUuid} is not of length 32
	 */
	public static String expandShortString(String shortUuid) {
		if (shortUuid.length() != 32) {
			throw new IllegalArgumentException("Short uuid " + shortUuid + " must be of length 32");
		}
		return new StringBuilder().append(shortUuid, 0, 8).append('-').append(shortUuid, 8, 12).append('-')
				.append(shortUuid, 12, 16).append('-').append(shortUuid, 16, 20).append('-').append(shortUuid, 20, 32)
				.toString();
	}

	/**
	 * Contracts the full form of a UUID string to its shortened form. This is the
	 * inverse operation of {@link #expandShortString(String)}, and should be
	 * identical to replacing/removing all hyphens in the full uuid string, e.g.
	 * <code>fullUuid.replace("{@literal -}", "")</code> for a valid full UUID
	 * string called {@code fullUuid}.
	 * 
	 * @param fullUuid the full uuid string
	 * @return the short uuid string
	 * @throws IllegalArgumentException if {@code fullUuid} is not of length 36
	 */
	public static String contractFullString(String fullUuid) {
		if (fullUuid.length() != 36) {
			throw new IllegalArgumentException("Full uuid " + fullUuid + " must be of length 36");
		}
		return new StringBuilder().append(fullUuid, 0, 8).append(fullUuid, 9, 13).append(fullUuid, 14, 18)
				.append(fullUuid, 19, 23).append(fullUuid, 24, 36).toString();
	}

	/*
	 * 
	 * Short form conversions
	 * 
	 */

	/**
	 * Converts a {@code UUID} to its short form string representation. This is the
	 * inverse operation of {@link #fromShortString(String)}. <br>
	 * <br>
	 * The returned string will always be of length 32.
	 * 
	 * @param uuid the UUID
	 * @return the short uuid string
	 */
	public static String toShortString(UUID uuid) {
		StringBuilder builder = new StringBuilder();
		formatAsHex(uuid.getMostSignificantBits(), builder);
		formatAsHex(uuid.getLeastSignificantBits(), builder);
		return builder.toString();
	}

	private static void formatAsHex(long bits, StringBuilder builder) {
		String hex = Long.toHexString(bits);
		int leadingZeroes = 16 - hex.length();
		builder.append("0".repeat(leadingZeroes));
		builder.append(hex);
	}

	/**
	 * Converts a short form uuid string to a {@code UUID}. This is the inverse
	 * operation of {@link #toShortString(UUID)}
	 * 
	 * @param shortUuid the short uuid string
	 * @return the UUID
	 * @throws IllegalArgumentException if {@code shortUuid} is not of length 32
	 * @throws NumberFormatException if {@code shortUuid} is not a valid short uuid string
	 */
	public static UUID fromShortString(String shortUuid) {
		if (shortUuid.length() != 32) {
			throw new IllegalArgumentException("Short uuid " + shortUuid + " must be of length 32");
		}
		return new UUID(
				Long.parseUnsignedLong(shortUuid.substring(0, 16), 16),
				Long.parseUnsignedLong(shortUuid.substring(16, 32), 16));
	}

	/*
	 * 
	 * Byte Array Conversions
	 * 
	 */

	/**
	 * Creates a byte array and writes a UUID to it. This would be the inverse
	 * operation of {@link #fromByteArray(byte[])}
	 * 
	 * @param uuid the UUID
	 * @return the byte array, will always be length 16
	 */
	public static byte[] toByteArray(UUID uuid) {
		byte[] result = new byte[16];
		toByteArray(uuid, result, 0);
		return result;
	}

	/**
	 * Writes a UUID to a byte array at the specified offset. This would be the
	 * inverse operation of {@link #fromByteArray(byte[], int)}
	 * 
	 * @param uuid      the UUID
	 * @param byteArray the byte array to write to, must be at least of length
	 *                  (offset + 16)
	 * @param offset    the offset in the byte array after which to write bytes
	 * @throws IndexOutOfBoundsException if the byte array is not of the right size
	 */
	public static void toByteArray(UUID uuid, byte[] byteArray, int offset) {
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();

		for (int i = 7; i >= 0; i--) {
			byteArray[offset + i] = (byte) (msb & 0xffL);
			msb >>= 8;
		}
		for (int i = 15; i >= 8; i--) {
			byteArray[offset + i] = (byte) (lsb & 0xffL);
			lsb >>= 8;
		}
	}

	/**
	 * Reads a UUID from a byte array. This is the inverse operation of
	 * {@link #toByteArray(UUID)}.
	 * 
	 * @param byteArray the byte array to read from, must be at least of length 16
	 * @return the UUID
	 * @throws IndexOutOfBoundsException if the byte array is not of the right size
	 */
	public static UUID fromByteArray(byte[] byteArray) {
		return new UUID(
				longFromBytes(
						byteArray[0], byteArray[1], byteArray[2], byteArray[3],
						byteArray[4], byteArray[5], byteArray[6], byteArray[7]),
				longFromBytes(
						byteArray[8], byteArray[9], byteArray[10], byteArray[11],
						byteArray[12], byteArray[13], byteArray[14], byteArray[15]));
	}

	/**
	 * Reads a UUID from a byte array with a specified offset. This is the inverse
	 * operation of {@link #toByteArray(UUID, byte[], int)}
	 * 
	 * @param byteArray the byte array to read from, must be at least of length
	 *                  (offset + 16)
	 * @param offset    the offset after which to begin reading bytes
	 * @return the UUID
	 * @throws IndexOutOfBoundsException if the byte array is not of the right size
	 */
	public static UUID fromByteArray(byte[] byteArray, int offset) {
		return new UUID(
				longFromBytes(
						byteArray[offset], byteArray[offset + 1],
						byteArray[offset + 2], byteArray[offset + 3],
						byteArray[offset + 4], byteArray[offset + 5],
						byteArray[offset + 6], byteArray[offset + 7]),
				longFromBytes(
						byteArray[offset + 8], byteArray[offset + 9],
						byteArray[offset + 10],
						byteArray[offset + 11], byteArray[offset + 12],
						byteArray[offset + 13], byteArray[offset + 14],
						byteArray[offset + 15]));
	}

	private static long longFromBytes(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
		return (b1 & 0xffL) << 56 | (b2 & 0xffL) << 48 | (b3 & 0xffL) << 40 | (b4 & 0xffL) << 32 | (b5 & 0xffL) << 24
				| (b6 & 0xffL) << 16 | (b7 & 0xffL) << 8 | (b8 & 0xffL);
	}

}
