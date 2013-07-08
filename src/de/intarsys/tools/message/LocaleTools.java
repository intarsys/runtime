package de.intarsys.tools.message;

import java.util.Locale;

public class LocaleTools {

	/**
	 * @param locale
	 *            locale
	 * @return the windows LCID (Language Code Id)
	 */
	public static int getLcid(Locale locale) {
		String lang = locale.getLanguage();
		if (Locale.GERMAN.getLanguage().equals(lang)) {
			return 0x0407;
		}
		if (Locale.ENGLISH.getLanguage().equals(lang)) {
			return 0x0409;
		}
		if (Locale.ITALIAN.getLanguage().equals(lang)) {
			return 0x0410;
		}
		if (Locale.FRENCH.getLanguage().equals(lang)) {
			return 0x040c;
		}
		if (Locale.JAPANESE.getLanguage().equals(lang)) {
			return 0x0411;
		}
		if (Locale.KOREAN.getLanguage().equals(lang)) {
			return 0x0412;
		}
		if (Locale.CHINESE.getLanguage().equals(lang)) {
			return 0x0804;
		}
		/** Spanish */
		if ("es".equals(lang)) { //$NON-NLS-1$
			return 0x040a;
		}
		return 0;
	}

	private LocaleTools() {
		// 
	}

}
