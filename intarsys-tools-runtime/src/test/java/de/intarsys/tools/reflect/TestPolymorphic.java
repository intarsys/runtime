package de.intarsys.tools.reflect;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestPolymorphic {

	@Test
	public void testCastPojo() {
		Object pojo;
		//
		pojo = new Object();
		assertThrows(ClassCastException.class, () -> IPolymorphic.objectCast(pojo, List.class));
	}

	@Test
	public void testCastPolymorphic() {
		MyPolymorphic poly;
		List wrapped;
		List result;
		//
		wrapped = new ArrayList();
		poly = new MyPolymorphic(wrapped);
		result = IPolymorphic.objectCast(poly, List.class);
		assertThat(result, instanceOf(List.class));
	}

	@Test
	public void testCastPolymorphicDeep() {
		MyPolymorphic poly1;
		MyPolymorphic poly2;
		List wrapped;
		List result;
		//
		wrapped = new ArrayList();
		poly1 = new MyPolymorphic(wrapped);
		poly2 = new MyPolymorphic(poly1);
		result = IPolymorphic.objectCast(poly2, List.class);
		assertThat(result, instanceOf(List.class));
	}

	@Test
	public void testCastWrapper() {
		MyWrapper wrapper;
		List wrapped;
		List result;
		//
		wrapped = new ArrayList();
		wrapper = new MyWrapper(wrapped);
		result = IPolymorphic.objectCast(wrapper, List.class);
		assertThat(result, instanceOf(List.class));
	}

	@Test
	public void testImplementingPojo() {
		Object pojo;
		boolean implementing;
		//
		pojo = new Object();
		implementing = IPolymorphic.objectIsImplementing(pojo, List.class);
		assertThat(implementing, is(false));
	}

	@Test
	public void testImplementingPolymorphic() {
		MyPolymorphic poly;
		List wrapped;
		boolean implementing;
		//
		wrapped = new ArrayList();
		poly = new MyPolymorphic(wrapped);
		implementing = IPolymorphic.objectIsImplementing(poly, List.class);
		assertThat(implementing, is(true));
	}

	@Test
	public void testImplementingPolymorphicDeep() {
		MyPolymorphic poly1;
		MyPolymorphic poly2;
		List wrapped;
		boolean implementing;
		//
		wrapped = new ArrayList();
		poly1 = new MyPolymorphic(wrapped);
		poly2 = new MyPolymorphic(poly1);
		implementing = IPolymorphic.objectIsImplementing(poly2, List.class);
		assertThat(implementing, is(true));
	}

	@Test
	public void testImplementingWrapper() {
		MyWrapper wrapper;
		List wrapped;
		boolean implementing;
		//
		wrapped = new ArrayList();
		wrapper = new MyWrapper(wrapped);
		implementing = IPolymorphic.objectIsImplementing(wrapper, List.class);
		assertThat(implementing, is(true));
	}

}
