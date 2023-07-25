package de.intarsys.tools.reflect;

import de.intarsys.tools.functor.IArgs;

public class Predicates {

	public static class And implements IMethodHandler {
		private final IMethodHandler[] operators;

		public And(IMethodHandler[] operators) {
			super();
			this.operators = operators;
		}

		@Override
		public Object invoke(Object receiver, IArgs args) throws MethodException {
			for (IMethodHandler handler : operators) {
				if (!Boolean.TRUE.equals(handler.invoke(receiver, args))) {
					return false;
				}
			}
			return true;
		}
	}

	public static class Not implements IMethodHandler {
		private final IMethodHandler operator;

		public Not(IMethodHandler operator) {
			super();
			this.operator = operator;
		}

		@Override
		public Object invoke(Object receiver, IArgs args) throws MethodException {
			return Boolean.FALSE.equals(operator.invoke(receiver, args));
		}
	}

	public static class Or implements IMethodHandler {
		private final IMethodHandler[] operators;

		public Or(IMethodHandler[] operators) {
			super();
			this.operators = operators;
		}

		@Override
		public Object invoke(Object receiver, IArgs args) throws MethodException {
			for (IMethodHandler handler : operators) {
				if (Boolean.TRUE.equals(handler.invoke(receiver, args))) {
					return true;
				}
			}
			return false;
		}
	}

	public static final IMethodHandler FALSE = new IMethodHandler() {
		@Override
		public Object invoke(Object receiver, IArgs args) throws MethodException {
			return false;
		}
	};

	public static final IMethodHandler TRUE = new IMethodHandler() {
		@Override
		public Object invoke(Object receiver, IArgs args) throws MethodException {
			return true;
		}
	};

	public static IMethodHandler and(IMethodHandler... op) {
		if (op == null || op.length == 0) {
			return FALSE;
		}
		if (op.length == 1) {
			return op[0];
		}
		return new And(op);
	}

	public static IMethodHandler not(IMethodHandler op) {
		return new Not(op);
	}

	public static IMethodHandler or(IMethodHandler... op) {
		if (op == null || op.length == 0) {
			return TRUE;
		}
		if (op.length == 1) {
			return op[0];
		}
		return new Or(op);
	}

	private Predicates() {
	}

}