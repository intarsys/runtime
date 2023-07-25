package de.intarsys.tools.infoset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractElement implements IElement {

	private List<PropertyDeclaration> declarations;

	@Override
	public void addDeclaration(PropertyDeclaration declaration) {
		if (declarations == null) {
			declarations = new ArrayList<>();
		}
		declarations.add(declaration);
	}

	@Override
	public List<PropertyDeclaration> getDeclarations() {
		if (declarations == null) {
			return Collections.emptyList();
		}
		return declarations;
	}

}
