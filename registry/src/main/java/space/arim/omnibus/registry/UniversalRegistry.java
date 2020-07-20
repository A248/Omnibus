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
package space.arim.omnibus.registry;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

import space.arim.omnibus.events.Events;
import space.arim.omnibus.events.UniversalEvents;
import space.arim.omnibus.util.ArraysUtil;

/**
 * The main implementation of {@link Registry}. <br>
 * <br>
 * To retrieve the central instance, use {@link #get()}. Instances may also be constructed as desired.
 * 
 * @author A248
 *
 */
public class UniversalRegistry implements Registry {

	/**
	 * The registry itself
	 * 
	 */
	private final ConcurrentHashMap<Class<?>, Registration<?>[]> registry = new ConcurrentHashMap<>();
	
	/**
	 * The corresponding {@link Events} instance
	 * 
	 */
	private final Events events;
	
	/**
	 * A list of registration events we'll fire. <br>
	 * We don't want to spend time firing events inside computation lambdas,
	 * particularly if listeners spend a long time.
	 * 
	 */
	private final Queue<RegistryEvent<?>> eventQueue = new ConcurrentLinkedQueue<>();
	
	/**
	 * The main instance, lazily initialized
	 * 
	 */
	private static class MainInstance {
		static final UniversalRegistry INST = new UniversalRegistry(UniversalEvents.get());
	}
	
	/**
	 * Creates a UniversalRegistry. <br>
	 * This may be useful for creating one's own instances. A service registered in one instance
	 * has no relation to any other. <br>
	 * <br>
	 * The backing events instance provided is used for firing {@link RegistryEvent}s.
	 * 
	 * @param events the events instance on which to fire registration events
	 */
	public UniversalRegistry(Events events) {
		this.events = events;
	}
	
	/**
	 * Gets the main Registry instance
	 * 
	 * @return the central instance
	 */
	public static Registry get() {
		return MainInstance.INST;
	}
	
	@Override
	public Events getEvents() {
		return events;
	}
	
	private void fireRegistryEvents() {
		synchronized (eventQueue) {
			RegistryEvent<?> event;
			while ((event = eventQueue.poll()) != null) {
				events.fireEvent(event);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> Registration<T>[] addRegistration(Class<T> service, Registration<T> registration, T provider) {
		events.fireEvent(new RegistrationAddEventImpl<>(service, registration));

		Registration<?>[] result = registry.compute(service, (s, registers) -> {
			if (registers == null) {
				// no existing registrations
				eventQueue.offer(new ServiceChangeEventImpl<T>(service, null, registration));
				return new Registration<?>[] {registration};
			}
			for (Registration<?> existing : registers) {
				if (existing.getProvider() == provider) {
					throw new DuplicateRegistrationException(
							"Provider " + provider + " already registered for service " + service);
				}
			}
			int highPriorityIndex = registers.length - 1;
			int insertionIndex = - (Arrays.binarySearch(registers, registration) + 1);
			if (highPriorityIndex == insertionIndex) {
				Registration<T> previous = (Registration<T>) registers[highPriorityIndex];
				eventQueue.offer(new ServiceChangeEventImpl<T>(service, previous, registration));
			}
			return ArraysUtil.expandAndInsert(registers, registration, insertionIndex);
		});
		fireRegistryEvents();
		return (Registration<T>[]) result;
	}
	
	@Override
	public <T> Registration<T> register(Class<T> service, byte priority, T provider, String name) {
		Registration<T> registration = new Registration<>(priority, provider, name); // constructor checks for null params

		addRegistration(service, registration, provider);

		return registration;
	}
	
	@Override
	public <T> Registration<T> registerAndGet(Class<T> service, byte priority, T provider, String name) {
		Registration<T> registration = new Registration<T>(priority, provider, name); // constructor checks for null params

		Registration<T>[] updated = addRegistration(service, registration, provider);

		return updated[updated.length - 1];
	}
	
	@SuppressWarnings("unchecked")
	private <T> Registration<T>[] getRegistered(Class<T> service) {
		return (Registration<T>[]) registry.get(service);
	}
	
	@Override
	public <T> T getProvider(Class<T> service) {
		Registration<T>[] registrations = getRegistered(service);
		return (registrations == null) ? null : registrations[registrations.length - 1].getProvider();
	}
	
	@Override
	public <T> Registration<T> getRegistration(Class<T> service) {
		Registration<T>[] registrations = getRegistered(service);
		return (registrations == null) ? null : registrations[registrations.length - 1];
	}
	
	@Override
	public <T> List<Registration<T>> getAllRegistrations(Class<T> service) {
		Registration<T>[] registrations = getRegistered(service);
		return (registrations == null) ? List.of() : List.of(registrations);
	}
	
	@Override
	public <T> boolean isProvidedFor(Class<T> service) {
		return registry.containsKey(service);
	}
	
	@Override
	public <T> Registration<T> registerIfAbsent(Class<T> service, Supplier<Registration<T>> supplier) {
		@SuppressWarnings("unchecked")
		Registration<T>[] result = (Registration<T>[]) registry.computeIfAbsent(service, (s) -> {
			Registration<T> supplied = supplier.get();
			if (supplied == null) {
				return null;
			}
			eventQueue.offer(new RegistrationAddEventImpl<>(service, supplied));
			eventQueue.offer(new ServiceChangeEventImpl<>(service, null, supplied));
			return new Registration<?>[] {supplied};
		});
		if (result == null) {
			return null;
		}
		fireRegistryEvents();
		return result[result.length - 1];
	}
	
	@Override
	public <T> Registration<T> unregister(Class<T> service, Registration<T> registration) {
		@SuppressWarnings("unchecked")
		Registration<T>[] result = (Registration<T>[]) registry.computeIfPresent(service, (s, registers) -> {
			int locationIndex = Arrays.binarySearch(registers, registration);
			if (locationIndex < 0) {
				// silently ignore
				return registers;
			}
			eventQueue.offer(new RegistrationRemoveEventImpl<T>(service, registration));
			if (registers.length == 1) {
				eventQueue.offer(new ServiceChangeEventImpl<>(service, registration, null));
				return null;
			}
			@SuppressWarnings("unchecked")
			Registration<T>[] updated = (Registration<T>[]) ArraysUtil.contractAndRemove(registers, locationIndex);
			if (locationIndex == registers.length - 1) {
				eventQueue.offer(new ServiceChangeEventImpl<>(service, registration, updated[updated.length - 1]));
			}
			return updated;
		});
		fireRegistryEvents();
		return (result == null) ? null : result[result.length - 1];
	}
	
}