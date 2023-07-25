package de.intarsys.tools.file;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import de.intarsys.tools.file.FileTools.Lock;

public class TestTempTools {

	public TestTempTools() {
	}

	@Test
	public void testAlreadyLocked() {
		File root, temp1;
		Lock lock;
		//
		root = new File(".");
		TempTools.cleanUp(root);
		temp1 = TempTools.createTempDirVM(root);
		lock = FileTools.lockBasic(temp1);
		assertThat(lock, is(nullValue()));
	}

	@Test
	public void testAlreadyLockedInAnotherThread() throws Exception {
		File root, temp1;
		CompletableFuture f;
		//
		root = new File(".");
		TempTools.cleanUp(root);
		temp1 = TempTools.createTempDirVM(root);
		f = new CompletableFuture();
		Thread t = new Thread(() -> {
			Lock lock = FileTools.lockBasic(temp1);
			f.complete(lock);
		});
		t.start();
		assertThat(f.get(), is(nullValue()));
	}

	@Test
	public void testCreateTwice() {
		File root, temp1, temp2;
		//
		root = new File(".");
		TempTools.cleanUp(root);
		temp1 = TempTools.createTempDirVM(root);
		temp2 = TempTools.createTempDirVM(root);
		assertTrue("two different directories", !temp1.equals(temp2));
	}

	@Test
	public void testLockedNotCleaned() {
		File root, temp1;
		Lock lock;
		//
		root = new File(".");
		TempTools.cleanUp(root);
		temp1 = TempTools.createTempDirVM(root);
		TempTools.cleanUp(root);
		assertThat(temp1.exists(), is(true));
	}

}
