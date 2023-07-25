package de.intarsys.tools.progress;

/**
 * A nested {@link IProgressMonitor} that can create a "view" on a section of
 * work within the parent {@link IProgressMonitor}. The nested monitor can
 * enumerate its progress independently of the parent progress. The nested
 * progress is mapped directly on the parent.
 * 
 */
public class SubProgressMonitor extends BaseProgressMonitor {

	private final IProgressMonitor parent;

	private float parentWork;

	private float parentWorked;

	private float scale;

	public SubProgressMonitor(IProgressMonitor parent, float parentWork) {
		super();
		this.parent = parent;
		this.parentWork = parentWork;
		this.parentWorked = 0;
	}

	@Override
	public void begin(String name, float totalWork) {
		super.begin(name, totalWork);
		scale = totalWork <= 0 ? 0 : parentWork / totalWork;
		getParent().subTask(name);
	}

	protected String createParentSubTask() {
		String tempPrefix = getTaskName();
		String tempSuffix = getSubTaskName();
		if (tempSuffix == null) {
			return tempPrefix;
		} else if (tempPrefix == null) {
			return tempSuffix;
		} else {
			tempPrefix = tempPrefix + " - "; //$NON-NLS-1$
			if (tempSuffix.startsWith(tempPrefix)) {
				return tempSuffix;
			} else {
				return tempPrefix + tempSuffix;
			}
		}
	}

	@Override
	public void end() {
		super.end();
		// maybe client missed "begin" - lets propagate rest anyway
		if (parentWork > parentWorked) {
			workedParent(parentWork - parentWorked);
		}
	}

	public IProgressMonitor getParent() {
		return parent;
	}

	public double getParentWork() {
		return parentWork;
	}

	@Override
	public boolean isCancelled() { // NOSONAR needs no synchronization
		return parent.isCancelled();
	}

	protected void setParentWork(float parentWork) {
		this.parentWork = parentWork;
	}

	@Override
	public void subTask(String name) {
		super.subTask(name);
		getParent().subTask(createParentSubTask());
	}

	@Override
	public void worked(float work) {
		super.worked(work);
		float realWork = scale * work;
		workedParent(realWork);
	}

	protected void workedParent(float realWork) {
		// avoid overdraw parent quota
		realWork = Math.min(realWork, parentWork - parentWorked);
		if (realWork > 0) {
			parentWorked += realWork;
			parent.worked(realWork);
		}
	}

}
