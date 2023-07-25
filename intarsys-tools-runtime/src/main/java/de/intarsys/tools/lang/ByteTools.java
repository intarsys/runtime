package de.intarsys.tools.lang;

public final class ByteTools {

	public static boolean equalsUnsigned(byte byteValue, int intValue) {
		return (byteValue & 0xff) == intValue;
	}

	private ByteTools() {
	}
}
