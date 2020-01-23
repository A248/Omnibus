/* 
 * UniversalRegistry, a common registry for plugin resources
 * Copyright © 2019 Anand Beh <https://www.arim.space>
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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import space.arim.universal.events.Events;
import space.arim.universal.events.UniversalEvents;

/**
 * <b>UniversalRegistry</b>: Main class <br>
 * <br>
 * Used for retrieving {@link Registry} instances: <br>
 * * {@link #get()} <br>
 * * {@link #getByClass(Class)} <br>
 * * {@link #threadLocal()} <br>
 * * {@link #getOrDefault(Class, Supplier)}
 * 
 * @author A248
 *
 */
public final class UniversalRegistry implements Registry {
	
	/**
	 * The id of the instance
	 * 
	 */
	private final String id;
	
	/**
	 * The registry itself
	 * 
	 */
	private final ConcurrentHashMap<Class<?>, Registrable> registry = new ConcurrentHashMap<Class<?>, Registrable>();
	
	/**
	 * The corresponding {@link Events} instance
	 * 
	 */
	private final Events events;
	
	/**
	 * Instances map to prevent duplicate ids
	 * 
	 */
	private static final ConcurrentHashMap<String, UniversalRegistry> INSTANCES = new ConcurrentHashMap<String, UniversalRegistry>();
	
	/**
	 * The main instance id <br>
	 * <br>
	 * Equivalent to {@link UniversalEvents#DEFAULT_ID}
	 */
	public static final String DEFAULT_ID = UniversalEvents.DEFAULT_ID;
	
	/**
	 * The thread local
	 * 
	 */
	private static final ThreadLocal<Registry> THREAD_LOCAL = ThreadLocal.withInitial(() -> byEvents(UniversalEvents.threadLocal().get()));
	
	// Control instantiation
	private UniversalRegistry(String id, Events events) {
		this.id = id;
		this.events = events;
	}
	
	static Registry demandRegistry(String id, Events events) {
		return INSTANCES.computeIfAbsent(id, (instanceId) -> new UniversalRegistry(instanceId, events));
	}
	
	static Registry byEvents(Events events) {
		return demandRegistry(events.getId(), events);
	}
	
	/**
	 * Registry instances are thread safe; however, you may wish for a thread specific instance nonetheless.
	 * 
	 * @return ThreadLocal a {@link ThreadLocal}
	 */
	public static ThreadLocal<Registry> threadLocal() {
		return THREAD_LOCAL;
	}
	
	/**
	 * Retrieves a Registry instance by class.
	 * If no instance for the classname exists, a new one is created.<br>
	 * <br>
	 * This is the preferred approach to using your own UniversalRegistry instances.
	 * 
	 * @param clazz the class
	 * @return Registry the instance. If none exists, a new instance is created.
	 */
	public static Registry getByClass(Class<?> clazz) {
		return byEvents(UniversalEvents.getByClass(clazz));
	}
	
	/**
	 * Gets a Registry instance by class with a default value, issued by the Supplier, if it does not exist. <br>
	 * <br>
	 * This method is useful for checking for a specific instance and falling back to a default value. <br>
	 * 
	 * @param clazz see {@link #getByClass(Class)}
	 * @param defaultSupplier from which to return back default values.
	 * @return Registry a registered instance if the id exists, otherwise the default value
	 */
	public static Registry getOrDefault(Class<?> clazz, Supplier<Registry> defaultSupplier) {
		Registry registry = INSTANCES.get("class-" + clazz.getName());
		return registry != null ? registry : defaultSupplier.get();
	}
	
	/**
	 * Gets the main instance of UniversalRegistry
	 * 
	 * @return UniversalRegistry the instance
	 */
	public static Registry get() {
		return byEvents(UniversalEvents.get());
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public Events getEvents() {
		return events;
	}
	
	@Override
	public synchronized <T extends Registrable> void register(Class<T> service, T provider) {
		Objects.requireNonNull(provider, "Provider must not be null!");
		if (!registry.containsKey(service) || provider.getPriority() > registry.get(service).getPriority()) {
			registry.put(service, provider);
			getEvents().fireEvent(new RegistrationEvent<T>(getEvents().getUtil().isAsynchronous(), service, provider));
		}
	}
	
	@Override
	public <T extends Registrable> boolean isProvidedFor(Class<T> service) {
		return registry.containsKey(service);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends Registrable> T getRegistration(Class<T> service) {
		return (T) registry.get(service);
	}
	
	@Override
	public Map<Class<?>, Registrable> getRegistrations() {
		return Collections.unmodifiableMap(registry);
	}
	
}