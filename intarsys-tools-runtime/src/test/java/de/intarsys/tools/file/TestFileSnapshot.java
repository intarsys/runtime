package de.intarsys.tools.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.intarsys.tools.stream.StreamTools;
import junit.framework.TestCase;

public class TestFileSnapshot extends TestCase {

	protected static void append(File file, int value) throws FileNotFoundException, IOException {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(file, true);
			os.write(value);
		} finally {
			StreamTools.close(os);
		}
	}

	public void testDirectory() throws IOException {
		File file;
		File dir;
		FileSnapshot snapshot;
		//
		dir = TempTools.createTempDir("xyz", "test");
		snapshot = new FileSnapshot(dir);
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		//
		dir = TempTools.createTempDir("xyz", "test");
		file = File.createTempFile("abc", ".txt", dir);
		file.deleteOnExit();
		snapshot = new FileSnapshot(dir);
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		//
		dir = TempTools.createTempDir("xyz", "test");
		file = File.createTempFile("abc", ".txt", dir);
		file.deleteOnExit();
		snapshot = new FileSnapshot(dir);
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		append(file, 11);
		assertTrue(snapshot.isAvailable());
		assertTrue(snapshot.isChanged());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		//
		dir = TempTools.createTempDir("xyz", "test");
		file = File.createTempFile("abc", ".txt", dir);
		file.deleteOnExit();
		snapshot = new FileSnapshot(dir);
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		file = File.createTempFile("abc", ".txt", dir);
		file.deleteOnExit();
		assertTrue(snapshot.isAvailable());
		assertTrue(snapshot.isChanged());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		append(file, 11);
		assertTrue(snapshot.isAvailable());
		assertTrue(snapshot.isChanged());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		//
		dir = TempTools.createTempDir("xyz", "test");
		file = File.createTempFile("abc", ".txt", dir);
		file.deleteOnExit();
		file = File.createTempFile("abc", ".txt", dir);
		file.deleteOnExit();
		file = File.createTempFile("abc", ".txt", dir);
		file.deleteOnExit();
		snapshot = new FileSnapshot(dir);
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		file.delete();
		assertTrue(snapshot.isAvailable());
		assertTrue(snapshot.isChanged());
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		//
		dir = TempTools.createTempDir("xyz", "test");
		file = File.createTempFile("abc", ".txt", dir);
		file.deleteOnExit();
		file = File.createTempFile("abc", ".txt", dir);
		file.deleteOnExit();
		file = File.createTempFile("abc", ".txt", dir);
		file.deleteOnExit();
		snapshot = new FileSnapshot(dir);
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		file.delete();
		file = File.createTempFile("abc", ".txt", dir);
		file.deleteOnExit();
		assertTrue(snapshot.isAvailable());
		assertTrue(snapshot.isChanged());
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
	}

	public void testDirectoryNested() throws IOException {
		File file;
		File dir;
		File sub1;
		File sub2;
		FileSnapshot snapshot;
		//
		dir = TempTools.createTempDir("xyz", "test");
		snapshot = new FileSnapshot(dir);
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		//
		dir = TempTools.createTempDir("xyz", "test");
		snapshot = new FileSnapshot(dir);
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		sub1 = new File(dir, "sub1");
		sub1.mkdir();
		assertTrue(snapshot.isChanged());
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		file = File.createTempFile("abc", ".txt", sub1);
		file.deleteOnExit();
		assertTrue(snapshot.isChanged());
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		append(file, 11);
		assertTrue(snapshot.isChanged());
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		sub2 = new File(dir, "sub2");
		sub2.mkdir();
		assertTrue(snapshot.isChanged());
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		file = File.createTempFile("abc", ".txt", sub2);
		file.deleteOnExit();
		assertTrue(snapshot.isChanged());
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		append(file, 11);
		assertTrue(snapshot.isChanged());
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		file.delete();
		assertTrue(snapshot.isChanged());
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
	}

	public void testFile() throws IOException {
		File file;
		FileSnapshot snapshot;
		//
		file = File.createTempFile("abc", ".txt");
		file.deleteOnExit();
		snapshot = new FileSnapshot(file);
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isChanged());
		assertTrue(!snapshot.isLost());
		//
		file = File.createTempFile("abc", ".txt");
		file.deleteOnExit();
		snapshot = new FileSnapshot(file);
		append(file, 11);
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isLost());
		assertTrue(snapshot.isChanged());
		assertTrue(!snapshot.isChanged());
		append(file, 11);
		assertTrue(snapshot.isChanged());
		assertTrue(!snapshot.isChanged());
		//
		file = File.createTempFile("abc", ".txt");
		file.deleteOnExit();
		snapshot = new FileSnapshot(file);
		append(file, 11);
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isLost());
		assertTrue(snapshot.isChanged());
		assertTrue(!snapshot.isChanged());
		file.delete();
		assertTrue(snapshot.isChanged());
		assertTrue(snapshot.isLost());
		//
		file = File.createTempFile("abc", ".txt");
		file.deleteOnExit();
		snapshot = new FileSnapshot(file);
		file.setLastModified(123);
		assertTrue(snapshot.isAvailable());
		assertTrue(!snapshot.isLost());
		assertTrue(snapshot.isChanged());
		assertTrue(!snapshot.isChanged());
	}
}
