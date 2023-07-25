package de.intarsys.tools.json;

import java.io.IOException;

import junit.framework.TestCase;

public class TestJsonReader extends TestCase {

	public void testReaderObject() throws IOException {
		String source;
		JsonReader reader;
		JsonObject jo;
		//
		source = "";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo == null);
		//
		source = "{}";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo.size() == 0);
		//
		source = "{ 'a': 'b' }";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get("a").equals("b"));
		//
		source = "{ 'a': \"b\" }";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get("a").equals("b"));
		//
		source = "{ 'a': true }";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get("a").equals(true));
		//
		source = "{ 'a': false }";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get("a").equals(false));
		//
		source = "{ 'a': null }";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get("a") == null);
		//
		source = "{ 'a': { } }";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get("a") instanceof JsonObject);
		//
		source = "{ 'a': [ ] }";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get("a") instanceof JsonArray);
		//
		source = "{ 'a': 1 }";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get("a").equals(1));
		//
		source = "{ 'a': 1.2}";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get("a").equals(1.2));
		//
		source = "{ 'a': .3 }";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get("a").equals(0.3));
		//
		source = "{ 'a': 'b', 'x': 'y' }";
		reader = new JsonReader(source);
		jo = (JsonObject) reader.readValue();
		assertTrue(jo.size() == 2);
		assertTrue(jo.get("a").equals("b"));
		assertTrue(jo.get("x").equals("y"));
	}

	public void testReaderArray() throws IOException {
		String source;
		JsonReader reader;
		JsonArray jo;
		//
		source = "";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo == null);
		//
		source = "[]";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo.size() == 0);
		//
		source = "[ ]";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo.size() == 0);
		//
		source = "[ 'a' ]";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get(0).equals("a"));
		//
		source = "[ true ]";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get(0).equals(true));
		//
		source = "[ false ]";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get(0).equals(false));
		//
		source = "[ null ]";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get(0) == null);
		//
		source = "[ 1 ]";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get(0).equals(1));
		//
		source = "[ 1.2 ]";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get(0).equals(1.2));
		//
		source = "[ .2 ]";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get(0).equals(0.2));
		//
		source = "[ [ ]  ]";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get(0) instanceof JsonArray);
		//
		source = "[ { } ]";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo.size() == 1);
		assertTrue(jo.get(0) instanceof JsonObject);
		//
		source = "[ { }, [ ] ]";
		reader = new JsonReader(source);
		jo = (JsonArray) reader.readValue();
		assertTrue(jo.size() == 2);
		assertTrue(jo.get(0) instanceof JsonObject);
		assertTrue(jo.get(1) instanceof JsonArray);
	}
}
