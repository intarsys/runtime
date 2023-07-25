package de.intarsys.tools.reflect;

/**
 * Simple adapter for {@link IRelationHandler}
 * 
 */
public class RelationHandlerAdapter<C, E> implements IRelationHandler<C, E> {

	@Override
	public E[] get(C owner) {
		return (E[]) new Object[0];
	}

	@Override
	public C getOwner(E element) {
		return null;
	}

	@Override
	public E insert(C owner, E value) {
		return value;
	}

	@Override
	public E remove(C owner, E value) {
		return value;
	}

	@Override
	public int size(C owner) {
		return 0;
	}

	@Override
	public E update(C owner, E value, E newValue) {
		return newValue;
	}
}
