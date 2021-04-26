/*
 * Omnibus
 * Copyright © 2021 Anand Beh
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

package space.arim.omnibus.eventbusexceptions;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import space.arim.omnibus.DefaultOmnibus;
import space.arim.omnibus.events.AsyncEvent;
import space.arim.omnibus.events.Event;
import space.arim.omnibus.events.EventBus;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static space.arim.omnibus.events.ListenerPriorities.HIGH;
import static space.arim.omnibus.events.ListenerPriorities.LOW;
import static space.arim.omnibus.events.ListenerPriorities.LOWER;
import static space.arim.omnibus.events.ListenerPriorities.LOWEST;
import static space.arim.omnibus.events.ListenerPriorities.NORMAL;

public class SkipExceptionsTest {

	public static class Counter implements Event {

		private int count;

		void count() {
			count++;
		}

		int getCount() {
			return count;
		}

	}

	public static class AsyncCounter extends Counter implements AsyncEvent { }

	private int determineCount(EventBus eventBus, Supplier<? extends Counter> evtCreator) {
		Counter evt = evtCreator.get();
		if (evt instanceof AsyncEvent) {
			eventBus.fireAsyncEvent((AsyncEvent) evt).orTimeout(500L, TimeUnit.MILLISECONDS).join();
		} else {
			eventBus.fireEvent(evt);
		}
		return evt.getCount();
	}

	record ExceptionTestPlan(boolean leadingException, boolean middleException, boolean trailingException) {}
	private Stream<ExceptionTestPlan> exceptionTestPlans() {
		return Stream.of(true, false).flatMap((leadingException) -> {
			return Stream.of(true, false).flatMap((middleException) -> {
				return Stream.of(true, false).map((trailingException) -> {
					return new ExceptionTestPlan(leadingException, middleException, trailingException);
				});
			});
		});
	}
	private <T> Stream<DynamicNode> runTests(Stream<T> plans, Consumer<T> testRunner) {
		ToggleableLoggerFinder loggerFinder = ToggleableLoggerFinder.getLoggerFinder();
		return plans.map((testPlan) -> {
			String testName = "With plan " + testPlan;
			return DynamicTest.dynamicTest(testName, () -> {
				loggerFinder.runSilently(() -> testRunner.accept(testPlan));
			});
		});
	}

	private EventBus newEventBus() {
		return new DefaultOmnibus().getEventBus();
	}

	@TestFactory
	public Stream<DynamicNode> callNextDespiteException() {

		return runTests(exceptionTestPlans(), (plan) -> {
			EventBus eventBus = newEventBus();
			if (plan.leadingException()) {
				eventBus.registerListener(Counter.class, LOWEST, (evt) -> { throw new RuntimeException(); });
			}
			eventBus.registerListener(Counter.class, LOWER, Counter::count);
			if (plan.middleException()) {
				eventBus.registerListener(Counter.class, LOW, (evt) -> { throw new RuntimeException(); });
			}
			eventBus.registerListener(Counter.class, NORMAL, Counter::count);
			if (plan.trailingException()) {
				eventBus.registerListener(Counter.class, HIGH, (evt) -> { throw new RuntimeException(); });
			}
			assertEquals(2, determineCount(eventBus, Counter::new));
		});
	}

	private void registerExceptionalListener(EventBus eventBus, byte priority, boolean useController) {
		if (useController) {
			eventBus.registerListener(AsyncCounter.class, priority, (evt, controller) -> {
				throw new RuntimeException();
			});
		} else {
			eventBus.registerListener(AsyncCounter.class, priority, (evt) -> { throw new RuntimeException(); });
		}
	}

	@TestFactory
	public Stream<DynamicNode> callNextDespiteExceptionAsync() {
		return runTests(exceptionTestPlans(), (plan) -> {
			EventBus eventBus = newEventBus();
			if (plan.leadingException()) {
				registerExceptionalListener(eventBus, LOWEST, ThreadLocalRandom.current().nextBoolean());
			}
			eventBus.registerListener(AsyncCounter.class, LOWER, Counter::count);
			if (plan.middleException()) {
				registerExceptionalListener(eventBus, LOW, ThreadLocalRandom.current().nextBoolean());
			}
			eventBus.registerListener(AsyncCounter.class, NORMAL, Counter::count);
			if (plan.trailingException()) {
				registerExceptionalListener(eventBus, HIGH, ThreadLocalRandom.current().nextBoolean());
			}
			assertEquals(2, determineCount(eventBus, AsyncCounter::new));
		});
	}

}
