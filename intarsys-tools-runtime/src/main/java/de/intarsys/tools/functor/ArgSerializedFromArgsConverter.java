package de.intarsys.tools.functor;

import java.util.Iterator;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.converter.IConverter;
import de.intarsys.tools.functor.IArgs.IBinding;

/**
 * Convert any {@link IArgs} to a {@link ArgSerialized} format.
 */
public class ArgSerializedFromArgsConverter implements IConverter<IArgs, IArgs> {

	public ArgSerializedFromArgsConverter() {
		super();
	}

	@Override
	public IArgs convert(IArgs source) throws ConversionException {
		IArgs result = Args.create();
		Iterator<IBinding> it = source.bindings();
		while (it.hasNext()) {
			IBinding binding = it.next();
			if (binding.isDefined()) {
				Object tempValue = ConverterRegistry.get().convert(binding.getValue(), ArgSerialized.class);
				if (binding.getName() != null) {
					result.put(binding.getName(), tempValue);
				} else {
					result.add(tempValue);
				}
			}
		}
		return result;
	}

	@Override
	public Class<?> getSourceType() {
		return IArgs.class;
	}

	@Override
	public Class<?> getTargetType() {
		return ArgSerialized.class;
	}

}
