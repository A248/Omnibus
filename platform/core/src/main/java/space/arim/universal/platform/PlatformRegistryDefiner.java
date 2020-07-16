/* 
 * Universal-platform-core
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-platform-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-platform-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-platform-core. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.platform;

import java.util.Objects;

/**
 * Registration of platform-specific objects which is automatically created for
 * whichever caller defined the {@link space.arim.universal.registry.Registry
 * Registry} via a {@link PlatformConvention}. <br>
 * <br>
 * The platform-specific plugin and server types are outlined as follows: <br>
 * <br>
 * For Bukkit, an enabled JavaPlugin (org.bukkit.plugin.java.JavaPlugin) and
 * Server (org.bukkit.Server) <br>
 * <br>
 * For BungeeCord, an enabled Plugin (net.md_5.bungee.api.plugin.Plugin) and
 * ProxyServer (net.md_5.bungee.api.ProxyServer) <br>
 * <br>
 * For Sponge, a PluginContainer (org.spongepowered.api.plugin.PluginContainer)
 * with loaded instance and Game (org.spongepowered.api.Game) <br>
 * <br>
 * For Velocity, a PluginContainer
 * (com.velocitypowered.api.plugin.PluginContainer) with loaded instance and
 * ProxyServer (com.velocitypowered.api.proxy.ProxyServer)
 * 
 * @author A248
 *
 * @param <P> the type of the platform-specific plugin object
 * @param <S> the type of the platform-specific server object
 */
public final class PlatformRegistryDefiner<P, S> {

	private final P plugin;
	private final S server;
	
	/**
	 * Creates from a platform-specific plugin and server
	 * 
	 * @param plugin the platform-specific plugin, must not be null
	 * @param server the platform-specific server, must not be null
	 */
	public PlatformRegistryDefiner(P plugin, S server) {
		this.plugin = Objects.requireNonNull(plugin, "Plugin must not be null");
		this.server = Objects.requireNonNull(server, "Server must not be null");
	}
	
	/**
	 * Gets the platform-specific plugin object
	 * 
	 * @return the platform-specific plugin
	 */
	public P getPlugin() {
		return plugin;
	}
	
	/**
	 * Gets the platform-specific server object
	 * 
	 * @return the platform-specific server
	 */
	public S getServer() {
		return server;
	}

	@Override
	public String toString() {
		return "PlatformRegistryDefiner [plugin=" + plugin + ", server=" + server + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + plugin.hashCode();
		result = prime * result + server.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof PlatformRegistryDefiner<?,?>)) {
			return false;
		}
		PlatformRegistryDefiner<?,?> other = (PlatformRegistryDefiner<?,?>) object;
		return plugin.equals(other.plugin) && server.equals(other.server);
	}
	
}
