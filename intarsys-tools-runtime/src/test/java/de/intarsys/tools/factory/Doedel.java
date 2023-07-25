package de.intarsys.tools.factory;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ObjectCreationException;

public class Doedel implements IFactory {

	private final String id;

	public Doedel() {
		this.id = getClass().getName();
	}

	public Doedel(String id) {
		super();
		this.id = id;
	}

	@Override
	public Object createInstance(IArgs args) throws ObjectCreationException {
		return new Diedel();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Class getResultType() {
		return Diedel.class;
	}

}
