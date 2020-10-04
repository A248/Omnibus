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
package space.arim.omnibus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import space.arim.omnibus.defaultimpl.DefaultOmnibusDefiner;
import space.arim.omnibus.spi.OmnibusDefiner;
import space.arim.omnibus.spi.OmnibusProviderSpi;

/**
 * Instance holder for the central {@link Omnibus} instance.
 * 
 * @author A248
 *
 */
public final class OmnibusProvider {

	private static final OmnibusDefiner DEFINER;
	
	private OmnibusProvider() {}
	
	static {
		DEFINER = getDefiner();
	}
	
	private static OmnibusDefiner getDefiner() {
		ModuleLayer layer = OmnibusProvider.class.getModule().getLayer();
		ServiceLoader<OmnibusProviderSpi> loader = ServiceLoader.load(layer, OmnibusProviderSpi.class);
		Iterator<OmnibusProviderSpi> it = loader.iterator();
		if (!it.hasNext()) {
			return new DefaultOmnibusDefiner();
		}
		List<OmnibusProviderSpi> providers = new ArrayList<>();
		do {
			providers.add(it.next());
		} while (it.hasNext());

		providers.sort(new Comparator<OmnibusProviderSpi>() {
			@Override
			public int compare(OmnibusProviderSpi o1, OmnibusProviderSpi o2) {
				return o1.priority() - o2.priority();
			}
		}.reversed()); // reverse to use descending order
		return providers.get(0).createDefiner();
	}
	
	/**
	 * Gets the central {@link Omnibus} instance.
	 *
	 * @return the {@code Omnibus} instance
	 */
	public static Omnibus getOmnibus() {
		Class<?> caller = (DEFINER.requiresCallerClass()) ? WalkerHolder.WALKER.getCallerClass() : null;
		return DEFINER.getOmnibus(caller);
	}

	private static class WalkerHolder {
		static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
	}
	
}
