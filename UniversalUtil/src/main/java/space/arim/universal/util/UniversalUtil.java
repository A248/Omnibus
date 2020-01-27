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

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * <b>UniversalUtil</b>: Main class <br>
 * <br>
 * The most common usage of this class is to check synchronisation: {@link #isAsynchronous()} <br>
 *  <br>
 * To retrieve an instance: <br>
 * * {@link #get()} <br>
 * * {@link #getByClass(Class)} <br>
 * * {@link #threadLocal()} <br>
 * * {@link #getOrDefault(Class, Supplier)}
 * 
 * @author A248
 *
 */
public final class UniversalUtil implements Util {
	
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
	 * Instances map to prevent duplicate ids
	 * 
	 */
	private static final ConcurrentHashMap<String, UniversalUtil> INSTANCES = new ConcurrentHashMap<String, UniversalUtil>();

	/**
	 * The main instance id
	 * 
	 */
	public static final String DEFAULT_ID = "main";
	
	/**
	 * The thread local
	 * 
	 */
	private static final ThreadLocal<Util> THREAD_LOCAL = ThreadLocal.withInitial(() -> demandUtil("thread-" + Long.toString(System.currentTimeMillis()) + "-" + Thread.currentThread().getName()));
	
	// Control instantiation
	private UniversalUtil(String id) {
		this.id = id;
	}
	
	static Util demandUtil(String id) {
		return INSTANCES.computeIfAbsent(id, (instanceId) -> new UniversalUtil(instanceId));
	}
	
	/**
	 * Util instances are thread safe; however, you may wish for a thread specific instance. <br>
	 * <br>
	 * A thread specific instance of Util will take the thread itself as the main thread.
	 * Thus, <code>UniversalUtil.threadLocal().asynchronous()</code> will always return <code>false</code>.
	 * 
	 * @return ThreadLocal a {@link ThreadLocal}
	 */
	public static ThreadLocal<Util> threadLocal() {
		return THREAD_LOCAL;
	}
	
	/**
	 * Retrieves a Util instance by class.
	 * If no instance for the classname exists, a new one is created.<br>
	 * <br>
	 * This is the preferred approach to using your own UniversalUtil instances.
	 * 
	 * @param clazz the class
	 * @return the instance. If none exists, a new instance is created.
	 */
	public static Util getByClass(Class<?> clazz) {
		return demandUtil("class-" + clazz.getName());
	}
	
	/**
	 * Gets a Util instance by class with a default value, issued by the Supplier, if it does not exist. <br>
	 * <br>
	 * This method is useful for checking for a specific instance and falling back to a default value. <br>
	 * 
	 * @param clazz see {@link #getByClass(Class)}
	 * @param defaultSupplier from which to return back default values.
	 * @return the instance if it exists, otherwise the default value
	 */
	public static Util getOrDefault(Class<?> clazz, Supplier<Util> defaultSupplier) {
		UniversalUtil util = INSTANCES.get("class-" + clazz.getName());
		return util != null ? util : defaultSupplier.get();
	}
	
	/**
	 * Gets the main Util instance
	 * 
	 * @return the instance
	 */
	public static Util get() {
		return demandUtil(DEFAULT_ID);
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public boolean isAsynchronous() {
		return Thread.currentThread().getId() != mainThread;
	}
	
}
