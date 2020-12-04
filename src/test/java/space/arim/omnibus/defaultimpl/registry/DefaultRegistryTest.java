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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import space.arim.omnibus.defaultimpl.events.DefaultEvents;
import space.arim.omnibus.registry.DuplicateRegistrationException;
import space.arim.omnibus.registry.Registration;
import space.arim.omnibus.registry.Registry;
import space.arim.omnibus.registry.RegistryPriorities;

public class DefaultRegistryTest {

	private Registry registry;
	private TestService provider;

	@BeforeEach
	public void setup() {
		registry = new DefaultRegistry(new DefaultEvents());
		provider = new TestServiceImpl();
	}

	private static byte randomPriority() {
		return (byte) (ThreadLocalRandom.current().nextInt(-2 * Byte.MIN_VALUE) + Byte.MIN_VALUE);
	}

	@Test
	public void testBasicRegister() {
		Registration<TestService> registration = registry.register(TestService.class, randomPriority(), provider,
				"impl");
		assertEquals(provider, registration.getProvider());

		assertEquals(registration, registry.getRegistration(TestService.class));
		assertTrue(provider == registry.getProvider(TestService.class));
	}

	@Test
	public void testRegisterAndGet() {
		Registration<TestService> highestPriorityRegistration = registry.registerAndGet(TestService.class, randomPriority(), provider,
				"impl");
		assertTrue(provider == highestPriorityRegistration.getProvider());

		assertEquals(highestPriorityRegistration, registry.getRegistration(TestService.class));
		assertTrue(provider == registry.getProvider(TestService.class));
	}

	@Test
	public void testDuplicateRegister() {
		registry.register(TestService.class, randomPriority(), provider, "original");
		assertThrows(DuplicateRegistrationException.class, () -> {
			registry.register(TestService.class, randomPriority(), provider, "duplicate");
		});
	}

	@Test
	public void testMultipleRegistrations() {
		TestService alt = new TestServiceImpl();
		Registration<TestService> regis1 = registry.register(TestService.class, RegistryPriorities.LOWER, provider,
				"low priority");
		Registration<TestService> regis2 = registry.register(TestService.class, RegistryPriorities.HIGHER, alt,
				"higher priority");

		assertEquals(alt, regis2.getProvider());
		assertEquals(alt, registry.getProvider(TestService.class));
		assertEquals(List.of(regis1, regis2), registry.getAllRegistrations(TestService.class));

		TestService highest = new TestServiceImpl();
		Registration<TestService> regis3 = registry.registerAndGet(TestService.class, RegistryPriorities.HIGHEST,
				highest, "highest priority");

		assertEquals(highest, regis3.getProvider());
		assertEquals(highest, registry.getProvider(TestService.class));
		assertEquals(List.of(regis1, regis2, regis3), registry.getAllRegistrations(TestService.class));
	}

}
