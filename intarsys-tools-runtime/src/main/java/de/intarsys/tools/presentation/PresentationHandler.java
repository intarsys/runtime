package de.intarsys.tools.presentation;

import de.intarsys.tools.string.StringTools;

public class PresentationHandler implements IPresentationHandler {

	protected String basicGetDescription(Object object) {
		return getTip(object);
	}

	/**
	 * Hook method
	 * 
	 * @param object
	 *            The object whose icon should be returned
	 * @return
	 */
	protected String basicGetIconName(Object object) {
		return null;
	}

	protected String basicGetLabel(Object object) {
		return StringTools.safeString(object);
	}

	protected String basicGetTip(Object object) {
		return getLabel(object);
	}

	@Override
	public String getDescription(Object object) {
		if (object instanceof IPresentationSupport) {
			return ((IPresentationSupport) object).getDescription();
		}
		return basicGetDescription(object);
	}

	@Override
	public String getIconName(Object object) {
		if (object instanceof IPresentationSupport) {
			return ((IPresentationSupport) object).getIconName();
		}
		return basicGetIconName(object);
	}

	@Override
	public String getLabel(Object object) {
		if (object instanceof IPresentationSupport) {
			return ((IPresentationSupport) object).getLabel();
		}
		return basicGetLabel(object);
	}

	@Override
	public String getTip(Object object) {
		if (object instanceof IPresentationSupport) {
			return ((IPresentationSupport) object).getTip();
		}
		return basicGetTip(object);
	}

}
