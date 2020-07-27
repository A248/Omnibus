/* 
 * Omnibus-api
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-api. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus;

/**
 * Holder for the version constant
 * 
 * @author A248
 *
 */
public final class OmnibusVersion {

	/**
	 * The version of Omnibus <br>
	 * (protected against constant folding)
	 * 
	 */
	public static final String VERSION = "${project.version}".toString(); // toString() prevents folding
	
	private OmnibusVersion() {}
	
}
