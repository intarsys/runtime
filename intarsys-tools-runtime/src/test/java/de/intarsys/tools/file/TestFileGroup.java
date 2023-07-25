package de.intarsys.tools.file;

import java.io.File;
import java.io.IOException;

import de.intarsys.tools.attachment.Attachment;
import de.intarsys.tools.expression.ConstantResolver;
import de.intarsys.tools.expression.IStringEvaluator;
import junit.framework.TestCase;

public class TestFileGroup extends TestCase {

	private File tempDir;

	private File testDir;

	protected File createDir(File parent, String name) throws IOException {
		File file = new File(parent, name);
		FileTools.mkdirs(file);
		return file;
	}

	protected File createFile(File dir, String name) throws IOException {
		File file = new File(dir, name);
		return file;
	}

	protected File fillFile(File dir, String name, String content) throws IOException {
		File file = new File(dir, name);
		FileTools.mkdirs(file.getParentFile());
		FileTools.write(file, content);
		return file;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tempDir = TempTools.getTempDirVM();
		testDir = createDir(tempDir, "test");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		FileTools.deleteRecursivly(testDir);
	}

	public void testAttach1() throws Exception {
		File master;
		File attach1;
		File sourceDir;
		File targetDir;
		FileGroup group;
		File expected;
		IStringEvaluator resolver;
		//
		resolver = new ConstantResolver("diedel");
		sourceDir = createDir(testDir, "a/b");
		targetDir = createDir(testDir, "x/y");
		//
		master = fillFile(sourceDir, "b.txt", "test");
		attach1 = fillFile(sourceDir, "b.att", "test");
		group = new FileGroup(master);
		group.getAttachments().add(new Attachment("a", attach1));
		group.move(targetDir, "${file.name}", resolver, true, false, true);
		assertTrue(!master.exists());
		assertTrue(!attach1.exists());
		expected = createFile(targetDir, "b.txt");
		assertTrue(expected.exists());
		assertTrue(group.getMaster().equals(expected));
		expected = createFile(targetDir, "b.att");
		assertTrue(expected.exists());
		//
		master = fillFile(sourceDir, "c.txt", "test");
		attach1 = fillFile(sourceDir, "c.txt.att", "test");
		group = new FileGroup(master);
		group.getAttachments().add(new Attachment("a", attach1));
		group.move(targetDir, "${file.name}", resolver, true, false, true);
		assertTrue(!master.exists());
		assertTrue(!attach1.exists());
		expected = createFile(targetDir, "c.txt");
		assertTrue(expected.exists());
		assertTrue(group.getMaster().equals(expected));
		expected = createFile(targetDir, "c.txt.att");
		assertTrue(expected.exists());
	}

	public void testAttach2() throws Exception {
		File master;
		File attach1;
		File attach2;
		File sourceDir;
		File targetDir;
		FileGroup group;
		File expected;
		IStringEvaluator resolver;
		//
		resolver = new ConstantResolver("diedel");
		sourceDir = createDir(testDir, "a/b");
		targetDir = createDir(testDir, "x/y");
		//
		master = fillFile(sourceDir, "b.txt", "test");
		attach1 = fillFile(sourceDir, "b.attach1", "test");
		attach2 = fillFile(sourceDir, "b.attach2", "test");
		group = new FileGroup(master);
		group.getAttachments().add(new Attachment("a", attach1));
		group.getAttachments().add(new Attachment("b", attach2));
		group.move(targetDir, "${file.name}", resolver, true, false, true);
		assertTrue(!master.exists());
		assertTrue(!attach1.exists());
		assertTrue(!attach2.exists());
		expected = createFile(targetDir, "b.txt");
		assertTrue(expected.exists());
		assertTrue(group.getMaster().equals(expected));
		expected = createFile(targetDir, "b.attach1");
		assertTrue(expected.exists());
		expected = createFile(targetDir, "b.attach2");
		assertTrue(expected.exists());
		//
		master = fillFile(sourceDir, "c.txt", "test");
		attach1 = fillFile(sourceDir, "c.txt.attach1", "test");
		attach2 = fillFile(sourceDir, "c.txt.attach2", "test");
		group = new FileGroup(master);
		group.getAttachments().add(new Attachment("a", attach1));
		group.getAttachments().add(new Attachment("b", attach2));
		group.move(targetDir, "${file.name}", resolver, true, false, true);
		assertTrue(!master.exists());
		assertTrue(!attach1.exists());
		assertTrue(!attach2.exists());
		expected = createFile(targetDir, "c.txt");
		assertTrue(expected.exists());
		assertTrue(group.getMaster().equals(expected));
		expected = createFile(targetDir, "c.txt.attach1");
		assertTrue(expected.exists());
		expected = createFile(targetDir, "c.txt.attach2");
		assertTrue(expected.exists());
	}

	public void testAttach2MoveTwice() throws Exception {
		File master;
		File attach1;
		File attach2;
		File sourceDir;
		File targetDir;
		FileGroup group;
		File expected;
		IStringEvaluator resolver;
		//
		resolver = new ConstantResolver("diedel");
		sourceDir = createDir(testDir, "a/b");
		targetDir = createDir(testDir, "x/y");
		//
		master = fillFile(sourceDir, "b.txt", "test");
		attach1 = fillFile(sourceDir, "b.attach1", "test");
		attach2 = fillFile(sourceDir, "b.attach2", "test");
		group = new FileGroup(master);
		group.getAttachments().add(new Attachment("a", attach1));
		group.getAttachments().add(new Attachment("b", attach2));
		group.move(targetDir, "${file.name}", resolver, true, false, true);
		assertTrue(!master.exists());
		assertTrue(!attach1.exists());
		assertTrue(!attach2.exists());
		expected = createFile(targetDir, "b.txt");
		assertTrue(expected.exists());
		assertTrue(group.getMaster().equals(expected));
		expected = createFile(targetDir, "b.attach1");
		assertTrue(expected.exists());
		expected = createFile(targetDir, "b.attach2");
		assertTrue(expected.exists());

		master = createFile(targetDir, "b.txt");
		attach1 = createFile(targetDir, "b.attach1");
		attach2 = createFile(targetDir, "b.attach2");
		targetDir = createDir(testDir, "foo/bar");
		group.move(targetDir, "${file.name}", resolver, true, false, true);
		assertTrue(!master.exists());
		assertTrue(!attach1.exists());
		assertTrue(!attach2.exists());
		expected = createFile(targetDir, "b.txt");
		assertTrue(expected.exists());
		assertTrue(group.getMaster().equals(expected));
		expected = createFile(targetDir, "b.attach1");
		assertTrue(expected.exists());
		expected = createFile(targetDir, "b.attach2");
		assertTrue(expected.exists());
	}

	public void testCollisionAttach2() throws Exception {
		File master;
		File attach1;
		File attach2;
		File sourceDir;
		File targetDir;
		FileGroup group;
		File expected;
		IStringEvaluator resolver;
		//
		resolver = new ConstantResolver("diedel");
		sourceDir = createDir(testDir, "a/b");
		targetDir = createDir(testDir, "x/y");
		//
		master = fillFile(sourceDir, "b.txt", "test");
		attach1 = fillFile(sourceDir, "b.txt.attach1", "test");
		attach2 = fillFile(sourceDir, "b.attach2", "test");
		group = new FileGroup(master);
		group.getAttachments().add(new Attachment("a", attach1));
		group.getAttachments().add(new Attachment("b", attach2));
		group.move(targetDir, "${file.name}", resolver, true, false, true);
		master = fillFile(sourceDir, "b.txt", "test");
		attach1 = fillFile(sourceDir, "b.txt.attach1", "test");
		attach2 = fillFile(sourceDir, "b.attach2", "test");
		group = new FileGroup(master);
		group.getAttachments().add(new Attachment("a", attach1));
		group.getAttachments().add(new Attachment("b", attach2));
		try {
			group.move(targetDir, "${file.name}", resolver, true, false, true);
			fail("collision expected");
		} catch (Exception e) {
			group.move(targetDir, "${file.basename}.x.${file.extension}", resolver, true, false, true);
		}
		assertTrue(!master.exists());
		assertTrue(!attach1.exists());
		assertTrue(!attach2.exists());
		expected = createFile(targetDir, "b.x.txt");
		assertTrue(expected.exists());
		assertTrue(group.getMaster().equals(expected));
		expected = createFile(targetDir, "b.x.txt.attach1");
		assertTrue(expected.exists());
		expected = createFile(targetDir, "b.x.attach2");
		assertTrue(expected.exists());
	}

	public void testCollisionReplaceMaster() throws Exception {
		File master;
		File attach1;
		File attach2;
		File sourceDir;
		File targetDir;
		FileGroup group;
		File expected;
		IStringEvaluator resolver;
		//
		resolver = new ConstantResolver("diedel");
		sourceDir = createDir(testDir, "a/b");
		targetDir = createDir(testDir, "x/y");
		//
		master = fillFile(sourceDir, "b.txt", "test");
		attach1 = fillFile(sourceDir, "b.txt.attach1", "test");
		attach2 = fillFile(sourceDir, "duplicate/b.txt", "duplicate");
		group = new FileGroup(master);
		group.getAttachments().add(new Attachment("a", attach1));
		group.getAttachments().add(new Attachment("b", attach2));
		group.move(targetDir, "${file.name}", resolver, true, false, true);
		master = fillFile(sourceDir, "b.txt", "test");
		attach1 = fillFile(sourceDir, "b.txt.attach1", "test");
		attach2 = fillFile(sourceDir, "duplicate/b.txt", "duplicate");
		group = new FileGroup(master);
		group.getAttachments().add(new Attachment("a", attach1));
		group.getAttachments().add(new Attachment("b", attach2));
		try {
			group.move(targetDir, "${file.name}", resolver, true, false, true);
			fail("collision expected");
		} catch (Exception e) {
			group.move(targetDir, "${file.basename}.x.${file.extension}", resolver, true, false, true);
		}
		assertTrue(!master.exists());
		assertTrue(!attach1.exists());
		assertTrue(!attach2.exists());
		expected = createFile(targetDir, "b.x.txt");
		assertTrue(expected.exists());
		assertTrue("duplicate".equals(FileTools.getString(expected)));
		assertTrue(group.getMaster().equals(expected));
		expected = createFile(targetDir, "b.x.txt.attach1");
		assertTrue(expected.exists());
	}

	public void testCollisionSingle() throws Exception {
		File master;
		File sourceDir;
		File targetDir;
		FileGroup group;
		File expected;
		IStringEvaluator resolver;
		//
		resolver = new ConstantResolver("diedel");
		sourceDir = createDir(testDir, "a/b");
		targetDir = createDir(testDir, "x/y");
		master = fillFile(sourceDir, "b.txt", "test");
		group = new FileGroup(master);
		group.move(targetDir, "${file.name}", resolver, true, false, true);
		master = fillFile(sourceDir, "b.txt", "test");
		group = new FileGroup(master);
		try {
			group.move(targetDir, "${file.name}", resolver, true, false, true);
			fail("collision expected");
		} catch (Exception e) {
			group.move(targetDir, "${file.basename}.x.${file.extension}", resolver, true, false, true);
		}
		expected = createFile(targetDir, "b.x.txt");
		assertTrue(!master.exists());
		assertTrue(expected.exists());
		assertTrue(group.getMaster().equals(expected));
	}

	public void testDelete() throws Exception {
		File master;
		File attach1;
		File attach2;
		File sourceDir;
		File targetDir;
		FileGroup group;
		File expected;
		IStringEvaluator resolver;
		//
		resolver = new ConstantResolver("diedel");
		sourceDir = createDir(testDir, "a/b");
		targetDir = createDir(testDir, "x/y");
		//
		master = fillFile(sourceDir, "b.txt", "test");
		attach1 = fillFile(sourceDir, "b.attach1", "test");
		group = new FileGroup(master);
		group.getAttachments().add(new Attachment("a", attach1));
		group.delete();
		assertTrue(!master.exists());
		assertTrue(!attach1.exists());
		assertTrue(!group.getMaster().exists());
		// assertTrue(group.getAttachments().isEmpty());
		group.getAttachments().add(new Attachment("m", master));
		group.getAttachments().add(new Attachment("a", attach1));
		attach2 = fillFile(sourceDir, "b.attach2", "test");
		group.getAttachments().add(new Attachment("b", attach2));
		group.move(targetDir, "${file.name}", resolver, true, false, true);
		assertTrue(!attach2.exists());
		expected = createFile(targetDir, "b.txt");
		assertTrue(!expected.exists());
		expected = createFile(targetDir, "b.attach1");
		assertTrue(!expected.exists());
		expected = createFile(targetDir, "b.attach2");
		assertTrue(expected.exists());
	}

	public void testDeleteSubdirectories() throws Exception {
		File master;
		File attach1;
		File attach2;
		File sourceDir;
		File targetDir;
		FileGroup group;
		File expected;
		IStringEvaluator resolver;
		//
		resolver = new ConstantResolver("diedel");
		sourceDir = createDir(testDir, "a/b");
		targetDir = createDir(testDir, "x/y");
		//
		master = fillFile(sourceDir, "b.txt", "test");
		attach1 = fillFile(sourceDir, "b.attach1", "test");
		attach2 = fillFile(sourceDir, "b.attach2", "test");
		group = new FileGroup(tempDir, master);
		group.getAttachments().add(new Attachment("a", attach1));
		group.getAttachments().add(new Attachment("b", attach2));
		group.move(targetDir, "${file.name}", resolver, true, true, true);
		assertTrue(!master.exists());
		assertTrue(!attach1.exists());
		assertTrue(!attach2.exists());
		expected = createFile(testDir, "a/b/b.txt");
		assertTrue(!expected.exists());
		expected = createFile(testDir, "a/b");
		assertTrue(!expected.exists());
		expected = createFile(testDir, "a");
		assertTrue(!expected.exists());

		master = createFile(targetDir, "b.txt");
		attach1 = createFile(targetDir, "b.attach1");
		attach2 = createFile(targetDir, "b.attach2");
		targetDir = createDir(testDir, "foo/bar");
		group.move(targetDir, "${file.name}", resolver, true, true, true);
		assertTrue(!master.exists());
		assertTrue(!attach1.exists());
		assertTrue(!attach2.exists());
		expected = createFile(testDir, "x/y/b.txt");
		assertTrue(!expected.exists());
		expected = createFile(testDir, "x/y");
		assertTrue(!expected.exists());
		expected = createFile(testDir, "x");
		assertTrue(!expected.exists());

	}

	public void testReplaceMaster() throws Exception {
		File master;
		File attach1;
		File attach2;
		File sourceDir;
		File targetDir;
		FileGroup group;
		File expected;
		IStringEvaluator resolver;
		//
		resolver = new ConstantResolver("diedel");
		sourceDir = createDir(testDir, "a/b");
		targetDir = createDir(testDir, "x/y");
		//
		master = fillFile(sourceDir, "b.txt", "test");
		attach1 = fillFile(sourceDir, "b.txt.attach1", "test");
		attach2 = fillFile(sourceDir, "duplicate/b.txt", "duplicate");
		group = new FileGroup(master);
		group.getAttachments().add(new Attachment("a", attach1));
		group.getAttachments().add(new Attachment("b", attach2));
		group.move(targetDir, "${file.name}", resolver, true, false, true);
		assertTrue(!master.exists());
		assertTrue(!attach1.exists());
		assertTrue(!attach2.exists());
		expected = createFile(targetDir, "b.txt");
		assertTrue(expected.exists());
		assertTrue("duplicate".equals(FileTools.getString(expected)));
		assertTrue(group.getMaster().equals(expected));
		expected = createFile(targetDir, "b.txt.attach1");
		assertTrue(expected.exists());
	}

	public void testSingle() throws Exception {
		File master;
		File sourceDir;
		File targetDir;
		FileGroup group;
		File expected;
		IStringEvaluator resolver;
		//
		resolver = new ConstantResolver("diedel");
		sourceDir = createDir(testDir, "a/b");
		targetDir = createDir(testDir, "x/y");
		master = fillFile(sourceDir, "b.txt", "test");
		group = new FileGroup(master);
		group.move(targetDir, "${file.name}", resolver, true, false, true);
		expected = createFile(targetDir, "b.txt");
		assertTrue(!master.exists());
		assertTrue(expected.exists());
		assertTrue(group.getMaster().equals(expected));
	}
}
