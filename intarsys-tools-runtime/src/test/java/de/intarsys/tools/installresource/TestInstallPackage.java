package de.intarsys.tools.installresource;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class TestInstallPackage extends TestCase {

	public void testSimple() throws IOException {
		InstallZip install = new InstallZip("foo/bar", "data.zip", true);
		File file = install.load();
		assertThat(file, notNullValue());
	}
}
