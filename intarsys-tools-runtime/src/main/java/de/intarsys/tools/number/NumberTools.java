package de.intarsys.tools.number;

public final class NumberTools {

	public static int byteSizeOf(int number) {
		int mask = 0xff000000;
		for (int size = 4; size > 0; size--) {
			if ((number & mask) != 0) {
				return size;
			}
			mask = mask >> 8;
		}
		return 0;
	}

	public static int byteSizeOf(long number) {
		long mask = 0xff00000000000000L;
		for (int size = 8; size > 0; size--) {
			if ((number & mask) != 0) {
				return size;
			}
			mask = mask >> 8;
		}
		return 0;
	}

	public static int toIntSigned(byte value) {
		return value;
	}

	public static int toIntUnsigned(byte value) {
		return (value & 0xff);
	}

	private NumberTools() {
	}

}
