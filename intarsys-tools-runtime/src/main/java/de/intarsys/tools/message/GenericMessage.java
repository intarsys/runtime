package de.intarsys.tools.message;

import java.util.Arrays;

public class GenericMessage implements IMessage {

	private final IMessageBundle bundle;
	private final String code;
	private final Object[] args;
	private final Object[] formattedArgs;

	public GenericMessage(IMessageBundle bundle, String code, Object[] args) {
		super();
		this.bundle = bundle;
		this.code = code;
		if (args != null) {
			Object[] tempArgs = args;
			Object[] tempFormattedArgs = args;
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof FormattedObject) {
					if (tempArgs == tempFormattedArgs) {
						tempArgs = Arrays.copyOf(args, args.length);
						tempFormattedArgs = Arrays.copyOf(args, args.length);
					}
					tempArgs[i] = ((FormattedObject) args[i]).getObject();
					tempFormattedArgs[i] = ((FormattedObject) args[i]).getFormattedObject();
				}
			}
			this.args = tempArgs;
			this.formattedArgs = tempFormattedArgs;
		} else {
			this.args = null;
			this.formattedArgs = null;
		}
	}

	@Override
	public Object getArgumentAt(int index) {
		if (args == null || index < 0 || index >= args.length) {
			return null;
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

	public IMessageBundle getBundle() {
		return bundle;
	}

	@Override
	public String getCode() {
		return code;
	}

	public Object[] getFormattedArgs() {
		return formattedArgs;
	}

	/**
	 * Old style code access. May be referenced from scripting code.
	 * 
	 * @return The message code
	 */
	public String getKey() {
		return getCode();
	}

	@Override
	public String getPattern() {
		return getBundle().getPattern(code);
	}

	@Override
	public String getString() {
		return getBundle().getString(code, formattedArgs);
	}

	@Override
	public String toString() {
		return getString();
	}

}