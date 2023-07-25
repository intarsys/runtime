package de.intarsys.tools.functor;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.converter.IConverter;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorSupport;

/**
 * Convert any {@link ILocator} to a {@link ArgSerialized} format.
 */
public class ArgSerializedFromLocatorSupportConverter implements IConverter<ILocatorSupport, Object> {

	public ArgSerializedFromLocatorSupportConverter() {
		super();
	}

	@Override
	public Object convert(ILocatorSupport source) throws ConversionException {
		return ConverterRegistry.get().convert(source.getLocator(), ArgSerialized.class);
	}

	@Override
	public Class<?> getSourceType() {
		return ILocatorSupport.class;
	}

	@Override
	public Class<?> getTargetType() {
		return ArgSerialized.class;
	}

}
