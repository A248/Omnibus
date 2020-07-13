/* 
 * Universal-platform-spigot
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-platform-spigot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-platform-spigot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-platform-spigot. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.platform.bukkit;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import space.arim.universal.events.UniversalEvents;
import space.arim.universal.platform.PlatformConvention;
import space.arim.universal.platform.PlatformRegistryDefiner;
import space.arim.universal.registry.Registration;
import space.arim.universal.registry.Registry;
import space.arim.universal.registry.RegistryPriority;
import space.arim.universal.registry.UniversalRegistry;

/**
 * Initialiser of the {@link Registry} on Bukkit servers, using the Bukkit ServicesManager.
 * 
 * @author A248
 *
 */
public class BukkitPlatformConvention implements PlatformConvention {

	private final JavaPlugin plugin;
	
	/**
	 * Creates from a JavaPlugin to use. The plugin must be enabled
	 * 
	 * @param plugin the plugin to use
	 * @throws IllegalArgumentException if the plugin is not enabled
	 */
	public BukkitPlatformConvention(JavaPlugin plugin) {
		if (!plugin.isEnabled()) {
			throw new IllegalArgumentException("JavaPlugin must be enabled");
		}
		this.plugin = plugin;
	}
	
	/**
	 * Gets the {@link Registry} using the Bukkit ServicesManager, initialising it if necessary.
	 * 
	 * @return the initialised registry, never {@code null}
	 */
	@Override
	public Registry getRegistry() {
		Registry registry = getRegistry0();
		registry.registerIfAbsent(PlatformRegistryDefiner.class, () -> {

			PlatformRegistryDefiner<JavaPlugin, Server> definer;
			definer = new PlatformRegistryDefiner<>(plugin, plugin.getServer());

			return new Registration<>(RegistryPriority.LOWEST, definer,
					plugin.getDescription().getFullName());
		});
		return registry;
	}
	
	private Registry getRegistry0() {
		Registry registry;
		ServicesManager bukkitServices = plugin.getServer().getServicesManager();
		synchronized (bukkitServices) { // ServicesManager does not have a #registerIfAbsent, so synchronise on it

			ServicePriority priority;
			RegisteredServiceProvider<Registry> rsp = bukkitServices.getRegistration(Registry.class);
			if (rsp == null) {
				priority = ServicePriority.Lowest;

			} else {
				Registry provided = rsp.getProvider();
				if (provided != null) {
					return provided;
				} else {

					ServicePriority higherPriority = getHigherPriority(rsp.getPriority());
					if (higherPriority == null) {
						/*
						 * The SimpleServicesManager implementation suggests our registration will be prioritised
						 * over registrations with the same priority.
						 */
						priority = ServicePriority.Highest;
					} else {
						priority = higherPriority;
					}
				}
			}
			registry = new UniversalRegistry(new UniversalEvents());
			bukkitServices.register(Registry.class, registry, plugin, priority);
		}
		return registry;
	}
	
	/**
	 * Returns a priority higher than the specified priority, or {@code null}
	 * if the specified priority has no greater priority
	 * 
	 * @param initial the priority to overcome
	 * @return a priority higher than {@code initial}, or {@code null} if there is none
	 */
	private static ServicePriority getHigherPriority(ServicePriority initial) {
		switch (initial) {
		case Lowest:
			return ServicePriority.Low;
		case Low:
			return ServicePriority.Normal;
		case Normal:
			return ServicePriority.High;
		case High:
			return ServicePriority.Highest;
		case Highest:
		default:
			return null;
		}
	}
	
}
