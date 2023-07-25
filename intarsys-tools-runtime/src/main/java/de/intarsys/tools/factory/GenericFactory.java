package de.intarsys.tools.factory;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * A generic factory implementation.
 * 
 * The instance is completely defined in a a child {@link IElement} named
 * "template".
 * 
 * @param <T>
 */
public class GenericFactory<T> extends CommonInstantiatingFactory<T> {

	public static final String EN_TEMPLATE = "template";

	private String configNameResultClass = "resultClass"; //$NON-NLS-1$

	private IElement template;

	private Class resultClass = Object.class;

	@Override
	protected T basicCreateInstance(IArgs args) throws ObjectCreationException {
		IElement myTemplate = getTemplate();
		if (myTemplate == null) {
			IElement config = (IElement) args.get(CommonFactory.ARG_CONFIGURATION);
			if (config != null) {
				myTemplate = config.element(EN_TEMPLATE);
			}
		}
		if (myTemplate == null) {
			throw new ObjectCreationException("template missing");
		}
		IArgs initArgs = ArgTools.getArgs(args, "properties", Args.create());
		return (T) ElementTools.createObject(myTemplate, resultClass, getContext(args), initArgs);
	}

	@Override
	protected void basicCreateInstanceConfig(T object, IArgs args) throws ObjectCreationException {
		// already done in the tools
	}

	@Override
	public void basicCreateInstanceInit(T object, IArgs args) {
		// already done in the tools
	}

	@Override
	public void configure(IElement pElement) throws ConfigurationException {
		super.configure(pElement);
		setTemplate(pElement.element(EN_TEMPLATE));
		try {
			setResultClass(ElementTools.createClass(pElement, getConfigNameResultClass(), Object.class, this));
		} catch (ObjectCreationException e) {
			throw new ConfigurationException(e);
		}
	}

	public String getConfigNameResultClass() {
		return configNameResultClass;
	}

	@Override
	protected ClassLoader getDefaultClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	@Override
	protected String getDefaultId() {
		if (getTemplate() != null) {
			return ElementTools.getString(getTemplate(), "class", null);
		}
		return super.getDefaultId();
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

	public void setConfigNameResultClass(String configNameResultClass) {
		this.configNameResultClass = configNameResultClass;
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

	public void validate() throws ObjectCreationException {
		ElementTools.createClass(getTemplate(), "class", Object.class, this);
	}
}
