package de.intarsys.tools.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.intarsys.tools.functor.IArgs;

public class JsonArrayArgs implements IArgs {

	private class Binding implements IBinding {

		private int index;

		public Binding(int index) {
			super();
			this.index = index;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public Object getValue() {
			return get(index);
		}

		@Override
		public boolean isDefined() {
			return true;
		}

		@Override
		public void setName(String name) {
			throw new UnsupportedOperationException("not supported");
		}

		@Override
		public void setValue(Object value) {
			put(index, value);
		}
	}

	private JsonArray json;

	public JsonArrayArgs(JsonArray json) {
		super();
		this.json = json;
	}

	@Override
	public IBinding add(Object object) {
		json.basicAdd(JsonObjectArgs.wrap(object));
		return new Binding(json.size() - 1);
	}

	@Override
	public Iterator<IBinding> bindings() {
		List<IBinding> bindings = new ArrayList<>();
		for (int i = 0; i < json.size(); i++) {
			bindings.add(new Binding(i));
		}
		return bindings.iterator();
	}

	@Override
	public void clear() {
		json.clear();
	}

	@Override
	public IArgs copy() {
		return new JsonArrayArgs(Json.copy(json));
	}

	@Override
	public IBinding declare(String name) {
		throw new IllegalStateException("not supported");
	}

	@Override
	public Object get(int index) {
		return JsonObjectArgs.unwrap(json.get(index));
	}

	@Override
	public Object get(int index, Object defaultValue) {
		Object value = json.get(index);
		return JsonObjectArgs.unwrap(value);
	}

	@Override
	public Object get(String name) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public Object get(String name, Object defaultValue) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public boolean isDefined(int index) {
		return json.size() > index;
	}

	@Override
	public boolean isDefined(String name) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public Iterator<IBinding> iterator() {
		return bindings();
	}

	@Override
	public Set<String> names() {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public IBinding put(int index, Object value) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public IBinding put(String name, Object value) {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public int size() {
		return json.size();
	}

	@Override
	public void undefine(int index) {
		json.remove(index);
	}

	@Override
	public void undefine(String name) {
		throw new UnsupportedOperationException("not supported");
	}

}
