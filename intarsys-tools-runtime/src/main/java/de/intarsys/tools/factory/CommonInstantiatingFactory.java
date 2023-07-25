package de.intarsys.tools.factory;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.component.IInitializeable;
import de.intarsys.tools.event.CreatedEvent;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.ArgumentDeclarator;
import de.intarsys.tools.functor.DeclarationBlock;
import de.intarsys.tools.functor.DeclarationException;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgsAccess;
import de.intarsys.tools.functor.IDeclarationBlock;
import de.intarsys.tools.functor.IDeclarationSupport;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.functor.common.DeclarationIO;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.preferences.IPreferences;
import de.intarsys.tools.preferences.PreferencesTools;
import de.intarsys.tools.reflect.IClassLoaderAccess;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

/**
 * A common superclass for implementing factories that really finally
 * instantiate the target instance.
 * 
 * It's important to understand that this factory post-processes the instance
 * created and issues a {@link CreatedEvent} when done.
 * 
 * Target configuration:
 * 
 * The factory can statically hold an instance configuration element (in
 * &lt;instanceConfiguration&gt; element of its configuration) that is applied to
 * every new instance. In addition, a dynamic argument "configuration" is
 * evaluated from the arguments {@link #basicCreateInstance(IArgs)} and applied
 * to the target.
 * 
 * @param <T>
 */
public abstract class CommonInstantiatingFactory<T> extends CommonFactory<T> implements IDeclarationSupport {

	public static final String PREFS_ARGS = "args"; //$NON-NLS-1$

	private static final ILogger Log = PACKAGE.Log;

	private IElement instanceConfiguration;

	private IDeclarationBlock declarationBlock = new DeclarationBlock(this);

	private final Object lockCreate = new Object();

	protected CommonInstantiatingFactory() {
		super();
	}

	protected abstract T basicCreateInstance(IArgs args) throws ObjectCreationException;

	protected void basicCreateInstanceConfig(T object, IArgs args) throws ObjectCreationException {
		try {
			if (object instanceof IArgsAccess) {
				((IArgsAccess) object).setArgs(args);
			}
			ClassLoader classLoader = getClassLoader(args);
			boolean init = false;
			// apply the default factory based configuration
			if (getInstanceConfiguration() != null) {
				init = true;
				if (object instanceof IElementConfigurable) {
					((IElementConfigurable) object).configure(getInstanceConfiguration());
				}
				ElementTools.setProperties(object, getInstanceConfiguration(), classLoader);
			}
			IElement configuration = getConfiguration(args);
			// apply the instance specific configuration from the arguments
			if (configuration != null) {
				init = true;
				if (object instanceof IElementConfigurable) {
					((IElementConfigurable) object).configure(configuration);
				}
				ElementTools.setProperties(object, configuration, classLoader);
			}
			if (init && object instanceof IInitializeable) {
				((IInitializeable) object).initializeAfterConstruction();
			}
			ObjectTools.invokeMethodAnnotatedWith(object, PostConstruct.class);
		} catch (ObjectCreationException e) {
			throw e;
		} catch (Exception e) {
			throw new ObjectCreationException(e);
		}
	}

	protected void basicCreateInstanceInit(T object, IArgs args) throws ObjectCreationException {
		if (object instanceof IInitializeable) {
			((IInitializeable) object).initializeAfterCreation();
		}
		Object context = getContext(args);
		if (context != null && object instanceof IContextSupport) {
			try {
				((IContextSupport) object).setContext(context);
			} catch (ConfigurationException e) {
				throw new ObjectCreationException(e);
			}
		}
		ClassLoader classLoader = getClassLoader(args);
		if (classLoader != null && object instanceof IClassLoaderAccess) {
			((IClassLoaderAccess) object).setClassLoader(classLoader);
		}
	}

	/**
	 * If the factory has some notion of identity, you should implement it here.
	 * 
	 * @param args
	 *            The creation args
	 * @return An optional already existing instance, depending on the notion of
	 *         identity of the factory.
	 */
	protected T basicLookupInstance(IArgs args) {
		return null;
	}

	protected IArgs basicPrepareArgs(IArgs args) throws ObjectCreationException {
		IArgs localArgs = args.copy();
		preferencesMergeArgs(localArgs);
		try {
			new ArgumentDeclarator().apply(getDeclarationBlock(), localArgs);
		} catch (DeclarationException e) {
			throw new ObjectCreationException(e);
		}
		return localArgs;
	}

	@Override
	public void configure(IElement pElement) throws ConfigurationException {
		setInstanceConfiguration(pElement.element("instanceConfiguration"));
		super.configure(pElement);
		try {
			declarationBlock = new DeclarationBlock(this);
			new DeclarationIO().deserializeDeclarationBlock(declarationBlock, pElement);
		} catch (ObjectCreationException e) {
			throw new ConfigurationException(e);
		}
	}

	@Override
	public T createInstance(IArgs args) throws ObjectCreationException {
		boolean created = false;
		T object;
		synchronized (lockCreate) {
			object = basicLookupInstance(args);
			if (object == null) {
				args = basicPrepareArgs(args);
				object = basicCreateInstance(args);
				if (object != null) {
					created = true;
					basicCreateInstanceInit(object, args);
					basicCreateInstanceConfig(object, args);
				}
			}
		}
		if (created) {
			Object callback = ArgTools.getObject(args, "onCreated", null);
			args.undefine("onCreated");
			if (callback instanceof Consumer) {
				try {
					((Consumer) callback).accept(object);
				} catch (Exception e) {
					throw new ObjectCreationException("'onCreated' callback failed", e);
				}
			} else if (callback instanceof IFunctor) {
				IFunctorCall call = FunctorCall.noargs(null);
				call.getArgs().put("object", object);
				try {
					((IFunctor) callback).perform(call);
				} catch (FunctorException e) {
					throw new ObjectCreationException("'onCreated' callback failed", e);
				}
			} else if (callback != null) {
				Log.warn("{} callback 'onCreated' for {} not supported", getLabel(), callback);
				throw new ObjectCreationException("'onCreated' callback not supported");
			}
			triggerCreated(object);
		}
		return object;
	}

	@Override
	public IDeclarationBlock getDeclarationBlock() {
		return declarationBlock;
	}

	public IElement getInstanceConfiguration() {
		return instanceConfiguration;
	}

	@Override
	protected void preferencesInit(IPreferences preferences) {
		super.preferencesInit(preferences);
		// fill default preferences from declarations
		// todo lazy declaration handling
		if (getDeclarationBlock().size() > 0) {
			IArgs args = Args.create();
			try {
				new ArgumentDeclarator().apply(getDeclarationBlock(), args);
			} catch (DeclarationException e) {
				Log.log(Level.WARN, e.getMessage(), e);
			}
			if (args.size() > 0) {
				PreferencesTools.putArgsAll(preferences.node(PREFS_ARGS), args);
			}
		}
	}

	protected void preferencesMergeArgs(IArgs args) {
		PreferencesTools.mergeArgs(getPreferences().node(PREFS_ARGS), args);
	}

	public void setInstanceConfiguration(IElement instanceConfiguration) {
		this.instanceConfiguration = instanceConfiguration;
	}

}
