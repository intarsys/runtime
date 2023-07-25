package de.intarsys.tools.valueholder;

public class NamedValue<T> implements INamedValue<T> {

	private final String name;

	private final T value;

	public NamedValue(String name, T value) {
		super();
		this.name = name;
		this.value = value;
	}

	@Override
	public T get() {
		return getValue();
	}

	@Override
	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}

	@Override
	public T set(T newValue) {
		throw new UnsupportedOperationException();
	}

}
