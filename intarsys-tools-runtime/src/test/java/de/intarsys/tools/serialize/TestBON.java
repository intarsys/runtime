package de.intarsys.tools.serialize;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import junit.framework.TestCase;

public class TestBON extends TestCase {

	protected Object mangle(Object in) throws IOException {
		byte[] bytes = BONTools.serializeBytes(in);
		return BONTools.deserialize(bytes);
	}

	public void testBasic() throws IOException {
		Object in;
		Object out;
		//
		in = null;
		out = mangle(in);
		assertTrue(out == null);
		//
		in = 1;
		out = mangle(in);
		assertTrue(out.equals(1));
		//
		in = 1.1f;
		out = mangle(in);
		assertTrue(out.equals(1.1f));
		//
		in = true;
		out = mangle(in);
		assertTrue(out.equals(true));
		//
		in = false;
		out = mangle(in);
		assertTrue(out.equals(false));
		//
		in = "";
		out = mangle(in);
		assertTrue(out.equals(""));
		//
		in = "test";
		out = mangle(in);
		assertTrue(out.equals("test"));
		//
		in = "false";
		out = mangle(in);
		assertTrue(out.equals("false"));
		//
		in = "\"";
		out = mangle(in);
		assertTrue(out.equals("\""));
		//
		in = "\\";
		out = mangle(in);
		assertTrue(out.equals("\\"));
		//
		in = "\"\\\"";
		out = mangle(in);
		assertTrue(out.equals("\"\\\""));
		//
		in = "ÄÜÖäüöß";
		out = mangle(in);
		assertTrue(out.equals("ÄÜÖäüöß"));
		//
		in = new byte[] {};
		out = mangle(in);
		assertTrue(Arrays.equals((byte[]) in, (byte[]) out));
		//
		in = new byte[] { 0 };
		out = mangle(in);
		assertTrue(Arrays.equals((byte[]) in, (byte[]) out));
		//
		in = new byte[] { 1, 2, 3, 4, 5, '@', 0, 1 };
		out = mangle(in);
		assertTrue(Arrays.equals((byte[]) in, (byte[]) out));
	}

	public void testFlatten() throws IOException {
		Map map;
		Map inputMap;
		Map nestedMap;
		IArgs inputArgs;
		IArgs nestedArgs;

		// Plain
		//
		map = new HashMap();
		BONTools.flatten(null, "", map);
		assertTrue(map.get("").equals("null"));
		//
		map = new HashMap();
		BONTools.flatten(1, "", map);
		assertTrue(map.get("").equals("1"));
		//
		map = new HashMap();
		BONTools.flatten("test", "", map);
		assertTrue(map.get("").equals("\"test\""));

		// input map
		//
		//
		map = new HashMap();
		inputMap = new HashMap();
		inputMap.put("a", null);
		inputMap.put("b", 1);
		inputMap.put("c", "test");
		BONTools.flatten(inputMap, "", map);
		assertTrue(map.get("") == null);
		assertTrue(map.get("a").equals("null"));
		assertTrue(map.get("b").equals("1"));
		assertTrue(map.get("c").equals("\"test\""));

		// nested input map
		//
		//
		map = new HashMap();
		inputMap = new HashMap();
		inputMap.put("a", null);
		inputMap.put("b", 1);
		inputMap.put("c", "test");
		nestedMap = new HashMap();
		nestedMap.put("a", null);
		nestedMap.put("b", 1);
		nestedMap.put("c", "test");
		inputMap.put("nested", nestedMap);
		BONTools.flatten(inputMap, "", map);
		assertTrue(map.get("") == null);
		assertTrue(map.get("a").equals("null"));
		assertTrue(map.get("b").equals("1"));
		assertTrue(map.get("c").equals("\"test\""));
		assertTrue(map.get("nested.a").equals("null"));
		assertTrue(map.get("nested.b").equals("1"));
		assertTrue(map.get("nested.c").equals("\"test\""));

		// input args
		//
		//
		map = new HashMap();
		inputArgs = new Args();
		inputArgs.put("x", null);
		inputArgs.put("y", 1);
		inputArgs.put("z", "test");
		BONTools.flatten(inputArgs, "", map);
		assertTrue(map.get("") == null);
		assertTrue(map.get("x").equals("null"));
		assertTrue(map.get("y").equals("1"));
		assertTrue(map.get("z").equals("\"test\""));

		// nested input args
		//
		//
		map = new HashMap();
		inputArgs = new Args();
		inputArgs.put("x", null);
		inputArgs.put("y", 1);
		inputArgs.put("z", "test");
		nestedArgs = new Args();
		nestedArgs.put("x", null);
		nestedArgs.put("y", 1);
		nestedArgs.put("z", "test");
		inputArgs.put("nested", nestedArgs);
		BONTools.flatten(inputArgs, "", map);
		assertTrue(map.get("") == null);
		assertTrue(map.get("x").equals("null"));
		assertTrue(map.get("y").equals("1"));
		assertTrue(map.get("z").equals("\"test\""));
		assertTrue(map.get("nested.x").equals("null"));
		assertTrue(map.get("nested.y").equals("1"));
		assertTrue(map.get("nested.z").equals("\"test\""));

		// nested indexed input args
		//
		//
		map = new HashMap();
		inputArgs = new Args();
		inputArgs.put("x", null);
		inputArgs.put("y", 1);
		inputArgs.put("z", "test");
		nestedArgs = new Args();
		nestedArgs.add(null);
		nestedArgs.add(1);
		nestedArgs.add("test");
		inputArgs.put("nested", nestedArgs);
		BONTools.flatten(inputArgs, "", map);
		assertTrue(map.get("") == null);
		assertTrue(map.get("x").equals("null"));
		assertTrue(map.get("y").equals("1"));
		assertTrue(map.get("z").equals("\"test\""));
		assertTrue(map.get("nested.0").equals("null"));
		assertTrue(map.get("nested.1").equals("1"));
		assertTrue(map.get("nested.2").equals("\"test\""));

		// nested mixed
		//
		//
		map = new HashMap();
		inputArgs = new Args();
		inputArgs.put("x", null);
		inputArgs.put("y", 1);
		inputArgs.put("z", "test");
		nestedMap = new HashMap();
		nestedMap.put("a", null);
		nestedMap.put("b", 1);
		nestedMap.put("c", "test");
		nestedArgs = new Args();
		nestedArgs.put("x", null);
		nestedArgs.put("y", 1);
		nestedArgs.put("z", "test");
		nestedMap.put("nested", nestedArgs);
		inputArgs.put("nested", nestedMap);
		BONTools.flatten(inputArgs, "", map);
		assertTrue(map.get("") == null);
		assertTrue(map.get("x").equals("null"));
		assertTrue(map.get("y").equals("1"));
		assertTrue(map.get("z").equals("\"test\""));
		assertTrue(map.get("nested.a").equals("null"));
		assertTrue(map.get("nested.b").equals("1"));
		assertTrue(map.get("nested.c").equals("\"test\""));
		assertTrue(map.get("nested.nested.x").equals("null"));
		assertTrue(map.get("nested.nested.y").equals("1"));
		assertTrue(map.get("nested.nested.z").equals("\"test\""));

	}
}
