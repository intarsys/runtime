package de.intarsys.tools.converter;

public class NoopConverter implements IConverter<Object, Object> {

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
		return Object.class;
	}

}
