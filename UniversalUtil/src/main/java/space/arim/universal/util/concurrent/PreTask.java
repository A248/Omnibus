/* 
 * UniversalUtil, simple utilities for Spigot and BungeeCord
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
package space.arim.universal.util.concurrent;

/**
 * Internal class used for default implementations of {@link Scheduler} methods which accept a {@link Consumer} as the task parameter.
 * This object is passed to the Consumer parameters and then its {@link #cancel()}
 * 
 * @author A248
 *
 */
class PreTask implements Task {

	private volatile Task value;
	private volatile boolean cancelled = false;
	
	void fill(Task value) {
		this.value = value;
		if (cancelled) {
			value.cancel();
		}
	}
	
	/**
	 * Cancels the PreTask.
	 * 
	 */
	@Override
	public void cancel() {
		cancelled = true;
		if (value != null) {
			value.cancel();
		}
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
}
