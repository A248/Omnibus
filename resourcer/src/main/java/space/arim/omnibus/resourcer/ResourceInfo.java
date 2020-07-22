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

import java.util.Objects;

/**
 * Information about an implementation of a resource.
 * 
 * @author A248
 *
 * @param <T> the resource type
 */
public final class ResourceInfo<T> {

	private transient final String name;
	private final T implementation;
	private transient final ShutdownHandler shutdownHandler;
	
	/**
	 * Creates from a name, implementation, and shutdown handler. No parameter may be {@code null}
	 * 
	 * @param name the name of the implementation
	 * @param implementation the implementation
	 * @param shutdownHandler the shutdown handler
	 * @throws NullPointerException if any parameter is null
	 */
	public ResourceInfo(String name, T implementation, ShutdownHandler shutdownHandler) {
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.implementation = Objects.requireNonNull(implementation, "Implementation must not be null");
		this.shutdownHandler = Objects.requireNonNull(shutdownHandler, "Shutdown handler must not be null");
	}
	
	/**
	 * Gets the name of this implementation
	 * 
	 * @return the name, never {@code null}
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the implementation itself
	 * 
	 * @return the implementation, never {@code null}
	 */
	public T getImplementation() {
		return implementation;
	}
	
	/**
	 * Gets the {@link ShutdownHandler} this implementation uses. This method is
	 * intended for use by implementations of {@link Resourcer}.
	 * 
	 * @return the shutdown handler used by this implementation, never {@code null}
	 */
	public ShutdownHandler getShutdownHandler() {
		return shutdownHandler;
	}

	@Override
	public String toString() {
		return "ResourceInfo [name=" + name + ", implementation=" + implementation + ", shutdownHandler="
				+ shutdownHandler + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + implementation.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof ResourceInfo)) {
			return false;
		}
		ResourceInfo<?> other = (ResourceInfo<?>) object;
		return implementation == other.implementation;
	}
	
}
