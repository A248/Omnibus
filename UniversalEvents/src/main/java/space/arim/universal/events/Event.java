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

public interface Event {
	
	/**
	 * Whether an event is running asynchronously <br>
	 * <br>
	 * The contract of this method: <br>
	 * * If true, event MUST NOT run synchronously with the main thread <br>
	 * * If false, event MUST run synchronously with the main thread <br>
	 * <br>
	 * The main thread is generally taken as the thread doing the most work. <br>
	 * <br>
	 * <b>The event is vetted by UniversalEvents to ensure it complies with this specification</b>
	 * 
	 * @return true if and only if event is asynchronous
	 */
	boolean isAsynchronous();
	
}
