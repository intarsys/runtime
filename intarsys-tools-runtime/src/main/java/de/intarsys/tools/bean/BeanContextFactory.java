package de.intarsys.tools.bean;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * This JNDI Context Factory implementation creates a {@link BeanContext} giving
 * bean access through the naming API.
 */
public class BeanContextFactory implements InitialContextFactory {

	public BeanContextFactory() {
		super();
	}

	@Override
	public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
		BeanContext context = new BeanContext();
		return context;
	}

}
