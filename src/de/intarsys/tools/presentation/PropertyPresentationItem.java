package de.intarsys.tools.presentation;

import java.text.Format;

import de.intarsys.tools.reflect.FieldException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.valueholder.IValueHolder;

/**
 * This is a *very* generic tool for displaying object state.
 * <p>
 * The {@link PropertyPresentationItem} represents a view on object state, such
 * as the "age" property of a person in combination with its value.
 * <p>
 * The value can be both static (frozen) or dynamic (re-evaluation).
 * <p>
 * For both field and value part the {@link IPresentationSupport} can be
 * requested.
 * 
 */
public class PropertyPresentationItem extends PropertyPresentation implements
		IValueHolder {

	static final private Object DYNAMIC = new Object();

	final private String name;

	final private Object value;

	private Format format;

	final private IPresentationSupport valuePresentation = new IPresentationSupport() {

		@Override
		public String getDescription() {
			return getValueDescription();
		}

		@Override
		public String getIconName() {
			return getValueIconName();
		}

		@Override
		public String getLabel() {
			return getValueLabel();
		}

		@Override
		public String getTip() {
			return getValueTip();
		}
	};

	public PropertyPresentationItem(Object object, String name, String label) {
		super(object, label);
		this.name = name;
		this.value = DYNAMIC;
		this.format = null;
	}

	public PropertyPresentationItem(Object object, String name, String label,
			Format format) {
		super(object, label);
		this.name = name;
		this.value = DYNAMIC;
		this.format = format;
	}

	public PropertyPresentationItem(Object object, String name, String label,
			Object value, Format format) {
		super(object, label);
		this.name = name;
		this.value = value;
		this.format = format;
	}

	public PropertyPresentationItem(String label, Object value) {
		super(null, label);
		this.name = null;
		this.value = value;
		this.format = null;
	}

	public PropertyPresentationItem(String label, Object value, Format format) {
		super(null, label);
		this.name = null;
		this.value = value;
		this.format = format;
	}

	protected String basicGetValueDescription() {
		return getValueTip();
	}

	protected String basicGetValueIconName() {
		return null;
	}

	protected String basicGetValueLabel() {
		return StringTools.safeString(get());
	}

	protected String basicGetValueTip() {
		return getValueLabel();
	}

	@Override
	public Object get() {
		if (value == DYNAMIC) {
			try {
				return ObjectTools.get(getObject(), getName());
			} catch (FieldException e) {
				return "<not available>"; //$NON-NLS-1$
			}
		}
		return value;
	}

	public Format getFormat() {
		return format;
	}

	public String getName() {
		return name;
	}

	public String getValueDescription() {
		Object value = get();
		if (value instanceof IPresentationSupport) {
			return ((IPresentationSupport) value).getDescription();
		}
		return basicGetValueDescription();
	}

	public String getValueIconName() {
		Object value = get();
		if (value instanceof IPresentationSupport) {
			return ((IPresentationSupport) value).getIconName();
		}
		return basicGetValueIconName();
	}

	public String getValueLabel() {
		Object value = get();
		if (getFormat() != null) {
			try {
				return getFormat().format(value);
			} catch (Exception e) {
				return "<not available>";
			}
		}
		if (value instanceof IPresentationSupport) {
			return ((IPresentationSupport) value).getLabel();
		}
		return basicGetValueLabel();
	}

	public IPresentationSupport getValuePresentation() {
		return valuePresentation;
	}

	public String getValueTip() {
		Object value = get();
		if (value instanceof IPresentationSupport) {
			return ((IPresentationSupport) value).getTip();
		}
		return basicGetValueTip();
	}

	@Override
	public Object set(Object newValue) {
		throw new UnsupportedOperationException();
	}

	public void setFormat(Format format) {
		this.format = format;
	}

}
