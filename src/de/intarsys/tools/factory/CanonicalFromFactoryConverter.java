package de.intarsys.tools.factory;

import de.intarsys.tools.converter.Canonical;
import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.converter.IConverter;

/**
 * Convert any {@link CommonFactory} to a {@link Canonical} format.
 */
public class CanonicalFromFactoryConverter implements
		IConverter<CommonFactory, String> {

	public CanonicalFromFactoryConverter() {
		ConverterRegistry.get().registerConverter(this);
	}

	@Override
	public String convert(CommonFactory source) throws ConversionException {
		return source.getId();
	}

	@Override
	public Class<?> getSourceType() {
		return CommonFactory.class;
	}

	@Override
	public Class<?> getTargetType() {
		return Canonical.class;
	}

}
