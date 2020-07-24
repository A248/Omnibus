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

import java.util.function.Supplier;

import space.arim.omnibus.resourcer.ResourceHook;
import space.arim.omnibus.resourcer.ResourceInfo;

class ResourceHookImpl<T> implements ResourceHook<T> {

	private final DefaultResourcer resourcer;
	final Class<T> clazz;
	private final Supplier<ResourceInfo<T>> defaultProvider;
	
	volatile boolean dirty;
	
	ResourceHookImpl(DefaultResourcer resourcer, Class<T> clazz, Supplier<ResourceInfo<T>> defaultProvider) {
		this.resourcer = resourcer;
		this.clazz = clazz;
		this.defaultProvider = defaultProvider;
	}
	
	@Override
	public T getResource() {
		if (dirty) {
			throw new IllegalStateException("ResourceHook#getResource cannot be used once unhooked");
		}
		return resourcer.getResource(this);
	}
	
	ResourceProvider<T> createProvider(Class<?> resourceClass) {
		assert clazz == resourceClass;
		ResourceInfo<T> info = defaultProvider.get();
		resourceClass.cast(info.getImplementation());
		return new ResourceProvider<>(info, this);
	}
	
}
