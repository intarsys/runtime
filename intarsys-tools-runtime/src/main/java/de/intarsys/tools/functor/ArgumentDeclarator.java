package de.intarsys.tools.functor;

import java.util.Iterator;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.factory.InstanceSpec;
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
 * <li>!strict (default) &ndash; Any input args are preserved, declared args are
 * applied or added.</li>
 * <li>strict &amp;&amp; !lazy &ndash;  Only input args that occur in the declaration are
 * preserved, all others cleared.</li>
 * <li>strict &amp;&amp; lazy &ndash; If there are no {@link ArgumentDeclaration}
 * declarations, the input args are preserved. If there is any
 * {@link ArgumentDeclaration}, the behavior switches to "strict".</li>
 * </ul>
 * 
 */
public class ArgumentDeclarator {

	private static final Object UNDEFINED = new Object();

	private boolean lazy = true;

	private boolean strict;

	public ArgumentDeclarator() {
	}

	public ArgumentDeclarator(boolean strict, boolean lazy) {
		super();
		this.strict = strict;
		this.lazy = lazy;
	}

	protected void apply(ArgumentDeclaration declaration, IArgs args, IArgs targetArgs, IArgs scope)
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
			}
			if (binding.isDefined()) {
				value = binding.getValue();
			}
		}
		/**
		 * This special handling is necessary to handle "InstanceSpec" legacy
		 * style declarations. This should be handled in Args completely, but
		 * fails here when we have legacy format in declaration.
		 * 
		 * <pre>
		 * -) value is not IArgs -> fine 
		 * -) value is IArgs 
		 *   -) value is not InstanceSpec format -> fine 
		 *   -) value is InstanceSpec format 
		 *     -) "factory" is defined -> fine 
		 *     -) factory is undefined 
		 *       -) declaration has default -> assign factory
		 * </pre>
		 */
		if ((value instanceof IArgs) && Args.isInstanceSpec(value)) {
			if (declaration.isDefaultDefined()) {
				InstanceSpec spec = InstanceSpec.createFromArgs(Object.class, (IArgs) value);
				/*
				 * we should test for "undefined" instead, but we can't
				 */
				if (spec.getFactory() == null) {
					try {
						Object defaultValue = declaration.getDefaultValue(scope);
						if (defaultValue instanceof String) {
							spec.setFactory(defaultValue);
						}
					} catch (FunctorException e) {
						throw new DeclarationException(e);
					}
				}
			}
		} else if (value == UNDEFINED) {
			if (declaration.isDefaultDefined()) {
				try {
					value = declaration.getDefaultValue(scope);
					// hack - do not let escape literal value
					if (value instanceof IArgs) {
						value = ((IArgs) value).copy();
					}
				} catch (FunctorException e) {
					throw new DeclarationException(e);
				}
			} else {
				if (declarationBlock != null && declarationBlock.size() > 0) {
					value = Args.create();
				}
			}
		}
		if (value != UNDEFINED) {
			if (value instanceof IArgs && declarationBlock != null && declarationBlock.size() > 0) {
				new ArgumentDeclarator(strict, lazy).apply(declarationBlock, (IArgs) value);
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
						"Unable to convert value for argument '" + name + "' to type '" + type + "'", e);
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

	protected void apply(IDeclaration declaration, IArgs args, IArgs targetArgs, IArgs scope)
			throws DeclarationException {
		if (declaration instanceof ArgumentDeclaration) {
			ArgumentDeclaration argumentDeclaration = (ArgumentDeclaration) declaration;
			apply(argumentDeclaration, args, targetArgs, scope);
		} else if (declaration instanceof IDeclarationBlock) {
			apply((IDeclarationBlock) declaration, args, targetArgs, scope);
		} else if (declaration instanceof IDeclarationSupport) {
			apply(((IDeclarationSupport) declaration).getDeclarationBlock(), args, targetArgs, scope);
		}
	}

	public IArgs apply(IDeclarationBlock declarationBlock, IArgs args) throws DeclarationException {
		IArgs targetArgs = args;
		if (strict) {
			targetArgs = new Args();
		}
		apply(declarationBlock, args, targetArgs, args);
		if (strict) {
			Iterator<IBinding> bindings = targetArgs.bindings();
			if (!lazy || bindings.hasNext()) {
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

	protected void apply(IDeclarationBlock declarationBlock, IArgs args, IArgs targetArgs, IArgs scope)
			throws DeclarationException {
		IDeclaration[] declarations = declarationBlock.getDeclarationElements();
		for (int i = 0; i < declarations.length; i++) {
			apply(declarations[i], args, targetArgs, scope);
		}
	}
}
