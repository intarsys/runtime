package de.intarsys.tools.reflect;

import static de.intarsys.tools.reflect.ClassTools.getConstantName;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

@SuppressWarnings({ "nls", "ConstantName" })
public class TestClassToolsGetConstantName {

	public static class MyClass implements MyInterface {
		//
	}

	public static interface MyInterface {
		public static final boolean MyBool = true;

		public static final byte ByteLow = 1;
		public static final byte ByteHigh = 127;

		public static final int IntLow = 1;
		public static final int IntHigh = 123456789;

		public static final double DoubleLow = 1.0;
		public static final double DoubleHigh = 1.23e123;

		public static final Object MyObject = new Object();

		public static final String MyString = "test";

		public static final long FirstLong = 1L;
		public static final long SecondLong = 1L;
	}

	public static class MySubclass extends MyClass {
		//
	}

	@Test
	public void test() {
		Class<?>[] targets = new Class[] { MyInterface.class, MyClass.class, MySubclass.class };
		for (Class<?> target : targets) {
			assertEquals("MyBool", getConstantName(target, true));
			assertEquals("ByteLow", getConstantName(target, (byte) 1));
			assertEquals("IntLow", getConstantName(target, 1));
			assertEquals("IntHigh", getConstantName(target, 123456789));
			assertEquals("DoubleLow", getConstantName(target, 1.0));
			assertEquals("DoubleHigh", getConstantName(target, 1.23e123));
			assertEquals("MyObject", getConstantName(target, MyClass.MyObject));
			assertEquals("MyString", getConstantName(target, "test"));
			assertEquals("FirstLong", getConstantName(target, 1L));
			assertEquals(null, getConstantName(target, null));
			assertEquals(null, getConstantName(target, "does not exist"));
		}
	}
}
