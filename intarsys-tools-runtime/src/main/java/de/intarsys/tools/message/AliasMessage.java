package de.intarsys.tools.message;

public class AliasMessage extends DelegatingMessage {

	private final String code;
	private final Object[] args;

	public AliasMessage(String code, IMessage message, Object... args) {
		super(message);
		this.code = code;
		this.args = args;
	}

	@Override
	public Object getArgumentAt(int index) {
		if (args == null || index < 0 || index >= args.length) {
			return null;
		}
		if (args[index] instanceof FormattedObject) {
			return ((FormattedObject) args[index]).getObject();
		}
		return args[index];
	}

	@Override
	public int getArgumentSize() {
		if (args == null) {
			return 0;
		}
		return args.length;
	}

	@Override
	public String getCode() {
		return code;
	}

}
