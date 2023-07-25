package de.intarsys.tools.message;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.converter.IConverter;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.ipc.IPCObject;

public class IPCObjectFromIMessageConverter implements IConverter<IMessage, IArgs> {

	@Override
	public IArgs convert(IMessage source) throws ConversionException {
		return ConverterRegistry.get().convert(source, IArgs.class);
	}

	@Override
	public Class<IMessage> getSourceType() {
		return IMessage.class;
	}

	@Override
	public Class<IPCObject> getTargetType() {
		return IPCObject.class;
	}
}
