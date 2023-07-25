package de.intarsys.tools.collection;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import junit.framework.TestCase;

public class TestNestedIterator extends TestCase {

	public void test1() throws Exception {
		java.util.List l = new java.util.ArrayList();
		java.util.List li;
		java.util.List lii;

		//
		li = new java.util.ArrayList();
		li.add("1.1");
		li.add("1.2");
		li.add("1.3");
		l.add(li);
		//
		li = new java.util.ArrayList();
		li.add("2.1");
		li.add("2.2");
		l.add(li);
		//
		l.add(null);
		//
		li = new java.util.ArrayList();
		li.add("3.1");
		li.add("3.2");
		li.add("3.3");
		lii = new java.util.ArrayList();
		lii.add("3.3.1");
		lii.add("3.3.2");
		lii.add("3.3.3");
		li.add(lii);
		li.add("3.4");
		li.add("3.5");
		l.add(li);
		li = new java.util.ArrayList();
		li.add("4.1");
		li.add("4.2");
		l.add(li);

		de.intarsys.tools.collection.NestedIterator ni = new de.intarsys.tools.collection.NestedIterator(l);
		assertThat(ni.next(), is("1.1"));
		assertThat(ni.next(), is("1.2"));
		assertThat(ni.next(), is("1.3"));
		assertThat(ni.next(), is("2.1"));
		assertThat(ni.next(), is("2.2"));
		assertThat(ni.next(), nullValue());
		assertThat(ni.next(), is("3.1"));
		assertThat(ni.next(), is("3.2"));
		assertThat(ni.next(), is("3.3"));
		assertThat(ni.next(), is("3.3.1"));
		assertThat(ni.next(), is("3.3.2"));
		assertThat(ni.next(), is("3.3.3"));
		assertThat(ni.next(), is("3.4"));
		assertThat(ni.next(), is("3.5"));
		assertThat(ni.next(), is("4.1"));
		assertThat(ni.next(), is("4.2"));
	}

}
