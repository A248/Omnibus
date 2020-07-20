/* 
 * Omnibus-registry
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-registry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-registry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-registry. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.registry;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Denotes a requirement there must be a registration of specific service types in a {@link Registry}. <br>
 * Should be applied to either {@code Registry} parameters, or methods returning a <code>Registry</code>. <br>
 * <br>
 * If applied to a parameter, the parameter passed should have the service types registered.
 * If applied to a method, such method should return a registry with the service types registered. <br>
 * Such requirements should be validated immediately when the {@code Registry} is passed. <br>
 * <br>
 * For example, the constructor of the following example class would require its {@code Registry} parameter
 * to have a registration for the {@code Dummy} service: <br>
 * <pre>
 * public class Example {
 * 
 *   public Example(@RequireServices(Dummy.class) Registry registry) {
 *     // ...
 *   }
 * }
 * </pre>
 * 
 * @author A248
 *
 */
@Retention(CLASS)
@Target({ METHOD, PARAMETER })
public @interface RequireServices {

	/**
	 * The service types required, represented by their classes, for which
	 * providers must be registered in the relevant {@link Registry}.
	 * 
	 * @return all the service types required
	 */
	Class<?>[] value();
	
}
