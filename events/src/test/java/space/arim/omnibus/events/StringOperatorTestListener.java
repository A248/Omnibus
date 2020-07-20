/* 
 * UniversalEvents
 * Copyright © 2020 Anand Beh <https://www.arim.space>
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
package space.arim.omnibus.events;

import java.util.function.UnaryOperator;

import space.arim.omnibus.events.Listen;
import space.arim.omnibus.events.Listener;

public class StringOperatorTestListener implements Listener {

	static final UnaryOperator<String> OPERATOR = (str) -> str.replace(" ", "");
	
	@Listen
	public void onTestEvent(TestEventWithString evt) {
		evt.str = OPERATOR.apply(evt.str);
	}
	
}
