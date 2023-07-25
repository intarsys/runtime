package de.intarsys.tools.tag;

import java.io.IOException;

import junit.framework.TestCase;

public class TestTypedTag extends TestCase {

	@SuppressWarnings("nls")
	public void testMultiTypedTag() throws IOException {
		String tagString = "Integer#First=123;Second=value#123;Third;Integer#First=123;Type#Name";
		TypedTag[] tags = TypedTag.parse(tagString);

		// count
		assertEquals(5, tags.length);

		// split
		assertEquals("Integer", tags[0].getType());
		assertEquals("First", tags[0].getKey());
		assertEquals("123", tags[0].getValue());
		// split
		assertEquals("", tags[1].getType());
		assertEquals("Second", tags[1].getKey());
		assertEquals("value#123", tags[1].getValue());
		// split
		assertEquals("", tags[2].getType());
		assertEquals("Third", tags[2].getKey());
		assertEquals("", tags[2].getValue());
		// split
		assertEquals("Integer", tags[3].getType());
		assertEquals("First", tags[3].getKey());
		assertEquals("123", tags[3].getValue());
		// split
		assertEquals("Type", tags[4].getType());
		assertEquals("Name", tags[4].getKey());
		assertEquals("", tags[4].getValue());

		// concat
		assertEquals("Integer#First=123", tags[0].toString());
		assertEquals("Second=value#123", tags[1].toString());
		assertEquals("Third", tags[2].toString());
		assertEquals("Integer#First=123", tags[3].toString());
		assertEquals("Type#Name", tags[4].toString());

		// equals: object of 0 and 3 must be equal
		assertEquals(tags[0], tags[3]);
		// equals: hash code of 0 and 3 must be equal
		assertEquals(tags[0].hashCode(), tags[3].hashCode());

	}

	@SuppressWarnings("nls")
	public void testSingleTypedTag() throws IOException {
		String tagString = "First=123";
		TypedTag[] tags = TypedTag.parse(tagString);

		// count
		assertEquals(1, tags.length);
		// split
		assertEquals("", tags[0].getType());
		assertEquals("First", tags[0].getKey());
		assertEquals("123", tags[0].getValue());
		// concat
		assertEquals(tagString, tags[0].toString());
	}

}
