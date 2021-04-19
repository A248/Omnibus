import space.arim.omnibus.singleprovider.AltOmnibusProvider;

module space.arim.omnibus.singleprovider {
	requires space.arim.omnibus;
	provides space.arim.omnibus.spi.OmnibusProviderSpi with AltOmnibusProvider;
}