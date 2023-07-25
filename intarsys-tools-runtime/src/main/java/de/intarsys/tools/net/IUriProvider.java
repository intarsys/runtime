package de.intarsys.tools.net;

import java.net.URI;

/**
 * An object that can provide a {@link URI}.
 * 
 * This is useful when a component acts on URIs but needs some contextual
 * information regarding the deployment and the container setup.
 * 
 */
public interface IUriProvider {

	/**
	 * The {@link URI}.
	 * 
	 * @return
	 */
	public URI getUri();

}
