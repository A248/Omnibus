import space.arim.omnibus.multiprovider.one.AltOmnibusProviderOne;
import space.arim.omnibus.multiprovider.two.AltOmnibusProviderTwo;

module space.arim.omnibus.multiprovider {
	requires space.arim.omnibus;
	provides space.arim.omnibus.spi.OmnibusProviderSpi with AltOmnibusProviderOne, AltOmnibusProviderTwo;
}