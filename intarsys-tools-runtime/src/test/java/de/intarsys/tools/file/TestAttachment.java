package de.intarsys.tools.file;

import junit.framework.TestCase;

@SuppressWarnings({ "EqualsAvoidNull", "MultipleStringLiterals" })
public class TestAttachment extends TestCase {

	public void testMoveAtachment() {
		String a;
		String b;
		String c;
		String result;
		//
		a = "X.txt";
		b = "123.X.txt";
		c = "x.bak";
		result = FileAttachmentTools.createAttachmentName(a, b, c);
		assertTrue(result.equals("123.X.bak"));
		//
		a = "X.txt";
		b = "123.X.txt";
		c = "x.txt.bak";
		result = FileAttachmentTools.createAttachmentName(a, b, c);
		assertTrue(result.equals("123.X.txt.bak"));
		//
		a = "X.txt";
		b = "123.X.txt";
		c = "x.txt.diedel.bak";
		result = FileAttachmentTools.createAttachmentName(a, b, c);
		assertTrue(result.equals("123.X.txt.diedel.bak"));
		//
		a = "X.txt";
		b = "123.X.txt";
		c = "x.diedel.bak";
		result = FileAttachmentTools.createAttachmentName(a, b, c);
		assertTrue(result.equals("123.X.diedel.bak"));
		//
		a = "X";
		b = "123.X";
		c = "x.diedel.bak";
		result = FileAttachmentTools.createAttachmentName(a, b, c);
		assertTrue(result.equals("123.X.diedel.bak"));
		//
		a = "X.txt";
		b = "X.FirleFanz.txt";
		c = "x.bak";
		result = FileAttachmentTools.createAttachmentName(a, b, c);
		assertTrue(result.equals("X.FirleFanz.bak"));
		//
		a = "X.txt";
		b = "X.FirleFanz.txt";
		c = "x.txt.bak";
		result = FileAttachmentTools.createAttachmentName(a, b, c);
		assertTrue(result.equals("X.FirleFanz.txt.bak"));
		//
		a = "X.txt";
		b = "X.FirleFanz.txt";
		c = "x.diedel.bak";
		result = FileAttachmentTools.createAttachmentName(a, b, c);
		assertTrue(result.equals("X.FirleFanz.diedel.bak"));
	}
}
