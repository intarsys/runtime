package de.intarsys.tools.reflect;

import de.intarsys.tools.functor.IArgs;

public class Predicates {

	public static class Not implements IMethodHandler {
		private final IMethodHandler operator;

		public Not(IMethodHandler operator) {
			super();
			this.operator = operator;
		}

		public Object invoke(Object receiver, IArgs args)
				throws MethodInvocationException {
			return operator.invoke(receiver, args) == Boolean.FALSE;
		}
	}

	public static class And implements IMethodHandler {
		private final IMethodHandler[] operators;

		public And(IMethodHandler[] operators) {
			super();
			this.operators = operators;
		}

		public Object invoke(Object receiver, IArgs args)
				throws MethodInvocationException {
			for (IMethodHandler handler : operators) {
				if (handler.invoke(receiver, args) != Boolean.TRUE) {
					return false;
				}
			}
			return true;
		}
	}

	public static class Or implements IMethodHandler {
		private final IMethodHandler[] operators;

		public Or(IMethodHandler[] operators) {
			super();
			this.operators = operators;
		}

		public Object invoke(Object receiver, IArgs args)
				throws MethodInvocationException {
			for (IMethodHandler handler : operators) {
				if (handler.invoke(receiver, args) == Boolean.TRUE) {
					return true;
				}
			}
			return false;
		}
	}

	public static final IMethodHandler FALSE = new IMethodHandler() {
		public Object invoke(Object receiver, IArgs args)
				throws MethodInvocationException {
			return false;
		}
	};

	public static final IMethodHandler TRUE = new IMethodHandler() {
		public Object invoke(Object receiver, IArgs args)
				throws MethodInvocationException {
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

	public static IMethodHandler or(IMethodHandler... op) {
		if (op == null || op.length == 0) {
			return TRUE;
		}
		if (op.length == 1) {
			return op[0];
		}
		return new Or(op);
	}

	public static IMethodHandler not(IMethodHandler op) {
		return new Not(op);
	}

}