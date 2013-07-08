package de.intarsys.tools.preferences;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.IConverter;
import de.intarsys.tools.functor.IArgs;

/**
 * 
 */
public class PreferenceValueFromArgsConverter implements
		IConverter<IArgs, IArgs> {

	public IArgs convert(IArgs source) throws ConversionException {
		return source;
	}

	public Class<?> getSourceType() {
		return IArgs.class;
	}

	public Class<?> getTargetType() {
		return PreferenceValue.class;
	}

}
