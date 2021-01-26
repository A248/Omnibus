/*
 * Omnibus
 * Copyright © 2021 Anand Beh
 *
 * Omnibus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Omnibus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Omnibus. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */

package space.arim.omnibus.defaultimpl.events;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.events.EventBusDriver;

public class DefaultEventsExtension implements ParameterResolver {

	private final ExtensionContext.Namespace namespace = ExtensionContext.Namespace.create(getClass());

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		var parameterType = parameterContext.getParameter().getType();
		return parameterType.equals(EventBus.class) || parameterType.equals(EventBusDriver.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		EventBus eventBus = extensionContext.getStore(namespace)
				.getOrComputeIfAbsent(EventBus.class, (c) -> new DefaultEvents(), EventBus.class);
		return (parameterContext.getParameter().getType().equals(EventBus.class)) ? eventBus : eventBus.getDriver();
	}
}
