package de.intarsys.tools.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThreadLocalSnapshot {

	static class Snapshooter {
		public final Supplier getter;
		public final Consumer setter;
		public final Consumer remover;

		public Snapshooter(Supplier getter, Consumer setter, Consumer remover) {
			super();
			this.getter = getter;
			this.setter = setter;
			this.remover = remover;
		}

		public Snapshot run() {
			return new Snapshot(this, this.getter.get());
		}
	}

	static class Snapshot {
		public final Object value;
		public final Snapshooter snapshooter;

		public Snapshot(Snapshooter snapshooter, Object value) {
			super();
			this.value = value;
			this.snapshooter = snapshooter;
		}

		public void remove() {
			this.snapshooter.remover.accept(value);
		}

		public void set() {
			this.snapshooter.setter.accept(value);
		}
	}

	private static final List<Snapshooter> SNAPSHOOTERS = new CopyOnWriteArrayList<>();

	public static ThreadLocalSnapshot create() {
		ThreadLocalSnapshot result = new ThreadLocalSnapshot();
		result.copy();
		return result;
	}

	public static void register(ThreadLocal local) {
		SNAPSHOOTERS.add(new Snapshooter(() -> local.get(), (value) -> local.set(value), (value) -> local.remove()));
	}

	private List<Snapshot> snapshots = new ArrayList<>();

	protected void copy() {
		for (Snapshooter snapshooter : SNAPSHOOTERS) {
			snapshots.add(snapshooter.run());
		}
	}

	public void paste() {
		for (Snapshot snapshot : snapshots) {
			snapshot.set();
		}
	}

	public void remove() {
		for (Snapshot snapshot : snapshots) {
			snapshot.remove();
		}
		snapshots.clear();
	}

}
