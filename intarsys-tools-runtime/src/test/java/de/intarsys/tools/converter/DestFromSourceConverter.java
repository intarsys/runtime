package de.intarsys.tools.converter;

public class DestFromSourceConverter implements IConverter<SourceType, DestType> {

	@Override
	public DestType convert(SourceType source) throws ConversionException {
		return new DestType(source.getValue());
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
