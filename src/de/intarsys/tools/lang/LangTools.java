package de.intarsys.tools.lang;

public class LangTools {

	static public boolean equals(Object a, Object b) {
		if (a == null && b == null) {
			return true;
		}
		if (a == null && b != null) {
			return false;
		}
		return a.equals(b);
	}
}
