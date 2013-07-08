package de.intarsys.tools.objectmodel;

public class JavaClassSelector extends CommonClassSelector {

	private final java.lang.Class clazz;

	public JavaClassSelector(java.lang.Class clazz) {
		super();
		this.clazz = clazz;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof JavaClassSelector)) {
			return false;
		}
		return clazz.equals(((JavaClassSelector) obj).clazz);
	}

	public java.lang.Class getClazz() {
		return clazz;
	}

	public String getName() {
		return clazz.getName();
	}

	@Override
	public int hashCode() {
		return clazz.hashCode();
	}
}
