package de.intarsys.tools.file;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({ "EqualsAvoidNull", "MultipleStringLiterals" })
public class TestPathTools {

	@Test
	public void testGetBaseName() {
		String name;
		String result;
		//
		name = null;
		result = PathTools.getBaseName(name, null, null);
		Assert.assertTrue(result == null);
		//
		name = "";
		result = PathTools.getBaseName(name, null, null);
		Assert.assertTrue(result == null);
		//
		name = "abc";
		result = PathTools.getBaseName(name, null, null);
		Assert.assertTrue(result.equals("abc"));
		//
		name = "abc.txt";
		result = PathTools.getBaseName(name, null, null);
		Assert.assertTrue(result.equals("abc"));
		//
		name = ".txt";
		result = PathTools.getBaseName(name, null, null);
		Assert.assertTrue(result.equals(".txt"));
		//
		name = "abc.";
		result = PathTools.getBaseName(name, null, null);
		Assert.assertTrue(result.equals("abc"));
		//
		name = "foo.bar.txt";
		result = PathTools.getBaseName(name, null, null);
		Assert.assertTrue(result.equals("foo.bar"));
		//
		name = "gnu/foo.txt";
		result = PathTools.getBaseName(name, null, null);
		Assert.assertTrue(result.equals("foo"));
		//
		name = "gnu/";
		result = PathTools.getBaseName(name, null, null);
		Assert.assertTrue(result.equals(""));
		//
		name = "gnu/.foo";
		result = PathTools.getBaseName(name, null, null);
		Assert.assertTrue(result.equals(".foo"));
		// now use extension prefix
		name = "";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result == null);
		//
		name = "abc";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals("abc"));
		//
		name = "abc.txt";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals("abc"));
		//
		name = ".txt";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals(".txt"));
		//
		name = "abc.";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals("abc"));
		//
		name = "foo.bar.txt";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals("foo.bar"));
		//
		name = "gnu/foo.txt";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals("foo"));
		//
		name = "gnu/";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals(""));
		//
		name = "gnu/.foo";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals(".foo"));
		//
		name = "foo.prefix.txt";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals("foo"));
		//
		name = "foo.prefix.bar.txt";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals("foo.prefix.bar"));
		//
		name = "foo.bar.prefix.txt";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals("foo.bar"));
		//
		name = ".prefix.txt";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals(""));
		//
		name = "prefix.txt";
		result = PathTools.getBaseName(name, "prefix", null);
		Assert.assertTrue(result.equals("prefix"));
	}

	@Test
	public void testGetExtension() {
		String name;
		String result;
		//
		name = null;
		result = PathTools.getExtension(name, null, null);
		Assert.assertTrue(result == null);
		//
		name = "";
		result = PathTools.getExtension(name, null, null);
		Assert.assertTrue(result == null);
		//
		name = "abc";
		result = PathTools.getExtension(name, null, null);
		Assert.assertTrue(result == null);
		//
		name = "abc.txt";
		result = PathTools.getExtension(name, null, null);
		Assert.assertTrue(result.equals("txt"));
		//
		name = ".txt";
		result = PathTools.getExtension(name, null, null);
		Assert.assertTrue(result == null);
		//
		name = "abc.";
		result = PathTools.getExtension(name, null, null);
		Assert.assertTrue(result.equals(""));
		//
		name = "foo.bar.txt";
		result = PathTools.getExtension(name, null, null);
		Assert.assertTrue(result.equals("txt"));
		//
		name = "gnu.gnat/foo.txt";
		result = PathTools.getExtension(name, null, null);
		Assert.assertTrue(result.equals("txt"));
		//
		name = "gnu.gnat/";
		result = PathTools.getExtension(name, null, null);
		Assert.assertTrue(result == null);
		//
		name = "gnu.gnat/.foo";
		result = PathTools.getExtension(name, null, null);
		Assert.assertTrue(result == null);
		// now use extension prefix
		name = "";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result == null);
		//
		name = "abc";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result == null);
		//
		name = "abc.txt";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result.equals("txt"));
		//
		name = ".txt";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result == null);
		//
		name = "abc.";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result.equals(""));
		//
		name = "foo.bar.txt";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result.equals("txt"));
		//
		name = "gnu.gnat/foo.txt";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result.equals("txt"));
		//
		name = "gnu.gnat/";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result == null);
		//
		name = "gnu.gnat/.foo";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result == null);
		//
		name = "foo.prefix.txt";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result.equals("prefix.txt"));
		//
		name = "foo.prefix.bar.txt";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result.equals("txt"));
		//
		name = "foo.bar.prefix.txt";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result.equals("prefix.txt"));
		//
		name = ".prefix.txt";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result.equals("txt"));
		//
		name = "prefix.txt";
		result = PathTools.getExtension(name, "prefix", null);
		Assert.assertTrue(result.equals("txt"));
	}

	@Test
	public void testGetFileName() {
		String name;
		String result;
		//
		name = null;
		result = PathTools.getName(name, null);
		Assert.assertTrue(result == null);
		//
		name = "";
		result = PathTools.getName(name, null);
		Assert.assertTrue(result == null);
		//
		name = "abc";
		result = PathTools.getName(name, null);
		Assert.assertTrue(result.equals("abc"));
		//
		name = "abc.txt";
		result = PathTools.getName(name, null);
		Assert.assertTrue(result.equals("abc.txt"));
		//
		name = ".txt";
		result = PathTools.getName(name, null);
		Assert.assertTrue(result.equals(".txt"));
		//
		name = "abc.";
		result = PathTools.getName(name, null);
		Assert.assertTrue(result.equals("abc."));
		//
		name = "foo.bar.txt";
		result = PathTools.getName(name, null);
		Assert.assertTrue(result.equals("foo.bar.txt"));
		//
		name = "gnu/foo.txt";
		result = PathTools.getName(name, null);
		Assert.assertTrue(result.equals("foo.txt"));
		//
		name = "gnu/";
		result = PathTools.getName(name, null);
		Assert.assertTrue(result.equals(""));
		//
		name = "gnu/.foo";
		result = PathTools.getName(name, null);
		Assert.assertTrue(result.equals(".foo"));
	}

	@Test
	public void testGetPathName() {
		String result;
		//
		result = PathTools.getParent("");
		Assert.assertTrue(result.equals(""));
		//
		result = PathTools.getParent("gnu");
		Assert.assertTrue(result.equals(""));
		//
		result = PathTools.getParent("gnu.txt");
		Assert.assertTrue(result.equals(""));
		//
		result = PathTools.getParent("/gnu");
		Assert.assertTrue(result.equals("/"));
		//
		result = PathTools.getParent("a/gnu");
		Assert.assertTrue(result.equals("a"));
		//
		result = PathTools.getParent("/a/gnu");
		Assert.assertTrue(result.equals("/a"));
		//
		result = PathTools.getParent("c:/a/gnu");
		Assert.assertTrue(result.equals("c:/a"));
		//
		result = PathTools.getParent("c:gnu");
		Assert.assertTrue(result.equals(""));
		//
		result = PathTools.getParent("//host/gnu");
		Assert.assertTrue(result.equals("//host"));
		//
		result = PathTools.getParent("//host/a/gnu");
		Assert.assertTrue(result.equals("//host/a"));
	}

	@Test
	public void testJoinPath() {
		String result;
		//
		result = PathTools.join();
		Assert.assertTrue(result.equals(""));
		//
		result = PathTools.join("test");
		Assert.assertTrue(result.equals("test"));
		//
		result = PathTools.join("x", "y");
		Assert.assertTrue(result.equals("x/y"));
		//
		result = PathTools.join("x", "y", "z");
		Assert.assertTrue(result.equals("x/y/z"));
		//
		result = PathTools.join("x", "y", "z", "1");
		Assert.assertTrue(result.equals("x/y/z/1"));
		//
		result = PathTools.join("", "y");
		Assert.assertTrue(result.equals("y"));
		//
		result = PathTools.join("", "x", "", "y");
		Assert.assertTrue(result.equals("x/y"));
		//
		result = PathTools.join("/", "x", "", "y");
		Assert.assertTrue(result.equals("/x/y"));
		//
		result = PathTools.join("/", "/", "x", "", "y");
		Assert.assertTrue(result.equals("/x/y"));
		//
		result = PathTools.join("/");
		Assert.assertTrue(result.equals("/"));
		//
		result = PathTools.join("x", "/");
		Assert.assertTrue(result.equals("x/"));
		//
		result = PathTools.join("/x/", "/y/", "/z/");
		Assert.assertTrue(result.equals("/x/y/z/"));
	}

}
