package de.intarsys.tools.functor;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.IConverter;

/**
 * Convert any {@link Object} to a {@link ArgSerialized} format.
 */
public class ArgSerializedFromObjectConverter implements IConverter<Object, Object> {

	public ArgSerializedFromObjectConverter() {
		super();
	}

	@Override
	public Object convert(Object source) throws ConversionException {
		return source;
	}

	@Override
	public Class<?> getSourceType() {
		return Object.class;
	}

	@Override
	public Class<?> getTargetType() {
		return ArgSerialized.class;
	}

}
