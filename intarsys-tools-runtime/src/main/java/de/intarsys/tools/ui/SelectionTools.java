/*
 * (c) intarsys GmbH
 * all rights reserved
 *
 */
package de.intarsys.tools.ui;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class SelectionTools {

	public static class Attribute<T> {
		private Comparator<T> comparator;

		private boolean undefined;

		private T value;

		public Attribute() {
			this.undefined = true;
		}

		public Attribute(T value, Comparator<T> comparator) {
			this.value = value;
			this.undefined = false;
			this.comparator = comparator;
		}

		public Object getValue() {
			return value;
		}

		public boolean hasValue(T obj) {
			if (isUndefined()) {
				return false;
			}
			return comparator.compare(value, obj) == 0;
		}

		public boolean isUndefined() {
			return undefined;
		}
	}

	public static interface ISelectionCheck<T> extends Predicate<T> {
		public boolean accept(T object);

		@Override
		default boolean test(T t) {
			return accept(t);
		}
	}

	public static interface ISelectionPerformer<T> extends Function<T, T>, Consumer<T> {
		@Override
		default void accept(T t) {
			perform(t);
		}

		@Override
		default T apply(T t) {
			return perform(t);
		}

		public T perform(T object);
	}

	@SuppressWarnings("serial")
	public static class SelectionPerformerBreak extends RuntimeException {
	}

	protected static final Attribute UNDEFINED = new Attribute();

	public static <T> boolean check(ISelection<T> selection, Predicate<Object> check) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		return selection.getElements().stream().allMatch(check);
	}

	public static <T> Attribute<T> getCommonAttribute(ISelection<T> selection, Function<T, T> performer) {
		Comparator<T> comparator = new Comparator<T>() {
			@Override
			public int compare(T arg0, T arg1) {
				if (arg0 == null) {
					return arg1 == null ? 0 : 1;
				}
				return arg0.equals(arg1) ? 0 : 1;
			}
		};
		return getCommonAttribute(selection, performer, comparator);
	}

	public static <T> Attribute<T> getCommonAttribute(ISelection<T> selection, Function<T, T> performer,
			Comparator<T> comparator) {
		Attribute<T> commonValue = UNDEFINED;
		for (T element : selection.getElements()) {
			T elementValue = performer.apply(element);
			if (commonValue.isUndefined()) {
				commonValue = new Attribute<>(elementValue, comparator);
			} else {
				if (!commonValue.hasValue(elementValue)) {
					commonValue = UNDEFINED;
					break;
				}
			}
		}
		return commonValue;
	}

	public static <T> Attribute<T> getCommonAttribute(List<T> objects, Function<T, T> performer) {
		Comparator<T> comparator = new Comparator<T>() {
			@Override
			public int compare(T arg0, T arg1) {
				if (arg0 == null) {
					return arg1 == null ? 0 : 1;
				}
				return arg0.equals(arg1) ? 0 : 1;
			}
		};
		return getCommonAttribute(objects, performer, comparator);
	}

	public static <T> Attribute<T> getCommonAttribute(List<T> objects, Function<T, T> performer,
			Comparator<T> comparator) {
		Attribute<T> commonValue = UNDEFINED;
		for (T element : objects) {
			T elementValue = performer.apply(element);
			if (commonValue.isUndefined()) {
				commonValue = new Attribute<>(elementValue, comparator);
			} else {
				if (!commonValue.hasValue(elementValue)) {
					commonValue = UNDEFINED;
					break;
				}
			}
		}
		return commonValue;
	}

	public static <T> boolean hasType(ISelection<T> selection, Class<?> type) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}
		if (type == null) {
			return true;
		}
		return check(selection, element -> type.isInstance(element));
	}

	public static <T> void perform(ISelection<T> selection, Consumer<Object> performer) {
		if (selection == null) {
			return;
		}
		for (Object element : selection.getElements()) {
			try {
				performer.accept(element);
			} catch (SelectionPerformerBreak e) {
				break;
			}
		}
	}

	private SelectionTools() {
	}

}
