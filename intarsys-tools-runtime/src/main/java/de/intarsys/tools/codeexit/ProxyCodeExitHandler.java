/*
 *   o_
 * in|tarsys GmbH (c)
 *   
 * all rights reserved
 *
 */
package de.intarsys.tools.codeexit;

import javax.annotation.PostConstruct;

import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.ElementProxy;
import de.intarsys.tools.infoset.IElement;

/**
 * An {@link ICodeExitHandler} that defers loading of its delegate until it is
 * finally called.
 * 
 */
public class ProxyCodeExitHandler extends ElementProxy<ICodeExitHandler>
		implements ICodeExitHandler, ICodeExitMetaHandler {

	public static final String EA_ID = "id"; //$NON-NLS-1$

	private String id;

	@Override
	public void configure(IElement pElement) {
		super.configure(pElement);
		id = pElement.attributeValue(EA_ID, null);
	}

	@Override
	public boolean exists(CodeExit codeExit) {
		if (getRealized() instanceof ICodeExitMetaHandler) {
			return ((ICodeExitMetaHandler) getRealized()).exists(codeExit);
		}
		return true;
	}

	public String getId() {
		return id;
	}

	@Override
	public Object perform(CodeExit codeExit, IFunctorCall call) throws FunctorException {
		return getRealized().perform(codeExit, call);
	}

	@PostConstruct
	public void register() {
		CodeExitHandlerRegistry.get().registerCodeExitHandler(getId(), this);
	}

}
