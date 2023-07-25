package de.intarsys.tools.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class TestFilterIterator extends TestCase {

	public void testNumbers() throws Exception {
		List<Integer> input = new ArrayList<Integer>();
		input.add(1);
		input.add(2);
		input.add(3);
		input.add(4);
		input.add(5);
		input.add(6);
		input.add(7);
		input.add(8);
		input.add(9);
		Iterator<Integer> div3Iterator = new FilterIterator<Integer>(input.iterator()) {
			@Override
			protected boolean accept(Integer object) {
				return object % 3 == 0;
			}
		};
		int count = 0;
		while (div3Iterator.hasNext()) {
			int i = div3Iterator.next();
			assertEquals(i % 3, 0);
			count++;
		}
		assertEquals(count, 3);
	}

}
