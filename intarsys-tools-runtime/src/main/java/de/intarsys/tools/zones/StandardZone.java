package de.intarsys.tools.zones;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.exception.InvalidRequestException;
import de.intarsys.tools.function.Throwing;

/**
 * A simple standard implementation for {@link IZone}.
 * 
 */
public class StandardZone implements IZone {

	private final IAttributeSupport scope;

	private final IZone parent;

	private final String name;

	private final Consumer<IZone> onEnter;

	private final Consumer<IZone> onLeave;

	public StandardZone(IZone parent, ZoneSpec spec) {
		this( //
				spec.getName() == null ? parent.getName() + "'" : spec.getName(),
				parent,
				spec.getAttributeSupport() == null ? new AttributeMap() : spec.getAttributeSupport(),
				spec.getOnEnter(),
				spec.getOnLeave() //
		);
	}

	protected StandardZone(String name, IZone parent) {
		this(name, parent, new AttributeMap());
	}

	protected StandardZone(String name, IZone parent, IAttributeSupport scope) {
		super();
		this.name = name;
		this.parent = parent;
		this.scope = scope;
		this.onEnter = null;
		this.onLeave = null;
	}

	protected StandardZone(String name, IZone parent, IAttributeSupport scope, Consumer<IZone> onBefore,
			Consumer<IZone> onAfter) {
		super();
		this.name = name;
		this.parent = parent;
		this.scope = scope;
		this.onEnter = onBefore;
		this.onLeave = onAfter;
	}

	protected <R> Callable<R> createCallable(Callable<R> functionalInterface) {
		return new Callable<R>() {
			@Override
			public R call() throws Exception {
				try {
					onBefore();
					return functionalInterface.call();
				} finally {
					onFinally();
				}
			}
		};
	}

	protected <T> Consumer<T> createConsumer(Consumer<T> functionalInterface) {
		return new Consumer<T>() {
			@Override
			public void accept(T value) {
				try {
					onBefore();
					functionalInterface.accept(value);
				} finally {
					onFinally();
				}
			}
		};
	}

	protected <T, E extends Exception> Throwing.Consumer<T> createConsumer(Throwing.Specific.Consumer<T, E> functionalInterface) {
		return new Throwing.Consumer<T>() {
			@Override
			public void accept(T value) throws Exception {
				try {
					onBefore();
					functionalInterface.accept(value);
				} finally {
					onFinally();
				}
			}
		};
	}

	protected <T, R> Function<T, R> createFunction(Function<T, R> functionalInterface) {
		return new Function<T, R>() {
			@Override
			public R apply(T value) {
				try {
					onBefore();
					return functionalInterface.apply(value);
				} finally {
					onFinally();
				}
			}
		};
	}

	protected <T, R, E extends Exception> Throwing.Specific.Function<T, R, E> createFunction(Throwing.Specific.Function<T, R, E> functionalInterface) {
		return new Throwing.Specific.Function<T, R, E>() {
			@Override
			public R apply(T value) throws E {
				try {
					onBefore();
					return functionalInterface.apply(value);
				} finally {
					onFinally();
				}
			}
		};
	}

	protected Runnable createRunnable(Runnable functionalInterface) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					onBefore();
					functionalInterface.run();
				} finally {
					onFinally();
				}
			}
		};
	}

	protected <R> Supplier<R> createSupplier(Supplier<R> functionalInterface) {
		return new Supplier<R>() {
			@Override
			public R get() {
				try {
					onBefore();
					return functionalInterface.get();
				} finally {
					onFinally();
				}
			}
		};
	}

	protected <R, E extends Exception> Throwing.Supplier<R> createSupplier(Throwing.Specific.Supplier<R, E> functionalInterface) {
		return new Throwing.Supplier<R>() {
			@Override
			public R get() throws E {
				try {
					onBefore();
					return functionalInterface.get();
				} finally {
					onFinally();
				}
			}
		};
	}

	@Override
	public void enter() {
		Zone.push(this);
		if (this.onEnter != null) {
			this.onEnter.accept(this);
		}
	}

	@Override
	public IZone fork(ZoneSpec spec) {
		return new StandardZone(this, spec);
	}

	@Override
	public Object getAttribute(Object key) {
		Object result = scope.getAttribute(key);
		if (result != null) {
			return result;
		}
		if (getParent() != null) {
			return getParent().getAttribute(key);
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IZone getParent() {
		return parent;
	}

	@Override
	public void leave() {
		if (Zone.peek() != this) {
			throw new InvalidRequestException("zone " + getName() + " not active");
		}
		if (this.onLeave != null) {
			this.onLeave.accept(this);
		}
		Zone.pop();
	}

	protected void onBefore() {
		enter();
	}

	protected void onFinally() {
		leave();
	}

	@Override
	public Object removeAttribute(Object key) {
		return scope.removeAttribute(key);
	}

	@Override
	public Object setAttribute(Object key, Object value) {
		return scope.setAttribute(key, value);
	}

	@Override
	public String toString() {
		return "zone '" + getName() + "'";
	}

	@Override
	public Callable wrap(Callable functionalInterface) {
		return createCallable(functionalInterface);
	}

	@Override
	public Consumer wrap(Consumer functionalInterface) {
		return createConsumer(functionalInterface);
	}

	@Override
	public Function wrap(Function functionalInterface) {
		return createFunction(functionalInterface);
	}

	@Override
	public Runnable wrap(Runnable functionalInterface) {
		return createRunnable(functionalInterface);
	}

	@Override
	public Supplier wrap(Supplier functionalInterface) {
		return createSupplier(functionalInterface);
	}

	@Override
	public Throwing.Specific.Consumer wrap(Throwing.Specific.Consumer functionalInterface) {
		return createConsumer(functionalInterface);
	}

	@Override
	public Throwing.Specific.Function wrap(Throwing.Specific.Function functionalInterface) {
		return createFunction(functionalInterface);
	}

	@Override
	public Throwing.Specific.Supplier wrap(Throwing.Specific.Supplier functionalInterface) {
		return createSupplier(functionalInterface);
	}
}
