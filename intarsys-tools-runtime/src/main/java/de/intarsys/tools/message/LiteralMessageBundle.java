package de.intarsys.tools.message;

import java.util.Collections;
import java.util.Set;

/**
 * A message bundle without backing resources.
 * 
 */
public class LiteralMessageBundle implements IMessageBundle {

	@Override
	public String format(String pattern, Object... args) {
		return MessageTools.format(pattern, args);
	}

	@Override
	public Set<String> getCodes() {
		return Collections.emptySet();
	}

	@Override
	public IMessage getMessage(String code, Object... args) {
		return MessageTools.createMessage(code, code, args);
	}

	@Override
	public String getName() {
		return "literal";
	}

	@Override
	public String getPattern(String code) {
		return code;
	}

	@Override
	public String getString(String code, Object... args) {
		return getMessage(code, args).getString();
	}

}
