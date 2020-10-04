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
package space.arim.omnibus.util.concurrent.impl;

import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

/**
 * {@link FactoryOfTheFuture} implementation which regards <i>Sync</i> method calls to
 * the {@link CentralisedFuture}s produced by the factory as identical to <i>Async</i>
 * calls using the default executor. <br>
 * <br>
 * Specifically, this implementation produces {@link IndifferentCentralisedFuture}s. It is
 * suitable for applications where there is no "main" or "primary" thread. Calls to
 * {@link #executeSync(Runnable)} are treated identically as those to {@link #execute(Runnable)}.
 * 
 * @author A248
 *
 */
public class IndifferentFactoryOfTheFuture extends AbstractFactoryOfTheFuture {

	@Override
	public <U> CentralisedFuture<U> newIncompleteFuture() {
		return new IndifferentCentralisedFuture<>();
	}

	@Override
	public void executeSync(Runnable command) {
		execute(command);
	}

}
