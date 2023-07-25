package de.intarsys.tools.nls;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.intarsys.tools.servicelocator.ServiceLocator;

public class TestStandardNlsContext {

	private static Locale locale;

	@AfterClass
	public static void after() {
		Locale.setDefault(locale);
	}

	@BeforeClass
	public static void before() {
		locale = Locale.getDefault();
		ServiceLocator.get().resetServices();
	}

	@Test
	public void nlsContextIsStandard() {
		assertThat(NlsContext.get(), instanceOf(StandardNlsContext.class));
	}

	@Test
	public void nlsContextPresent() {
		Locale locale = NlsContext.get().getLocale();
		assertThat(locale, is(Locale.getDefault()));
	}

	@Test
	public void nlsContextStack() {
		Locale oldLocale = NlsContext.get().getLocale();
		NlsContext.get().pushLocale(Locale.CHINA);
		Locale newLocale = NlsContext.get().getLocale();
		assertThat(newLocale, is(Locale.CHINA));
		NlsContext.get().popLocale();
		newLocale = NlsContext.get().getLocale();
		assertThat(newLocale, is(oldLocale));
	}

	@Test
	public void nlsContextSwitchable() {
		Locale oldLocale = NlsContext.get().getLocale();
		NlsContext.get().setLocale(Locale.CHINA);
		Locale newLocale = NlsContext.get().getLocale();
		assertThat(newLocale, is(Locale.CHINA));
	}

}
