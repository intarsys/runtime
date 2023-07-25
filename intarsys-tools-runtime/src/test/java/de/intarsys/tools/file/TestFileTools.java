package de.intarsys.tools.file;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import de.intarsys.tools.system.SystemTools;

@SuppressWarnings({ "EqualsAvoidNull", "MultipleStringLiterals" })
public class TestFileTools {

	@Test
	public void testCopyRecursively() throws IOException {
		File source;
		File destination;
		File prepare;
		//
		prepare = new File("d1");
		prepare.mkdirs();
		prepare = new File("d1/d2");
		prepare.mkdirs();
		prepare = new File("d1/d2/f1");
		FileTools.createEmptyFile(prepare);
		prepare = new File("d1/d2/f2");
		FileTools.createEmptyFile(prepare);
		prepare = new File("d1/f1");
		FileTools.createEmptyFile(prepare);
		prepare = new File("d1/f2");
		FileTools.createEmptyFile(prepare);
		Assert.assertTrue(new File("d1/d2/f1").exists());
		Assert.assertTrue(new File("d1/d2/f2").exists());
		Assert.assertTrue(new File("d1/f1").exists());
		Assert.assertTrue(new File("d1/f2").exists());
		//
		source = new File("d1");
		destination = new File("x");
		FileTools.copyRecursively(source, destination);
		//
		Assert.assertTrue(new File("d1/d2/f1").exists());
		Assert.assertTrue(new File("d1/d2/f2").exists());
		Assert.assertTrue(new File("d1/f1").exists());
		Assert.assertTrue(new File("d1/f2").exists());
		Assert.assertTrue(new File("x/d2/f1").exists());
		Assert.assertTrue(new File("x/d2/f2").exists());
		Assert.assertTrue(new File("x/f1").exists());
		Assert.assertTrue(new File("x/f2").exists());

		FileTools.deleteRecursivly(new File("d1"));
		FileTools.deleteRecursivly(new File("x"));
	}

	@Test
	public void testCopyRecursivelyInto() throws IOException {
		File source;
		File destination;
		File prepare;
		//
		prepare = new File("d1");
		prepare.mkdirs();
		prepare = new File("d1/d2");
		prepare.mkdirs();
		prepare = new File("d1/d2/f1");
		FileTools.createEmptyFile(prepare);
		prepare = new File("d1/d2/f2");
		FileTools.createEmptyFile(prepare);
		prepare = new File("d1/f1");
		FileTools.createEmptyFile(prepare);
		prepare = new File("d1/f2");
		FileTools.createEmptyFile(prepare);
		Assert.assertTrue(new File("d1/d2/f1").exists());
		Assert.assertTrue(new File("d1/d2/f2").exists());
		Assert.assertTrue(new File("d1/f1").exists());
		Assert.assertTrue(new File("d1/f2").exists());
		//
		source = new File("d1");
		destination = new File("x");
		FileTools.copyRecursivelyInto(source, destination, null);
		//
		Assert.assertTrue(new File("d1/d2/f1").exists());
		Assert.assertTrue(new File("d1/d2/f2").exists());
		Assert.assertTrue(new File("d1/f1").exists());
		Assert.assertTrue(new File("d1/f2").exists());
		Assert.assertTrue(new File("x/d1/d2/f1").exists());
		Assert.assertTrue(new File("x/d1/d2/f2").exists());
		Assert.assertTrue(new File("x/d1/f1").exists());
		Assert.assertTrue(new File("x/d1/f2").exists());

		FileTools.deleteRecursivly(new File("d1"));
		FileTools.deleteRecursivly(new File("x"));
	}

	@Test
	public void testDeleteRecursively() throws IOException {
		File source;
		File prepare;
		//
		prepare = new File("d1");
		prepare.mkdirs();
		prepare = new File("d1/d2");
		prepare.mkdirs();
		prepare = new File("d1/d2/f1");
		FileTools.createEmptyFile(prepare);
		prepare = new File("d1/d2/f2");
		FileTools.createEmptyFile(prepare);
		prepare = new File("d1/f1");
		FileTools.createEmptyFile(prepare);
		prepare = new File("d1/f2");
		FileTools.createEmptyFile(prepare);
		Assert.assertTrue(new File("d1/d2/f1").exists());
		Assert.assertTrue(new File("d1/d2/f2").exists());
		Assert.assertTrue(new File("d1/f1").exists());
		Assert.assertTrue(new File("d1/f2").exists());
		//
		source = new File("d1");
		FileTools.deleteRecursivly(source);
		//
		Assert.assertFalse(new File("d1/d2/f1").exists());
		Assert.assertFalse(new File("d1/d2/f2").exists());
		Assert.assertFalse(new File("d1/f1").exists());
		Assert.assertFalse(new File("d1/f2").exists());
		Assert.assertFalse(new File("d1").exists());
	}

	@Test
	public void testParent() {
		File test = new File("a/b");
		File result = FileTools.getParentFile(test);
		File expected = new File("a");
		Assert.assertTrue(result.equals(expected));
		//
		test = new File("a/b/c");
		result = FileTools.getParentFile(test);
		expected = new File("a/b");
		Assert.assertTrue(result.equals(expected));
	}

	@Test
	public void testParentWindows() {
		assumeTrue(SystemTools.isWindows());
		File test;
		File result;
		File expected;
		//
		test = new File("c:a/b");
		result = FileTools.getParentFile(test);
		expected = new File("c:a");
		Assert.assertTrue(result.equals(expected));
		//
		test = new File("c:/a/b");
		result = FileTools.getParentFile(test);
		expected = new File("c:/a");
		Assert.assertTrue(result.equals(expected));
		//
		test = new File("c:/");
		result = FileTools.getParentFile(test);
		Assert.assertTrue(result == null);
	}

	@Test
	public void testTrim() {
		String result;
		//
		result = FileTools.trimPath("");
		Assert.assertTrue(result.equals(""));
		//
		result = FileTools.trimPath("a");
		Assert.assertTrue(result.equals("a"));
		//
		result = FileTools.trimPath("/a");
		Assert.assertTrue(result.equals("/a"));
		//
		result = FileTools.trimPath("/a*b");
		Assert.assertTrue(result.equals("/a_b"));
		//
		result = FileTools.trimPath("/a\"b");
		Assert.assertTrue(result.equals("/a_b"));
		//
		result = FileTools.trimPath("/a?b");
		Assert.assertTrue(result.equals("/a_b"));
		//
		result = FileTools.trimPath("/a!b");
		Assert.assertTrue(result.equals("/a!b"));
		//
		result = FileTools.trimPath("/a|b");
		Assert.assertTrue(result.equals("/a_b"));
		//
		result = FileTools.trimPath("/a<b");
		Assert.assertTrue(result.equals("/a_b"));
		//
		result = FileTools.trimPath("/a>b");
		Assert.assertTrue(result.equals("/a_b"));
		//
		result = FileTools.trimPath("/a:b");
		Assert.assertTrue(result.equals("/a_b"));
		//
		result = FileTools.trimPath("/a\nb");
		Assert.assertTrue(result.equals("/a_b"));
		//
		result = FileTools.trimPath("/a\tb");
		Assert.assertTrue(result.equals("/a_b"));
		//
		result = FileTools.trimPath("/a\rb");
		Assert.assertTrue(result.equals("/a_b"));
		//
		result = FileTools.trimPath("/a\fb");
		Assert.assertTrue(result.equals("/a_b"));
		//
		result = FileTools.trimPath("/a**::b");
		assertThat(result, is("/a____b"));
	}

	@Test
	public void testTrimWindows() {
		assumeTrue(SystemTools.isWindows());
		String result;
		//
		result = FileTools.trimPath("c:");
		Assert.assertTrue(result.equals("c:"));
		//
		result = FileTools.trimPath("c:a");
		Assert.assertTrue(result.equals("c:a"));
		//
		result = FileTools.trimPath("c:/a");
		Assert.assertTrue(result.equals("c:/a"));
		//
		result = FileTools.trimPath("c:/a*b");
		Assert.assertTrue(result.equals("c:/a_b"));
		//
		result = FileTools.trimPath("c:/a\"b");
		Assert.assertTrue(result.equals("c:/a_b"));
		//
		result = FileTools.trimPath("c:/a?b");
		Assert.assertTrue(result.equals("c:/a_b"));
		//
		result = FileTools.trimPath("c:/a!b");
		Assert.assertTrue(result.equals("c:/a!b"));
		//
		result = FileTools.trimPath("c:/a|b");
		Assert.assertTrue(result.equals("c:/a_b"));
		//
		result = FileTools.trimPath("c:/a<b");
		Assert.assertTrue(result.equals("c:/a_b"));
		//
		result = FileTools.trimPath("c:/a>b");
		Assert.assertTrue(result.equals("c:/a_b"));
		//
		result = FileTools.trimPath("c:/a:b");
		Assert.assertTrue(result.equals("c:/a_b"));
		//
		result = FileTools.trimPath("c:/a\nb");
		Assert.assertTrue(result.equals("c:/a_b"));
		//
		result = FileTools.trimPath("c:/a\tb");
		Assert.assertTrue(result.equals("c:/a_b"));
		//
		result = FileTools.trimPath("c:/a\rb");
		Assert.assertTrue(result.equals("c:/a_b"));
		//
		result = FileTools.trimPath("c:/a\fb");
		Assert.assertTrue(result.equals("c:/a_b"));
		//
		result = FileTools.trimPath("c:/a**::b");
		Assert.assertTrue(result.equals("c:/a____b"));
	}
}
