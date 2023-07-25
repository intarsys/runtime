package de.intarsys.tools.string;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StringEncodingTools {

	protected static final String[] TRUSTED_ENCODINGS = new String[] { "IBM00858", "IBM850", "ISO-8859-1", "US-ASCII", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"UTF-8", //$NON-NLS-1$
			"windows-1250", "windows-1252", "x-MacCentralEurope" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	public static String[] charsets(boolean trusted) {
		if (trusted) {
			List available = new ArrayList();
			for (int i = 0; i < TRUSTED_ENCODINGS.length; i++) {
				String encoding = TRUSTED_ENCODINGS[i];
				try {
					if (Charset.forName(encoding) != null) {
						available.add(encoding);
					}
				} catch (Exception e) {
					// not available
				}
			}
			return (String[]) available.toArray(new String[available.size()]);
		}
		Set charsets = Charset.availableCharsets().keySet();
		String[] result = new String[TRUSTED_ENCODINGS.length + charsets.size()];
		System.arraycopy(TRUSTED_ENCODINGS, 0, result, 0, TRUSTED_ENCODINGS.length);
		int i = TRUSTED_ENCODINGS.length;
		for (Iterator it = charsets.iterator(); it.hasNext();) {
			String encoding = (String) it.next();
			result[i++] = "[" + encoding + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return result;
	}

	private StringEncodingTools() {
	}

}
