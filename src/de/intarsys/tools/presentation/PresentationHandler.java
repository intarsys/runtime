package de.intarsys.tools.presentation;

import de.intarsys.tools.string.StringTools;

public class PresentationHandler implements IPresentationHandler {

	protected String basicGetDescription(Object object) {
		return getTip(object);
	}

	protected String basicGetIconName(Object object) {
		return null;
	}

	protected String basicGetLabel(Object object) {
		return StringTools.safeString(object);
	}

	protected String basicGetTip(Object object) {
		return getLabel(object);
	}

	public String getDescription(Object object) {
		if (object instanceof IPresentationSupport) {
			return ((IPresentationSupport) object).getDescription();
		}
		return basicGetDescription(object);
	}

	public String getIconName(Object object) {
		if (object instanceof IPresentationSupport) {
			return ((IPresentationSupport) object).getIconName();
		}
		return basicGetIconName(object);
	}

	public String getLabel(Object object) {
		if (object instanceof IPresentationSupport) {
			return ((IPresentationSupport) object).getLabel();
		}
		return basicGetLabel(object);
	}

	public String getTip(Object object) {
		if (object instanceof IPresentationSupport) {
			return ((IPresentationSupport) object).getTip();
		}
		return basicGetTip(object);
	}

}
