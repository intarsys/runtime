package de.intarsys.tools.locator;

import org.junit.Assert;
import org.junit.Test;

public class TestByteArrayLocator {

	@Test
	public void testURL() throws Exception {
		ByteArrayLocator locator;
		//
		locator = new ByteArrayLocator(new byte[0], "foo/bar.dat");
		Assert.assertEquals("bar.dat", locator.getName());
		Assert.assertEquals("foo/bar.dat", locator.getPath());
		Assert.assertEquals("bytes:[@]", locator.toURI().toString());

	}
}
