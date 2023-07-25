package de.intarsys.tools.objectmodel;

import de.intarsys.tools.string.StringTools;

public class JavaInstanceSelector extends CommonClassSelector {

	private static final Object NULL = new Object();

	private final java.lang.Class clazz;

	private final Object id;

	protected JavaInstanceSelector(java.lang.Class clazz, Object id) {
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
		return clazz.equals(((JavaInstanceSelector) obj).clazz) && id.equals(((JavaInstanceSelector) obj).id);
	}

	public java.lang.Class getClazz() {
		return clazz;
	}

	public Object getId() {
		return id;
	}

	@Override
	public String getName() {
		return clazz.getName() + ":" + StringTools.safeString(id);
	}

	@Override
	public int hashCode() {
		return clazz.hashCode() + id.hashCode();
	}
}
