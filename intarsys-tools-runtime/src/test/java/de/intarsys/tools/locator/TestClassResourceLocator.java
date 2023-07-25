package de.intarsys.tools.locator;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class TestClassResourceLocator {

	public void testLookup() {
		ClassResourceLocator locator;
		//
		locator = new ClassResourceLocator(getClass(), "/resource.txt");
		assertTrue(!locator.exists());
		//
		locator = new ClassResourceLocator(getClass(), "resource.txt");
		assertTrue(locator.exists());
		//
		locator = new ClassResourceLocator(getClass(), "/child/resource.txt");
		assertTrue(!locator.exists());
		//
		locator = new ClassResourceLocator(getClass(), "child/resource.txt");
		assertTrue(locator.exists());
	}

	public void testName() throws ClassNotFoundException {
		ClassResourceLocator locator;
		//
		locator = new ClassResourceLocator(Class.forName("RootClass"), "de/intarsys/tools/locator/resource.txt");
		assertThat(locator.getPath(), endsWith("/de/intarsys/tools/locator/resource.txt"));
		assertThat(LocatorTools.getBaseName(locator), is("resource"));
		assertThat(LocatorTools.getExtension(locator), is("txt"));
		assertThat(locator.getName(), is("resource.txt"));
	}

	public void testNavigation() throws ClassNotFoundException {
		ClassResourceLocator locator;
		ILocator other;
		//
		locator = new ClassResourceLocator(Class.forName("RootClass"), "de/intarsys/tools");
		//
		other = locator.getParent();
		Assert.assertTrue(other.getPath().equals("/de/intarsys"));
		other = other.getParent();
		Assert.assertTrue(other.getPath().equals("/de"));
		other = other.getParent();
		Assert.assertTrue(other.getPath().equals("/"));
		other = other.getParent();
		Assert.assertTrue(other == null);
		//
		other = locator.getChild("locator");
		Assert.assertTrue(other.getPath().equals("/de/intarsys/tools/locator"));
		other = other.getChild("resource.txt");
		Assert.assertTrue(other.getPath().equals("/de/intarsys/tools/locator/resource.txt"));
	}

	@Test
	public void testXFS() throws IOException, ClassNotFoundException {
		ClassResourceLocator locator;
		ILocator[] children;
		ILocator child;
		//
		locator = new ClassResourceLocator(Class.forName("RootClass"), "");
		children = locator.listLocators(null);
		Assert.assertTrue(children.length == 1);
		child = children[0];
		Assert.assertTrue(child.getName().equals("xfs-test"));
		children = child.listLocators(null);
		Assert.assertTrue(children.length == 3);
		child = children[0];
		Assert.assertTrue(child.getName().equals("abc"));
		child = children[1];
		Assert.assertTrue(child.getName().equals("foo.txt"));
		child = children[2];
		Assert.assertTrue(child.getName().equals("xyz"));
		//
		child = children[0];
		Assert.assertTrue(child.getName().equals("abc"));
		children = child.listLocators(null);
		Assert.assertTrue(children.length == 3);
		child = children[0];
		Assert.assertTrue(child.getName().equals("123"));
		child = children[1];
		Assert.assertTrue(child.getName().equals("456"));
		child = children[2];
		Assert.assertTrue(child.getName().equals("bar.txt"));
		//
		child = locator.listLocators(null)[0];
		child = child.getChild("abc");
		child = child.getChild("123");
		Assert.assertTrue(child.exists());
		child = locator.listLocators(null)[0];
		child = child.getChild("abc");
		child = child.getChild("bar.txt");
		Assert.assertTrue(child.exists());
		child = locator.listLocators(null)[0];
		child = child.getChild("abc");
		child = child.getChild("gnu.txt");
		Assert.assertTrue(!child.exists());
		//
		child = locator.getChild("xfs-test/abc/123");
		Assert.assertTrue(child.exists());
		child = locator.getChild("xfs-test/abc/bar.txt");
		Assert.assertTrue(child.exists());
		child = locator.getChild("xfs-test/abc/gnu.txt");
		Assert.assertTrue(!child.exists());
	}
}
