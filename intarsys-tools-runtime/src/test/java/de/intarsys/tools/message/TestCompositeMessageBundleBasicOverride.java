package de.intarsys.tools.message;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

import de.intarsys.tools.locator.ClassLoaderResourceLocator;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.message.test.MessageDummy;
import de.intarsys.tools.nls.INlsContext;
import de.intarsys.tools.nls.StandardNlsContext;
import de.intarsys.tools.servicelocator.ServiceLocator;

public class TestCompositeMessageBundleBasicOverride {

	private Locale oldLocale;

	private OverrideMessageBundleFactory overridMessageBundleFactory;

	@org.junit.Before
	public void setup() {
		oldLocale = Locale.getDefault();
		CompositeMessageBundleFactory mainFactory = new CompositeMessageBundleFactory();
		overridMessageBundleFactory = new OverrideMessageBundleFactory();
		mainFactory.addFactory(overridMessageBundleFactory);
		mainFactory.addFactory(new BasicMessageBundleFactory());
		ServiceLocator.get().put(IMessageBundleFactory.class, mainFactory);
		ServiceLocator.get().put(INlsContext.class, new StandardNlsContext());
	}

	@org.junit.After
	public void teardown() {
		if (oldLocale != null) {
			Locale.setDefault(oldLocale);
		}
		ServiceLocator.get().remove(IMessageBundleFactory.class);
		ServiceLocator.get().remove(INlsContext.class);
	}

	@Test
	public void testBundleLoadKl() throws IOException {
		Locale locale = new Locale("kl");
		Locale.setDefault(locale);

		ILocator locator = new ClassLoaderResourceLocator(MessageDummy.class.getClassLoader(),
				"de/intarsys/tools/message/test-bundle.properties");
		String name = MessageTools.getBundleName(getClass(), "test");
		OverrideMessageBundle messageBundle = (OverrideMessageBundle) overridMessageBundleFactory.getMessageBundle(name,
				getClass().getClassLoader());
		messageBundle.load(locator);

		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2");
		Tools.checkPropertyValue(getClass(), "prop3", "bundle-value3");
		Tools.checkPropertyValue(getClass(), "prop4", "bundle-value4");
	}

	@Test
	public void testBundleRegisterKl() {

		Locale locale = new Locale("kl");
		Locale.setDefault(locale);

		String name = MessageTools.getBundleName(getClass(), "test");
		OverrideMessageBundle messageBundle = (OverrideMessageBundle) overridMessageBundleFactory.getMessageBundle(name,
				getClass().getClassLoader());
		messageBundle.register("prop3", "register-value3");
		messageBundle.register("prop4", "register-value4");

		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2");
		Tools.checkPropertyValue(getClass(), "prop3", "register-value3");
		Tools.checkPropertyValue(getClass(), "prop4", "register-value4");
	}

	@Test
	public void testFactoryLoadKl() throws IOException {

		Locale locale = new Locale("kl");
		Locale.setDefault(locale);

		ILocator locator = new ClassLoaderResourceLocator(MessageDummy.class.getClassLoader(),
				"de/intarsys/tools/message/test-factory.properties");
		overridMessageBundleFactory.load(locator);

		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2");
		Tools.checkPropertyValue(getClass(), "prop3", "factory-value3");
		Tools.checkPropertyValue(getClass(), "prop4", "factory-value4");
	}

}
