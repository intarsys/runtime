package de.intarsys.tools.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestPathFilter {

	@Test
	public void testDefault() {
		PathFilter filter = new PathFilter();

		String path;
		path = "license_my.lic";
		assertTrue(filter.accept(path));
		path = "somewhere/license_my.lic";
		assertTrue(filter.accept(path));
		path = "somewhere/license_my.txt";
		assertTrue(filter.accept(path));
	}

	@Test
	public void testExclude() {
		PathFilter filter = new PathFilter();
		filter.addExclude("somewhere/*");

		String path;
		path = "somewhere/license_my.lic";
		assertFalse(filter.accept(path));
	}

	@Test
	public void testExcludeAll() {
		PathFilter filter = new PathFilter();
		filter.addExclude("*");

		String path;
		path = "license_my.lic";
		assertFalse(filter.accept(path));
		path = "somewhere/license_my.lic";
		assertFalse(filter.accept(path));
		path = "somewhere/license_my.txt";
		assertFalse(filter.accept(path));
	}

	@Test
	public void testExcludeSubdirFile() {
		PathFilter filter = new PathFilter();
		filter.addExclude("*.lic");

		String path;
		path = "somewhere/license_my.lic";
		assertFalse(filter.accept(path));
	}

	@Test
	public void testInclude() {
		PathFilter filter = new PathFilter();
		filter.addInclude("*.lic");

		String path;
		path = "somewhere/license_my.lic";
		assertTrue(filter.accept(path));
	}

	@Test
	public void testIncludeAll() {
		PathFilter filter = new PathFilter();
		filter.addInclude("*");

		String path;
		path = "license_my.lic";
		assertTrue(filter.accept(path));
		path = "somewhere/license_my.lic";
		assertTrue(filter.accept(path));
		path = "somewhere/license_my.txt";
		assertTrue(filter.accept(path));
	}

	@Test
	public void testSubdirFile() {
		PathFilter filter = new PathFilter();
		filter.addInclude("*.lic");

		String path;
		path = "somewhere/license_my.lic";
		assertTrue(filter.accept(path));
		path = "somewhere/license_my.txt";
		assertFalse(filter.accept(path));
	}

}
