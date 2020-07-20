/* 
 * Universal-resourcer
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-resourcer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-resourcer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-resourcer. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.resourcer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import space.arim.omnibus.events.Events;

/**
 * Implementation of {@link ResourceManager}. Uses lazy instantiation of resource providers.
 * 
 * @author A248
 *
 */
public class UniversalResourceManager implements ResourceManager {

	/**
	 * The associated {@code Events} instance
	 * 
	 */
	private final Events events;
	
	private final ConcurrentMap<Class<?>, ResourceHolder<?>> resources = new ConcurrentHashMap<>();
	
	/**
	 * Creates from a {@link Events} instance, on which {@link ShutdownEvent}s are called.
	 * 
	 * @param events the {@code Events} instance to use
	 */
	public UniversalResourceManager(Events events) {
		this.events = events;
	}
	
	@Override
	public Events getEvents() {
		return events;
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
