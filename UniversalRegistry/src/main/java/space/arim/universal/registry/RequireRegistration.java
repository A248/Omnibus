/* 
 * UniversalRegistry
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * UniversalRegistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalRegistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalRegistry. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.registry;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Denotes a requirement that specific service types be registered in a {@link Registry}. <br>
 * Should be applied to <code>Registry</code> parameters or methods returning a <code>Registry</code>. <br>
 * <br>
 * It is recommended that such requirements be validated immediately when the <code>Registry</code> is passed. <br>
 * <br>
 * If applied to a parameter, the parameter passed should have the service types registered. <br>
 * If applied to a method, such method should return a registry with the service types registered.
 * 
 * @author A248
 *
 */
@Retention(SOURCE)
@Target({ METHOD, PARAMETER })
public @interface RequireRegistration {

	Class<?>[] value();
	
}
