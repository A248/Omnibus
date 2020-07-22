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

import java.util.Objects;

import space.arim.omnibus.resourcer.ResourceInfo;
import space.arim.omnibus.resourcer.ShutdownHandler;

class ResourceHolder<T> {

	private final DefaultResourcer resourcer;
	private final Class<T> clazz;

	private ResourceProvider<T> provider;
	
	ResourceHolder(DefaultResourcer resourcer, Class<T> clazz) {
		this.resourcer = resourcer;
		this.clazz = clazz;
	}
	
	private ResourceProvider<T> removeHook0(ResourceHookImpl<T> owner) {
		synchronized (this) {
			owner.dirty = true;
			ResourceProvider<T> provider = this.provider;
			if (provider != null && provider.owner == owner) {
				this.provider = null;
				return provider;
			}
			return null;
		}
	}
	
	void removeHook(ResourceHookImpl<T> hook) {
		ResourceProvider<T> provider = removeHook0(hook);
		if (provider != null) {
			ResourceInfo<T> info = provider.info;
			ShutdownHandler shutHandler = info.getShutdownHandler();
			try {
				shutHandler.preShutdownEvent();
			} finally {
				resourcer.events.fireEvent(new ShutdownEventImpl<>(clazz, info.getImplementation()));
			}
			shutHandler.postShutdownEvent();
		}
	}
	
	T getResource(ResourceHookImpl<T> requester) {
		ResourceProvider<T> provider;
		synchronized (this) {
			provider = this.provider;
			if (provider == null) {
				ResourceInfo<T> info = requester.defaultProvider.get();
				Objects.requireNonNull(info, "ResourceInfo returned from defaultImplProvider must not be null");
				provider = new ResourceProvider<>(info, requester);
				T impl = clazz.cast(provider.info.getImplementation());
				this.provider = provider;
				return impl;
			}
		}
		return provider.info.getImplementation();
	}
	
}
