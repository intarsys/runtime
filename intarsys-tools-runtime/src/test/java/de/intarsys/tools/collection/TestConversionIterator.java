package de.intarsys.tools.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class TestConversionIterator extends TestCase {

	public void testNumbers() throws Exception {
		List<Integer> input = new ArrayList<Integer>();
		input.add(2);
		input.add(3);
		input.add(5);
		input.add(7);
		input.add(11);
		input.add(13);
		Iterator<Integer> iterator = input.iterator();
		Iterator<Integer> addIterator = new ConversionIterator<Integer, Integer>(input.iterator()) {
			@Override
			protected Integer createTargetObject(Integer sourceObject) {
				return sourceObject + 1;
			}
		};
		while (addIterator.hasNext()) {
			assertTrue(iterator.hasNext());
			int added = addIterator.next();
			int original = iterator.next();
			assertEquals(added, original + 1);
		}
	}

}
