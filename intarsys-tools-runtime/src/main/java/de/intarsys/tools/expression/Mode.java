package de.intarsys.tools.expression;

public enum Mode {
	/**
	 * Restrict access to sensitive data and components, for example, when processing user-supplied data.
	 */
	UNTRUSTED,

	/**
	 * No access restrictions.
	 */
	TRUSTED
}
