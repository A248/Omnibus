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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.google.gson.Gson;

/**
 * <b>UniversalUtil</b>: Main class <br>
 * <br>
 * The most common usage of this class is to check synchronisation: {@link #isAsynchronous()}
 * 
 * @author A248
 *
 */
public final class UniversalUtil {
	
	/**
	 * The id of the instance
	 * 
	 */
	private final String id;
	
	/**
	 * The id of the thread on which the instance is initialised.
	 * 
	 * This thread is taken to be the main thread.
	 * 
	 */
	private final long mainThread = Thread.currentThread().getId();
	
	/**
	 * Since Gson is thread-safe, this is a common Gson for any class to use.
	 * 
	 */
	public static final Gson COMMON_GSON = new Gson();
	
	/**
	 * Instances map to prevent duplicate ids
	 * 
	 */
	private static final ConcurrentHashMap<String, UniversalUtil> INSTANCES = new ConcurrentHashMap<String, UniversalUtil>();

	/**
	 * The main instance id
	 * 
	 */
	public static final String DEFAULT_ID = "main";
	
	// Control instantiation
	private UniversalUtil(String id) {
		this.id = id;
	}
	
	static synchronized UniversalUtil demandUtil(String id) {
		if (!INSTANCES.containsKey(id)) {
			INSTANCES.put(id, new UniversalUtil(id));
		}
		return INSTANCES.get(id);
	}
	
	/**
	 * UniversalUtil instances are thread-safe; however, you may wish for a thread-specific instance. <br>
	 * <br>
	 * A thread-specific instance of UniversalUtil will take the thread itself as the main thread.
	 * Thus, <code>UniversalUtil.threadLocal().asynchronous()</code> will always return <code>false</code>.
	 * 
	 * @return ThreadLocal<UniversalUtil> - a {@link ThreadLocal}
	 */
	public static ThreadLocal<UniversalUtil> threadLocal() {
		return ThreadLocal.withInitial(() -> demandUtil("thread-" + Long.toString(System.currentTimeMillis()) + "-" + Thread.currentThread().getName()));
	}
	
	/**
	 * Retrieves a UniversalUtil instance by class.
	 * If no instance for the classname exists, a new one is created.<br>
	 * <br>
	 * This is the preferred approach to using your own UniversalUtil instances.
	 * 
	 * @param clazz - the class
	 * @return UniversalUtil - the instance. If none exists, a new instance is created.
	 */
	public static UniversalUtil getByClass(Class<?> clazz) {
		return demandUtil("class-" + clazz.getName());
	}
	
	/**
	 * Gets a UniversalUtil by class with a default value, issued by the Supplier, if it does not exist. <br>
	 * <br>
	 * This method is useful for checking for a specific instance and falling back to a default value. <br>
	 * E.g.: <br>
	 * <code>UniversalUtil util = UniversalUtil.getOrDefault(AnotherClass.class, () -> UniversalUtil.get());</code> <br>
	 * Would retrieve the UniversalUtil instance corresponding to AnotherClass.class if the instance exists, fetching the default UniversalUtil instance if not.
	 * 
	 * @param clazz - see {@link #getByClass(Class)}
	 * @param defaultSupplier - from which to return back default values.
	 * @return UniversalUtil - a registered instance if the id exists, otherwise the default value
	 */
	public static UniversalUtil getOrDefault(Class<?> clazz, Supplier<UniversalUtil> defaultSupplier) {
		UniversalUtil util = INSTANCES.get("class-" + clazz.getName());
		return util != null ? util : defaultSupplier.get();
	}
	
	/**
	 * Gets the main instance of UniversalUtil
	 * 
	 * @return UniversalUtil - the instance
	 */
	public static UniversalUtil get() {
		return demandUtil(DEFAULT_ID);
	}
	
	/**
	 * Returns the id of this UniversalUtil instance. <br>
	 * <br>
	 * The current implementation: <br>
	 * * For the main instance, it is {@link #DEFAULT_ID} <br>
	 * * For classname instances retrieved with {@link #getByClassname(String)}, it is "class-" followed by the classname<br>
	 * * For thread-local instances retrieved with {@link #threadLocal()}, it is "thread-" followed by the thread name <br>
	 * However, these values may change.
	 * 
	 * @return String - the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns whether code is running asynchronously. <br>
	 * <br>
	 * Note that the "main thread" is taken to mean the thread on which the instance is initialised. <br>
	 * It is thus recommended to fetch your own instance with {@link #getByClassname(String)} if you plan on using this method. <br>
	 * E.g.: <br>
	 * <code>UniversalUtil myUtil = UniversalUtil.getByClassname(MyClass.class.getName());</code> <br>
	 * <b>By making your own instance on the main thread, you guarantee the validity of this method return</b>
	 * 
	 * @return true if and only if asynchronous
	 */
	public boolean isAsynchronous() {
		return Thread.currentThread().getId() != mainThread;
	}
	
	/**
	 * Recursively retrieves a specified type of object from a Map of potentially nested maps. <br>
	 * Periods delineate a nested map.
	 * <br>
	 * This method is particularly useful for configuration loaded thorugh SnakeYAML. <br>
	 * Specifically, if one must retrieve the yaml value key1.subkey.value as an Integer from the map <code>configValues</code>,
	 * one should call use <code>getFromMapRecursive(configValues, "key1.subkey.value", Integer.class)</code> <br>
	 * <br>
	 * E.g.: <br>
	 * <code>Yaml yaml = new Yaml();<br>
	 * Map<String, Object> cfg = (Map<String, Object>) yaml.load(FILE_INPUT);
	 * String myConfigString = UniversalUtil.getFromMapRecursive(cfg, "settings.language.encoding", String.class);</code>
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
			return type.isInstance(obj) ? (T) obj : null;
		} else if (key.startsWith(".") || key.endsWith(".")) {
			throw new IllegalArgumentException("Cannot retrieve value for invalid key " + key);
		}
		return getFromMapRecursive((Map<String, Object>) map.get(key.substring(0, key.indexOf("."))), key.substring(key.indexOf(".") + 1), type);
	}
	
}
