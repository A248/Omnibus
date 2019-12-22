/* 
 * UniversalEvents, a common server event-handling api
 * Copyright © 2019 Anand Beh <https://www.arim.space>
 * 
 * UniversalEvents is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalEvents is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalEvents. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.events;

/**
 * Class containing priority constants for referential use
 * 
 * <br><br>See {@link space.arim.universal.events.EventHandler#priority() EventHandler.priority()}
 * 
 * @author A248
 */
public final class EventPriority {

	public static final byte LOWEST = (byte) -96;
	public static final byte LOWER = (byte) -64;
	public static final byte LOW = (byte) -32;
	public static final byte NORMAL = (byte) 0;
	public static final byte HIGH = (byte) 31;
	public static final byte HIGHER = (byte) 63;
	public static final byte HIGHEST = (byte) 95;
	
	private EventPriority() {}
	
}
