package de.intarsys.tools.authenticate;

import de.intarsys.tools.factory.CommonInstantiatingFactory;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reflect.ObjectCreationException;

public class ConstantPasswordProviderFactory extends CommonInstantiatingFactory<ConstantPasswordProvider> {

	public static final String ARG_PASSWORD = "password";

	public ConstantPasswordProviderFactory() {
		super();
	}

	@Override
	protected ConstantPasswordProvider basicCreateInstance(IArgs args) throws ObjectCreationException {
		char[] password = null;
		IElement configuration = getConfiguration(args);
		if (configuration == null) {
			password = ArgTools.getCharArray(args, ARG_PASSWORD, null);
		} else {
			password = ElementTools.getCharArray(configuration, ARG_PASSWORD, null);
		}

		return new ConstantPasswordProvider(password);
	}

	@Override
	public Class<ConstantPasswordProvider> getResultType() {
		return ConstantPasswordProvider.class;
	}

}
