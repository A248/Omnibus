/* 
 * Omnibus-core
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * Omnibus-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Omnibus-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Omnibus-core. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU General Public License.
 */
package space.arim.omnibus;

import java.util.ServiceLoader;

import space.arim.omnibus.defaultimpl.DefaultOmnibus;

/**
 * Instance holder for the central {@link Omnibus} instance.
 * 
 * @author A248
 *
 */
public final class OmnibusProvider {

	private static final OmnibusProviderSpi PROVIDER;
	
	private OmnibusProvider() {}
	
	static {
		ServiceLoader<OmnibusProviderSpi> loader = ServiceLoader.load(OmnibusProviderSpi.class);
		PROVIDER = loader.findFirst().orElseGet(DefaultOmnibusProvider::new);
	}
	
	/**
	 * Gets the central {@link Omnibus} instance
	 * 
	 * @return the {@code Omnibus} instance
	 */
	public static Omnibus getOmnibus() {
		Class<?> caller = (PROVIDER.requiresCallerClass()) ? WalkerHolder.WALKER.getCallerClass() : null;
		return PROVIDER.getOmnibus(caller);
	}
	
	private static class DefaultOmnibusProvider implements OmnibusProviderSpi {
		private final Omnibus inst = new DefaultOmnibus();
		@Override
		public Omnibus getOmnibus(Class<?> callerClass) {
			return inst;
		}
		@Override
		public boolean requiresCallerClass() {
			return false;
		}
	}
	
	private static class WalkerHolder {
		static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
	}
	
}
