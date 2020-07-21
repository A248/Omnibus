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

import java.util.function.Supplier;

import space.arim.omnibus.events.Events;

/**
 * A manager responsible for marshalling shared resources, avoiding duplication of the same
 * resource interface. <br>
 * <br>
 * Sharing resources consists in multiple moving parts. First, all clients requiring a specific resource
 * register a {@link ResourceHook} using {@link #hookUsage(Class, Supplier)}. The {@code Supplier} is used
 * to create the default implementation of the resource, as a {@link ResourceInfo}, and is invoked either
 * immediately or lazily by the {@code ResourceManager} implementation. There is one such {@code Supplier}
 * for each resource hook representing that hook's preferred implementation should be it be asked to provide
 * the resource provider. <br>
 * <br>
 * The resource implementation is said to be the resource provider; only one provider may exist at once.
 * {@link ResourceHook#getResource()} should be used whenever the resource is needed, to return the current
 * resource provider. <br>
 * <br>
 * When the resource hook is no longer required, it should be unhooked via {@link #unhookUsage(ResourceHook)}.
 * If such hook's resource provider was used, it will be shut down. First, {@link ShutdownHandler#preShutdownEvent()}
 * will be called. Next, a {@link ShutdownEvent} will be fired using the {@link Events} instance ({@link #getEvents()}).
 * Finally, {@link ShutdownHandler#postShutdownEvent()}. <br>
 * <br>
 * Any remaining resource hooks may listen to the shutdown event and act accordingly. Some resources may require
 * some form of "migration". For example, presume a kind of scheduling service, which returns tasks which may be cancelled.
 * Scheduler users, having cached such cancellable tasks in fields, may need to discard the previous tasks and restart
 * them using the new scheduler implementation.
 * 
 * @author A248
 *
 */
public interface ResourceManager {
	
	/**
	 * Hooks into the usage of a specific resource, with a function to instantiate the resource and its associated
	 * info if no existing provider exists. <br>
	 * <br>
	 * Returns a {@link ResourceHook} whose {@link ResourceHook#getResource()} either returns the existing resource provider
	 * or creates one from the {@code defaultImplProvider} function, with this {@code ResourceManager} as its argument,
	 * and sets the resource provider to the created one.
	 * 
	 * @param <T> the resource type
	 * @param clazz the resource class
	 * @param defaultImplProvider the supplier to create the resource provider if necessary
	 * @return a resource hook which should be used whenever such resource is required
	 * @throws NullPointerException if either parameter is null
	 */
	<T> ResourceHook<T> hookUsage(Class<T> clazz, Supplier<ResourceInfo<T>> defaultImplProvider);
	
	/**
	 * Unhooks a {@link ResourceHook} obtained from {@link #hookUsage(Class, Supplier)}. Signals that the caller
	 * no longer needs such resource. <br>
	 * <br>
	 * If the current provider of the resource was obtained from the {@code Supplier} passed to {@code hookUsage},
	 * then it will be shutdown by its shutdown handler ({@link ResourceInfo#getShutdownHandler()}. Any remaining
	 * clients, still requiring this resource who have not yet called this method, should listen to the event
	 * {@link ShutdownEvent} as needed, and possibly get the next resource provider via recalling
	 * {@link ResourceHook#getResource()} in their own {@code ResourceHook}.
	 * 
	 * @param <T> the resource type
	 * @param hook the resource hook to unhook
	 */
	<T> void unhookUsage(ResourceHook<T> hook);
	
}
