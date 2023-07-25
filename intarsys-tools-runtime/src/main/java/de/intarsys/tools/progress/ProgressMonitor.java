package de.intarsys.tools.progress;

import de.intarsys.tools.attribute.Attribute;
import de.intarsys.tools.attribute.IAttributeSupport;

public class ProgressMonitor {

	private static final Object ATTR = new Attribute("progressMonitor");

	private static final IProgressMonitor NULL_MONITOR = new NullProgressMonitor();

	public static IProgressMonitor get(IAttributeSupport as) {
		IProgressMonitor monitor = (IProgressMonitor) as.getAttribute(ATTR);
		return monitor == null ? NULL_MONITOR : monitor;
	}

	public static void remove(IAttributeSupport as) {
		as.removeAttribute(ATTR);
	}

	public static void set(IAttributeSupport as, IProgressMonitor monitor) {
		as.setAttribute(ATTR, monitor);
	}

	private ProgressMonitor() {
	}

}
