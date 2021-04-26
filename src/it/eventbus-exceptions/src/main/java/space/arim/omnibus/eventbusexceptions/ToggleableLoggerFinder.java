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

import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.logging.Level;

public final class ToggleableLoggerFinder extends System.LoggerFinder {

	private final ThreadLocal<Boolean> output = ThreadLocal.withInitial(() -> Boolean.TRUE);

	public static ToggleableLoggerFinder getLoggerFinder() {
		return (ToggleableLoggerFinder) System.LoggerFinder.getLoggerFinder();
	}

	public void runSilently(Runnable command) {
		output.set(false);
		try {
			command.run();
		} finally {
			output.set(true);
		}
	}

	public <T> T runSilently(Supplier<T> command) {
		output.set(false);
		T value;
		try {
			value = command.get();
		} finally {
			output.set(true);
		}
		return value;
	}

	@Override
	public System.Logger getLogger(String name, Module module) {
		return new Logger(name);
	}

	private final class Logger implements System.Logger {

		private final String name;

		private Logger(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean isLoggable(Level level) {
			return level.getSeverity() >= Level.INFO.getSeverity() && output.get();
		}

		@Override
		public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {
			if (isLoggable(level)) {
				java.util.logging.Logger.getLogger(name).logrb(convertLevel(level), bundle, msg, thrown);
			}
		}

		@Override
		public void log(Level level, ResourceBundle bundle, String format, Object... params) {
			if (isLoggable(level)) {
				java.util.logging.Logger.getLogger(name).logrb(convertLevel(level), bundle, format, params);
			}
		}
	}

	private static Level convertLevel(System.Logger.Level level) {
		return switch (level) {
		case ALL -> Level.ALL;
		case TRACE -> Level.FINER;
		case DEBUG -> Level.FINE;
		case INFO -> Level.INFO;
		case WARNING -> Level.WARNING;
		case ERROR -> Level.SEVERE;
		case OFF -> Level.OFF;
		};
	}
}
