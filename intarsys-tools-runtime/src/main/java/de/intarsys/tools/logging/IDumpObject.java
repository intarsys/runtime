package de.intarsys.tools.logging;

import java.util.List;

public interface IDumpObject {

	public static final String INDENT = "    "; //$NON-NLS-1$

	List<String> dump(String prefix, Object object, IDumpObject details);

}
