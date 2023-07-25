package de.intarsys.tools.json;

import java.util.Iterator;
import java.util.Set;

import de.intarsys.tools.collection.ConversionIterator;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;

public class JsonObjectArgs implements IArgs {

	private class Binding implements IBinding {

		private String key;

		public Binding(String key) {
			super();
			this.key = key;
		}

		@Override
		public String getName() {
			return key;
		}

		@Override
		public Object getValue() {
			return get(key);
		}

		@Override
		public boolean isDefined() {
			return JsonObjectArgs.this.isDefined(key);
		}

		@Override
		public void setName(String name) {
			throw new UnsupportedOperationException("not supported");
		}

		@Override
		public void setValue(Object value) {
			put(key, value);
		}
	}

	protected static Object unwrap(Object value) {
		if (value instanceof JsonObject) {
			return new JsonObjectArgs((JsonObject) value);
		}
		if (value instanceof JsonArray) {
			return new JsonArrayArgs((JsonArray) value);
		}
		return value;
	}

	protected static Object wrap(Object value) {
		if (value instanceof IArgs) {
			value = ArgTools.toJavaDeep((IArgs) value);
		}
		return Json.wrap(value);
	}

	private JsonObject json;

	public JsonObjectArgs(JsonObject json) {
		super();
		this.json = json;
	}

	@Override
	public IBinding add(Object object) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public Iterator<IBinding> bindings() {
		return new ConversionIterator<String, IArgs.IBinding>(json.names().iterator()) {
			@Override
			protected de.intarsys.tools.functor.IArgs.IBinding createTargetObject(String key) {
				return new Binding(key);
			}
		};
	}

	@Override
	public void clear() {
		json.clear();
	}

	@Override
	public IArgs copy() {
		return new JsonObjectArgs(Json.copy(json));
	}

	@Override
	public IBinding declare(String name) {
		return new Binding(name);
	}

	@Override
	public Object get(int index) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public Object get(int index, Object defaultValue) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public Object get(String name) {
		Object value = json.get(name);
		return unwrap(value);
	}

	@Override
	public Object get(String name, Object defaultValue) {
		Object value = get(name);
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	@Override
	public boolean isDefined(int index) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public boolean isDefined(String name) {
		return json.has(name);
	}

	@Override
	public Iterator<IBinding> iterator() {
		return bindings();
	}

	@Override
	public Set<String> names() {
		return json.names();
	}

	@Override
	public IBinding put(int index, Object value) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public IBinding put(String name, Object value) {
		json.basicPut(name, wrap(value));
		return new Binding(name);
	}

	@Override
	public int size() {
		return json.size();
	}

	@Override
	public void undefine(int index) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public void undefine(String name) {
		json.remove(name);
	}

}
