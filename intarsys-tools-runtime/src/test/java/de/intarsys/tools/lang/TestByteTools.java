package de.intarsys.tools.lang;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestByteTools {

	@Test
	public void testEqualsUnsigned() {
		assertTrue(ByteTools.equalsUnsigned((byte) 0, 0));
		assertTrue(ByteTools.equalsUnsigned((byte) 1, 0x01));
		assertTrue(ByteTools.equalsUnsigned((byte) 127, 0x7f));
		assertTrue(ByteTools.equalsUnsigned((byte) 128, 0x80));
		assertTrue(ByteTools.equalsUnsigned((byte) 255, 0xff));
	}
}
