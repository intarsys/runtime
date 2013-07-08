package de.intarsys.tools.locator.trusted;

import java.io.IOException;
import java.io.OutputStream;

import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * This {@link ILocator} guarantees that a change to its physical resource while
 * its is used by application code will be detected or healed.
 * <p>
 * This behavior is for example important for an application creating a
 * signature for a document, where it is crucial that no one will change the
 * data to be signed while the user verifies and accepts the original version.
 * <p>
 * An {@link ITrustedLocator} guarantess that
 * <ul>
 * <li>Any input stream acquired after the first input stream will read exactly
 * the same data OR fail.</li>
 * <li>If the client code changes data via an {@link OutputStream} or
 * {@link IRandomAccess} acquired from this {@link ITrustedLocator}, the new
 * data stream will become valid at least after a "close" or "flush" operation.
 * After the data is written to the physical resource it will contain exactly
 * the new data where changed and exactly the old data where not. Otherwise the
 * write will fail. Subsequent input stream will read the new data as described
 * above.</li>
 * <li>These constraints provide secure access from on the first read/write
 * access to the resource. Sometimes a resource must be "freezed" before a
 * physical access is done. This can be achieved by calling "checkpoint". An
 * access to the resource after "checkpoint" behaves as described above, but the
 * {@link ITrustedLocator} is free to implement "checkpoint" in a more
 * performant way.</li>
 * </ul>
 * 
 */
public interface ITrustedLocator extends ILocator {

	/**
	 * Make sure current state of the resource is still the same as seen with
	 * last "checkpoint". If this is called without any prior state information
	 * available, an implicit initial "checkpoint" is made. Subsequent access to
	 * the resources data will see exactly the state as seen at this moment or
	 * FAIL.
	 * <p>
	 * Most likely the {@link ITrustedLocator} will take a hash value here which
	 * it uses to compare when a physical access is made later. Be sure to
	 * understand the documentation on the measures taken by a concrete
	 * implementation as this is crucial for security level and performance.
	 * 
	 * @throws IOException
	 */
	public void checkpoint() throws IOException;

	/**
	 * Check if <code>locator</code> references data that is equal to the
	 * one referenced by this.
	 * 
	 * @param locator
	 *            The {@link ILocator} to the data to be checked against this.
	 * @throws IOException
	 */
	public void ensureEqual(ILocator locator) throws IOException;

}
