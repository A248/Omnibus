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
package space.arim.universal.util;

import java.util.Map;

import com.google.gson.Gson;

public class UniversalUtil {
	
	public static final Gson COMMON_GSON = new Gson();
	
	private static final long mainThread = Thread.currentThread().getId();

	public static boolean asynchronous() {
		return Thread.currentThread().getId() != mainThread;
	}
	
	/**
	 * Recursively retrieves a specified type of object from a Map of potentially nested maps. <br>
	 * Periods delineate a nested map.
	 * <br>
	 * This method is particularly useful for configuration loaded thorugh SnakeYAML. <br>
	 * Specifically, if one must retrieve the yaml value key1.subkey.value as an Integer from the map <code>configValues</code>,
	 * one should call use <code>getFromMapRecursive(configValues, "key1.subkey.value", Integer.class)</code>
	 * 
	 * @param <T> - the type to retrieve. If the object found is not this type, <code>null</code> is returned
	 * @param map - the map from which to retrieve recursively
	 * @param key - the key string
	 * @param type - the type class
	 * @return the object if found, null if not
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getFromMapRecursive(Map<String, Object> map, String key, Class<T> type) {
		if (!key.contains(".")) {
			Object obj = map.get(key);
			return (type.isInstance(obj)) ? (T) obj : null;
		} else if (key.startsWith(".") || key.endsWith(".")) {
			throw new IllegalArgumentException("Cannot retrieve value for invalid key " + key);
		}
		return getFromMapRecursive((Map<String, Object>) map.get(key.substring(0, key.indexOf("."))), key.substring(key.indexOf(".") + 1), type);
	}
	
}
