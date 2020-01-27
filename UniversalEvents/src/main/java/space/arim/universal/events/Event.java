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
 * A universal event <br>
 * <br>
 * Since this is an interface rather than an abstract class,
 * you may create custom events in other event managers which double as a universal event,
 * saving the hassle of making separate event classes for every platform. <br>
 * <br>
 * <b>Or, better yet, use UniversalEvents entirely</b>, and never deal with such problems again!
 * 
 * @author A248
 *
 */
public interface Event {
	
	/**
	 * Whether an event is running asynchronously <br>
	 * <br>
	 * <b>Contract</b>: <br>
	 * * If true, event MUST NOT run synchronously with the main thread <br>
	 * * If false, event MUST run synchronously with the main thread <br>
	 * <br>
	 * The main thread is generally taken as the thread doing the most work. <br>
	 * <br>
	 * <b>The event is vetted by UniversalEvents according to {@link space.arim.universal.util.Util#isAsynchronous} to ensure it complies with this specification</b>
	 * 
	 * @return true if and only if event is asynchronous
	 */
	boolean isAsynchronous();
	
}
