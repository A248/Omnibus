/* 
 * Omnibus
 * Copyright © 2020 Anand Beh
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
package space.arim.omnibus.registry;

import java.util.Objects;

/**
 * A registration wrapper for a specific service, including a registration priority,
 * service provider implementing the service, and accompanying optional display name.
 * 
 * @author A248
 *
 * @param <T> the service type
 */
public final class Registration<T> implements Comparable<Registration<T>> {
	
	private final byte priority;
	private final T provider;
	private transient final String name;
	
	/**
	 * Creates from a priority, provider, and display name.
	 * 
	 * @param priority the priority of the registration
	 * @param provider the provider which implements the service
	 * @param name the name
	 * @throws NullPointerException if {@code provider} or {@code name} is null
	 */
	public Registration(byte priority, T provider, String name) {
		this.priority = priority;
		this.provider = Objects.requireNonNull(provider, "provider");
		this.name = Objects.requireNonNull(name, "name");
	}

	/**
	 * The priority of this registration. If multiple registrations for
	 * the same service exist, the higher priority registration is used.
	 * 
	 * @return the priority
	 */
	public byte getPriority() {
		return priority;
	}
	
	/**
	 * Gets the provider, or the actual service.
	 *
	 * @return the service provider, never {@code null}
	 */
	public T getProvider() {
		return provider;
	}
	
	/**
	 * Gets a friendly display name for the service.
	 * 
	 * @return the name, never {@code null}
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public int compareTo(Registration<T> o) {
		int priorityDiff = priority - o.priority;
		if (priorityDiff == 0) {
			// Same priority, different registration
			return System.identityHashCode(provider) - System.identityHashCode(o.provider);
		}
		return priorityDiff;
	}
	
	@Override
	public String toString() {
		return "Registration [priority=" + priority + ", provider=" + provider + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + priority;
		result = prime * result + System.identityHashCode(provider);
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Registration<?>)) {
			return false;
		}
		Registration<?> other = (Registration<?>) object;
		return priority == other.priority && provider == other.provider;
	}
	
}
