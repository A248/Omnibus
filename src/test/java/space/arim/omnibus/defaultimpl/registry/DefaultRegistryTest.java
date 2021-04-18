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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.arim.omnibus.events.AsyncEvent;
import space.arim.omnibus.events.EventBus;
import space.arim.omnibus.registry.DuplicateRegistrationException;
import space.arim.omnibus.registry.Registration;
import space.arim.omnibus.registry.Registry;
import space.arim.omnibus.registry.RegistryPriorities;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
public class DefaultRegistryTest {

	private final EventBus eventBus;
	private Registry registry;

	public DefaultRegistryTest(@Mock EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@BeforeEach
	public void setup() {
		registry = new DefaultRegistry(eventBus);
	}

	private static byte randomPriority() {
		byte[] value = new byte[1];
		ThreadLocalRandom.current().nextBytes(value);
		return value[0];
	}

	private static byte cappedRandomPriority() {
		byte priority;
		do {
			priority = randomPriority();
		} while (priority == Byte.MIN_VALUE || priority == Byte.MAX_VALUE);
		return priority;
	}

	private void assertTopRegistration(Registration<TestService> registration) {
		assertEquals(registration, registry.getRegistration(TestService.class).orElse(null));
		assertSame(registration.getProvider(), registry.getProvider(TestService.class).orElse(null));
		assertTrue(registry.isProvidedFor(TestService.class));
	}

	private void assertRegistrations(List<Registration<TestService>> registrations) {
		assertEquals(registrations, registry.getAllRegistrations(TestService.class));
	}

	private void assertNoRegistrations() {
		assertEquals(Optional.empty(), registry.getRegistration(TestService.class));
		assertEquals(Optional.empty(), registry.getProvider(TestService.class));
		assertRegistrations(List.of());
		assertFalse(registry.isProvidedFor(TestService.class));
	}

	private void assertFiredEvents(AsyncEvent...events) {
		InOrder inOrder = inOrder(eventBus);
		for (AsyncEvent event : events) {
			inOrder.verify(eventBus).fireAsyncEventWithoutFuture(event);
		}
		inOrder.verifyNoMoreInteractions();
	}

	private Registration<TestService> register(boolean useRegisterAndGet, byte priority,
											   TestService provider, String name) {
		return (useRegisterAndGet) ?
						registry.registerAndGet(TestService.class, priority, provider, name)
						: registry.register(TestService.class, priority, provider, name);
	}

	private Registration<TestService> register(byte priority, TestService provider, String name) {
		return register(false, priority, provider, name);
	}

	private Optional<Registration<TestService>> unregister(Registration<TestService> registration) {
		return registry.unregister(TestService.class, registration);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void basicRegisterUnregister(boolean useRegisterAndGet) {
		TestService provider = new TestServiceImpl();
		byte priority = randomPriority();
		Registration<TestService> registration = register(useRegisterAndGet, priority, provider, "impl");
		assertEquals(provider, registration.getProvider());
		assertTopRegistration(registration);
		assertRegistrations(List.of(registration));

		assertEquals(Optional.empty(), unregister(registration));
		assertNoRegistrations();

		assertFiredEvents(
				new RegistrationAddEventImpl<>(TestService.class, registration),
				new ServiceChangeEventImpl<>(TestService.class, null, registration),
				new RegistrationRemoveEventImpl<>(TestService.class, registration),
				new ServiceChangeEventImpl<>(TestService.class, registration, null));
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void registerHigherProvider(boolean useRegisterAndGet) {
		byte lowPriority = cappedRandomPriority();
		TestService lowProvider = new TestServiceImpl();
		Registration<TestService> lowRegistration = register(useRegisterAndGet, lowPriority, lowProvider, "impl1");
		assertTopRegistration(lowRegistration);
		assertRegistrations(List.of(lowRegistration));

		byte highPriority = (byte) (lowPriority + 1);
		TestService highProvider = new TestServiceImpl();
		Registration<TestService> highRegistration = register(useRegisterAndGet, highPriority, highProvider, "impl2");
		assertTopRegistration(highRegistration);
		assertRegistrations(List.of(lowRegistration, highRegistration));

		assertEquals(Optional.of(lowRegistration), unregister(highRegistration));
		assertTopRegistration(lowRegistration);
		assertEquals(Optional.empty(), unregister(lowRegistration));
		assertNoRegistrations();

		assertFiredEvents(
				new RegistrationAddEventImpl<>(TestService.class, lowRegistration),
				new ServiceChangeEventImpl<>(TestService.class, null, lowRegistration),
				new RegistrationAddEventImpl<>(TestService.class, highRegistration),
				new ServiceChangeEventImpl<>(TestService.class, lowRegistration, highRegistration),
				new RegistrationRemoveEventImpl<>(TestService.class, highRegistration),
				new ServiceChangeEventImpl<>(TestService.class, highRegistration, lowRegistration),
				new RegistrationRemoveEventImpl<>(TestService.class, lowRegistration),
				new ServiceChangeEventImpl<>(TestService.class, lowRegistration, null));
	}

	@TestFactory
	public Stream<DynamicNode> duplicateRegister() {
		return Stream.of(true, false).flatMap((registerAndGet1) -> {
			return Stream.of(true, false).map((registerAndGet2) -> {
				return DynamicTest.dynamicTest("Using " + registerAndGet1 + " and " + registerAndGet2, () -> {
					setup();

					TestService provider = new TestServiceImpl();
					register(registerAndGet1, randomPriority(), provider, "original");
					assertThrows(DuplicateRegistrationException.class, () -> {
						register(registerAndGet2, randomPriority(), provider, "duplicate");
					});
				});
			});
		});
	}

	@Test
	public void duplicateUnregister() {
		TestService provider = new TestServiceImpl();
		Registration<TestService> regis = register(randomPriority(), provider, "impl");
		assertEquals(Optional.empty(), unregister(regis));
		assertNoRegistrations();
		assertEquals(Optional.empty(), unregister(regis));
		assertNoRegistrations();
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void multipleRegistrations(boolean useRegisterAndGet) {
		TestService low = new TestServiceImpl();
		TestService higher = new TestServiceImpl();
		Registration<TestService> regis1 = register(RegistryPriorities.LOWER, low, "low");
		Registration<TestService> regis2 = register(RegistryPriorities.HIGHER, higher, "higher");

		assertTopRegistration(regis2);
		assertRegistrations(List.of(regis1, regis2));

		TestService highest = new TestServiceImpl();
		Registration<TestService> regis3 = register(useRegisterAndGet, RegistryPriorities.HIGHEST, highest, "highest");

		assertTopRegistration(regis3);
		assertRegistrations(List.of(regis1, regis2, regis3));

		assertEquals(Optional.of(regis3), unregister(regis2));
		assertTopRegistration(regis3);
		assertRegistrations(List.of(regis1, regis3));

		assertEquals(Optional.of(regis3), unregister(regis1));
		assertEquals(Optional.of(regis3), unregister(regis1), "Extra unregister is a no-op");
		assertTopRegistration(regis3);
		assertRegistrations(List.of(regis3));

		assertEquals(Optional.empty(), unregister(regis3));
		assertNoRegistrations();

		assertFiredEvents(
				new RegistrationAddEventImpl<>(TestService.class, regis1),
				new ServiceChangeEventImpl<>(TestService.class, null, regis1),
				new RegistrationAddEventImpl<>(TestService.class, regis2),
				new ServiceChangeEventImpl<>(TestService.class, regis1, regis2),
				new RegistrationAddEventImpl<>(TestService.class, regis3),
				new ServiceChangeEventImpl<>(TestService.class, regis2, regis3),
				new RegistrationRemoveEventImpl<>(TestService.class, regis2),
				new RegistrationRemoveEventImpl<>(TestService.class, regis1),
				new RegistrationRemoveEventImpl<>(TestService.class, regis3),
				new ServiceChangeEventImpl<>(TestService.class, regis3, null));
	}

}
