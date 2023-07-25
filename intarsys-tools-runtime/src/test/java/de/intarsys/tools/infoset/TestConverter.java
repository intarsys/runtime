package de.intarsys.tools.infoset;

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;

public class TestConverter {

	protected Object get(Map map, Object... path) {
		Object current = map;
		Object result = map;
		for (Object segment : path) {
			current = result;
			if (segment instanceof String) {
				result = ((Map) current).get(segment);
			} else {
				result = ((List) current).get(((Number) segment).intValue());
			}
		}
		return result;
	}

	protected List getList(Map map, Object... path) {
		return (List) get(map, path);
	}

	@Test
	public void testArray() throws ElementSerializationException {
		IElement element;
		Map<String, Object> result;
		List<Object> children;
		//
		element = ElementFactory.get().createElement("root");
		children = new ArrayList<>();
		ElementTools.serializeArray(element, "children", "child", children);
		result = JavaConverter.convert(element);
		assertThat(result.get("children"), IsInstanceOf.instanceOf(List.class));
		assertThat(getList(result, "children").size(), IsEqual.equalTo(0));
		//
		element = ElementFactory.get().createElement("root");
		children = new ArrayList<>();
		children.add(new Child("x", 1));
		ElementTools.serializeArray(element, "children", "child", children);
		result = JavaConverter.convert(element);
		assertThat(result.get("children"), IsInstanceOf.instanceOf(List.class));
		assertThat(getList(result, "children").size(), IsEqual.equalTo(1));
		assertThat(get(result, "children", 0), IsInstanceOf.instanceOf(Map.class));
		assertThat(get(result, "children", 0, "a"), IsEqual.equalTo("x"));
		assertThat(get(result, "children", 0, "b"), IsEqual.equalTo("1"));
		//
		element = ElementFactory.get().createElement("root");
		children = new ArrayList<>();
		children.add(new Child("x", 1));
		children.add(new Child("y", 2));
		ElementTools.serializeArray(element, "children", "child", children);
		result = JavaConverter.convert(element);
		assertThat(result.get("children"), IsInstanceOf.instanceOf(List.class));
		assertThat(getList(result, "children").size(), IsEqual.equalTo(2));
		assertThat(get(result, "children", 0), IsInstanceOf.instanceOf(Map.class));
		assertThat(get(result, "children", 0, "a"), IsEqual.equalTo("x"));
		assertThat(get(result, "children", 0, "b"), IsEqual.equalTo("1"));
	}

	@Test
	public void testNestedArray() throws ElementSerializationException {
		IElement element;
		IElement nested;
		Map<String, Object> result;
		List<Object> children;
		//
		element = ElementFactory.get().createElement("root");
		nested = element.newElement("nested");
		children = new ArrayList<>();
		ElementTools.serializeArray(nested, "children", "child", children);
		result = JavaConverter.convert(element);
		assertThat(get(result, "nested", "children"), IsInstanceOf.instanceOf(List.class));
		assertThat(getList(result, "nested", "children").size(), IsEqual.equalTo(0));
		//
		element = ElementFactory.get().createElement("root");
		nested = element.newElement("nested");
		children = new ArrayList<>();
		children.add(new Child("x", 1));
		ElementTools.serializeArray(nested, "children", "child", children);
		result = JavaConverter.convert(element);
		assertThat(get(result, "nested", "children"), IsInstanceOf.instanceOf(List.class));
		assertThat(getList(result, "nested", "children").size(), IsEqual.equalTo(1));
		assertThat(get(result, "nested", "children", 0), IsInstanceOf.instanceOf(Map.class));
		assertThat(get(result, "nested", "children", 0, "a"), IsEqual.equalTo("x"));
		assertThat(get(result, "nested", "children", 0, "b"), IsEqual.equalTo("1"));
		//
		element = ElementFactory.get().createElement("root");
		nested = element.newElement("nested");
		children = new ArrayList<>();
		children.add(new Child("x", 1));
		children.add(new Child("y", 2));
		ElementTools.serializeArray(nested, "children", "child", children);
		result = JavaConverter.convert(element);
		assertThat(get(result, "nested", "children"), IsInstanceOf.instanceOf(List.class));
		assertThat(getList(result, "nested", "children").size(), IsEqual.equalTo(2));
		assertThat(get(result, "nested", "children", 0), IsInstanceOf.instanceOf(Map.class));
		assertThat(get(result, "nested", "children", 0, "a"), IsEqual.equalTo("x"));
		assertThat(get(result, "nested", "children", 0, "b"), IsEqual.equalTo("1"));
	}

	@Test
	public void testSimple() throws ElementSerializationException {
		IElement element;
		Map<String, Object> result;
		//
		element = ElementFactory.get().createElement("root");
		element.setAttributeValue("gnu", "gnat");
		ElementTools.serializeObject(element, "foo", "bar");
		result = JavaConverter.convert(element);
		assertThat(result.get("gnu"), IsEqual.equalTo("gnat"));
		assertThat(result.get("foo"), IsInstanceOf.instanceOf(Map.class));
		assertThat(get(result, "foo", "value"), IsEqual.equalTo("bar"));
	}

}
