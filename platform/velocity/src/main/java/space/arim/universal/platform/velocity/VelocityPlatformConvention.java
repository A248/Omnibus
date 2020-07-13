/* 
 * Universal-platform-velocity
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-platform-velocity is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-platform-velocity is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-platform-velocity. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.platform.velocity;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.proxy.ProxyServer;

import space.arim.universal.platform.PlatformConvention;
import space.arim.universal.platform.PlatformRegistryDefiner;
import space.arim.universal.registry.Registration;
import space.arim.universal.registry.Registry;
import space.arim.universal.registry.RegistryPriority;
import space.arim.universal.registry.UniversalRegistry;

/**
 * Initialiser of the {@link Registry} on Velocity servers.
 * 
 * @author A248
 *
 */
public class VelocityPlatformConvention implements PlatformConvention {

	private final PluginContainer plugin;
	private final ProxyServer server;
	
	/**
	 * Creates from a PluginContainer and ProxyServer to use. The plugin's instance must exist
	 * 
	 * @param plugin the plugin to use
	 * @param server the server to use
	 */
	public VelocityPlatformConvention(PluginContainer plugin, ProxyServer server) {
		this.plugin = plugin;
		this.server = server;
	}
	
	/**
	 * Gets the {@link Registry}, initialising it if necessary. <br>
	 * <br>
	 * For Velocity, this is currently implemented as {@code UniversalRegistry#get()}, however,
	 * this implementation may change if or once Velocity gets a services manager.
	 * 
	 * @return the initialised registry, never {@code null}
	 */
	@Override
	public Registry getRegistry() {
		Registry registry = UniversalRegistry.get();
		registry.registerIfAbsent(PlatformRegistryDefiner.class, () -> {

			PlatformRegistryDefiner<PluginContainer, ProxyServer> definer;
			definer = new PlatformRegistryDefiner<>(plugin, server);

			PluginDescription description = plugin.getDescription();
			String name = description.getName().orElse(description.getId()) + " v"
					+ description.getVersion().orElse("Unknown");
			return new Registration<>(RegistryPriority.LOWEST, definer, name);
		});
		return registry;
	}

}
