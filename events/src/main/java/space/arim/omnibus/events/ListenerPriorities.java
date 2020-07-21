/* 
 * Omnibus-events
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-events is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-events is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-events. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.events;

/**
 * Class containing priority constants for referential use. Use of these constants is optional. <br>
 * <br>
 * See {@link EventBus#registerListener(Class, byte, EventConsumer)} for information on priorities.
 * 
 * @author A248
 */
public final class ListenerPriorities {

	public static final byte LOWEST = (byte) -96;
	public static final byte LOWER = (byte) -64;
	public static final byte LOW = (byte) -32;
	public static final byte NORMAL = (byte) 0;
	public static final byte HIGH = (byte) 31;
	public static final byte HIGHER = (byte) 63;
	public static final byte HIGHEST = (byte) 95;
	
	private ListenerPriorities() {}
	
}
