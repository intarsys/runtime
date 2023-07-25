package de.intarsys.tools.message;

import java.util.Locale;

import org.junit.Test;

import de.intarsys.tools.servicelocator.ServiceLocator;

public class TestBasicMessageBundlePlain {

	private Locale oldLocale;

	@Test
	public void checkDe() throws Exception {
		Locale locale = new Locale("de");
		Locale.setDefault(locale);
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2_de");
	}

	@Test
	public void checkEn() throws Exception {
		Locale locale = new Locale("en");
		Locale.setDefault(locale);
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2_en");
	}

	@Test
	public void checkKl() throws Exception {
		Locale locale = new Locale("kl");
		Locale.setDefault(locale);
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2");
	}

	@org.junit.Before
	public void setup() {
		oldLocale = Locale.getDefault();
		BasicMessageBundleFactory messageBundleFactory = new BasicMessageBundleFactory();
		ServiceLocator.get().put(IMessageBundleFactory.class, messageBundleFactory);
	}

	@org.junit.After
	public void teardown() {
		if (oldLocale != null) {
			Locale.setDefault(oldLocale);
		}
		ServiceLocator.get().remove(IMessageBundleFactory.class);
	}

}
