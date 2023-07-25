package de.intarsys.tools.nls;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.intarsys.tools.servicelocator.ServiceLocator;

public class TestThreadNlsContext {

	private static Locale locale;

	@AfterClass
	public static void after() {
		Locale.setDefault(locale);
	}

	@BeforeClass
	public static void before() {
		locale = Locale.getDefault();
		ServiceLocator.get().resetServices();
		ServiceLocator.get().put(INlsContext.class, new ThreadNlsContext());
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
		NlsContext.get().setLocale(Locale.CANADA);
		Locale newLocale = NlsContext.get().getLocale();
		assertThat(oldLocale, not(newLocale));
		assertThat(newLocale, is(Locale.CANADA));
		/*
		 * intended behavior - the locale is not stored back into Java VM
		 */
		assertThat(oldLocale, is(Locale.getDefault()));
	}

	@Test
	public void nlsContextThreadBound() throws InterruptedException {
		Locale locale = NlsContext.get().getLocale();
		Runnable target = () -> {
			NlsContext.get().setLocale(Locale.CHINA);
			Locale innerLocale = NlsContext.get().getLocale();
			assertThat(locale, is(Locale.getDefault()));
			assertThat(innerLocale, is(Locale.CHINA));
		};
		Thread t = new Thread(target);
		t.start();
		t.join();
		Locale outerLocale = NlsContext.get().getLocale();
		assertThat(locale, is(outerLocale));
	}

}
