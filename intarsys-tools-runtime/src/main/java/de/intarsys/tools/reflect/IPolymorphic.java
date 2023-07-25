package de.intarsys.tools.reflect;

public interface IPolymorphic {

	static <T> T basicCast(Object object, Class<T> clazz) {
		if (clazz.isInstance(object)) {
			return (T) object;
		}
		if (object instanceof IWrapper) {
			Object delegate = ((IWrapper) object).getWrapped();
			if (delegate instanceof IPolymorphic) {
				return ((IPolymorphic) delegate).cast(clazz);
			}
			if (clazz.isInstance(delegate)) {
				return (T) delegate;
			}
		}
		throw new ClassCastException("can not cast to " + clazz);
	}

	static boolean basicIsImplementing(Object object, Class<?> clazz) {
		if (clazz.isInstance(object)) {
			return true;
		}
		if (object instanceof IWrapper) {
			Object delegate = ((IWrapper) object).getWrapped();
			if (delegate instanceof IPolymorphic) {
				return ((IPolymorphic) delegate).isImplementing(clazz);
			}
			return clazz.isInstance(delegate);
		}
		return false;
	}

	static <T> T objectCast(Object object, Class<T> clazz) {
		if (object instanceof IPolymorphic) {
			return ((IPolymorphic) object).cast(clazz);
		}
		return basicCast(object, clazz);
	}

	static boolean objectIsImplementing(Object object, Class<?> clazz) {
		if (object instanceof IPolymorphic) {
			return ((IPolymorphic) object).isImplementing(clazz);
		}
		return basicIsImplementing(object, clazz);
	}

	default <T> T cast(Class<T> clazz) throws ClassCastException {
		return basicCast(this, clazz);
	}

	default boolean isImplementing(Class<?> clazz) {
		return basicIsImplementing(this, clazz);
	}

}
