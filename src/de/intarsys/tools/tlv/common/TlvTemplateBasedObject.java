package de.intarsys.tools.tlv.common;

/**
 * An object that is based on a {@link TlvTemplate}, a sequence of
 * {@link TlvElement} instances.
 * 
 */
public abstract class TlvTemplateBasedObject {

	final private TlvTemplate template;

	public TlvTemplateBasedObject(TlvTemplate template) {
		this.template = template;
	}

	protected TlvElement getElementAt(int index) {
		return template.getElementAt(index);
	}

	/**
	 * The {@link TlvElement} identified by identifier or <code>null</code>.
	 * 
	 * @param identifier
	 * @return The {@link TlvElement} identified by identifier or
	 *         <code>null</code>.
	 */
	protected TlvElement getElementTagged(int identifier) {
		return template.getElementTagged(identifier);
	}

	public TlvTemplate getTemplate() {
		return template;
	}

}
