package de.intarsys.tools.bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.exception.TunnelingException;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;

public class ScriptedLifecycleBean implements IElementConfigurable {

	private IElement element;

	@Override
	public void configure(IElement pElement) throws ConfigurationException {
		this.element = pElement;
	}

	public IElement getElement() {
		return element;
	}

	@PostConstruct
	public void postConstruct() {
		try {
			IElement elFunctor = getElement().element("postConstruct");
			IFunctor functor = ElementTools.createFunctor(this, elFunctor, null, null);
			if (functor != null) {
				functor.perform(FunctorCall.create(this));
			}
		} catch (Exception e) {
			// we must not declare a checked exception here (see
			// @PostConstruct).
			throw new TunnelingException(e);
		}
	}

	@PreDestroy
	public void preDestroy() {
		try {
			IElement elFunctor = getElement().element("preDestroy");
			IFunctor functor = ElementTools.createFunctor(this, elFunctor, null, null);
			if (functor != null) {
				functor.perform(FunctorCall.create(this));
			}
		} catch (Exception e) {
			// we must not declare a checked exception here (see @PreDestroy).
			throw new TunnelingException(e);
		}
	}

	public void setElement(IElement element) {
		this.element = element;
	}

}
