package de.intarsys.tools.xfs;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestXFSNode {

	private int occurences;

	@Test
	public void test() throws IOException {
		IXFSNode node;
		IXFSNode child;
		List<IXFSNode> children;
		//
		node = new XFSClassLoaderNode(getClass().getClassLoader());
		children = node.getChildren();
		Assert.assertTrue(children.size() == 1);
		child = children.get(0);
		Assert.assertTrue(child.getName().equals("xfs-test"));
		children = child.getChildren();
		Assert.assertTrue(children.size() == 3);
		child = children.get(0);
		Assert.assertTrue(child.getName().equals("abc"));
		child = children.get(1);
		Assert.assertTrue(child.getName().equals("foo.txt"));
		child = children.get(2);
		Assert.assertTrue(child.getName().equals("xyz"));
		//
		child = children.get(0);
		Assert.assertTrue(child.getName().equals("abc"));
		children = child.getChildren();
		Assert.assertTrue(children.size() == 3);
		child = children.get(0);
		Assert.assertTrue(child.getName().equals("123"));
		child = children.get(1);
		Assert.assertTrue(child.getName().equals("456"));
		child = children.get(2);
		Assert.assertTrue(child.getName().equals("bar.txt"));
		//
		child = node.getChildren().get(0);
		child = child.getChild("abc");
		child = child.getChild("123");
		Assert.assertTrue(child.exists());
		child = node.getChildren().get(0);
		child = child.getChild("abc");
		child = child.getChild("bar.txt");
		Assert.assertTrue(child.exists());
		child = node.getChildren().get(0);
		child = child.getChild("abc");
		child = child.getChild("gnu.txt");
		Assert.assertTrue(!child.exists());
		//
		child = node.getChild("xfs-test/abc/123");
		Assert.assertTrue(child.exists());
		child = node.getChild("xfs-test/abc/bar.txt");
		Assert.assertTrue(child.exists());
		child = node.getChild("xfs-test/abc/gnu.txt");
		Assert.assertTrue(!child.exists());
	}

	@Test
	public void testScanner() throws Exception {
		XFSScanner scanner;
		//
		scanner = new XFSScanner();
		scanner.setPattern(".*/abc");
		occurences = 0;
		scanner.scan(node -> occurences++);
		Assert.assertTrue(occurences == 1);
	}

}
