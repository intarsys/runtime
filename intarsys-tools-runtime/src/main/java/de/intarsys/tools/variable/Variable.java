package de.intarsys.tools.variable;

import javax.annotation.PostConstruct;

import de.intarsys.tools.bean.IBeanInstallationInstruction;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.string.StringTools;

public class Variable implements IElementConfigurable, IBeanInstallationInstruction {

	private String name;

	private String namespace;

	private String value;

	@Override
	public void configure(IElement element) throws ConfigurationException {
		setNamespace(element.attributeValue("namespace", getNamespace()));
		setName(element.attributeValue("name", getName()));
		setValue(element.attributeValue("value", getValue()));
	}

	public String getName() {
		return name;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getValue() {
		return value;
	}

	@PostConstruct
	public void register() {
		if (StringTools.isEmpty(getNamespace())) {
			return;
		}
		if (StringTools.isEmpty(getName())) {
			return;
		}
		IVariableNamespaces namespaces = VariableNamespaces.get();
		IVariableNamespace myNamespace = namespaces.getNamespace(getNamespace());
		if (myNamespace instanceof VariableNamespace) {
			// do not pollute preferences
			((VariableNamespace) myNamespace).basicPutVariable(getName(), getValue());
		} else {
			myNamespace.putVariable(getName(), getValue());
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
