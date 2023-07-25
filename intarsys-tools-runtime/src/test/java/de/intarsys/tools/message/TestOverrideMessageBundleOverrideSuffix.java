package de.intarsys.tools.message;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

import de.intarsys.tools.nls.INlsContext;
import de.intarsys.tools.nls.StandardNlsContext;
import de.intarsys.tools.servicelocator.ServiceLocator;

public class TestOverrideMessageBundleOverrideSuffix {

	private Locale oldLocale;
	private OverrideMessageBundleFactory messageBundleFactory;

	@org.junit.Before
	public void setup() {
		oldLocale = Locale.getDefault();
		messageBundleFactory = new OverrideMessageBundleFactory();
		messageBundleFactory.setOverrideSuffix("-override");
		ServiceLocator.get().put(IMessageBundleFactory.class, messageBundleFactory);
		ServiceLocator.get().put(INlsContext.class, new StandardNlsContext());
	}

	@org.junit.After
	public void teardown() {
		ServiceLocator.get().remove(IMessageBundleFactory.class);
		ServiceLocator.get().remove(INlsContext.class);
		if (oldLocale != null) {
			Locale.setDefault(oldLocale);
		}
	}

	@Test
	public void testEn() throws IOException {
		Locale locale = new Locale("en");
		Locale.setDefault(locale);

		Tools.checkPropertyValue(getClass(), "prop1", null);
		Tools.checkPropertyValue(getClass(), "prop2", null);
		Tools.checkPropertyValue(getClass(), "prop3", "override-value3");
		Tools.checkPropertyValue(getClass(), "prop4", "override-value4_en");
	}

	@Test
	public void testKl() throws IOException {
		Locale locale = new Locale("kl");
		Locale.setDefault(locale);

		Tools.checkPropertyValue(getClass(), "prop1", null);
		Tools.checkPropertyValue(getClass(), "prop2", null);
		Tools.checkPropertyValue(getClass(), "prop3", "override-value3");
		Tools.checkPropertyValue(getClass(), "prop4", "override-value4");
	}
}
