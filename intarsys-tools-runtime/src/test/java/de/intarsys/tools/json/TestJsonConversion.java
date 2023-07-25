package de.intarsys.tools.json;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TestJsonConversion {

	@Test
	public void testObject() throws Exception {
		JsonObject json;
		Map map;
		//
		json = Json.createObject();
		map = json.toJava();
		Assert.assertTrue(map.isEmpty());
		//
		json = Json.createObject();
		json.put("bool", true);
		json.put("int", 5);
		json.put("String", "Lala");
		map = json.toJava();
		Assert.assertTrue(map.size() == 3);
		Assert.assertTrue(map.get("bool").equals(true));
		Assert.assertTrue(map.get("int").equals(5));
		Assert.assertTrue(map.get("String").equals("Lala"));
		//
		json = Json.createObject();
		json.put("foo",
				Json //
						.createObject() //
						.put("bar", "test1") //
		);
		json.put("gnu",
				Json //
						.createObject() //
						.put("gnat",
								Json //
										.createObject() //
										.put("gnarf", "test2")) //
		);
		map = json.toJava();
		Assert.assertTrue(map.size() == 2);
		Assert.assertTrue(((Map) map.get("foo")).get("bar").equals("test1"));
		Assert.assertTrue(((Map) ((Map) map.get("gnu")).get("gnat")).get("gnarf").equals("test2"));
	}

}
