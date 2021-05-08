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

package space.arim.omnibus.util.concurrent.impl;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AbstractFactoryOfTheFutureTest {

	private final FactoryOfTheFuture factory = new IndifferentFactoryOfTheFuture();

	private Function<Executable, DynamicNode> toTests() {
		return (action) -> DynamicTest.dynamicTest("Test", action);
	}

	@TestFactory
	public Stream<DynamicNode> nullChecks() {
		List<Executable> actions = List.of(
				() -> factory.execute(null),
				() -> factory.executeSync(null),
				() -> factory.allOf((CentralisedFuture<?>[]) null),
				() -> factory.allOf((Collection<? extends CentralisedFuture<Object>>) null),
				() -> factory.copyFuture(null),
				() -> factory.copyStage(null),
				() -> factory.failedFuture(null),
				() -> factory.failedStage(null),
				() -> factory.runAsync(null),
				() -> factory.runAsync(null, null),
				() -> factory.runSync(null),
				() -> factory.supplyAsync(null),
				() -> factory.supplyAsync(null, null),
				() -> factory.supplySync(null)
		);
		return actions.stream().map((action) -> {
			return (Executable) () -> assertThrows(NullPointerException.class, action);
		}).map(toTests());
	}

	/**
	 * Ensures that completing the result of {@code allOf} does not
	 * affect any input futures
	 */
	@TestFactory
	public Stream<DynamicNode> allOfIsUnmodifiable() {
		return IntStream.range(0, 5).mapToObj((inputFuturesCount) -> {
			CentralisedFuture<?>[] inputFutures = new CentralisedFuture[inputFuturesCount];
			for (int n = 0; n < inputFuturesCount; n++) {
				inputFutures[n] = factory.newIncompleteFuture();
			}
			return (Executable) () -> runAllOfIsUnmodifiable(inputFutures);
		}).map(toTests());
	}

	private void runAllOfIsUnmodifiable(CentralisedFuture<?>[] inputFutures) {
		CentralisedFuture<?> resultFuture = factory.allOf(inputFutures);
		resultFuture.complete(null);
		for (CentralisedFuture<?> inputFuture : inputFutures) {
			assertFalse(inputFuture.isDone(), "Completing result of allOf should not complete any input futures");
		}
	}
}
