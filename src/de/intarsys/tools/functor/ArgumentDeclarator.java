package de.intarsys.tools.functor;

import java.util.Iterator;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.functor.IArgs.IBinding;

/**
 * An argument declaration strategy that modifies its input {@link IArgs}
 * according to the {@link IDeclaration}.
 * <p>
 * Every {@link ArgumentDeclaration} instance is applied to the input args.
 * <ul>
 * <li>A name is associated</li>
 * <li>An optional default is assigned</li>
 * <li>An optional type casting is applied</li>
 * </ul>
 * <p>
 * The {@link ArgumentDeclarator} knows three declaration "modes".
 * <ul>
 * <li>!strict (default)</li> Any input args are preserved, declared args are
 * applied or added.
 * <li>strict && !lazy</li> Only input args that occur in the declaration are
 * preserved, all others cleared.
 * <li>strict && lazy</li> If there are no {@link ArgumentDeclaration}
 * declarations, the input args are preserved. If there is any
 * {@link ArgumentDeclaration}, the behavior switches to "strict".
 * </ul>
 * 
 */
public class ArgumentDeclarator {

	private boolean lazy = true;

	private boolean strict = false;

	private static final Object UNDEFINED = new Object();

	public ArgumentDeclarator() {
	}

	public ArgumentDeclarator(boolean strict, boolean lazy) {
		super();
		this.strict = strict;
		this.lazy = lazy;
	}

	protected void apply(ArgumentDeclaration declaration, int index,
			IArgs args, IArgs targetArgs, IArgs scope)
			throws DeclarationException {
		String name = declaration.getName();
		Class<?> type = declaration.getType();
		IDeclarationBlock declarationBlock = declaration.getDeclarationBlock();
		//
		Object value = UNDEFINED;
		IBinding targetBinding = null;
		if (".".equals(name)) {
			type = IArgs.class;
		} else {
			IBinding binding = args.declare(name);
			targetBinding = binding;
			if (targetArgs != args) {
				targetBinding = targetArgs.declare(name);
			} else {
				targetBinding = binding;
			}
			if (binding.isDefined()) {
				value = binding.getValue();
			}
		}
		if (value == UNDEFINED) {
			if (declaration.isDefaultDefined()) {
				try {
					value = declaration.getDefaultValue(scope);
				} catch (FunctorInvocationException e) {
					throw new DeclarationException(e);
				}
			} else {
				if (declarationBlock != null && declarationBlock.size() > 0) {
					value = Args.create();
				}
			}
		}
		if (value != UNDEFINED) {
			if (value instanceof IArgs && declarationBlock != null
					&& declarationBlock.size() > 0) {
				new ArgumentDeclarator(strict, lazy).apply(declarationBlock,
						(IArgs) value);
			}
			try {
				if (type == IArgs.class) {
					// todo write correct converter
					value = ArgTools.toArgs(value);
				} else if (type != Object.class) {
					value = ConverterRegistry.get().convert(value, type);
				}
			} catch (ConversionException e) {
				throw new DeclarationException(
						"Unable to convert value for argument '" + name
								+ "' to type '" + type + "'", e);
			}
			if (targetBinding == null) {
				if (value instanceof IArgs) {
					ArgTools.putAll(targetArgs, (IArgs) value);
				}
			} else {
				targetBinding.setValue(value);
			}
		}
	}

	protected void apply(IDeclaration declaration, int index, IArgs args,
			IArgs targetArgs, IArgs scope) throws DeclarationException {
		if (declaration instanceof ArgumentDeclaration) {
			ArgumentDeclaration argumentDeclaration = (ArgumentDeclaration) declaration;
			apply(argumentDeclaration, index, args, targetArgs, scope);
		} else if (declaration instanceof IDeclarationBlock) {
			apply((IDeclarationBlock) declaration, args, targetArgs, scope);
		} else if (declaration instanceof IDeclarationSupport) {
			apply(((IDeclarationSupport) declaration).getDeclarationBlock(),
					args, targetArgs, scope);
		}
	}

	public IArgs apply(IDeclarationBlock declarationBlock, IArgs args)
			throws DeclarationException {
		IArgs targetArgs = args;
		if (strict) {
			targetArgs = new Args();
		}
		apply(declarationBlock, args, targetArgs, args);
		if (strict) {
			Iterator<IBinding> bindings = targetArgs.bindings();
			if (!lazy || (lazy && bindings.hasNext())) {
				args.clear();
				while (bindings.hasNext()) {
					IBinding binding = bindings.next();
					if (binding.getName() != null) {
						args.put(binding.getName(), binding.getValue());
					}
				}
			}
		}
		return args;
	}

	protected void apply(IDeclarationBlock declarationBlock, IArgs args,
			IArgs targetArgs, IArgs scope) throws DeclarationException {
		IDeclaration[] declarations = declarationBlock.getDeclarationElements();
		for (int i = 0; i < declarations.length; i++) {
			apply(declarations[i], i, args, targetArgs, scope);
		}
	}
}
