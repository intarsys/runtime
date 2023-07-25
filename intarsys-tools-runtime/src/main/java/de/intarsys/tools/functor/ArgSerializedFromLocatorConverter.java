package de.intarsys.tools.functor;

import java.io.IOException;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.IConverter;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.stream.StreamTools;

/**
 * Convert any {@link ILocator} to a {@link ArgSerialized} format.
 */
public class ArgSerializedFromLocatorConverter implements IConverter<ILocator, IArgs> {

	public ArgSerializedFromLocatorConverter() {
		super();
	}

	@Override
	public IArgs convert(ILocator source) throws ConversionException {
		try {
			byte[] content = StreamTools.getBytes(source.getInputStream());
			IArgs result = Args.create();
			result.put("name", source.getName());
			result.put("content", content);
			return result;
		} catch (IOException e) {
			throw new ConversionException(e);
		}
	}

	@Override
	public Class<?> getSourceType() {
		return ILocator.class;
	}

	@Override
	public Class<?> getTargetType() {
		return ArgSerialized.class;
	}

}
