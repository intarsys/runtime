package de.intarsys.tools.file;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import de.intarsys.tools.functor.Args;
import junit.framework.TestCase;

@SuppressWarnings({ "EqualsAvoidNull", "MultipleStringLiterals" })
public class TestFilenameResolver extends TestCase {

	public void testGetPathName() throws Exception {
		String result;
		File file;
		//
		result = (String) new FilenameResolver(new File("")).evaluate("path", Args.create());
		assertTrue(result.equals(""));
		//
		result = (String) new FilenameResolver(new File("")).evaluate("basename", Args.create());
		assertTrue(result.equals(""));
		//
		result = (String) new FilenameResolver(new File("")).evaluate("extension", Args.create());
		assertTrue(result.equals(""));
		//
		result = (String) new FilenameResolver(new File("")).evaluate("name", Args.create());
		assertTrue(result.equals(""));
		//
		result = (String) new FilenameResolver(new File("gnu")).evaluate("path", Args.create());
		assertTrue(result.equals(""));
		//
		result = (String) new FilenameResolver(new File("gnu")).evaluate("basename", Args.create());
		assertTrue(result.equals("gnu"));
		//
		result = (String) new FilenameResolver(new File("gnu")).evaluate("extension", Args.create());
		assertTrue(result.equals(""));
		//
		result = (String) new FilenameResolver(new File("gnu")).evaluate("name", Args.create());
		assertTrue(result.equals("gnu"));
		//
		result = (String) new FilenameResolver(new File("gnu.txt")).evaluate("path", Args.create());
		assertTrue(result.equals(""));
		//
		result = (String) new FilenameResolver(new File("gnu.txt")).evaluate("basename", Args.create());
		assertTrue(result.equals("gnu"));
		//
		result = (String) new FilenameResolver(new File("gnu.txt")).evaluate("extension", Args.create());
		assertTrue(result.equals("txt"));
		//
		result = (String) new FilenameResolver(new File("gnu.txt")).evaluate("name", Args.create());
		assertTrue(result.equals("gnu.txt"));
		//
		//
		result = (String) new FilenameResolver(new File("foo/gnu.txt")).evaluate("path", Args.create());
		assertTrue(result.equals("foo"));
		//
		result = (String) new FilenameResolver(new File("foo/gnu.txt")).evaluate("basename", Args.create());
		assertTrue(result.equals("gnu"));
		//
		result = (String) new FilenameResolver(new File("foo/gnu.txt")).evaluate("extension", Args.create());
		assertTrue(result.equals("txt"));
		//
		result = (String) new FilenameResolver(new File("foo/gnu.txt")).evaluate("name", Args.create());
		assertTrue(result.equals("gnu.txt"));
		//
		//
		result = (String) new FilenameResolver(new File("/foo/bar/gnu.txt")).evaluate("path", Args.create());
		assertThat(PathTools.toCanonicalSeparator(result), is("/foo/bar"));
		//
		result = (String) new FilenameResolver(new File("/foo/bar/gnu.txt")).evaluate("basename", Args.create());
		assertTrue(result.equals("gnu"));
		//
		result = (String) new FilenameResolver(new File("/foo/bar/gnu.txt")).evaluate("extension", Args.create());
		assertTrue(result.equals("txt"));
		//
		result = (String) new FilenameResolver(new File("/foo/bar/gnu.txt")).evaluate("name", Args.create());
		assertTrue(result.equals("gnu.txt"));
	}
}
