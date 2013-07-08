package de.intarsys.tools.format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISO8601Format {

	private final SimpleDateFormat format;

	private static final ISO8601Format Instance = new ISO8601Format();

	public static final ISO8601Format getInstance() {
		return Instance;
	}

	protected ISO8601Format() {
		format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"); //$NON-NLS-1$
	}

	public String format(Date date) {
		String partial = format.format(date);
		partial = partial.replaceAll("\\+0000$", "Z"); //$NON-NLS-1$//$NON-NLS-2$
		partial = partial.replaceAll("(\\d\\d)$", ":$1"); //$NON-NLS-1$//$NON-NLS-2$
		return partial;
	}

	public Date parse(String aISO8601Date) throws ParseException {
		String dateString = aISO8601Date;
		dateString = dateString.replaceAll(":(\\d\\d)$", "$1"); //$NON-NLS-1$//$NON-NLS-2$
		dateString = dateString.replaceAll("Z", "\\+0000"); //$NON-NLS-1$//$NON-NLS-2$
		return format.parse(dateString);
	}
}
