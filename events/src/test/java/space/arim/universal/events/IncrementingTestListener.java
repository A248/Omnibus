/* 
 * Universal-events
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-events is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-events is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-events. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.events;

public class IncrementingTestListener implements Listener {

	@Listen(priority = EventPriority.NORMAL)
	public void listenTo(TestEventWithInteger te) {
		te.someValue = te.someValue + 1;
	}
	
}
