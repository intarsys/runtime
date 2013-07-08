package de.intarsys.tools.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;

import de.intarsys.tools.collection.ConversionIterator;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;

public class PreferencesArgs implements IArgs {

	class Binding implements IBinding {
		private String name;

		public Binding(String name) {
			super();
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Object getValue() {
			return PreferencesArgs.this.get(name);
		}

		@Override
		public boolean isDefined() {
			return PreferencesArgs.this.isDefined(name);
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public void setValue(Object value) {
			PreferencesArgs.this.put(name, value);
		}
	}

	private static final String UNDEFINED = new String();

	final private IPreferences preferences;

	public PreferencesArgs(IPreferences preferences) {
		super();
		this.preferences = preferences;
	}

	@Override
	public IBinding add(Object object) {
		throw new UnsupportedOperationException(
				"can not write to PreferencesArgs");
	}

	@Override
	public Iterator<IBinding> bindings() {
		return new ConversionIterator<String, IBinding>(names().iterator()) {
			@Override
			protected IArgs.IBinding createTargetObject(String name) {
				return new Binding(name);
			};
		};
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException(
				"can not write to PreferencesArgs");
	}

	@Override
	public IArgs copy() {
		return new PreferencesArgs(preferences);
	}

	@Override
	public IBinding declare(String name) {
		return new Binding(name);
	}

	@Override
	public Object get(int index) {
		return get(String.valueOf(index));
	}

	@Override
	public Object get(int index, Object defaultValue) {
		return defaultValue;
	}

	@Override
	public Object get(String name) {
		try {
			if (preferences.nodeExists(name)) {
				IPreferences childNode = preferences.node(name);
				if (childNode == null) {
					return null;
				}
				return new PreferencesArgs(childNode);
			}
			return preferences.get(name, null);
		} catch (BackingStoreException e) {
			return null;
		}
	}

	@Override
	public Object get(String name, Object defaultValue) {
		try {
			if (preferences.nodeExists(name)) {
				IPreferences childNode = preferences.node(name);
				return new PreferencesArgs(childNode);
			}
			Object result = preferences.get(name, null);
			if (result == null) {
				return defaultValue;
			}
			return result;
		} catch (BackingStoreException e) {
			return null;
		}
	}

	public boolean isDefined(int index) {
		return isDefined(String.valueOf(index));
	}

	@Override
	public boolean isDefined(String name) {
		return preferences.get(name, UNDEFINED) != UNDEFINED;
	}

	public boolean isIndexed() {
		return false;
	}

	public boolean isNamed() {
		return true;
	}

	@Override
	public Set names() {
		try {
			List children = new ArrayList(Arrays.asList(preferences
					.childrenNames()));
			List keys = Arrays.asList(preferences.keys());
			children.addAll(keys);
			return new HashSet(children);
		} catch (BackingStoreException e) {
			return new HashSet();
		}
	}

	@Override
	public IBinding put(int index, Object value)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"can not write to PreferencesArgs");
	}

	@Override
	public IBinding put(String name, Object value)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"can not write to PreferencesArgs");
	}

	@Override
	public int size() {
		try {
			return preferences.childrenNames().length
					+ preferences.keys().length;
		} catch (BackingStoreException e) {
			return 0;
		}
	}

	@Override
	public String toString() {
		return ArgTools.toString(this, "[p]");
	}

	@Override
	public void undefine(int index) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"can not write to PreferencesArgs");
	}

	@Override
	public void undefine(String name) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"can not write to PreferencesArgs");
	}
}
