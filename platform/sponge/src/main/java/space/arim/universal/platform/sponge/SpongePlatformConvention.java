/* 
 * Universal-platform-sponge
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-platform-sponge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-platform-sponge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-platform-sponge. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.platform.sponge;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.ServiceManager;

import space.arim.universal.events.UniversalEvents;
import space.arim.universal.platform.PlatformConvention;
import space.arim.universal.platform.PlatformRegistryDefiner;
import space.arim.universal.registry.Registration;
import space.arim.universal.registry.Registry;
import space.arim.universal.registry.RegistryPriority;
import space.arim.universal.registry.UniversalRegistry;

/**
 * Initialiser of the {@link Registry} on Sponge servers, using the Sponge ServiceManager.
 * 
 * @author A248
 *
 */
public class SpongePlatformConvention implements PlatformConvention {

	private final PluginContainer plugin;
	
	/**
	 * Creates from a PluginContainer to use. The plugin's instance must exist
	 * 
	 * @param plugin the plugin to use
	 */
	public SpongePlatformConvention(PluginContainer plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Gets the {@link Registry} using the Sponge ServiceManager, initialising it if necessary.
	 * 
	 * @return the initialised registry, never {@code null}
	 */
	@Override
	public Registry getRegistry() {
		Registry registry = getRegistry0();
		registry.registerIfAbsent(PlatformRegistryDefiner.class, () -> {

			PlatformRegistryDefiner<PluginContainer, Game> definer;
			definer = new PlatformRegistryDefiner<>(plugin, Sponge.getGame());

			return new Registration<>(RegistryPriority.LOWEST, definer,
					plugin.getName() + " v" + plugin.getVersion().orElse("Unknown"));
		});
		return registry;
	}
	
	private Registry getRegistry0() {
		Registry registry;
		ServiceManager spongeServices = Sponge.getServiceManager();
		synchronized (spongeServices) { // ServiceManager does not have a #registerIfAbsent, so synchronise on it

			Registry provided = spongeServices.provide(Registry.class).orElse(null);
			if (provided == null) {
				registry = new UniversalRegistry(new UniversalEvents());
				spongeServices.setProvider(plugin.getInstance().get(), Registry.class, registry);

			} else {
				registry = provided;
			}
		}
		return registry;
	}
	
}
