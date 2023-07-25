package de.intarsys.tools.action;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.IElement;

public abstract class NamedAction extends Action {

	private String name;

	protected NamedAction() {
		super();
	}

	protected NamedAction(Object owner) {
		super(owner);
	}

	protected NamedAction(Object owner, boolean checked) {
		super(owner, checked);
	}

	protected NamedAction(String id, Object owner) {
		super(id, owner);
	}

	protected NamedAction(String id, Object owner, boolean checked) {
		super(id, owner, checked);
	}

	@Override
	public void configure(IElement pElement) throws ConfigurationException {
		super.configure(pElement);
		name = pElement.attributeValue("name", null);
		if (name == null) {
			name = getId();
		}
	}

	public String getName() {
		return name;
	}
}
