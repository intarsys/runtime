package de.intarsys.tools.adapter;

import javax.annotation.PostConstruct;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.exception.TunnelingException;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

public class GenericAdapterFactory implements IAdapterFactory<Object>, IElementConfigurable, IContextSupport {

	private static final ILogger Log = LogTools.getLogger(GenericAdapterFactory.class);

	/**
	 * The link to the definition element in the extension
	 */
	private IElement element;

	private Object context;
	private Class<Object> baseType;
	private Class<Object> targetType;
	private Object adapter;

	public GenericAdapterFactory() {
		super();
	}

	public GenericAdapterFactory(Class baseType, Class targetType, Object adapter) {
		super();
		this.baseType = baseType;
		this.targetType = targetType;
		this.adapter = adapter;
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		this.element = element;
	}

	@Override
	public <T> T getAdapter(Object object, Class<T> clazz) {
		try {
			if (getTargetType().equals(clazz)) {
				if (adapter == null) {
					adapter = ElementTools.createObject(getElement(), "adapterclass", clazz, getContext(),
							Args.create());
				}
				return (T) adapter;
			}
		} catch (ObjectCreationException e) {
			Log.log(Level.SEVERE, "error creating adapter", e);
		}
		return null;
	}

	@Override
	public Class<Object> getBaseType() {
		if (baseType == null) {
			try {
				baseType = ElementTools.createClass(getElement(), "baseclass", Object.class, getContext());
				if (baseType == null) {
					throw new RuntimeException("baseclass must be defined");
				}
			} catch (ObjectCreationException e) {
				String msg = "generic adapter factory error loading base type";
				Log.warn("{} ({})", msg, ExceptionTools.getMessage(e));
				throw new TunnelingException(msg, e);
			}
		}
		return baseType;
	}

	public Object getContext() {
		return context;
	}

	public IElement getElement() {
		return element;
	}

	public Class<?> getTargetType() {
		if (targetType == null) {
			try {
				targetType = ElementTools.createClass(getElement(), "targetclass", Object.class, getContext());
				if (targetType == null) {
					throw new RuntimeException("targetclass must be defined");
				}
			} catch (ObjectCreationException e) {
				String msg = "generic adapter factory error loading target type";
				Log.warn("{} ({})", msg, ExceptionTools.getMessage(e));
				throw new TunnelingException(msg, e);
			}
		}
		return targetType;
	}

	@PostConstruct
	public void register() {
		AdapterOutlet.get().registerAdapterFactory(this);
	}

	@Override
	public void setContext(Object context) {
		this.context = context;
	}

}
