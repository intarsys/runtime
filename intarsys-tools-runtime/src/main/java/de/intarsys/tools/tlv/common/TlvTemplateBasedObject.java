package de.intarsys.tools.tlv.common;

import java.util.Iterator;

import de.intarsys.tools.collection.ConversionIterator;

/**
 * An object that is based on a {@link TlvTemplate}, a sequence of
 * {@link TlvElement} instances.
 * 
 */
public abstract class TlvTemplateBasedObject extends TlvBasedObject {

	private final TlvTemplate template;

	protected TlvTemplateBasedObject(TlvTemplate template) {
		this.template = template;
	}

	protected TlvElementBasedObject createTargetObject(TlvElement sourceObject) {
		return new OpaqueObject(sourceObject);
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

	public Iterator<TlvElementBasedObject> getObjects() {
		return new ConversionIterator<TlvElement, TlvElementBasedObject>(getTemplate().iterator()) {
			@Override
			protected TlvElementBasedObject createTargetObject(TlvElement sourceObject) {
				return TlvTemplateBasedObject.this.createTargetObject(sourceObject);
			}
		};
	}

	public TlvTemplate getTemplate() {
		return template;
	}

	@Override
	protected void toStringMembers(StringBuilder sb, int level) {
		Iterator<TlvElementBasedObject> it = getObjects();
		int index = 0;
		while (it.hasNext()) {
			toStringMember(sb, level, "[" + index++ + "]", it.next(), null);
		}
	}

	@Override
	protected void toStringPrimitive(StringBuilder sb, int level) {
	}
}
