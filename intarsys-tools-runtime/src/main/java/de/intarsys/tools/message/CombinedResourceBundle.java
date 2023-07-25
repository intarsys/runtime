package de.intarsys.tools.message;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

class CombinedResourceBundle extends ResourceBundle {

	static class CombinedResourceBundleEnumeration implements Enumeration<String> { // NOSONAR

		Set<Object> set;
		Iterator<Object> iterator;
		Enumeration<String> enumeration; // may remain null

		String next = null;

		public CombinedResourceBundleEnumeration(Set<Object> set, Enumeration enumeration) {
			this.set = set;
			this.iterator = set.iterator();
			this.enumeration = enumeration;
		}

		@Override
		public boolean hasMoreElements() {
			if (next == null) {
				if (iterator.hasNext()) {
					next = (String) iterator.next();
				} else if (enumeration != null) {
					while (next == null && enumeration.hasMoreElements()) {
						next = enumeration.nextElement();
						if (set.contains(next)) {
							// do not repeat elements
							next = null;
						}
					}
				}
			}
			return next != null;
		}

		@Override
		public String nextElement() {
			if (hasMoreElements()) {
				String result = next;
				next = null;
				return result;
			} else {
				throw new NoSuchElementException();
			}
		}
	}

	public static final Control CONTROL = new CombinedResourceBundleControl();

	private final Properties properties;

	protected CombinedResourceBundle(Properties properties) {
		this.properties = properties;
	}

	@Override
	public Enumeration<String> getKeys() {
		ResourceBundle parent = this.parent;
		return new CombinedResourceBundleEnumeration(properties.keySet(), (parent != null) ? parent.getKeys() : null);
	}

	@Override
	protected Object handleGetObject(String key) {
		return properties.get(key);
	}
}