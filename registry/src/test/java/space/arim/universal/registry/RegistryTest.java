/* 
 * Universal-registry
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-registry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-registry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-registry. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.registry;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import space.arim.universal.events.UniversalEvents;

public class RegistryTest {

	private Registry registry;
	private TestService provider;
	
	@BeforeEach
	public void setup() {
		registry = new UniversalRegistry(new UniversalEvents());
		provider = new TestServiceImpl();
	}
	
	private static byte randomPriority() {
		return (byte) (ThreadLocalRandom.current().nextInt(-2*Byte.MIN_VALUE) + Byte.MIN_VALUE);
	}
	
	@Test
	public void testBasicRegister() {
		Registration<TestService> registration = registry.register(TestService.class, randomPriority(), provider, "impl");
		assertEquals(provider, registration.getProvider());
		TestService highestPriorityProvider = registry.getProvider(TestService.class);
		assertTrue(provider == highestPriorityProvider);
	}
	
	@Test
	public void testDuplicateRegister() {
		registry.register(TestService.class, randomPriority(), provider, "original");
		try {
			registry.register(TestService.class, randomPriority(), provider, "duplicate");
			fail("Registered duplicate registration without exception");
		} catch (DuplicateRegistrationException expected) {
			
		}
	}
	
	@Test
	public void testMultipleRegistrations() {
		TestService alt = new TestServiceImpl();
		Registration<TestService> regis1 = registry.register(TestService.class, RegistryPriority.LOWER, provider, "low priority");
		Registration<TestService> regis2 = registry.register(TestService.class, RegistryPriority.HIGHER, alt, "higher priority");
		assertEquals(alt, regis2.getProvider());
		assertEquals(alt, registry.getProvider(TestService.class));
		TestService highest = new TestServiceImpl();
		Registration<TestService> regis3 = registry.registerAndGet(TestService.class, RegistryPriority.HIGHEST, highest, "highest priority");
		assertEquals(highest, regis3.getProvider());
		assertEquals(highest, registry.getProvider(TestService.class));
		assertEquals(List.of(regis1, regis2, regis3), registry.getAllRegistrations(TestService.class));
	}
	
}
