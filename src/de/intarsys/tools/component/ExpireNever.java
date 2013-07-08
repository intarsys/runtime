package de.intarsys.tools.component;

/**
 * Expire never.
 * 
 */
public class ExpireNever extends ExpirationPredicate {

	@Override
	public ExpirationPredicate copy() {
		return new ExpireNever();
	}

	@Override
	public String getType() {
		return "never";
	}

	@Override
	public long getValue() {
		return 0;
	}

	@Override
	public boolean isExpired() {
		return false;
	}

	@Override
	protected void toString(StringBuilder sb) {
		sb.append("never(");
		sb.append(")");
	}

	@Override
	public void touch() {
	}
}
