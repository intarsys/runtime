package de.intarsys.tools.bean;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * This JNDI Context implementation exposes the {@link BeanContainer} through
 * the naming API.
 */
public class BeanContext implements Context {

	private static final String PREFIX_JAVACOMP = "java:comp/";

	private static final String PATH_ENV = "env/";

	public BeanContext() {
		super();
	}

	@Override
	public Object addToEnvironment(String propName, Object propVal) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public void bind(Name name, Object obj) throws NamingException {
		bind(name.toString(), obj);
	}

	@Override
	public void bind(String name, Object obj) throws NamingException {
		BeanContainer.get().registerBean(stripPrefix(name), null, obj);
	}

	@Override
	public void close() throws NamingException {
		// not required
	}

	@Override
	public Name composeName(Name name, Name prefix) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public String composeName(String name, String prefix) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public Context createSubcontext(String name) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public Object lookup(Name name) throws NamingException {
		return lookup(name.toString());
	}

	@Override
	public Object lookup(String name) throws NamingException {
		return BeanContainer.get().lookupBean(stripPrefix(name), Object.class);
	}

	@Override
	public Object lookupLink(Name name) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public void rebind(Name name, Object obj) throws NamingException {
		rebind(name.toString(), obj);
	}

	@Override
	public void rebind(String name, Object obj) throws NamingException {
		bind(name, obj);
	}

	@Override
	public Object removeFromEnvironment(String propName) throws NamingException {
		throw new NamingException("function not supported");
	}

	@Override
	public void rename(Name oldName, Name newName) throws NamingException {
		rename(oldName.toString(), newName.toString());
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
		Object obj = lookup(oldName);
		if (obj == null) {
			return;
		}
		unbind(oldName);
		bind(newName, obj);
	}

	protected String stripPrefix(String path) {
		String myPath = path;
		if (myPath.startsWith(PREFIX_JAVACOMP)) {
			myPath = myPath.substring(PREFIX_JAVACOMP.length());
			if (myPath.startsWith(PATH_ENV)) {
				myPath = myPath.substring(PATH_ENV.length());
			}
		}
		return myPath;
	}

	@Override
	public void unbind(Name name) throws NamingException {
		unbind(name.toString());
	}

	@Override
	public void unbind(String name) throws NamingException {
		BeanContainer.get().unregisterBean(stripPrefix(name));
	}

}
