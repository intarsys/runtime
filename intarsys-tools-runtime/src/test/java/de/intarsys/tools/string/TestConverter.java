package de.intarsys.tools.string;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class TestConverter extends TestCase {

	public void testConverterList() {
		List list;
		String value;
		//
		value = "";
		list = Converter.asList(value);
		assertTrue(list.size() == 0);
		//
		value = "a";
		list = Converter.asList(value);
		assertTrue(list.size() == 1);
		assertTrue("a".equals(list.get(0)));
		//
		value = " a";
		list = Converter.asList(value);
		assertTrue(list.size() == 1);
		assertTrue(" a".equals(list.get(0)));
		//
		value = "a ";
		list = Converter.asList(value);
		assertTrue(list.size() == 1);
		assertTrue("a ".equals(list.get(0)));
		//
		value = "a;b";
		list = Converter.asList(value);
		assertTrue(list.size() == 2);
		assertTrue("a".equals(list.get(0)));
		assertTrue("b".equals(list.get(1)));
		//
		value = "a;;b";
		list = Converter.asList(value);
		assertTrue(list.size() == 3);
		assertTrue("a".equals(list.get(0)));
		assertTrue("".equals(list.get(1)));
		assertTrue("b".equals(list.get(2)));
		//
		value = "a;";
		list = Converter.asList(value);
		assertTrue(list.size() == 1);
		assertTrue("a".equals(list.get(0)));
		//
		value = "a mit\\;b";
		list = Converter.asList(value);
		assertTrue(list.size() == 2);
		assertTrue("a mit\\".equals(list.get(0)));
		assertTrue("b".equals(list.get(1)));
		//
		value = "a mit\\\\";
		list = Converter.asList(value);
		assertTrue(list.size() == 1);
		assertTrue("a mit\\\\".equals(list.get(0)));
		//
		value = "\"mit\"";
		list = Converter.asList(value);
		assertTrue(list.size() == 1);
		assertTrue("mit".equals(list.get(0)));
		//
		value = "\"mit";
		list = Converter.asList(value);
		assertTrue(list.size() == 1);
		assertTrue("mit".equals(list.get(0)));
		//
		value = "\"mit\";b";
		list = Converter.asList(value);
		assertTrue(list.size() == 2);
		assertTrue("mit".equals(list.get(0)));
		assertTrue("b".equals(list.get(1)));
		//
		value = "\"mit\"test;b";
		list = Converter.asList(value);
		assertTrue(list.size() == 2);
		assertTrue("mit".equals(list.get(0)));
		assertTrue("b".equals(list.get(1)));
		//
		value = "\"mit;b\";c";
		list = Converter.asList(value);
		assertTrue(list.size() == 2);
		assertTrue("mit;b".equals(list.get(0)));
		assertTrue("c".equals(list.get(1)));
		//
		value = "\"mit\\\";b\";c";
		list = Converter.asList(value);
		assertTrue(list.size() == 2);
		assertTrue("mit\";b".equals(list.get(0)));
		assertTrue("c".equals(list.get(1)));
	}

	public void testConverterMap() {
		Map map;
		String value;
		//
		value = "";
		map = Converter.asMap(value);
		assertTrue(map.size() == 0);
		//
		value = "a";
		map = Converter.asMap(value);
		assertTrue(map.size() == 1);
		assertTrue("".equals(map.get("a")));
		//
		value = " a";
		map = Converter.asMap(value);
		assertTrue(map.size() == 1);
		assertTrue("".equals(map.get("a")));
		//
		value = "a ";
		map = Converter.asMap(value);
		assertTrue(map.size() == 1);
		assertTrue("".equals(map.get("a")));
		//
		value = " a =b";
		map = Converter.asMap(value);
		assertTrue(map.size() == 1);
		assertTrue("b".equals(map.get("a")));
		//
		value = " a = b ";
		map = Converter.asMap(value);
		assertTrue(map.size() == 1);
		assertTrue(" b ".equals(map.get("a")));
		//
		value = "a;b";
		map = Converter.asMap(value);
		assertTrue(map.size() == 2);
		assertTrue("".equals(map.get("a")));
		assertTrue("".equals(map.get("b")));
		//
		value = "a = x ;b= y ";
		map = Converter.asMap(value);
		assertTrue(map.size() == 2);
		assertTrue(" x ".equals(map.get("a")));
		assertTrue(" y ".equals(map.get("b")));
		//
		value = "a = x ;;b= y ";
		map = Converter.asMap(value);
		assertTrue(map.size() == 2);
		assertTrue(" x ".equals(map.get("a")));
		assertTrue(" y ".equals(map.get("b")));
		//
		value = "a =mit\\;";
		map = Converter.asMap(value);
		assertTrue(map.size() == 1);
		assertTrue("mit\\".equals(map.get("a")));
		//
		value = "a =mit\\;b=x";
		map = Converter.asMap(value);
		assertTrue(map.size() == 2);
		assertTrue("mit\\".equals(map.get("a")));
		assertTrue("x".equals(map.get("b")));
		//
		value = "a =mit\\slash";
		map = Converter.asMap(value);
		assertTrue(map.size() == 1);
		assertTrue("mit\\slash".equals(map.get("a")));
		//
		value = "a =mit\\\\drin";
		map = Converter.asMap(value);
		assertTrue(map.size() == 1);
		assertTrue("mit\\\\drin".equals(map.get("a")));
		//
		value = "a =\"mit\"";
		map = Converter.asMap(value);
		assertTrue(map.size() == 1);
		assertTrue("mit".equals(map.get("a")));
		//
		value = "a =\"mit";
		map = Converter.asMap(value);
		assertTrue(map.size() == 1);
		assertTrue("mit".equals(map.get("a")));
		//
		value = "a =\"mit\";b=x";
		map = Converter.asMap(value);
		assertTrue(map.size() == 2);
		assertTrue("mit".equals(map.get("a")));
		assertTrue("x".equals(map.get("b")));
		//
		value = "a =\"mit\"test;b=x";
		map = Converter.asMap(value);
		assertTrue(map.size() == 2);
		assertTrue("mit".equals(map.get("a")));
		assertTrue("x".equals(map.get("b")));
		//
		value = "a =\"mit;c=y\";b=x";
		map = Converter.asMap(value);
		assertTrue(map.size() == 2);
		assertTrue("mit;c=y".equals(map.get("a")));
		assertTrue("x".equals(map.get("b")));
		//
		value = "a =\"mit\\\";c=y\";b=x";
		map = Converter.asMap(value);
		assertTrue(map.size() == 2);
		assertTrue("mit\";c=y".equals(map.get("a")));
		assertTrue("x".equals(map.get("b")));
		//
		value = "my_arg=foo;otherArg=bar;another=\"this is quoted; \\\"fine\\\" he says...\";last1=this is the end";
		map = Converter.asMap(value);
		assertTrue(map.size() == 4);
		assertTrue("foo".equals(map.get("my_arg")));
		assertTrue("bar".equals(map.get("otherArg")));
		assertTrue("this is quoted; \"fine\" he says...".equals(map.get("another")));
		assertTrue("this is the end".equals(map.get("last1")));
	}

	public void testConverterMapFromJson() {
		Map map;
		String value;
		//
		value = "";
		map = Converter.asMap(value);
		assertTrue(map.size() == 0);
		//
		value = "{}";
		map = Converter.asMap(value);
		assertTrue(map.size() == 0);
		//
		value = "{ 'a': 'b'}";
		map = Converter.asMap(value);
		/*
		 * json tools not loaded
		 */
		assertTrue(map.size() == 0);
	}
}
