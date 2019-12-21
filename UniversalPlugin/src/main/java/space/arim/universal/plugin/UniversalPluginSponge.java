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

import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "universalplugin", name = "UniversalPlugin", version = "see_plugin_jar_resource=plugin.yml")
public class UniversalPluginSponge {
	
	/*
	 * I am so glad there is no implementation required here.
	 * The Sponge API is just absolutely horrible.
	 * 
	 * So much of its access is disgustingly static. There are no plugin or server instances for use.
	 * Worse, the extreme over-use of annotations for basic functionality is just appalling
	 */
	
}
