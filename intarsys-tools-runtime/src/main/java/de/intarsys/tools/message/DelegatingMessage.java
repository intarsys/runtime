package de.intarsys.tools.message;

/**
 * A wrapper for {@link IMessage} implementations.
 * 
 */
public abstract class DelegatingMessage implements IMessage {

	private final IMessage message;

	protected DelegatingMessage(IMessage message) {
		super();
		this.message = message;
	}

	@Override
	public Object getArgumentAt(int index) {
		return message.getArgumentAt(index);
	}

	@Override
	public int getArgumentSize() {
		return message.getArgumentSize();
	}

	@Override
	public String getCode() {
		return message.getCode();
	}

	@Override
	public String getPattern() {
		return message.getPattern();
	}

	@Override
	public String getString() {
		return message.getString();
	}

}
