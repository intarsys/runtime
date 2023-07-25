package de.intarsys.tools.factory;

import java.util.Map;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * Iconification of instance creation, i.e. a request for an object instance. An
 * {@link InstanceSpec} either consists of the plain vanilla instance value or a
 * factory description which will be able to defer the construction.
 * 
 * <p>
 * There are various ways to create a valid {@link InstanceSpec}.
 * <ul>
 * <li>directly from an instance:
 * {@link InstanceSpec#createFromValue(Class, Object)}</li>
 * <li>from a factory and an argument set to the factory:
 * {@link InstanceSpec#createFromFactory(Class, Object, Object)}</li>
 * <li>from an argument set containing factory and its arguments in canonical
 * form: {@link #createFromArgs(Class, Object)}</li>
 * </ul>
 * Example:
 * 
 * <pre>
 * {
 *   factory: &lt;polymorphic factory object>,
 *   args: &lt;iargs structure>
 * }
 * </pre>
 * 
 * <pre>
 * {
 *   value: (R)&lt;any object>
 * }
 * </pre>
 * 
 * <ul>
 * <li>from an argument set containing the instance spec properties, designated
 * by a given role within the arguments:
 * {@link InstanceSpec#get(IArgs, String, Class, InstanceSpec)}</li>
 * </ul>
 * Example:
 * 
 * <pre>
 * canonical role based 
 * 
 * {
 *   &lt;role>: {
 *     factory: &lt;polymorphic factory object>,
 *     args: &lt;iargs structure>
 *   }
 * }
 * 
 * or
 * 
 * {
 *   &lt;role>: {
 *     value: (R)&lt;any object>
 *   }
 * }
 * </pre>
 * 
 * <pre>
 * creation shortcut factory based
 * 
 * {
 *   &lt;role>: "&lt;class name or factory id>"
 * }
 * </pre>
 * 
 * <pre>
 * creation shortcut instance based. Notice that it is 
 * impossible to access a string directly this way.
 *  
 * {
 *   &lt;role>: (R)&lt;any object>
 * }
 * </pre>
 * 
 * <pre>
 * role based, separated. deprecated
 * {
 *   &lt;role>: &lt;polymorphic factory>,
 *   &lt;role>Args: &lt;iargs structure>
 * }
 * </pre>
 */
public class InstanceSpec<R extends Object> {

	// review static final use
	public static final InstanceSpec<Object> EMPTY_SPEC = new InstanceSpec(Object.class, null, Args.create()) {

		@Override
		public IArgs getArgs() {
			return Args.create();
		}
	};

	protected static final String META_ENABLED = "enabled";

	public static final String ARG_ARGS = "args"; //$NON-NLS-1$

	public static final String ARG_META = "_meta"; //$NON-NLS-1$

	public static final String ARG_FACTORY = "factory"; //$NON-NLS-1$

	public static final String ARG_CLASS = "class"; //$NON-NLS-1$

	public static final String ARG_PROCESSOR = "processor"; //$NON-NLS-1$

	public static final String ARG_VALUE = "value"; //$NON-NLS-1$

	private static final Object UNDEFINED = new Object();

	/**
	 * Create an {@link InstanceSpec} based on the canonical {@link IArgs}
	 * structure.
	 * 
	 * <pre>
	 * {
	 *   factory: &lt;factory&gt;,
	 *   args: {
	 *     foo: "bar"
	 *   }
	 * }
	 * </pre>
	 * 
	 * The definition is allowed to be "partial", i.e. factory or args are
	 * missing. If all properties are missing (argsDef is an empty definition)
	 * null is returned.
	 * 
	 * @param clazz
	 * @param argsDef
	 * @return The {@link InstanceSpec} based on argsDef
	 */
	public static <R> InstanceSpec<R> createFromArgs(Class<R> clazz, Object argsDef) {
		IArgs args = ArgTools.toArgs(argsDef);
		if (args == null) {
			return null;
		}
		IArgs metaArgs = ArgTools.getArgs(args, ARG_META, null);
		Object value = ArgTools.getObject(args, ARG_VALUE, null);
		if (value == null) {
			Object myFactory = ArgTools.getObject(args, ARG_FACTORY, null);
			if (myFactory == null) {
				myFactory = ArgTools.getObject(args, ARG_PROCESSOR, null);
				if (myFactory == null) {
					myFactory = ArgTools.getObject(args, ARG_CLASS, null);
				}
			}
			myFactory = FactoryTools.cleanup(myFactory);
			IArgs myArgs = ArgTools.getArgs(args, ARG_ARGS, null);
			if (myFactory == null && myArgs == null) {
				return null;
			}
			return createFromFactory(clazz, args, myFactory, myArgs, metaArgs);
		} else {
			return createFromValue(clazz, args, value, metaArgs);
		}
	}

	protected static <R> InstanceSpec<R> createFromFactory(Class<R> clazz, IArgs container, Object factoryDef,
			Object argsDef, IArgs metaArgs) {
		factoryDef = FactoryTools.cleanup(factoryDef);
		IArgs args = ArgTools.toArgs(argsDef);
		if (args == null) {
			args = Args.create();
		}
		InstanceSpec<R> result = new InstanceSpec<>(clazz, container, factoryDef, args, UNDEFINED, metaArgs);
		return result;
	}

	/**
	 * Create an {@link InstanceSpec} based on the factory/argument combination.
	 * 
	 * The definition is allowed to be "partial", i.e. factory or args are
	 * missing.
	 * 
	 * @param clazz
	 *            The target class of the {@link InstanceSpec}
	 * 
	 * @param factoryDef
	 *            A polymorphic factory definition
	 * @param argsDef
	 *            A polymorphic args definition
	 * @return
	 */
	public static <R extends Object> InstanceSpec<R> createFromFactory(Class<R> clazz, Object factoryDef,
			Object argsDef) {
		return createFromFactory(clazz, Args.create(), factoryDef, argsDef, null);
	}

	/**
	 * Create an {@link InstanceSpec} as a copy of another template.
	 * 
	 * @param template
	 *            The original template {@link InstanceSpec}
	 * @return
	 */
	public static <R extends Object> InstanceSpec<R> createFromTemplate(InstanceSpec<R> template) {
		InstanceSpec<R> result = new InstanceSpec<>(template.getInstanceClass(), Args.create(), template.getFactory(),
				template.getArgs().copy(), UNDEFINED,
				template.getMetaArgs() == null ? null : template.getMetaArgs().copy());
		return result;
	}

	protected static <R> InstanceSpec<R> createFromValue(Class<R> clazz, IArgs container, Object value,
			IArgs metaArgs) {
		InstanceSpec<R> result = new InstanceSpec<>(clazz, container, null, Args.create(), value, metaArgs);
		return result;
	}

	public static <R extends Object> InstanceSpec<R> createFromValue(Class<R> clazz, Object value) {
		return createFromValue(clazz, Args.create(), value, null);
	}

	/**
	 * Create an {@code InstanceSpec} based on the factory/argument combination.
	 * 
	 * The definition is allowed to be "partial", i.e. factory or args are missing.
	 * 
	 * @param clazz The target class of the {@code InstanceSpec}
	 * @return
	 */
	public static <R extends Object> InstanceSpec<R> createNew(Class<R> clazz) {
		return createFromFactory(clazz, Args.create(), null, Args.create(), null);
	}

	/**
	 * Get an {@link InstanceSpec} from "args".
	 * 
	 * The InstanceSpec may be null or only partially complete (factory ==
	 * null).
	 * 
	 * @param args
	 * @param role
	 * @param clazz
	 * @param defaultValue
	 * @return
	 */
	public static <R> InstanceSpec<R> get(IArgs args, String role, Class<R> clazz, InstanceSpec<R> defaultValue) {
		Object myValue = ArgTools.getObject(args, role, null);
		if (myValue == null) {
			return defaultValue;
		}
		InstanceSpec<R> spec;
		if (myValue instanceof IArgs) {
			spec = InstanceSpec.createFromArgs(clazz, (IArgs) myValue);
		} else if (myValue instanceof InstanceSpec) {
			spec = (InstanceSpec<R>) myValue;
		} else if (myValue instanceof String || myValue instanceof Class || myValue instanceof IFactory) {
			if (clazz != Object.class && clazz.isInstance(myValue)) {
				spec = InstanceSpec.createFromValue(clazz, myValue);
			} else {
				// replace legacy style definition syntax
				spec = InstanceSpec.createFromFactory(clazz, myValue, Args.create());
				put(args, role, spec);
			}
		} else {
			spec = InstanceSpec.createFromValue(clazz, myValue);
		}
		if (spec == null) {
			return defaultValue;
		}
		return spec;
	}

	/**
	 * Get (or create) an instance from the argument "name". After computing the
	 * {@link InstanceSpec} contained in the args->name, the instance is created
	 * and returned immediately.
	 * 
	 * @param args
	 * @param name
	 * @param clazz
	 * @param defaultValue
	 * @return
	 * @throws ObjectCreationException
	 */
	public static <R extends Object> R getAsInstance(IArgs args, String name, Class<R> clazz, R defaultValue)
			throws ObjectCreationException {
		InstanceSpec<R> spec = InstanceSpec.get(args, name, clazz, null);
		if (spec == null || (spec.getFactory() == null && spec.isInstanceUndefined())) {
			return defaultValue;
		}
		return spec.createInstance();
	}

	/**
	 * Get an {@link InstanceSpec} from "args" or create an empty one if absent
	 * 
	 * @param args
	 * @param role
	 * @param clazz
	 * @return
	 */
	public static <R> InstanceSpec<R> getOrCreate(IArgs args, String role, Class<R> clazz) {
		InstanceSpec spec = get(args, role, clazz, null);
		if (spec == null) {
			spec = new InstanceSpec<>(clazz, null, Args.create());
			args.put(role, spec.toArgs());
		}
		return spec;
	}

	public static boolean isInstanceSpec(IArgs args) {
		Object value;
		value = ArgTools.getObject(args, ARG_VALUE, null);
		if (value != null) {
			return true;
		}
		value = ArgTools.getObject(args, ARG_FACTORY, null);
		if (value != null) {
			return true;
		}
		value = ArgTools.getObject(args, ARG_PROCESSOR, null);
		if (value != null) {
			return true;
		}
		value = ArgTools.getObject(args, ARG_CLASS, null);
		if (value != null) {
			return true;
		}
		return false;
	}

	/**
	 * Put an {@link InstanceSpec} into "args" at "role".
	 * 
	 * <pre>
	 * Example/Before:
	 * 
	 * args = {
	 *   foo : "bar"
	 * }
	 * 
	 * Example/After:
	 * 
	 * args = {
	 *   foo : "bar",
	 *   &lt;role&gt; : {
	 *     factory : "de.intarsys.Bar",
	 *     args : {
	 *       gnu : "gnat"
	 *     }
	 *   }
	 * }
	 * </pre>
	 * 
	 * @param args
	 * @param role
	 * @param spec
	 */
	public static IArgs put(IArgs args, String role, Object spec) {
		if (spec instanceof InstanceSpec) {
			args.put(role, ((InstanceSpec) spec).toArgs());
		} else {
			args.put(role, spec);
		}
		return args;
	}

	public static <R> InstanceSpec<R> toInstanceSpec(Object value, Class<R> clazz) {
		if (value == null) {
			return null;
		}
		if (value instanceof Map) {
			value = ArgTools.toArgs(value);
		}
		if (value instanceof IArgs) {
			return InstanceSpec.createFromArgs(clazz, (IArgs) value);
		} else if (value instanceof InstanceSpec) {
			return (InstanceSpec<R>) value;
		} else if (value instanceof String || value instanceof Class || value instanceof IFactory) {
			if (clazz != Object.class && clazz.isInstance(value)) {
				return InstanceSpec.createFromValue(clazz, value);
			} else {
				return InstanceSpec.createFromFactory(clazz, value, Args.create());
			}
		} else {
			return InstanceSpec.createFromValue(clazz, value);
		}
	}

	private Object factory;

	private IFactory<R> realFactory;

	private final IArgs container;

	private final IArgs args;

	private IArgs metaArgs;

	private Object instance;

	private final Class<R> instanceClass;

	protected InstanceSpec(Class<R> instanceClass, IArgs container, Object factory, IArgs args, Object instance,
			IArgs metaArgs) {
		super();
		this.instanceClass = instanceClass;
		this.container = container;
		this.factory = factory;
		this.args = args;
		this.instance = instance;
		this.metaArgs = metaArgs;
		this.putInto(container);
	}

	public InstanceSpec(Class<R> instanceClass, IFactory<R> factory, IArgs args) {
		this(instanceClass, Args.create(), factory, args, UNDEFINED, null);
	}

	public InstanceSpec(Class<R> instanceClass, Object instance) {
		this(instanceClass, Args.create(), null, Args.create(), instance, null);
	}

	protected InstanceSpec(InstanceSpec<R> template) {
		this.args = template.args == null ? null : template.args.copy();
		this.container = Args.create();
		this.factory = template.factory;
		this.instance = template.instance;
		this.instanceClass = template.instanceClass;
		this.metaArgs = template.metaArgs == null ? null : template.metaArgs.copy();
		this.realFactory = template.realFactory;
		this.putInto(container);
	}

	public InstanceSpec<R> copy() {
		return new InstanceSpec<>(this);
	}

	protected IFactory<R> createFactory() throws ObjectCreationException {
		return FactoryTools.toFactory(getFactory());
	}

	/**
	 * Return the instance designated by this specification.
	 * 
	 * @return
	 * @throws ObjectCreationException
	 */
	public R createInstance() throws ObjectCreationException {
		R result;
		if (instance != UNDEFINED) {
			result = (R) instance;
		} else {
			if (getRealFactory() == null) {
				throw new ObjectCreationException("No factory");
			}
			result = getRealFactory().createInstance(getArgs());
		}
		if (instanceClass != null && result != null) {
			if (!instanceClass.isInstance(result)) {
				throw new ObjectCreationException("not an instance of " + instanceClass);
			}
		}
		return result;
	}

	/**
	 * The {@link IArgs} to this {@link InstanceSpec}.
	 * 
	 * @return
	 */
	public IArgs getArgs() {
		return args;
	}

	protected IArgs getContainer() {
		return container;
	}

	/**
	 * The original client defined factory for this {@link InstanceSpec}.
	 * 
	 * @return
	 */
	public Object getFactory() {
		return factory;
	}

	protected R getInstance() {
		return (R) instance;
	}

	protected Class<R> getInstanceClass() {
		return instanceClass;
	}

	protected IArgs getMetaArgs() {
		return metaArgs;
	}

	public IFactory<R> getRealFactory() throws ObjectCreationException {
		if (realFactory == null) {
			realFactory = createFactory();
		}
		return realFactory;
	}

	public boolean isEnabled() {
		if (getMetaArgs() == null) {
			return true;
		}
		return ArgTools.getBoolStrict(getMetaArgs(), META_ENABLED, true);
	}

	/**
	 * <code>true</code> if this {@link InstanceSpec} does not hold a predefined
	 * instance (but is a instance creation definition).
	 * 
	 * @return
	 */
	public boolean isInstanceUndefined() {
		return instance == UNDEFINED;
	}

	/**
	 * Create the standard {@link IArgs} form of the spec top level in the
	 * {@link IArgs}.
	 * 
	 * <pre>
	 * Example/Before:
	 * 
	 * args = {
	 * 	foo : "bar"
	 * }
	 * 
	 * Example/After:
	 * 
	 * args = {
	 * 	foo : "bar",
	 * 	factory : "de.intarsys.Bar",
	 * 	args : {
	 * 		gnu : "gnat"
	 * 	}
	 * }
	 * </pre>
	 * 
	 * 
	 */
	public IArgs putInto(IArgs args) {
		if (isInstanceUndefined()) {
			args.put(ARG_FACTORY, getFactory());
			args.undefine(ARG_PROCESSOR);
			args.put(ARG_ARGS, getArgs());
			if (getMetaArgs() != null && ArgTools.hasDefinedBindings(getMetaArgs())) {
				args.put(ARG_META, getMetaArgs());
			} else {
				args.undefine(ARG_META);
			}
			args.undefine(ARG_VALUE);
		} else {
			args.undefine(ARG_FACTORY);
			args.undefine(ARG_PROCESSOR);
			args.undefine(ARG_ARGS);
			args.put(ARG_VALUE, getInstance());
		}
		return args;
	}

	public void setEnabled(boolean value) {
		if (value) {
			if (getMetaArgs() != null) {
				getMetaArgs().undefine(META_ENABLED);
				if (!ArgTools.hasDefinedBindings(getMetaArgs())) {
					getContainer().undefine(ARG_META);
				}
			}
		} else {
			if (getMetaArgs() == null) {
				metaArgs = Args.create();
			}
			getMetaArgs().put(META_ENABLED, false);
			getContainer().put(ARG_META, getMetaArgs());
		}
	}

	public void setFactory(Object factory) {
		this.factory = factory;
		this.realFactory = null;
		this.instance = UNDEFINED;
		putInto(getContainer());
	}

	public void setInstance(Object instance) {
		this.factory = null;
		this.instance = instance;
		putInto(getContainer());
	}

	/**
	 * Create the standard {@link IArgs} form of the spec.
	 * 
	 */
	public IArgs toArgs() {
		return getContainer();
	}

	@Override
	public String toString() {
		return toArgs().toString();
	}
}
