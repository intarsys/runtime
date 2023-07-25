/*
 * (c) intarsys GmbH
 * all rights reserved
 *
 */
package de.intarsys.tools.ui;

import java.util.Arrays;
import java.util.List;

public abstract class CommonSelection<T> implements ISelection<T> {

	public static <T> ISelection<T> createFromTarget(T target) {
		if (target == null) {
			return new EmptySelection<>();
		}
		if (target instanceof List) {
			return new ListSelection<>((List<T>) target);
		}
		if (target.getClass().isArray()) {
			return new ListSelection<>(Arrays.asList((T[]) target));
		}
		return new SingleObjectSelection<>(target);
	}

	public static <T> ISelection<T> createFromTargets(T... target) {
		if (target == null || target.length == 0) {
			return new EmptySelection<>();
		}
		if (target.length == 1) {
			return new SingleObjectSelection<>(target[0]);
		}
		return new ListSelection<>(Arrays.asList(target));
	}

	protected CommonSelection() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ISelection)) {
			return false;
		}
		ISelection<?> partner = (ISelection<?>) obj;
		return getElements().equals(partner.getElements());
	}

	@Override
	public int hashCode() {
		return getElements().hashCode();
	}
}
