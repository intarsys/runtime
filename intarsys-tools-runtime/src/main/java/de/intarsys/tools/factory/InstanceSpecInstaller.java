package de.intarsys.tools.factory;

import javax.annotation.PostConstruct;

import de.intarsys.tools.exception.InitializationException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ObjectCreationException;

public class InstanceSpecInstaller {

	private Object factory;

	private IArgs args;

	public IArgs getArgs() {
		return args;
	}

	public Object getFactory() {
		return factory;
	}

	@PostConstruct
	public void install() {
		InstanceSpec spec = InstanceSpec.createFromFactory(Object.class, getFactory(), getArgs());
		try {
			/*
			 * when used in Spring, this object is *not* instrumented
			 */
			spec.createInstance();
		} catch (ObjectCreationException e) {
			throw new InitializationException(e);
		}
	}

	public void setArgs(IArgs args) {
		this.args = args;
	}

	public void setFactory(Object factory) {
		this.factory = factory;
	}

}
