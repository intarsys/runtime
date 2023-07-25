package de.intarsys.tools.message;

/**
 * A common superclass for implementing {@link IMessageBundle}.
 * 
 */
public abstract class CommonMessageBundle implements IMessageBundle {

	private final CommonMessageBundleFactory factory;

	private final String name;

	private final ClassLoader classLoader;

	protected CommonMessageBundle(CommonMessageBundleFactory factory, String name,
			ClassLoader classLoader) {
		this.factory = factory;
		this.name = name;
		this.classLoader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
	}

	@Override
	public String format(String pattern, Object... args) {
		return MessageTools.format(pattern, args);
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public CommonMessageBundleFactory getFactory() {
		return factory;
	}

	protected String getFallbackString(String key, Object... objects) {
		StringBuilder sb = new StringBuilder();
		sb.append("{"); //$NON-NLS-1$
		sb.append(key);
		sb.append("}"); //$NON-NLS-1$
		if (objects != null) {
			for (int i = 0; i < objects.length; i++) {
				sb.append("["); //$NON-NLS-1$
				sb.append(getFormattedObject(objects[i]));
				sb.append("]"); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

	protected Object getFormattedObject(Object object) {
		if (object instanceof FormattedObject) {
			return ((FormattedObject) object).getFormattedObject();
		}
		return object;
	}

	@Override
	public IMessage getMessage(String code, Object... arg) {
		return new GenericMessage(this, code, arg);
	}

	@Override
	public String getName() {
		return name;
	}

	protected boolean isLogMode() {
		return getFactory().isLogMode();
	}

	protected boolean isRawMode() {
		return getFactory().isRawMode();
	}
}