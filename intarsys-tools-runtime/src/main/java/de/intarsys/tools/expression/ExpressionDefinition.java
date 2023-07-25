package de.intarsys.tools.expression;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import de.intarsys.tools.bean.IBeanInstallationInstruction;
import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.reflect.FieldException;
import de.intarsys.tools.reflect.IAccessSupport;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * Support the dynamic definition of a new expression in the {@link ExpressionEvaluator}.
 *
 * <p>
 * Note that this class requires the {@link ExpressionEvaluator global expression evaluator} to implement
 * {@link IAccessSupport}! Otherwise, the definition has no effect except for a warning at runtime.
 */
public class ExpressionDefinition implements IBeanInstallationInstruction {
	private static final ILogger Log = LogTools.getLogger(ExpressionDefinition.class);

	private String name;
	private Object value;
	private Set<Mode> modes;

	/**
	 * Creates a definition that will be applied in all {@link Mode}s.
	 *
	 * @param name name of the expression
	 * @param value value of the expression
	 */
	public ExpressionDefinition(String name, Object value) {
		this(name, value, EnumSet.allOf(Mode.class));
	}

	/**
	 * Creates a definition that will be applied in the given modes.
	 *
	 * @param name name of the expression
	 * @param value value of the expression
	 * @param firstMode first mode
	 * @param moreModes more modes
	 */
	public ExpressionDefinition(String name, Object value, Mode firstMode, Mode... moreModes) {
		this(name, value, EnumSet.of(firstMode, moreModes));
	}

	public ExpressionDefinition(String name, Object value, Set<Mode> modes) {
		this.name = name;
		this.value = value;
		this.modes = EnumSet.copyOf(modes);
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	@PostConstruct
	public void install() {
		modes.forEach(this::install);
	}

	private void install(Mode mode) {
		IStringEvaluator resolver = ExpressionEvaluator.get(mode);
		if (resolver instanceof IAccessSupport accessSupport) {
			try {
				accessSupport.setValue(getName(), getValue());
			} catch (FieldException e) {
				Log.warn("expression evaluator ({}) write error, cannot define {} ({})", //$NON-NLS-1$
						mode, getName(), ExceptionTools.getMessage(e));
			}
		} else {
			Log.warn("expression evaluator ({}) not writable, cannot define {}", mode, getName()); //$NON-NLS-1$
		}
	}
}