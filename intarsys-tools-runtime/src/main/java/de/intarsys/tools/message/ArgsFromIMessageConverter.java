package de.intarsys.tools.message;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.IConverter;
import de.intarsys.tools.functor.ArgsBuilder;
import de.intarsys.tools.functor.IArgs;

public class ArgsFromIMessageConverter implements IConverter<IMessage, IArgs> {

	@Override
	public IArgs convert(IMessage source) throws ConversionException {
		return new ArgsBuilder() //
				.put("code", source.getCode()) //
				.put("string", source.getString()) //
				.getArgs();
	}

	@Override
	public Class<IMessage> getSourceType() {
		return IMessage.class;
	}

	@Override
	public Class<IArgs> getTargetType() {
		return IArgs.class;
	}
}
