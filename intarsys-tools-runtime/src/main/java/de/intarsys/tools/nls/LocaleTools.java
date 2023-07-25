///*
// * Copyright (c) 2007, intarsys GmbH
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// * - Redistributions of source code must retain the above copyright notice,
// *   this list of conditions and the following disclaimer.
// *
// * - Redistributions in binary form must reproduce the above copyright notice,
// *   this list of conditions and the following disclaimer in the documentation
// *   and/or other materials provided with the distribution.
// *
// * - Neither the name of intarsys nor the names of its contributors may be used
// *   to endorse or promote products derived from this software without specific
// *   prior written permission.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// * POSSIBILITY OF SUCH DAMAGE.
// */
package de.intarsys.tools.nls;

import java.util.Locale;

/**
 * Tool methods for dealing with {@link Locale} and NLS.
 * 
 */
public final class LocaleTools {

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
