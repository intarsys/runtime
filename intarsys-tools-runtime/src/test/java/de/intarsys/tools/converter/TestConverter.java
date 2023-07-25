package de.intarsys.tools.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.intarsys.tools.servicelocator.ServiceLocator;

public class TestConverter {

	@Before
	public void init() {
		ServiceLocator.get().remove(IConverterRegistry.class);
	}

	@Test
	public void testToBoolean() throws ConversionException {
		Object in;
		Object out;
		Class targetType = Boolean.class;
		//
		in = new Object();
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(!(Boolean) out);
		in = null;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(out == null);
		in = -1;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue((Boolean) out);
		in = 0;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(!(Boolean) out);
		in = 1;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue((Boolean) out);
		in = 1.1f;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue((Boolean) out);
		in = "true";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue((Boolean) out);
		in = "false";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(!(Boolean) out);
		in = "t";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue((Boolean) out);
		in = "f";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(!(Boolean) out);
		in = "1";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue((Boolean) out);
		in = "0";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(!(Boolean) out);
		in = true;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue((Boolean) out);
		in = false;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(!(Boolean) out);
		in = 'X';
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(!(Boolean) out);
		in = 't';
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue((Boolean) out);
		in = 'f';
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(!(Boolean) out);
		in = '0';
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(!(Boolean) out);
		in = '1';
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue((Boolean) out);
		in = new Date();
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue((Boolean) out);
	}

	@Test
	public void testToCharacter() throws ConversionException {
		Object in;
		Object out;
		Class targetType = Character.class;
		//
		in = new Object();
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in.toString().charAt(0) == (Character) out);
		in = null;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(out == null);
		in = 1;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue('1' == (Character) out);
		in = 1.1f;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue('1' == (Character) out);
		in = "test";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue('t' == (Character) out);
		in = true;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue('t' == (Character) out);
		in = 'X';
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue('X' == (Character) out);
		in = new Date();
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(String.valueOf(((Date) in).getTime()).charAt(0) == (Character) out);
	}

	@Test
	public void testToDate() throws ConversionException {
		Object in;
		Object out;
		Class targetType = Date.class;
		//
		in = new Object();
		try {
			out = ConverterRegistry.get().convert(in, targetType);
			fail();
		} catch (Exception e) {
			// expected
		}
		in = null;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(out == null);
		in = 1;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(new Date(1).equals(out));
		in = "1";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(new Date(1).equals(out));
		in = '1';
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(new Date(1).equals(out));
		in = 1.1f;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(new Date(1).equals(out));
		in = "test";
		try {
			out = ConverterRegistry.get().convert(in, targetType);
			fail();
		} catch (Exception e) {
			// expected
		}
		in = true;
		try {
			out = ConverterRegistry.get().convert(in, targetType);
			fail();
		} catch (Exception e) {
			// expected
		}
		in = 'X';
		try {
			out = ConverterRegistry.get().convert(in, targetType);
			fail();
		} catch (Exception e) {
			// expected
		}
		in = new Date();
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in.equals(out));
	}

	@Test(expected = ConversionException.class)
	public void testToDestFail() throws ConversionException {
		ConverterRegistry.get().registerConverter(new DestFromSourceConverter());
		//
		Object in = "test";
		DestType out = ConverterRegistry.get().convert(in, DestType.class);
	}

	@Test
	public void testToDestMulti() throws ConversionException {
		ConverterRegistry.get().registerConverter(new DestFromSourceConverter());
		ConverterRegistry.get().registerConverter(new DestFromNegativeSourceConverter());
		//
		SourceType in = new SourceType(4);
		DestType out = ConverterRegistry.get().convert(in, DestType.class);
		assertThat(out.getValue(), is(4));
		in = new SourceType(-4);
		out = ConverterRegistry.get().convert(in, DestType.class);
		assertThat(out.getValue(), is(4));
	}

	@Test
	public void testToDestSuccess() throws ConversionException {
		ConverterRegistry.get().registerConverter(new DestFromSourceConverter());
		//
		SourceType in = new SourceType(4);
		DestType out = ConverterRegistry.get().convert(in, DestType.class);
		assertThat(out.getValue(), is(4));
	}

	@Test
	public void testToFloat() throws ConversionException {
		Object in;
		Object out;
		Class targetType = Float.class;
		//
		in = new Object();
		try {
			out = ConverterRegistry.get().convert(in, targetType);
			fail("should fail");
		} catch (Exception e) {
			// expected
		}
		in = null;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in == out);
		in = 1;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(1 == (Float) out);
		in = 1.1f;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(1.1f == (Float) out);
		in = "test";
		try {
			out = ConverterRegistry.get().convert(in, targetType);
			fail("should fail");
		} catch (Exception e) {
			// expected
		}
		in = "123";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(123 == (Float) out);
		in = "123.12";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(123.12f == (Float) out);
		in = true;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(1f == (Float) out);
		in = 'X';
		try {
			out = ConverterRegistry.get().convert(in, targetType);
			fail("should fail");
		} catch (Exception e) {
			// expected
		}
		in = new Date();
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue((float) (((Date) in).getTime()) == (Float) out);
	}

	@Test
	public void testToInteger() throws ConversionException {
		Object in;
		Object out;
		Class targetType = Integer.class;
		//
		in = new Object();
		try {
			out = ConverterRegistry.get().convert(in, targetType);
			fail("should fail");
		} catch (Exception e) {
			// expected
		}
		in = null;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in == out);
		in = 1;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(1 == (Integer) out);
		in = 1.1f;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(1 == (Integer) out);
		in = "test";
		try {
			out = ConverterRegistry.get().convert(in, targetType);
			fail("should fail");
		} catch (Exception e) {
			// expected
		}
		in = "123";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(123 == (Integer) out);
		in = "123.12";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(123 == (Integer) out);
		in = true;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(1 == (Integer) out);
		in = 'X';
		try {
			out = ConverterRegistry.get().convert(in, targetType);
			fail("should fail");
		} catch (Exception e) {
			// expected
		}
		in = new Date();
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue((int) (((Date) in).getTime()) == (Integer) out);
	}

	@Test
	public void testToObject() throws ConversionException {
		Object in;
		Object out;
		Class targetType = Object.class;
		//
		in = new Object();
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in == out);
		in = null;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in == out);
		in = 1;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in == out);
		in = 1.1f;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in == out);
		in = "test";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in == out);
		in = true;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in == out);
		in = 'X';
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in == out);
		in = new Date();
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in == out);
	}

	@Test
	public void testToString() throws ConversionException {
		Object in;
		Object out;
		Class targetType = String.class;
		//
		in = new Object();
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in.toString().equals(out));
		in = null;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(out == null);
		in = 1;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue("1".equals(out));
		in = 1.1f;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue("1.1".equals(out));
		in = "test";
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(in.equals(out));
		in = true;
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue("true".equals(out));
		in = 'X';
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue("X".equals(out));
		in = new Date();
		out = ConverterRegistry.get().convert(in, targetType);
		assertTrue(String.valueOf(((Date) in).getTime()).equals(out));
	}

}
