/* 
 * UniversalUtil, simple utilities for Spigot and BungeeCord
 * Copyright © 2019 Anand Beh <https://www.arim.space>
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
package space.arim.registry.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
	
	private final DateFormat date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	@Override
	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder(date.format(record.getMillis()));
		builder.append(' ').append('[');
		builder.append(record.getLevel().getLocalizedName());
		builder.append(']').append(' ');
		builder.append(formatMessage(record));
		builder.append('\n');
		if (record.getThrown() != null) {
			StringWriter writer1 = new StringWriter();
			try (PrintWriter writer2 = new PrintWriter(writer1)){
				record.getThrown().printStackTrace(writer2);
			}
			builder.append(writer1);
		}
		return builder.toString();
	}
	
}
