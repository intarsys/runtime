package de.intarsys.tools.functor;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * A simple fluent API for building {@link IArgs} structures.
 *
 */
public class ArgsBuilder {

	private final ArgsBuilder parent;

	private final IArgs args;

	public ArgsBuilder() {
		parent = null;
		args = Args.create();
	}

	protected ArgsBuilder(ArgsBuilder parent, IArgs current) {
		super();
		this.parent = parent;
		args = current;
	}

	public ArgsBuilder(IArgs args) {
		super();
		parent = null;
		this.args = args;
	}

	public ArgsBuilder add(Collection[] values) {
		for (Object value : values) {
			args.add(value);
		}
		return this;
	}

	public ArgsBuilder add(Object value) {
		args.add(value);
		return this;
	}

	public ArgsBuilder add(Object[] values) {
		for (Object value : values) {
			args.add(value);
		}
		return this;
	}

	public ArgsBuilder add(Stream values) {
		values.forEach((value) -> args.add(value));
		return this;
	}

	public ArgsBuilder argsClose() {
		return parent;
	}

	public ArgsBuilder argsOpen() {
		IArgs newArgs = Args.create();
		args.add(newArgs);
		return new ArgsBuilder(this, newArgs);
	}

	public ArgsBuilder argsOpen(String name) {
		IArgs newArgs = Args.create();
		args.put(name, newArgs);
		return new ArgsBuilder(this, newArgs);
	}

	public IArgs build() {
		return args;
	}

	public IArgs getArgs() {
		return args;
	}

	public ArgsBuilder put(int index, Object value) {
		args.put(index, value);
		return this;
	}

	public ArgsBuilder put(String name, Object value) {
		ArgTools.putPath(args, name, value);
		return this;
	}

	public ArgsBuilder putAll(IArgs other) {
		ArgTools.putAll(args, other);
		return this;
	}

	public ArgsBuilder putAllDeep(IArgs other) {
		ArgTools.putAllDeep(args, other);
		return this;
	}

	public ArgsBuilder putAllIfAbsent(IArgs other) {
		ArgTools.putAllIfAbsent(args, other);
		return this;
	}

	public ArgsBuilder putAllIfAbsentDeep(IArgs other) {
		ArgTools.putAllIfAbsentDeep(args, other);
		return this;
	}

	public ArgsBuilder putIfAbsent(String name, Object value) {
		ArgTools.putPathIfAbsent(args, name, value);
		return this;
	}
}
