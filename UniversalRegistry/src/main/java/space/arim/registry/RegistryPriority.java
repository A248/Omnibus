package space.arim.registry;

/**
 * Class containing priority constants for referential use
 * 
 * <br><br>See {@link space.arim.registry.Registrable#getPriority() Registrable.getPriority()}
 * 
 * @author anandbeh
 */
public final class RegistryPriority {
	public static final byte LOWEST = Byte.MIN_VALUE + (byte) 16;
	public static final byte LOWER = (byte) -64;
	public static final byte LOW = (byte) -32;
	public static final byte NORMAL = (byte) 0;
	public static final byte HIGH = (byte) 31;
	public static final byte HIGHER = (byte) 63;
	public static final byte HIGHEST = Byte.MAX_VALUE - (byte) 16;
}
