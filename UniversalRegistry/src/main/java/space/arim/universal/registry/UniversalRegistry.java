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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

import space.arim.universal.events.Events;
import space.arim.universal.events.UniversalEvents;

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
	private final ConcurrentHashMap<Class<?>, List<Registration<?>>> registry = new ConcurrentHashMap<Class<?>, List<Registration<?>>>();
	
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
	private final BlockingQueue<RegistrationEvent<?>> eventQueue = new LinkedBlockingQueue<>();
	
	/**
	 * The main instance
	 * 
	 */
	private static final UniversalRegistry DEFAULT_REGISTRY = new UniversalRegistry(UniversalEvents.get());
	
	/**
	 * Creates a UniversalRegistry. <br>
	 * This may be useful for creating one's own instances. A service registered in one instance
	 * has no relation to any other. <br>
	 * <br>
	 * The backing events instance provided is used for firing {@link RegistrationEvent}s.
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
		return DEFAULT_REGISTRY;
	}
	
	@Override
	public Events getEvents() {
		return events;
	}
	
	private <T> List<Registration<?>> computeRegistrations(Class<T> service, Registration<T> registration) {
		return registry.compute(service, (s, registrations) -> {

			if (registrations == null) {
				registrations = new CopyOnWriteArrayList<Registration<?>>();
				registrations.add(registration);

			} else {
				// adds the element into the list according to priority
				// this is faster than using List#sort
				int position = Collections.binarySearch(registrations, registration);
				if (position < 0) {
					registrations.add(-(position + 1), registration);
				} else {
					registrations.add(position, registration);
				}
			}

			addEventToFire(service, registration);
			return registrations;
		});
	}
	
	/**
	 * Get the highest priority element in the list in a thread safe,
	 * efficient manner, using the iterator. <br>
	 * CopyOnWriteArrayList's iterator returns a snapshot of the list state.
	 * 
	 * @param <T> the registration type to cast to
	 * @param registrations the list of registrations
	 * @return the registration, casted
	 */
	@SuppressWarnings("unchecked")
	private <T> Registration<T> getHighestPriorityFrom(List<Registration<?>> registrations) {
		/*
		 * Note that our lists are sorted in ascending order because Collections#binarySearch
		 * requires ascending order. Therefore we have to get the last element, because
		 * the last element will have the highest priority.
		 * 
		 */
		Registration<?> registration = null;
		for (Iterator<Registration<?>> it = registrations.iterator(); it.hasNext();) {
			registration = it.next();
		}
		return (Registration<T>) registration;
	}
	
	private <T> void addEventToFire(Class<T> service, Registration<T> registration) {
		eventQueue.add(new RegistrationEvent<>(service, registration));
	}
	
	private void fireRegistrationEvents() {
		RegistrationEvent<?> event;
		while ((event = eventQueue.poll()) != null) {
			events.fireEvent(event);
		}
	}
	
	@Override
	public <T> Registration<T> register(Class<T> service, byte priority, T provider, String name) {
		Registration<T> registration = new Registration<T>(priority, provider, name); // constructor checks for null params

		computeRegistrations(service, registration);
		fireRegistrationEvents();

		return registration;
	}
	
	@Override
	public <T> Registration<T> registerAndGet(Class<T> service, byte priority, T provider, String name) {
		Registration<T> registration = new Registration<T>(priority, provider, name); // constructor checks for null params

		Registration<T> result = getHighestPriorityFrom(computeRegistrations(service, registration));
		fireRegistrationEvents();

		return result;
	}
	
	@Override
	public <T> T registerAndLoad(Class<T> service, byte priority, T provider, String name) {
		return registerAndGet(service, priority, provider, name).getProvider();
	}
	
	@Override
	public <T> T load(Class<T> service) {
		Registration<T> registration = getRegistration(service);
		return (registration != null) ? registration.getProvider() : null;
	}
	
	@Override
	public <T> Registration<T> getRegistration(Class<T> service) {
		List<Registration<?>> registrations = registry.get(service);
		return (registrations != null) ? getHighestPriorityFrom(registrations) : null;
	}
	
	@Override
	public <T> boolean isProvidedFor(Class<T> service) {
		/*
		 * A service is provided for if there are registrations for it.
		 * 
		 * Because #unregister removes empty lists atomically, we can simply check if
		 * the key is contained in the registry map.
		 * 
		 */
		return registry.containsKey(service);
	}
	
	@Override
	public <T> Registration<T> registerIfAbsent(Class<T> service, Supplier<Registration<T>> computer) {
		List<Registration<?>> registrations = registry.computeIfAbsent(service, (c) -> {

			Registration<T> registration = computer.get();
			if (registration == null) {
				return null; // don't enter the mapping
			}

			List<Registration<?>> list = new CopyOnWriteArrayList<Registration<?>>();
			list.add(registration);
			addEventToFire(service, registration);
			return list;
		});
		/*
		 * Now, there are 4 possibilities regarding the list we now have:
		 * 
		 * 1. The list has just been created and the result of the supplier has
		 * been added to it, so is nonempty.
		 * 2. The list is null because the result of the supplier was null.
		 * 3. The list contains existing registrations, so is nonempty.
		 * 4. The list is empty because it is in an intermediate state where
		 * #unregister is in progress and has just removed the last element from the list.
		 * 
		 * 
		 * If #2, we just return null per the specifications of Registry.
		 */
		if (registrations == null) {
			return null;
		}
		fireRegistrationEvents();
		/*
		 * Otherwise, we have to return a nonnull value, either an existing registration
		 * or a registration we have just added.
		 * 
		 * If the list is nonempty (majority of cases) #getHighestPriorityFrom
		 * will return the nonnull registration, so we can return that.
		 * 
		 * If the list is empty, we have to double back until the intermediate state
		 * has passed. So we recurse.
		 */
		Registration<T> registration = getHighestPriorityFrom(registrations);
		return (registration != null) ? registration : registerIfAbsent(service, computer);
	}
	
	@Override
	public <T> void unregister(Class<T> service, Registration<T> registration) {
		/*
		 * If the list of registrations for the service does not exist, nothing happens.
		 * Otherwise, if the removal of the registration changed the registration list,
		 * and the resulting list is therefore empty, remove it.
		 * 
		 * This way, the registry will not retain empty lists after unregistration.
		 * 
		 */
		registry.compute(service, (clazz, registrations) -> (registrations != null && registrations.remove(registration)
				&& registrations.isEmpty()) ? null : registrations);
	}
	
}