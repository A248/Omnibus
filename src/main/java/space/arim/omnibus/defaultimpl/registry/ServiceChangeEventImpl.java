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
package space.arim.omnibus.defaultimpl.registry;

import space.arim.omnibus.registry.Registration;
import space.arim.omnibus.registry.ServiceChangeEvent;

import java.util.Objects;
import java.util.Optional;

class ServiceChangeEventImpl<T> implements ServiceChangeEvent<T> {

	private final Class<T> service;
	private final Registration<T> previous;
	private final Registration<T> updated;

	ServiceChangeEventImpl(Class<T> service, Registration<T> previous, Registration<T> updated) {
		this.service = service;
		this.previous = previous;
		this.updated = updated;
		assert previous != null || updated != null : service;
	}

	@Override
	public Class<T> getService() {
		return service;
	}

	@Override
	public Optional<Registration<T>> getPrevious() {
		return Optional.ofNullable(previous);
	}

	@Override
	public Optional<Registration<T>> getUpdated() {
		return Optional.ofNullable(updated);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServiceChangeEventImpl<?> that = (ServiceChangeEventImpl<?>) o;
		return service.equals(that.service) && Objects.equals(previous, that.previous) && Objects.equals(updated, that.updated);
	}

	@Override
	public int hashCode() {
		int result = service.hashCode();
		result = 31 * result + (previous != null ? previous.hashCode() : 0);
		result = 31 * result + (updated != null ? updated.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ServiceChangeEventImpl{" +
				"service=" + service +
				", previous=" + previous +
				", updated=" + updated +
				'}';
	}
}
