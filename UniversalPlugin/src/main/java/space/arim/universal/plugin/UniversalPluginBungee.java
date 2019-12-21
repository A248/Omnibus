/* 
 * UniversalPlugin, a shaded implementation of UniversalRegistry
 * Copyright © 2019 Anand Beh <https://www.arim.space>
 * 
 * UniversalPlugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalPlugin. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.plugin;

import net.md_5.bungee.api.plugin.Plugin;

public class UniversalPluginBungee extends Plugin {

	@Override
	public void onEnable() {
		getLogger().info("Loaded all UniversalRegistry classes!");
	}
	
}
