/* 
 * Omnibus-resourcer
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-resourcer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-resourcer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-resourcer. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.resourcer;

/**
 * A resource hook which retrieves the current implementation of a resource as it is needed. <br>
 * <br>
 * Instances of {@code ResourceHook} are obtainable from {@link ResourceManager} and correspond to the
 * usage of a certain resource. <br>
 * <br>
 * <b>When no longer needed, {@code ResourceHook} MUST be unhooked via {@link ResourceManager#unhookUsage(ResourceHook)}. </b>
 * 
 * @author A248
 *
 * @param <T> the type of the resource
 */
public interface ResourceHook<T> {

	/**
	 * Gets the current resource provider's implementation. <br>
	 * <br>
	 * If the {@link ResourceManager} used for this hook uses lazy instantiation, the provider will be created
	 * as needed on invocation of this method. <br>
	 * <br>
	 * The return value of this method should not be cached in a field, because the current
	 * resource provider may change. Rather, this {@code ResourceHook} may be kept in a field,
	 * and this method should be called whenever the resource is needed.
	 * 
	 * @return the current resource provider's implementation
	 */
	T getResource();
	
}
