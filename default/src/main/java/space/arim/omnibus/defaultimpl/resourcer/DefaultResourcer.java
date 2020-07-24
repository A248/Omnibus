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

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import space.arim.omnibus.Omnibus;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.resourcer.ResourceHook;
import space.arim.omnibus.resourcer.ResourceInfo;
import space.arim.omnibus.resourcer.Resourcer;
import space.arim.omnibus.resourcer.ShutdownHandler;

/**
 * The default implementation of {@link Resourcer}
 * 
 * @author A248
 *
 */
public class DefaultResourcer implements Resourcer {

	private final EventBus events;
	private final Queue<ShutdownEventImpl<?>> shutdownEventQueue = new ConcurrentLinkedQueue<>();
	
	private final ConcurrentMap<Class<?>, ResourceProvider<?>> resources = new ConcurrentHashMap<>();
	
	/**
	 * Creates a {@code DefaultResourcer}
	 * 
	 * @param omnibus the omnibus instance
	 */
	public DefaultResourcer(Omnibus omnibus) {
		events = omnibus.getEvents();
	}
	
	// Testing
	DefaultResourcer(EventBus events) {
		this.events = events;
	}
	
	@Override
	public <T> ResourceHook<T> hookUsage(Class<T> clazz, Supplier<ResourceInfo<T>> defaultImplProvider) {
		return new ResourceHookImpl<>(this, clazz, defaultImplProvider);
	}

	@Override
	public <T> void unhookUsage(ResourceHook<T> hook) {
		ResourceHookImpl<T> hookImpl = ((ResourceHookImpl<T>) hook);
		hookImpl.dirty = true;
		resources.computeIfPresent(hookImpl.clazz, (c, provider) -> {
			if (provider.owner == hookImpl) {
				@SuppressWarnings("unchecked")
				ShutdownEventImpl<T> shutdownEvent = new ShutdownEventImpl<T>(hookImpl.clazz, (ResourceInfo<T>) provider.info);
				shutdownEventQueue.offer(shutdownEvent);
				return null;
			}
			return provider;
		});
		ShutdownEventImpl<?> event;
		while ((event = shutdownEventQueue.poll()) != null) {
			ShutdownHandler handler = event.info.getShutdownHandler();
			try {
				handler.preShutdownEvent();
			} finally {
				events.fireEvent(event);
			}
			handler.postShutdownEvent();
		}
	}

	<T> T getResource(ResourceHookImpl<T> requester) {
		@SuppressWarnings("unchecked")
		ResourceProvider<T> provider = (ResourceProvider<T>) resources.computeIfAbsent(requester.clazz, requester::createProvider);
		return provider.info.getImplementation();
	}

}
