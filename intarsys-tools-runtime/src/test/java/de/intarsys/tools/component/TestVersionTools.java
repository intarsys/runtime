package de.intarsys.tools.component;

import junit.framework.TestCase;

@SuppressWarnings({ "EqualsAvoidNull", "MultipleStringLiterals" })
public class TestVersionTools extends TestCase {

	public void testGetMajor() {
		String version;
		String result;
		//
		version = null;
		result = VersionTools.getMajor(version);
		assertTrue(result.equals(""));
		//
		version = "";
		result = VersionTools.getMajor(version);
		assertTrue(result.equals(""));
		//
		version = " ";
		result = VersionTools.getMajor(version);
		assertTrue(result.equals(""));
		//
		version = "1";
		result = VersionTools.getMajor(version);
		assertTrue(result.equals("1"));
		//
		version = " 1";
		result = VersionTools.getMajor(version);
		assertTrue(result.equals("1"));
		//
		version = "1 ";
		result = VersionTools.getMajor(version);
		assertTrue(result.equals("1"));
		//
		version = " 1 .4.5";
		result = VersionTools.getMajor(version);
		assertTrue(result.equals("1"));
	}

	public void testGetMicro() {
		String version;
		String result;
		//
		version = null;
		result = VersionTools.getMicro(version);
		assertTrue(result.equals(""));
		//
		version = "";
		result = VersionTools.getMicro(version);
		assertTrue(result.equals(""));
		//
		version = " ";
		result = VersionTools.getMicro(version);
		assertTrue(result.equals(""));
		//
		version = "1";
		result = VersionTools.getMicro(version);
		assertTrue(result.equals(""));
		//
		version = " 1";
		result = VersionTools.getMicro(version);
		assertTrue(result.equals(""));
		//
		version = "1 ";
		result = VersionTools.getMicro(version);
		assertTrue(result.equals(""));
		//
		version = " 1 . 2 ";
		result = VersionTools.getMicro(version);
		assertTrue(result.equals(""));
		//
		version = " 1 . .";
		result = VersionTools.getMicro(version);
		assertTrue(result.equals(""));
		//
		version = " 1 . 2 .";
		result = VersionTools.getMicro(version);
		assertTrue(result.equals(""));
		//
		version = " 1 . 2 . 3 ";
		result = VersionTools.getMicro(version);
		assertTrue(result.equals("3"));
	}

	public void testGetMinor() {
		String version;
		String result;
		//
		version = null;
		result = VersionTools.getMinor(version);
		assertTrue(result.equals(""));
		//
		version = "";
		result = VersionTools.getMinor(version);
		assertTrue(result.equals(""));
		//
		version = " ";
		result = VersionTools.getMinor(version);
		assertTrue(result.equals(""));
		//
		version = "1";
		result = VersionTools.getMinor(version);
		assertTrue(result.equals(""));
		//
		version = " 1";
		result = VersionTools.getMinor(version);
		assertTrue(result.equals(""));
		//
		version = "1 ";
		result = VersionTools.getMinor(version);
		assertTrue(result.equals(""));
		//
		version = " 1 . 2 ";
		result = VersionTools.getMinor(version);
		assertTrue(result.equals("2"));
		//
		version = " 1 . .";
		result = VersionTools.getMinor(version);
		assertTrue(result.equals(""));
		//
		version = " 1 . 2 .";
		result = VersionTools.getMinor(version);
		assertTrue(result.equals("2"));
		//
		version = " 1 . 2 . 3 ";
		result = VersionTools.getMinor(version);
		assertTrue(result.equals("2"));
	}
}
