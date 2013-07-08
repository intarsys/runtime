package de.intarsys.tools.logging;

import java.util.List;

public interface IDumpObject {

	List<String> dump(String prefix, Object object, IDumpObject details);

}
