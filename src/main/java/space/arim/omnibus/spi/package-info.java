/**
 * Defines a service provider framework allowing management of instances returned by {@link OmnibusProvider#getOmnibus()}. <br>
 * <br>
 * The interfaces in this package must be implemented by service providers. An example of replacing the global {@link Omnibus}
 * instance is provided: <br>
 * <pre>
 * public class MyProvider implements OmnibusProviderSpi {
 * 
 * 	public OmnibusDefiner createDefiner() {
 * 		return new MyDefiner();
 * 	}
 * 	public byte priority() {
 * 		return (byte) -32;
 * 	}
 * }
 * 
 * class MyDefiner implements OmnibusDefiner {
 * 
 * 	private final Omnibus instance = new CustomOmnibusImplementation();
 * 
 * 	public Omnibus getOmnibus(Class{@literal <?>} callerClass) {
 * 		return instance;
 * 	}
 * 
 * 	public boolean requiresCallerClass() {
 * 		return false;
 * 	}
 * 
 * }
 * </pre>
 * 
 */
package space.arim.omnibus.spi;

import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;
