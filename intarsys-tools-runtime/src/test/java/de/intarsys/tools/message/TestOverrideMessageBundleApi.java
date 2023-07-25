package de.intarsys.tools.message;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

import de.intarsys.tools.locator.ClassLoaderResourceLocator;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.message.test.MessageDummy;
import de.intarsys.tools.servicelocator.ServiceLocator;

/**
 * Testing standalone {@link OverrideMessageBundleFactory}
 * 
 */
public class TestOverrideMessageBundleApi {

	private Locale oldLocale;

	private OverrideMessageBundleFactory messageBundleFactory;

	@org.junit.Before
	public void setup() {
		oldLocale = Locale.getDefault();
		messageBundleFactory = new OverrideMessageBundleFactory();
		ServiceLocator.get().put(IMessageBundleFactory.class, messageBundleFactory);
	}

	@org.junit.After
	public void teardown() {
		if (oldLocale != null) {
			Locale.setDefault(oldLocale);
		}
		ServiceLocator.get().remove(IMessageBundleFactory.class);
	}

	@Test
	public void testBundleLoadKl() throws IOException {
		Locale locale = new Locale("kl");
		Locale.setDefault(locale);

		ILocator locator = new ClassLoaderResourceLocator(MessageDummy.class.getClassLoader(),
				"de/intarsys/tools/message/test-bundle.properties");
		String name = MessageTools.getBundleName(getClass(), "test");
		OverrideMessageBundle messageBundle = (OverrideMessageBundle) MessageTools.getMessageBundle(name, getClass()
				.getClassLoader());
		messageBundle.load(locator);

		Tools.checkPropertyValue(getClass(), "prop1", null);
		Tools.checkPropertyValue(getClass(), "prop2", null);
		Tools.checkPropertyValue(getClass(), "prop3", "bundle-value3");
		Tools.checkPropertyValue(getClass(), "prop4", "bundle-value4");
	}

	@Test
	public void testBundleRegisterKl() {

		Locale locale = new Locale("kl");
		Locale.setDefault(locale);

		String name = MessageTools.getBundleName(getClass(), "test");
		OverrideMessageBundle messageBundle = (OverrideMessageBundle) MessageTools.getMessageBundle(name, getClass()
				.getClassLoader());
		messageBundle.register("prop3", "register-value3");
		messageBundle.register("prop4", "register-value4");

		Tools.checkPropertyValue(getClass(), "prop1", null);
		Tools.checkPropertyValue(getClass(), "prop2", null);
		Tools.checkPropertyValue(getClass(), "prop3", "register-value3");
		Tools.checkPropertyValue(getClass(), "prop4", "register-value4");
	}

	@Test
	public void testFactoryLoadKl() throws IOException {

		Locale locale = new Locale("kl");
		Locale.setDefault(locale);

		ILocator locator = new ClassLoaderResourceLocator(MessageDummy.class.getClassLoader(),
				"de/intarsys/tools/message/test-factory.properties");
		messageBundleFactory.load(locator);

		Tools.checkPropertyValue(getClass(), "prop1", null);
		Tools.checkPropertyValue(getClass(), "prop2", null);
		Tools.checkPropertyValue(getClass(), "prop3", "factory-value3");
		Tools.checkPropertyValue(getClass(), "prop4", "factory-value4");
	}

}
