package de.intarsys.tools.collection;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestMapToolsBuilder {

	@Test
	public void testEmpty() {
		// @formatter:off
		Map value = MapTools.builder() 
				.build();
		assertThat(value.size(), is(0));
	}

	@Test
	public void testListAndLiteral() {
		// @formatter:off
		Map  value = MapTools.builder()
					.startList("list")
						.add("foo")
					.end(MapTools.Builder.class)
					.put("foo", "bar")
				.build();
		assertThat(value.size(), is(2));
		assertThat(value.get("list"), instanceOf(List.class));
		assertThat(((List)value.get("list")).get(0), is("foo"));
		assertThat(value.get("foo"), is("bar"));
	}

	@Test
	public void testLiteral() {
		// @formatter:off
		Map  value = MapTools.builder() 
					.put("foo", "bar")
				.build();
		assertThat(value.size(), is(1));
		assertThat(value.get("foo"), is("bar"));
	}

	@Test
	public void testMapAndLiteral() {
		// @formatter:off
		Map  value = MapTools.builder()
					.startMap("map")
						.put("gnu", "gnat")
					.end(MapTools.Builder.class)
					.put("foo", "bar")
				.build();
		assertThat(value.size(), is(2));
		assertThat(value.get("map"), instanceOf(Map.class));
		assertThat(((Map)value.get("map")).get("gnu"), is("gnat"));
		assertThat(value.get("foo"), is("bar"));
	}

}
