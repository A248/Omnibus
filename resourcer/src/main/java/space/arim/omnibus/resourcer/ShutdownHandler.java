/* 
 * Universal-resourcer
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Universal-resourcer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Universal-resourcer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Universal-resourcer. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus.resourcer;

/**
 * A shutdown handler to allow implementations of resources to act when they are discarded.
 * 
 * @author A248
 *
 */
public interface ShutdownHandler {

	/**
	 * Triggered before the shutdown event has been called.  Should not throw exceptions.
	 * 
	 */
	void preShutdownEvent();
	
	/**
	 * Triggered after the shutdown event has been called. Should not throw exceptions.
	 * 
	 */
	void postShutdownEvent();
	
	/**
	 * Gets a shutdown handler which does nothing
	 * 
	 * @return a shutdown handler which does nothing
	 */
	static ShutdownHandler none() {
		return ShutdownHandlerNone.INST;
	}
	
}

class ShutdownHandlerNone implements ShutdownHandler {

	static final ShutdownHandlerNone INST = new ShutdownHandlerNone();
	
	@Override
	public void preShutdownEvent() {
	}

	@Override
	public void postShutdownEvent() {
	}

}
