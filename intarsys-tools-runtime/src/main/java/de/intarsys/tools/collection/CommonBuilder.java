package de.intarsys.tools.collection;

/**
 * Common superclass for creating a container builder.
 */
public abstract class CommonBuilder {

	private final CommonBuilder parent;

	protected CommonBuilder(CommonBuilder parent) {
		super();
		this.parent = parent;
	}

	public abstract Object build();

	public CommonBuilder end() {
		return parent;
	}

	public final <B> B end(Class<B> clazz) { // NOSONAR
		return (B) end();
	}

	public CommonBuilder getParent() {
		return parent;
	}

}
