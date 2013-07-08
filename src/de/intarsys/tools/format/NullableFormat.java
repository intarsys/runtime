package de.intarsys.tools.format;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * This format allows a daisy chain style null filtering.
 * 
 */
public class NullableFormat extends Format {

	final private Format format;

	final private String nullValue;

	public NullableFormat(Format format, String nullValue) {
		super();
		this.format = format;
		this.nullValue = nullValue;
	}

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo,
			FieldPosition pos) {
		if (obj == null) {
			toAppendTo.append(getNullValue());
		} else {
			format.format(obj, toAppendTo, pos);
		}
		return toAppendTo;
	}

	public Format getFormat() {
		return format;
	}

	public String getNullValue() {
		return nullValue;
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		return format.parseObject(source, pos);
	}

}
