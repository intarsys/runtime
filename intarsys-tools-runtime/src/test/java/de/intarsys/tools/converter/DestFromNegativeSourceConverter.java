package de.intarsys.tools.converter;

public class DestFromNegativeSourceConverter implements IConverter<SourceType, DestType> {

	@Override
	public DestType convert(SourceType source) throws ConversionException {
		if (source.getValue() < 0) {
			return new DestType(-source.getValue());
		}
		return null;
	}

	@Override
	public int getPriority() {
		return -1;
	}

	@Override
	public Class<?> getSourceType() {
		return SourceType.class;
	}

	@Override
	public Class<?> getTargetType() {
		return DestType.class;
	}

}
