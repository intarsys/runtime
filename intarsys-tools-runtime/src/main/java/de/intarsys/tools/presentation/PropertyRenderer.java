package de.intarsys.tools.presentation;

import java.text.Format;

import de.intarsys.tools.reflect.FieldAccessException;
import de.intarsys.tools.reflect.FieldException;
import de.intarsys.tools.reflect.IFieldHandler;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.string.StringTools;

/**
 * This is a *very* generic tool for displaying object state.
 * <p>
 * The {@link PropertyRenderer} represents the first class "field" (property)
 * object, such as "age" of a person. In this role {@link IPresentationSupport}
 * is implemented, so that you can ask for a label for this field, for example
 * for display in a table.
 * <p>
 * In the role {@link IFieldHandler}, the real value of the field within an
 * object can be queried.
 * 
 */
public class PropertyRenderer
		implements IPresentationSupport, IPresentationHandler, IFieldHandler, IPresentationSupportFactory {

	private static final String LABEL_NOT_AVAILABLE = "<not available>";

	private final String name;

	private final String label;

	private Format format;

	public PropertyRenderer(String name, String label) {
		super();
		this.name = name;
		this.label = label;
	}

	public PropertyRenderer(String name, String label, Format format) {
		super();
		this.name = name;
		this.label = label;
		this.format = format;
	}

	protected String basicGetDescription(Object object) {
		return getTip(object);
	}

	/**
	 * @param object
	 *            The object whose icon is searched
	 * @return
	 */
	protected String basicGetIconName(Object object) {
		return null;
	}

	protected String basicGetLabel(Object value) {
		return StringTools.safeString(value);
	}

	protected String basicGetTip(Object object) {
		return getLabel(object);
	}

	@Override
	public IPresentationSupport createPresentationSupport(Object object) {
		return getValuePresentation(object);
	}

	/*
	 * The description for the field itself
	 * 
	 * @see de.intarsys.tools.presentation.IPresentationSupport#getDescription()
	 */
	@Override
	public String getDescription() {
		return getTip();
	}

	@Override
	public String getDescription(Object object) {
		Object value;
		try {
			value = getValue(object);
		} catch (FieldAccessException e) {
			value = "<error>";
		}
		if (value instanceof IPresentationSupport) {
			return ((IPresentationSupport) value).getDescription();
		}
		return basicGetDescription(value);
	}

	public IPresentationSupport getFieldPresentation() {
		return this;
	}

	public Format getFormat() {
		return format;
	}

	@Override
	public String getIconName() {
		return null;
	}

	@Override
	public String getIconName(Object object) {
		Object value;
		try {
			value = getValue(object);
		} catch (FieldAccessException e) {
			value = "<error>";
		}
		if (value instanceof IPresentationSupport) {
			return ((IPresentationSupport) value).getIconName();
		}
		return basicGetIconName(value);
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getLabel(Object object) {
		Object value;
		try {
			value = getValue(object);
		} catch (FieldAccessException e) {
			value = "<error>";
		}
		if (getFormat() != null) {
			try {
				return getFormat().format(value);
			} catch (Exception e) {
				return LABEL_NOT_AVAILABLE;
			}
		}
		if (value instanceof IPresentationSupport) {
			return ((IPresentationSupport) value).getLabel();
		}
		return basicGetLabel(value);
	}

	public String getName() {
		return name;
	}

	@Override
	public String getTip() {
		return getLabel();
	}

	@Override
	public String getTip(Object object) {
		Object value;
		try {
			value = getValue(object);
		} catch (FieldAccessException e) {
			value = "<error>";
		}
		if (value instanceof IPresentationSupport) {
			return ((IPresentationSupport) value).getTip();
		}
		return basicGetTip(value);
	}

	@Override
	public Object getValue(Object object) throws FieldAccessException {
		try {
			return ObjectTools.get(object, getName());
		} catch (FieldException e) {
			return LABEL_NOT_AVAILABLE; // $NON-NLS-1$
		}
	}

	public IPresentationSupport getValuePresentation(final Object object) {
		return new IPresentationSupport() {

			@Override
			public String getDescription() {
				return PropertyRenderer.this.getDescription(object);
			}

			@Override
			public String getIconName() {
				return PropertyRenderer.this.getIconName(object);
			}

			@Override
			public String getLabel() {
				return PropertyRenderer.this.getLabel(object);
			}

			@Override
			public String getTip() {
				return PropertyRenderer.this.getTip(object);
			}
		};
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	@Override
	public Object setValue(Object receiver, Object value) throws FieldAccessException {
		return null;
	}

}
