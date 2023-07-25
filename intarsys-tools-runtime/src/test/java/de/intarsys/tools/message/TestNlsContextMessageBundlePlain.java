package de.intarsys.tools.message;

import java.util.Locale;

import org.junit.Test;

import de.intarsys.tools.nls.INlsContext;
import de.intarsys.tools.nls.NlsContext;
import de.intarsys.tools.nls.ThreadNlsContext;
import de.intarsys.tools.servicelocator.ServiceLocator;

public class TestNlsContextMessageBundlePlain {

	private static final Locale KLINGON = new Locale("kl");

	private Locale oldLocale;

	@Test
	public void checkDe() throws Exception {
		NlsContext.get().setLocale(Locale.GERMANY);
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2_de");
	}

	@Test
	public void checkDeConcurrent() throws Exception {
		NlsContext.get().setLocale(Locale.GERMANY);
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2_de");
		Runnable target = () -> {
			NlsContext.get().setLocale(Locale.CHINA);
			Tools.checkPropertyValue(getClass(), "prop1", "value1");
			Tools.checkPropertyValue(getClass(), "prop2", "value2");
			NlsContext.get().setLocale(Locale.US);
			Tools.checkPropertyValue(getClass(), "prop1", "value1");
			Tools.checkPropertyValue(getClass(), "prop2", "value2_en");
		};
		Thread t = new Thread(target);
		t.start();
		t.join();
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2_de");
	}

	@Test
	public void checkEn() throws Exception {
		NlsContext.get().setLocale(Locale.US);
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2_en");
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2_en");
	}

	@Test
	public void checkEnConcurrent() throws Exception {
		NlsContext.get().setLocale(Locale.US);
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2_en");
		Runnable target = () -> {
			NlsContext.get().setLocale(Locale.CHINA);
			Tools.checkPropertyValue(getClass(), "prop1", "value1");
			Tools.checkPropertyValue(getClass(), "prop2", "value2");
			NlsContext.get().setLocale(Locale.GERMANY);
			Tools.checkPropertyValue(getClass(), "prop1", "value1");
			Tools.checkPropertyValue(getClass(), "prop2", "value2_de");
		};
		Thread t = new Thread(target);
		t.start();
		t.join();
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2_en");
	}

	@Test
	public void checkKl() throws Exception {
		NlsContext.get().setLocale(KLINGON);
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2");
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2");
	}

	@Test
	public void checkKlConcurrent() throws Exception {
		NlsContext.get().setLocale(KLINGON);
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2");
		Runnable target = () -> {
			NlsContext.get().setLocale(Locale.CHINA);
			Tools.checkPropertyValue(getClass(), "prop1", "value1");
			Tools.checkPropertyValue(getClass(), "prop2", "value2");
			NlsContext.get().setLocale(Locale.US);
			Tools.checkPropertyValue(getClass(), "prop1", "value1");
			Tools.checkPropertyValue(getClass(), "prop2", "value2_en");
		};
		Thread t = new Thread(target);
		t.start();
		t.join();
		Tools.checkPropertyValue(getClass(), "prop1", "value1");
		Tools.checkPropertyValue(getClass(), "prop2", "value2");
	}

	@org.junit.Before
	public void setup() {
		oldLocale = Locale.getDefault();
		ServiceLocator.get().put(INlsContext.class, new ThreadNlsContext());
		NlsContextMessageBundleFactory messageBundleFactory = new NlsContextMessageBundleFactory();
		ServiceLocator.get().put(IMessageBundleFactory.class, messageBundleFactory);
	}

	@org.junit.After
	public void teardown() {
		if (oldLocale != null) {
			Locale.setDefault(oldLocale);
		}
		ServiceLocator.get().remove(INlsContext.class);
		ServiceLocator.get().remove(IMessageBundleFactory.class);
	}

}
