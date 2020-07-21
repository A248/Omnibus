/* 
 * Omnibus-default
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-default is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-default is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-default. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.defaultimpl.resourcer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import space.arim.omnibus.Omnibus;
import space.arim.omnibus.events.Events;
import space.arim.omnibus.resourcer.ResourceHook;
import space.arim.omnibus.resourcer.ResourceInfo;
import space.arim.omnibus.resourcer.ResourceManager;

/**
 * The default implementation of {@link ResourceManager}
 * 
 * @author A248
 *
 */
public class DefaultResourceManager implements ResourceManager {

	/**
	 * The associated {@code Events} instance
	 * 
	 */
	final Events events;
	
	private final ConcurrentMap<Class<?>, ResourceHolder<?>> resources = new ConcurrentHashMap<>();
	
	/**
	 * Creates a {@code DefaultResourceManager}
	 * 
	 * @param omnibus the omnibus instance
	 */
	public DefaultResourceManager(Omnibus omnibus) {
		events = omnibus.getEvents();
	}
	
	// Testing
	DefaultResourceManager(Events events) {
		this.events = events;
	}
	
	@SuppressWarnings("unchecked")
	private <T> ResourceHolder<T> getHolder(Class<T> clazz) {
		return (ResourceHolder<T>) resources.computeIfAbsent(clazz, (c) -> new ResourceHolder<T>(this, (Class<T>) c));
	}
	
	@Override
	public <T> ResourceHook<T> hookUsage(Class<T> clazz, Supplier<ResourceInfo<T>> defaultImplProvider) {
		return new ResourceHookImpl<T>(getHolder(clazz), defaultImplProvider);
	}
	
	@Override
	public <T> void unhookUsage(ResourceHook<T> hook) {
		ResourceHookImpl<T> hookImpl = (ResourceHookImpl<T>) hook;
		hookImpl.holder.removeHook(hookImpl);
	}
	
}
