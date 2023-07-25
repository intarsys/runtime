package de.intarsys.tools.collection;

import java.util.Arrays;

import junit.framework.TestCase;

public class TestArrayTools extends TestCase {

	public TestArrayTools() {
		super();
	}

	public void testArrayReverse() {
		Object[] test = new Object[] { 0, 1, 2, 3 };
		ArrayTools.reverse(test);
		assertTrue(Arrays.equals(new Object[] { 3, 2, 1, 0 }, test));

		test = new Object[] { 0, 1, 2, 3, 4 };
		ArrayTools.reverse(test);
		assertTrue(Arrays.equals(new Object[] { 4, 3, 2, 1, 0 }, test));

		test = new Object[] { 0, 1, 2, 3, 4 };
		ArrayTools.reverse(test, 0, 3);
		assertTrue(Arrays.equals(new Object[] { 2, 1, 0, 3, 4 }, test));

		test = new Object[] { 0, 1, 2, 3, 4 };
		ArrayTools.reverse(test, 0, 4);
		assertTrue(Arrays.equals(new Object[] { 3, 2, 1, 0, 4 }, test));

		test = new Object[] { 0, 1, 2, 3, 4 };
		ArrayTools.reverse(test, 2, 2);
		assertTrue(Arrays.equals(new Object[] { 0, 1, 3, 2, 4 }, test));

		test = new Object[] { 0, 1, 2, 3, 4 };
		ArrayTools.reverse(test, 1, 3);
		assertTrue(Arrays.equals(new Object[] { 0, 3, 2, 1, 4 }, test));
	}

	public void testByteArrayReverse() {
		byte[] test = new byte[] { 0, 1, 2, 3 };
		ArrayTools.reverse(test);
		assertTrue(Arrays.equals(new byte[] { 3, 2, 1, 0 }, test));

		test = new byte[] { 0, 1, 2, 3, 4 };
		ArrayTools.reverse(test);
		assertTrue(Arrays.equals(new byte[] { 4, 3, 2, 1, 0 }, test));

		test = new byte[] { 0, 1, 2, 3, 4 };
		ArrayTools.reverse(test, 0, 3);
		assertTrue(Arrays.equals(new byte[] { 2, 1, 0, 3, 4 }, test));

		test = new byte[] { 0, 1, 2, 3, 4 };
		ArrayTools.reverse(test, 0, 4);
		assertTrue(Arrays.equals(new byte[] { 3, 2, 1, 0, 4 }, test));

		test = new byte[] { 0, 1, 2, 3, 4 };
		ArrayTools.reverse(test, 2, 2);
		assertTrue(Arrays.equals(new byte[] { 0, 1, 3, 2, 4 }, test));

		test = new byte[] { 0, 1, 2, 3, 4 };
		ArrayTools.reverse(test, 1, 3);
		assertTrue(Arrays.equals(new byte[] { 0, 3, 2, 1, 4 }, test));
	}

}
