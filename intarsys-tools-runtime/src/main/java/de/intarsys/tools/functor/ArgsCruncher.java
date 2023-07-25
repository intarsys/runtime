package de.intarsys.tools.functor;

import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.Mode;
import de.intarsys.tools.expression.TemplateEvaluator;
import de.intarsys.tools.functor.ArgTools.IBindingProcessor;
import de.intarsys.tools.functor.IArgs.IBinding;

/**
 * A functor that creates and manipulates input {@link IArgs} based on
 * instructions it receives.
 *
 * <pre>
 * -input
 * -- args The initial args we are working on. The arguments are manipulated in place.
 * -- instructions The instructions that are applied to "args" (in order of execution)
 * --- remove The {@link IArgs} substructure that gets removed
 * --- set The {@link IArgs} substructure that gets simply set
 * --- expand The {@link IArgs} substructure that gets expanded
 * </pre>
 *
 */
public class ArgsCruncher implements IFunctor<IArgs> {
	private Mode evaluationMode;

	public ArgsCruncher() {
		this(Mode.UNTRUSTED);
	}

	public ArgsCruncher(Mode evaluationMode) {
		this.evaluationMode = evaluationMode;
	}

	protected void applyExpand(final IArgs target, IArgs ins) {
		if (ins == null) {
			return;
		}
		final IStringEvaluator evaluator = TemplateEvaluator.get(evaluationMode);
		ArgTools.visitNamedBindings("", ins, new IBindingProcessor() {
			@Override
			public Object visitArgs(String path, IArgs args) {
				return null;
			}

			@Override
			public Object visitBinding(String path, IArgs args, IBinding binding) { // NOSONAR
				if (binding.getValue() instanceof IArgs) {
					return null;
				}
				IArgs tempArgs = ArgTools.getArgs(target, path, null);
				if (tempArgs == null) {
					return null;
				}
				ArgTools.expandDeep(tempArgs, binding.getName(), evaluator);
				return null;
			}
		});
	}

	protected void applyRemove(final IArgs target, IArgs ins) {
		if (ins == null) {
			return;
		}
		ArgTools.visitNamedBindings("", ins, new IBindingProcessor() {

			@Override
			public Object visitArgs(String path, IArgs args) {
				return null;
			}

			@Override
			public Object visitBinding(String path, IArgs args, IBinding binding) { // NOSONAR
				if (binding.getValue() instanceof IArgs) {
					return null; // NOSONAR
				}
				IArgs tempArgs = ArgTools.getArgs(target, path, null);
				if (tempArgs == null) {
					return null; // NOSONAR
				}
				tempArgs.undefine(binding.getName());
				return null; // NOSONAR
			}
		});
	}

	protected void applySet(IArgs target, IArgs ins) {
		if (ins == null) {
			return;
		}
		ArgTools.putAllDeep(target, ins);
	}

	@Override
	public IArgs perform(IFunctorCall call) throws FunctorException {
		IArgs args = call.getArgs();
		IArgs resultArgs = ArgTools.getArgs(args, "args", null);
		applyRemove(resultArgs, ArgTools.getArgs(args, "remove", null));
		applySet(resultArgs, ArgTools.getArgs(args, "set", null));
		applyExpand(resultArgs, ArgTools.getArgs(args, "expand", null));
		return resultArgs;
	}
}
