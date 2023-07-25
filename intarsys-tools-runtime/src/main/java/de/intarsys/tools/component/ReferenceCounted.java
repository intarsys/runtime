package de.intarsys.tools.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import de.intarsys.tools.exception.InvalidRequestException;

public class ReferenceCounted<T> {

	static class ReuserMap {
		private Map<String, ReferenceCounted<?>> objects = new ConcurrentHashMap<>();

		public <T> ReferenceCounted<T> get(String id, Supplier<T> supplier, Consumer<T> consumer) {
			return (ReferenceCounted<T>) objects.computeIfAbsent(id, (key) -> new ReferenceCounted(id, supplier,
					consumer));
		}
	}

	private static final Map<Class<?>, ReuserMap> reusers = new ConcurrentHashMap<>();

	public static <T> ReferenceCounted<T> get(Class<T> clazz, String id, Supplier<T> supplier, Consumer<T> consumer) {
		ReuserMap map = reusers.computeIfAbsent(clazz, (key) -> new ReuserMap());
		return map.get(id, supplier, consumer);
	}

	private final Supplier<T> supplier;

	private final Consumer<T> consumer;

	private final String id;

	private T object;

	private int counter;

	public ReferenceCounted(String id, Supplier<T> supplier, Consumer<T> consumer) {
		super();
		this.id = id;
		this.supplier = supplier;
		this.consumer = consumer;
	}

	public Object acquire() {
		synchronized (this) {
			if (object == null) {
				object = supplier.get();
			}
			counter++;
			return null;
		}
	}

	public T get() {
		synchronized (this) {
			return object;
		}
	}

	public Consumer<T> getConsumer() {
		return consumer;
	}

	public int getCounter() {
		return counter;
	}

	public String getId() {
		return id;
	}

	public Supplier<T> getSupplier() {
		return supplier;
	}

	/**
	 * @param handle
	 *            The handle received when calling acquire
	 */
	public void release(Object handle) {
		synchronized (this) {
			if (counter == 0) {
				throw new InvalidRequestException("already released");
			}
			counter--;
			if (counter == 0) {
				if (consumer != null) {
					consumer.accept(object);
				}
				object = null;
			}
		}
	}

}
