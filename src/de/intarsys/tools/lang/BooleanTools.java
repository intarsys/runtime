package de.intarsys.tools.lang;

public class BooleanTools {

	static public String toStringThreeState(Boolean value) {
		if (value == null) {
			return "?";
		}
		if (value) {
			return "true";
		}
		return "false";
	}
}
