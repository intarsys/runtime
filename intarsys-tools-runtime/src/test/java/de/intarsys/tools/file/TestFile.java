package de.intarsys.tools.file;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import org.junit.Test;

public class TestFile {

	@Test
	public void testEquals() throws IOException {
		File a;
		File b;
		//
		a = new File("file.txt");
		b = new File("./file.txt");
		//
		assertThat(b, not(a));
		try {
			assertTrue(Files.isSameFile(a.toPath(), b.toPath()));
			throw new IllegalStateException();
		} catch (NoSuchFileException e) {
			// expected
		}
		assertThat(a.getCanonicalFile(), is(b.getCanonicalFile()));
		assertThat(a.getAbsoluteFile(), not(b.getAbsoluteFile()));

	}
}
