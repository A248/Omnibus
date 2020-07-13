/* 
 * Universal-platform-bungee
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-platform-bungee is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-platform-bungee is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-platform-bungee. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.platform.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

import space.arim.universal.platform.PlatformConvention;
import space.arim.universal.platform.PlatformRegistryDefiner;
import space.arim.universal.registry.Registration;
import space.arim.universal.registry.Registry;
import space.arim.universal.registry.RegistryPriority;
import space.arim.universal.registry.UniversalRegistry;

/**
 * Initialiser of the {@link Registry} on BungeeCord servers.
 * 
 * @author A248
 *
 */
public class BungeePlatformConvention implements PlatformConvention {

	private final Plugin plugin;
	
	/**
	 * Creates from a Plugin to use. The plugin must be enabled
	 * 
	 * @param plugin the plugin to use
	 */
	public BungeePlatformConvention(Plugin plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Gets the {@link Registry}, initialising it if necessary. <br>
	 * <br>
	 * For BungeeCord, this is currently implemented as {@code UniversalRegistry#get()}, however,
	 * this implementation may change if or once BungeeCord gets a services manager.
	 * 
	 * @return the initialised registry, never {@code null}
	 */
	@Override
	public Registry getRegistry() {
		Registry registry = UniversalRegistry.get();
		registry.registerIfAbsent(PlatformRegistryDefiner.class, () -> {

			PlatformRegistryDefiner<Plugin, ProxyServer> definer;
			definer = new PlatformRegistryDefiner<>(plugin, plugin.getProxy());

			PluginDescription description = plugin.getDescription();
			return new Registration<>(RegistryPriority.LOWEST, definer,
					description.getName() + " v" + description.getVersion());
		});
		return registry;
	}

}
