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

import space.arim.omnibus.util.AutoClosable;
import space.arim.omnibus.util.concurrent.StoppableService;

/**
 * Utility for creating shutdown handlers. <br>
 * <br>
 * Typically, most cleanup is done after the shutdown event was called, via the implementation of
 * {@link ShutdownHandler#postShutdownEvent()}.
 * 
 * @author A248
 *
 */
public final class ShutdownHandlers {

	private ShutdownHandlers() {}
	
	private static class OfAutoClosable implements ShutdownHandler {

		private final AutoClosable closable;
		
		OfAutoClosable(AutoClosable closable) {
			this.closable = closable;
		}
		
		@Override
		public void preShutdownEvent() {
		}

		@Override
		public void postShutdownEvent() {
			closable.close();
		}
		
	}
	
	/**
	 * Creates a shutdown handler which calls {@link AutoClosable#close()} after the shutdown event is called.
	 * 
	 * @param closable the {@code AutoClosable} to wrap
	 * @return a shutdown handler calling {@code AutoClosable#close()} after the shutdown event
	 */
	public static ShutdownHandler ofAutoClosable(AutoClosable closable) {
		return new OfAutoClosable(closable);
	}
	
	private static class OfStoppableService implements ShutdownHandler {

		private final StoppableService stoppable;
		
		OfStoppableService(StoppableService stoppable) {
			this.stoppable = stoppable;
		}
		
		@Override
		public void preShutdownEvent() {

		}
		
		@Override
		public void postShutdownEvent() {
			stoppable.shutdown();
		}
		
	}
	
	/**
	 * Creates a shutdown handler which calls {@link StoppableService#shutdown()} after the shutdown event is called.
	 * 
	 * @param stoppable the {@code StoppableService} to wrap
	 * @return a shutdown handler calling {@code StoppableService#shutdown()} after the shutdown event
	 */
	public static ShutdownHandler ofStoppableService(StoppableService stoppable) {
		return new OfStoppableService(stoppable);
	}
	
}
