package de.intarsys.tools.component;

import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementSerializable;

/**
 * The logical "or" of two {@link ExpirationPredicate} instances.
 * 
 * The predicate is one of the operands is expired.
 * 
 */
public class ExpireOr extends ExpirationPredicate {

	final private ExpirationPredicate op1;

	final private ExpirationPredicate op2;

	public ExpireOr(ExpirationPredicate op1, ExpirationPredicate op2) {
		super();
		this.op1 = op1;
		this.op2 = op2;
	}

	@Override
	public ExpirationPredicate copy() {
		return new ExpireOr(op1.copy(), op2.copy());
	}

	public ExpirationPredicate getOp1() {
		return op1;
	}

	public ExpirationPredicate getOp2() {
		return op2;
	}

	@Override
	public String getType() {
		return "or";
	}

	@Override
	public long getValue() {
		return 0;
	}

	@Override
	public boolean isExpired() {
		if (op1.isExpired()) {
			return true;
		}
		if (op2.isExpired()) {
			return true;
		}
		return false;
	}

	@Override
	public void serialize(IElement element)
			throws ElementSerializationException {
		element.setAttributeValue("type", getType());
		IElement child;
		child = element.newElement("op1");
		((IElementSerializable) getOp1()).serialize(child);
		child = element.newElement("op2");
		((IElementSerializable) getOp2()).serialize(child);
	}

	@Override
	protected void toString(StringBuilder sb) {
		sb.append("or(");
		sb.append(op1);
		sb.append(", ");
		sb.append(op2);
		sb.append(")");
	}

	@Override
	public void touch() {
		op1.touch();
		op2.touch();
	}

}
