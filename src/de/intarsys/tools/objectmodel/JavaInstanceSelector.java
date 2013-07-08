package de.intarsys.tools.objectmodel;

import de.intarsys.tools.string.StringTools;

public class JavaInstanceSelector extends CommonClassSelector {

	final static private Object NULL = new Object();

	final private java.lang.Class clazz;

	final private Object id;

	public JavaInstanceSelector(java.lang.Class clazz, Object id) {
		super();
		this.clazz = clazz;
		if (id == null) {
			this.id = NULL;
		} else {
			this.id = id;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof JavaInstanceSelector)) {
			return false;
		}
		return clazz.equals(((JavaInstanceSelector) obj).clazz)
				&& id.equals(((JavaInstanceSelector) obj).id);
	}

	public java.lang.Class getClazz() {
		return clazz;
	}

	public Object getId() {
		return id;
	}

	public String getName() {
		return clazz.getName() + ":" + StringTools.safeString(id);
	}

	@Override
	public int hashCode() {
		return clazz.hashCode() + id.hashCode();
	}
}
