/* 
 * UniversalUtil
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * UniversalUtil is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * UniversalUtil is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with UniversalUtil. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.universal.util.concurrent.impl;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import space.arim.universal.util.concurrent.EnhancedExecutor;

/**
 * Base class for {@link EnhancedExecutor}, with common-sense implementations for a couple methods.
 * 
 * @author A248
 *
 */
public abstract class AbstractEnhancedExecutor implements EnhancedExecutor {

	@Override
	public CompletableFuture<?> submit(Runnable command) {
		return CompletableFuture.runAsync(command, this);
	}

	@Override
	public <T> CompletableFuture<T> supply(Supplier<T> supplier) {
		return CompletableFuture.supplyAsync(supplier, this);
	}

}
