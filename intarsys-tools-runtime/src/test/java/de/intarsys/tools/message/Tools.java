package de.intarsys.tools.message;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Tools {

	public static void checkPropertyValue(Class clazz, String prop, String value) {
		String name = MessageTools.getBundleName(clazz, "test");
		IMessageBundle messageBundle = MessageTools.getMessageBundle(name, clazz.getClassLoader());
		String msg = messageBundle.getMessage(prop).getString();
		assertThat(msg, is(value));
	}

}
