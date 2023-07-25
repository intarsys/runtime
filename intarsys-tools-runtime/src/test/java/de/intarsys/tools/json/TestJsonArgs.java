package de.intarsys.tools.json;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import de.intarsys.tools.functor.ArgumentDeclarator;
import de.intarsys.tools.functor.DeclarationBlock;
import de.intarsys.tools.functor.common.DeclarationIO;
import de.intarsys.tools.infoset.ElementFactory;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.stream.StreamTools;

public class TestJsonArgs {

	protected IElement parseElement(String resource) throws IOException {
		IElement element = null;
		InputStream is = null;
		try {
			is = this.getClass().getResourceAsStream(resource);
			element = ElementFactory.get().parse(is).getRootElement();
		} finally {
			StreamTools.close(is);
		}
		return element;
	}

	@Test
	public void testArgumentDeclaration() throws Exception {
		IElement eRoot = parseElement("declarations.xml");
		DeclarationIO io = new DeclarationIO();
		DeclarationBlock declarationBlock = new DeclarationBlock(this);
		io.deserializeDeclarationBlock(declarationBlock, eRoot);
		JsonObject json = Json.createObject();
		json.put("someDefinedString", "defined in code");
		JsonObjectArgs args = new JsonObjectArgs(json);
		new ArgumentDeclarator().apply(declarationBlock, args);
		Assert.assertTrue(json.has("someString"));
		Assert.assertEquals("someStringValue", json.get("someString"));
		Assert.assertFalse(json.has("someEmptyDeclaratrion"));
		Assert.assertEquals(null, json.get("someEmptyDeclaratrion"));
		Assert.assertTrue(json.has("someArgs"));
		Assert.assertTrue(json.get("someArgs") instanceof JsonObject);
		JsonObject someArgs = (JsonObject) json.get("someArgs");
		Assert.assertTrue(someArgs.has("someStringDescendant1"));
		Assert.assertEquals("lala", someArgs.get("someStringDescendant1"));
		Assert.assertTrue(someArgs.has("someBooleanDescendant1"));
		Assert.assertEquals(true, someArgs.get("someBooleanDescendant1"));
		Assert.assertTrue(json.has("someDefinedString"));
		Assert.assertEquals("defined in code", json.get("someDefinedString"));
	}

	@Test
	public void testArrayArgs() throws Exception {
		JsonArray json = Json.createArray();
		json.add(true);
		json.add(5);
		json.add("Lala");
		JsonArrayArgs args = new JsonArrayArgs(json);
		Assert.assertEquals(3, args.size());
		Assert.assertTrue(args.isDefined(0));
		Assert.assertTrue(args.isDefined(1));
		Assert.assertTrue(args.isDefined(2));
		Assert.assertFalse(args.isDefined(3));
		Assert.assertEquals(true, args.get(0));
		Assert.assertEquals(5, args.get(1));
		Assert.assertEquals("Lala", args.get(2));
		args.add("Hatmaker");
		Assert.assertEquals(4, args.size());
		Assert.assertEquals(4, json.size());
		Assert.assertEquals("Hatmaker", args.get(3));
		Assert.assertEquals("Hatmaker", json.get(3));
		args.undefine(1);
		Assert.assertEquals(3, json.size());
		Assert.assertEquals("Lala", args.get(1));
	}

	@Test
	public void testObjectArgs() throws Exception {
		JsonObject json = Json.createObject();
		json.put("bool", true);
		json.put("int", 5);
		json.put("String", "Lala");
		JsonObjectArgs args = new JsonObjectArgs(json);
		Assert.assertEquals(3, args.size());
		Assert.assertTrue(args.isDefined("bool"));
		Assert.assertTrue(args.isDefined("int"));
		Assert.assertTrue(args.isDefined("String"));
		Assert.assertFalse(args.isDefined("djahfushf"));
		Assert.assertEquals(true, args.get("bool"));
		Assert.assertEquals(5, args.get("int"));
		Assert.assertEquals("Lala", args.get("String"));
		args.put("StringFromArgs", "Hatmaker");
		Assert.assertEquals(4, args.size());
		Assert.assertEquals(4, json.size());
		Assert.assertEquals("Hatmaker", args.get("StringFromArgs"));
		Assert.assertEquals("Hatmaker", json.get("StringFromArgs"));
		args.undefine("int");
		Assert.assertEquals(3, json.size());
	}

}
