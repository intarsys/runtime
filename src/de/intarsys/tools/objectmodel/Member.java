package de.intarsys.tools.objectmodel;

abstract public class Member implements IMember {

	private IClass clazz;

	private String[] modifiers;

	public IClass getDeclaringClass() {
		return clazz;
	}

	public boolean hasModifier(String modifier) {
		if (modifiers == null) {
			return false;
		}
		for (int i = 0; i < modifiers.length; i++) {
			if (modifiers[i].equals(modifier)) {
				return true;
			}
		}
		return false;
	}

	public void setDeclaringClass(IClass pClazz) {
		this.clazz = pClazz;
	}

	public void setModifiers(String modifierString) {
		if (modifierString == null) {
			modifiers = null;
		} else {
			modifiers = modifierString.split(";");
			for (int i = 0; i < modifiers.length; i++) {
				modifiers[i] = modifiers[i].trim();
			}
		}
	}

}
