package de.intarsys.tools.preferences;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.IConverter;

/**
 * 
 */
public class PreferenceValueFromStringConverter implements
		IConverter<String, String> {

	public String convert(String source) throws ConversionException {
		return source;
	}

	public Class<?> getSourceType() {
		return String.class;
	}

	public Class<?> getTargetType() {
		return PreferenceValue.class;
	}

}
