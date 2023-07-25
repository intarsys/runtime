package de.intarsys.tools.preferences;

import java.util.prefs.Preferences;

import org.junit.Assert;
import org.junit.Test;

public class TestPreferences {

	public static final String LONG_NAME = "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

	@Test
	public void testMaxLength() {
		Preferences jPrefs;
		PreferencesAdapter prefs;
		//
		jPrefs = Preferences.userRoot();
		prefs = new PreferencesAdapter(null, jPrefs);
		prefs.put(LONG_NAME, "foo");
		Assert.assertEquals("foo", prefs.get(LONG_NAME));
		//
		prefs.node(LONG_NAME).put(LONG_NAME, "foo");
		Assert.assertEquals("foo", prefs.node(LONG_NAME).get(LONG_NAME));
	}

}
