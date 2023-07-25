package de.intarsys.tools.collection;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestListToolsBuilder {

	@Test
	public void testEmpty() {
		// @formatter:off
		List list = ListTools.builder() 
				.build();
		assertThat(list.size(), is(0));
	}

	@Test
	public void testListAndLiteral() {
		// @formatter:off
		List list = ListTools.builder()
					.startList()
						.add("foo")
					.end(ListTools.Builder.class)
					.add("string")
				.build();
		assertThat(list.size(), is(2));
		assertThat(list.get(0), instanceOf(List.class));
		assertThat(((List)list.get(0)).get(0), is("foo"));
		assertThat(list.get(1), is("string"));
	}

	@Test
	public void testLiteral() {
		// @formatter:off
		List list = ListTools.builder() 
					.add("string")
				.build();
		assertThat(list.size(), is(1));
		assertThat(list.get(0), is("string"));
	}

	@Test
	public void testMapAndLiteral() {
		// @formatter:off
		List list = ListTools.builder()
					.startMap()
						.put("foo", "bar")
					.end(ListTools.Builder.class)
					.add("string")
				.build();
		assertThat(list.size(), is(2));
		assertThat(list.get(0), instanceOf(Map.class));
		assertThat(((Map)list.get(0)).get("foo"), is("bar"));
		assertThat(list.get(1), is("string"));
	}

}
