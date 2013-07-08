package de.intarsys.tools.bean;

import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reflect.ObjectCreationException;

public class LazyBean implements IBeanProxy {

	final static private Object UNDEFINED = new Object();

	final private Object context;

	final private IElement element;

	private Object object;

	public LazyBean(IElement element, Object context) {
		super();
		this.element = element;
		this.context = context;
		this.object = UNDEFINED;
	}

	public Object getContext() {
		return context;
	}

	public IElement getElement() {
		return element;
	}

	@Override
	public Object getObject() {
		if (object == UNDEFINED) {
			object = realize();
		}
		return object;
	}

	protected Object realize() {
		try {
			return ElementTools.createObject(getElement(), Object.class, getContext());
		} catch (ObjectCreationException e) {
			throw new RuntimeException(e);
		}
	}

}
