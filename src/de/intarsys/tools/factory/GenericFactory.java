package de.intarsys.tools.factory;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * A generic factory implementation.
 * 
 * The instance is completely defined in the template.
 * 
 * @param <T>
 */
public class GenericFactory<T> extends CommonFactory<T> {

	private IElement template;

	private Class<T> resultClass;

	@Override
	protected T basicCreateInstance(IArgs args) throws ObjectCreationException {
		return ElementTools.createObject(getTemplate(), resultClass,
				getContext(args));
	}

	@Override
	public void basicCreateInstanceInit(T object, IArgs args) {
		// already done in the tools
	}

	@Override
	public void configure(IElement pElement) throws ConfigurationException {
		super.configure(pElement);
		setTemplate(pElement.element("template"));
		try {
			setResultClass(ElementTools.createClass(pElement, "resultClass",
					Object.class, this));
		} catch (ObjectCreationException e) {
			throw new ConfigurationException(e);
		}
	}

	public Class getResultClass() {
		return resultClass;
	}

	@Override
	public Class<T> getResultType() {
		return resultClass;
	}

	public IElement getTemplate() {
		return template;
	}

	public void setResultClass(Class resultClass) {
		if (resultClass == null) {
			resultClass = Object.class;
		}
		this.resultClass = resultClass;
	}

	public void setTemplate(IElement template) {
		this.template = template;
	}

}
