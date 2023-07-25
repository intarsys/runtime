package de.intarsys.tools.eventbus;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IIdentifiable;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;

/**
 * A predicate for accepting source objects with a certain id.
 * 
 */
public class EventSourceId implements EventSourcePredicate, IElementConfigurable {

	private String id;

	public EventSourceId() {
		super();
	}

	public EventSourceId(String id) {
		super();
		this.id = id;
	}

	@Override
	public boolean accepts(Object source) {
		String objectId = null;
		if (source instanceof IIdentifiable) {
			objectId = ((IIdentifiable) source).getId();
		}
		if (objectId == null) {
			return false;
		}
		return objectId.equals(getId());
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		setId(element.attributeValue("id", null));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
