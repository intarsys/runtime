/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.functor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An {@link IArgs} implementation allowing to concat two {@link IArgs}
 * together. Lookup will be performed in "args" first. If nothing is found,
 * lookup is done in "fallbackArgs".
 * 
 */
public class ChainedArgs implements IArgs {

	class Binding implements IBinding {

		final private IBinding argBinding;

		final private IBinding fallbackBinding;

		public Binding(IBinding argBinding, IBinding fallbackBinding) {
			super();
			this.argBinding = argBinding;
			this.fallbackBinding = fallbackBinding;
		}

		@Override
		public String getName() {
			return argBinding.getName();
		}

		@Override
		public Object getValue() {
			Object result = argBinding.getValue();
			if (result == null) {
				if (!argBinding.isDefined()) {
					Object fallbackValue = fallbackBinding.getValue();
					if (fallbackValue instanceof IArgs) {
						result = Args.create();
						argBinding.setValue(result);
					} else {
						result = fallbackValue;
					}
				}
			}
			if (result instanceof IArgs) {
				Object fallbackValue = fallbackBinding.getValue();
				if (fallbackValue instanceof IArgs) {
					// result = new ChainedArgs((IArgs) result,
					// (IArgs) fallbackValue);
					result = null;
				}
			}
			return result;
		}

		@Override
		public boolean isDefined() {
			return argBinding.isDefined() || fallbackBinding.isDefined();
		}

		@Override
		public void setName(String name) {
			argBinding.setName(name);
		}

		@Override
		public void setValue(Object value) {
			argBinding.setValue(value);
		}

	}

	private IArgs args;

	private IArgs fallbackArgs;

	/**
	 * Create new {@link ChainedArgs} where <code>args</code> are always looked
	 * up first. If lookup fails, <code>fallbackArgs</code> are used.
	 * 
	 * @param args
	 *            The main {@link IArgs} to use for lookup
	 * @param fallbackArgs
	 *            The fallback {@link IArgs} for lookup
	 */
	public ChainedArgs(IArgs args, IArgs fallbackArgs) {
		super();
		this.args = args;
		this.fallbackArgs = fallbackArgs;
	}

	public IBinding add(Object object) {
		return args.add(object);
	}

	@Override
	public Iterator<IBinding> bindings() {
		return new Iterator<IBinding>() {
			private IBinding current;
			private Iterator<IBinding> argsIterator = args.bindings();
			private Iterator<IBinding> fallbackIteratorIndexed = fallbackArgs
					.bindings();
			private Iterator<IBinding> fallbackIteratorNamed = fallbackArgs
					.bindings();

			@Override
			public boolean hasNext() {
				if (current != null) {
					return true;
				}
				IBinding currentArg = null;
				IBinding currentFallbackArg = null;
				if (argsIterator.hasNext()) {
					currentArg = argsIterator.next();
				}
				if (fallbackIteratorIndexed.hasNext()) {
					currentFallbackArg = fallbackIteratorIndexed.next();
				}
				if (currentArg == null && currentFallbackArg != null
						&& currentFallbackArg.getName() == null) {
					currentArg = args.add(null);
				}
				if (currentArg != null) {
					String name = currentArg.getName();
					if (name == null) {
						// positional match
						if (currentFallbackArg != null) {
							current = new Binding(currentArg,
									currentFallbackArg);
						} else {
							current = currentArg;
						}
					} else {
						// associative match
						if (fallbackArgs.isDefined(name)) {
							current = new Binding(currentArg,
									fallbackArgs.declare(name));
						} else {
							current = currentArg;
						}
					}
				}
				while (current == null && fallbackIteratorNamed.hasNext()) {
					currentFallbackArg = fallbackIteratorNamed.next();
					String name = currentFallbackArg.getName();
					if (name == null) {
						// visit only named
						continue;
					} else if (args.isDefined(name)) {
						// already visited
						continue;
					} else {
						current = new Binding(args.declare(name),
								currentFallbackArg);
					}
				}
				return false;
			}

			@Override
			public IBinding next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				IBinding result = current;
				current = null;
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public void clear() {
		args.clear();
	}

	@Override
	public IArgs copy() {
		IArgs result = fallbackArgs.copy();
		for (String name : args.names()) {
			Object value = args.get(name);
			if (value instanceof IArgs) {
				result.put(name, ((IArgs) value).copy());
			} else {
				result.put(name, value);
			}
		}
		return result;
	}

	@Override
	public IBinding declare(String name) {
		return new Binding(args.declare(name), fallbackArgs.declare(name));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.functor.IArgs#get(int)
	 */
	public Object get(int index) {
		Object result = args.get(index);
		if (result == null) {
			Object fallbackResult = fallbackArgs.get(index);
			if (fallbackResult instanceof IArgs) {
				result = Args.create();
				args.put(index, result);
			} else {
				result = fallbackResult;
			}
		}
		if (result instanceof IArgs) {
			Object fallbackResult = fallbackArgs.get(index);
			if (fallbackResult instanceof IArgs) {
				// result = new ChainedArgs((IArgs) result, (IArgs)
				// fallbackResult);
				result = null;
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.functor.IArgs#get(int, java.lang.Object)
	 */
	public Object get(int index, Object defaultValue) {
		Object result = get(index);
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.functor.IArgs#get(java.lang.String)
	 */
	public Object get(String name) {
		Object result = args.get(name);
		if (result == null) {
			if (!args.isDefined(name)) {
				Object fallbackResult = fallbackArgs.get(name);
				if (fallbackResult instanceof IArgs) {
					result = Args.create();
					args.put(name, result);
				} else {
					result = fallbackResult;
				}
			}
		}
		if (result instanceof IArgs) {
			Object fallbackResult = fallbackArgs.get(name);
			if (fallbackResult instanceof IArgs) {
				// result = new ChainedArgs((IArgs) result, (IArgs)
				// fallbackResult);
				result = null;
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.functor.IArgs#get(java.lang.String,
	 * java.lang.Object)
	 */
	public Object get(String name, Object defaultValue) {
		Object result = get(name);
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	/**
	 * The main (primary) {@link IArgs}.
	 * 
	 * @return The main (primary) {@link IArgs}.
	 */
	public IArgs getArgs() {
		return args;
	}

	/**
	 * The fallback (secondary) {@link IArgs};
	 * 
	 * @return The fallback (secondary) {@link IArgs};
	 */
	public IArgs getFallbackArgs() {
		return fallbackArgs;
	}

	public boolean isDefined(int index) {
		if (args.isDefined(index)) {
			return true;
		}
		return fallbackArgs.isDefined(index);
	}

	public boolean isDefined(String name) {
		if (args.isDefined(name)) {
			return true;
		}
		return fallbackArgs.isDefined(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.functor.IArgs#isIndexed()
	 */
	public boolean isIndexed() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.functor.IArgs#isNamed()
	 */
	public boolean isNamed() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.functor.IArgs#names()
	 */
	public Set names() {
		Set names = new HashSet(args.names());
		names.addAll(fallbackArgs.names());
		return names;
	}

	public IBinding put(int index, Object value) {
		return args.put(index, value);
	}

	public IBinding put(String name, Object value) {
		return args.put(name, value);
	}

	public int size() {
		return names().size();
	}

	@Override
	public String toString() {
		return ArgTools.toString(this, "");
	}

	@Override
	public void undefine(int index) {
	}

	@Override
	public void undefine(String name) {
	}
}
