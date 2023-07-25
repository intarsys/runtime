package de.intarsys.tools.infoset;

import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * Access to some context wide {@link IElement} instance for configuration
 * purposes.
 * 
 */
@ServiceImplementation(EmptyElementConfigurationProvider.class)
public interface IElementConfigurationProvider {

	public IElement getRootElement();

}
