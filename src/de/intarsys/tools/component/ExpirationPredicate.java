package de.intarsys.tools.component;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementSerializable;
import de.intarsys.tools.string.StringTools;

/**
 * A "predicate" that supports the definition of an expiration for an objects
 * lifecycle.
 * 
 */
abstract public class ExpirationPredicate implements IExpirationSupport,
		IElementSerializable {

	protected static ExpirationPredicate basicCreate(IArgs args, String role,
			ExpirationPredicate defaultValue) {
		String argName;
		argName = ArgTools.prefix(role, "type");
		String tempType = ArgTools.getString(args, argName, null);
		argName = ArgTools.prefix(role, "value");
		Object tempValue = ArgTools.getObject(args, argName, null);
		ExpirationPredicate result = create(tempType, tempValue, null);
		if (result == null) {
			result = createDeprecated(args);
		}
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	static public ExpirationPredicate create(IArgs args,
			ExpirationPredicate defaultValue) {
		return create(args, "expire", defaultValue);
	}

	public static ExpirationPredicate create(IArgs args, String role,
			ExpirationPredicate defaultValue) {
		if (StringTools.isEmpty(role)) {
			return null;
		}
		Object tempObject = ArgTools.getObject(args, role, null);
		if (tempObject != null) {
			if (tempObject instanceof ExpirationPredicate) {
				return (ExpirationPredicate) tempObject;
			}
			IArgs tempArgs = ArgTools.toArgs(tempObject);
			if (tempArgs != null) {
				return basicCreate(tempArgs, null, defaultValue);
			}
			return null;
		}
		return basicCreate(args, role, defaultValue);
	}

	public static ExpirationPredicate create(String type, Object value,
			ExpirationPredicate defaultValue) {
		if (type == null) {
			return defaultValue;
		} else if ("after".equals(type)) {
			return new ExpireAfter(toLong(value, 0));
		} else if ("at".equals(type)) {
			return new ExpireAt(toLong(value, 0));
		} else if ("timeout".equals(type)) {
			return new ExpireTimeout(toLong(value, 0));
		} else if ("usage".equals(type)) {
			return new ExpireUsage((int) toLong(value, 0));
		} else if ("never".equals(type)) {
			return new ExpireNever();
		} else if ("and".equals(type)) {
			IArgs args = ArgTools.toArgs(value);
			if (args != null) {
				ExpirationPredicate op1 = create(args, "op1", null);
				ExpirationPredicate op2 = create(args, "op2", null);
				return ExpirationPredicate.createAnd(op1, op2);
			}
			return null;
		} else if ("or".equals(type)) {
			IArgs args = ArgTools.toArgs(value);
			if (args != null) {
				ExpirationPredicate op1 = create(args, "op1", null);
				ExpirationPredicate op2 = create(args, "op2", null);
				return ExpirationPredicate.createOr(op1, op2);
			}
			return null;
		}
		return defaultValue;
	}

	static public ExpirationPredicate createAnd(ExpirationPredicate op1,
			ExpirationPredicate op2) {
		if (op1 == null) {
			return op2;
		}
		if (op2 == null) {
			return op1;
		}
		return new ExpireAnd(op1, op2);
	}

	protected static ExpirationPredicate createDeprecated(IArgs args) {
		ExpirationPredicate result = null;
		int tempInt;
		Object tempValue;
		tempInt = ArgTools.getInt(args, "expireTimeout", -1);
		if (tempInt != -1) {
			result = ExpirationPredicate.createOr(result, new ExpireTimeout(
					tempInt));
		}
		tempInt = ArgTools.getInt(args, "expireAfter", -1);
		if (tempInt != -1) {
			result = ExpirationPredicate.createOr(result, new ExpireAfter(
					tempInt));
		}
		tempInt = ArgTools.getInt(args, "expireAt", -1);
		if (tempInt != -1) {
			result = ExpirationPredicate
					.createOr(result, new ExpireAt(tempInt));
		}
		tempInt = ArgTools.getInt(args, "expireUsage", -1);
		if (tempInt != -1) {
			result = ExpirationPredicate.createOr(result, new ExpireUsage(
					tempInt));
		}
		tempValue = ArgTools.getObject(args, "expireNever", null);
		if (tempValue != null) {
			result = ExpirationPredicate.createOr(result, new ExpireNever());
		}
		return result;
	}

	static public ExpirationPredicate createOr(ExpirationPredicate op1,
			ExpirationPredicate op2) {
		if (op1 == null) {
			return op2;
		}
		if (op2 == null) {
			return op1;
		}
		return new ExpireOr(op1, op2);
	}

	protected static long toLong(Object value, long defaultValue) {
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		if (value instanceof String) {
			try {
				return Long.parseLong(((String) value).trim());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	public IExpirationSupport and(ExpirationPredicate op2) {
		return new ExpireAnd(this, op2);
	}

	abstract public ExpirationPredicate copy();

	abstract public String getType();

	abstract public long getValue();

	public IExpirationSupport or(ExpirationPredicate op2) {
		return new ExpireOr(this, op2);
	}

	@Override
	public void serialize(IElement element)
			throws ElementSerializationException {
		element.setAttributeValue("type", getType());
		element.setAttributeValue("value", String.valueOf(getValue()));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	abstract protected void toString(StringBuilder sb);
}
