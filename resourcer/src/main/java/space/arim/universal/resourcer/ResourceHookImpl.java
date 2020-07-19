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
package space.arim.universal.resourcer;

import java.util.function.Supplier;

class ResourceHookImpl<T> implements ResourceHook<T> {

	final ResourceHolder<T> holder;
	final Supplier<ResourceInfo<T>> defaultProvider;
	volatile boolean dirty;
	
	ResourceHookImpl(ResourceHolder<T> holder, Supplier<ResourceInfo<T>> defaultProvider) {
		this.holder = holder;
		this.defaultProvider = defaultProvider;
	}
	
	@Override
	public T getResource() {
		if (dirty) {
			throw new IllegalStateException("ResourceHook#getResource cannot be used once unhooked");
		}
		return holder.getResource(this);
	}
	
}
