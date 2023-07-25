package de.intarsys.tools.collection;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class TestMapTools extends TestCase {

	public void testDefineEntries() {
		String declarations;
		Map map;
		//
		map = new HashMap<>();
		declarations = null;
		MapTools.defineEntries(map, declarations);
		assertTrue(map.size() == 0);
		//
		map = new HashMap<>();
		declarations = "";
		MapTools.defineEntries(map, declarations);
		assertTrue(map.size() == 0);
		//
		map = new HashMap<>();
		declarations = "a";
		MapTools.defineEntries(map, declarations);
		assertTrue("".equals(map.get("a")));
		//
		map = new HashMap<>();
		declarations = "a=b";
		MapTools.defineEntries(map, declarations);
		assertTrue("b".equals(map.get("a")));
		//
		map = new HashMap<>();
		declarations = "a=b;foo=bar;;";
		MapTools.defineEntries(map, declarations);
		assertTrue("b".equals(map.get("a")));
		assertTrue("bar".equals(map.get("foo")));
		//
		map = new HashMap<>();
		declarations = "a=b;foo=\"bar\"";
		MapTools.defineEntries(map, declarations);
		assertTrue("b".equals(map.get("a")));
		assertTrue("bar".equals(map.get("foo")));
		//
		map = new HashMap<>();
		declarations = "a=b;foo=\"b\\\"ar\"";
		MapTools.defineEntries(map, declarations);
		assertTrue("b".equals(map.get("a")));
		assertTrue("b\"ar".equals(map.get("foo")));
	}
}
