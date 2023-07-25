package de.intarsys.tools.functor;

/**
 * Interface for objects supporting the setting of arguments.
 * 
 */
public interface IArgsAccess extends IArgsSupport {

	/**
	 * Assign the arguments.
	 */
	public void setArgs(IArgs args);
}
