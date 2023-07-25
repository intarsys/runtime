package de.intarsys.tools.component;

public class Foo {

	private boolean closed;

	public void close() {
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}

}
