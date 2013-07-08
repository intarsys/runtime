package de.intarsys.tools.functor.common;

import de.intarsys.tools.functor.ArgumentDeclaration;
import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IDeclarationBlock;
import de.intarsys.tools.functor.IDeclarationElement;
import de.intarsys.tools.functor.IDeclarationSupport;
import de.intarsys.tools.string.Converter;

public class DeclarationTools {

	public static Object getDeclarationDefaultValue(
			IDeclarationBlock declarationBlock, String path, IArgs scope) {
		ArgumentDeclaration declaration = (ArgumentDeclaration) getDeclarationElement(
				declarationBlock, path);
		if (declaration == null) {
			// no such declaration
			return null;
		}
		try {
			return declaration.getDefaultValue(scope);
		} catch (FunctorInvocationException e) {
			return null;
		}
	}

	public static boolean getDeclarationDefaultValueAsBoolean(
			IDeclarationBlock declarationBlock, String path, IArgs scope) {
		Object value = DeclarationTools.getDeclarationDefaultValue(
				declarationBlock, path, scope);
		if (value instanceof Boolean) {
			return (Boolean) value;
		} else {
			return Converter.asBoolean(String.valueOf(value), false);
		}
	}

	public static int getDeclarationDefaultValueAsInt(
			IDeclarationBlock declarationBlock, String path, IArgs scope) {
		Object value = DeclarationTools.getDeclarationDefaultValue(
				declarationBlock, path, scope);
		if (value instanceof Number) {
			return ((Number) value).intValue();
		} else {
			return Converter.asInteger(String.valueOf(value), 0);
		}
	}

	public static String getDeclarationDefaultValueAsString(
			IDeclarationBlock declarationBlock, String path, IArgs scope) {
		Object value = DeclarationTools.getDeclarationDefaultValue(
				declarationBlock, path, scope);
		if (value == null) {
			return null;
		} else {
			return String.valueOf(value);
		}
	}

	public static IDeclarationElement getDeclarationElement(
			IDeclarationBlock declarationBlock, String path) {
		IDeclarationElement result = null;
		String[] segments = path.split("\\."); //$NON-NLS-1$
		IDeclarationBlock currentBlock = declarationBlock;
		for (int i = 0; i < segments.length; i++) {
			String segment = segments[i];
			result = currentBlock.getDeclarationElement(segment);
			if (result instanceof IDeclarationBlock) {
				currentBlock = (IDeclarationBlock) result;
			} else if (result instanceof IDeclarationSupport) {
				currentBlock = ((IDeclarationSupport) result)
						.getDeclarationBlock();
			} else {
				if (i < segments.length - 1) {
					return null;
				}
			}
		}
		return result;
	}

	public static IDeclarationElement removeDeclarationElement(
			IDeclarationBlock declarationBlock, String path) {
		IDeclarationElement result = null;
		String[] segments = path.split("\\."); //$NON-NLS-1$
		IDeclarationBlock currentBlock = declarationBlock;
		int i = 0;
		while (i < segments.length) {
			String segment = segments[i];
			result = currentBlock.getDeclarationElement(segment);
			i++;
			if (i < segments.length) {
				if (result instanceof IDeclarationBlock) {
					currentBlock = (IDeclarationBlock) result;
				} else if (result instanceof IDeclarationSupport) {
					currentBlock = ((IDeclarationSupport) result)
							.getDeclarationBlock();
				} else {
					return null;
				}
			}
		}
		currentBlock.removeDeclarationElement(result);
		return result;
	}
}
