package de.intarsys.tools.message;

import org.junit.Assert;
import org.junit.Test;

public class TestMessage {

	@Test
	public void testFormattedObject() {
		IMessage message;
		Object object0;
		Object arg0;
		//
		message = MessageTools.createMessage("key", "value");
		Assert.assertTrue(message.getArgumentSize() == 0);
		Assert.assertTrue(message.getArgumentAt(-1) == null);
		Assert.assertTrue(message.getArgumentAt(0) == null);
		Assert.assertTrue(message.getArgumentAt(1) == null);
		//
		arg0 = new Object();
		message = MessageTools.createMessage("key", "value", arg0);
		Assert.assertTrue(message.getArgumentSize() == 1);
		Assert.assertTrue(message.getArgumentAt(-1) == null);
		Assert.assertTrue(message.getArgumentAt(0) == arg0);
		Assert.assertTrue(message.getArgumentAt(1) == null);
		//
		object0 = new Object();
		arg0 = new FormattedObject(object0, "label0");
		message = MessageTools.createMessage("key", "hello, {0}", arg0);
		Assert.assertTrue(message.getArgumentSize() == 1);
		Assert.assertTrue(message.getArgumentAt(-1) == null);
		Assert.assertTrue(message.getArgumentAt(0) == object0);
		Assert.assertTrue(message.getString().equals("hello, label0"));
	}
}
