package de.intarsys.tools.validation;

import de.intarsys.tools.notice.INotice;

/**
 * Tools for dealing with validations.
 * 
 */
public final class ValidationTools {

	/**
	 * Provide a textual summary on all notices available for the
	 * {@link IValidationResult}.
	 * 
	 * @param result
	 * @return
	 */
	public static String getSummary(IValidationResult result) {
		StringBuilder sb = new StringBuilder();
		for (INotice note : result.getNotices()) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(note.getString());
		}
		return sb.toString();
	}

	/**
	 * Provide a textual summary on all notices with a severity higher or equal
	 * to the argument available for the {@link IValidationResult}.
	 * 
	 * @param result
	 * @return
	 */
	public static String getSummary(IValidationResult result, int severity) {
		StringBuilder sb = new StringBuilder();
		for (INotice note : result.getNotices()) {
			if (note.getSeverity() < severity) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(note.getString());
		}
		return sb.toString();
	}

	private ValidationTools() {
	}

}
