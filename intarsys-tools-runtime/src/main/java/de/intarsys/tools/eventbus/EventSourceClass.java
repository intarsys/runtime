package de.intarsys.tools.eventbus;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * A predicate for accepting source objects of a certain class.
 * 
 */
public class EventSourceClass implements EventSourcePredicate, IElementConfigurable {

	private Class<?> sourceClass;

	public EventSourceClass() {
		super();
	}

	public EventSourceClass(Class<?> sourceClass) {
		super();
		this.sourceClass = sourceClass;
	}

	@Override
	public boolean accepts(Object source) {
		return getSourceClass().isInstance(source);
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		try {
			setSourceClass(ElementTools.createClass(element, "sourceClass", null, null));
		} catch (ObjectCreationException e) {
			throw new ConfigurationException(e);
		}
	}

	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public void setSourceClass(Class<?> sourceClass) {
		this.sourceClass = sourceClass;
	}

}
