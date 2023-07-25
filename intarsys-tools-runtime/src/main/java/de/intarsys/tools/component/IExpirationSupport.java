package de.intarsys.tools.component;

/**
 * Mark a resource that has a restricted lifetime.
 * <p>
 * {@link ExpirationPredicate} may be a good starting point for implementing
 * this behavior.
 * 
 */
public interface IExpirationSupport {

	/**
	 * true if this resource is no longer valid (has expired).
	 * 
	 * @return
	 */
	public boolean isExpired();

	/**
	 * Mark the resource as just having been used. This is used for example in
	 * implementing a idle timeout behavior.
	 */
	public void touch();

}
