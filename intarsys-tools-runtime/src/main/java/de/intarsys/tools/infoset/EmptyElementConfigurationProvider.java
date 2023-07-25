package de.intarsys.tools.infoset;

public class EmptyElementConfigurationProvider implements IElementConfigurationProvider {

	@Override
	public IElement getRootElement() {
		return ElementFactory.get().createElement("root");
	}

}
