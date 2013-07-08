package de.intarsys.tools.component;

/**
 * Expire at the predefined moment in time expireAt without any exception. This
 * condition can't be reset.
 * 
 */
public class ExpireAt extends ExpirationPredicate {

	final private long expireAt;

	public ExpireAt(long expireAt) {
		super();
		this.expireAt = expireAt;
	}

	@Override
	public ExpirationPredicate copy() {
		return new ExpireAt(getExpireAt());
	}

	public long getExpireAt() {
		return expireAt;
	}

	@Override
	public String getType() {
		return "at";
	}

	@Override
	public long getValue() {
		return getExpireAt();
	}

	@Override
	public boolean isExpired() {
		return System.currentTimeMillis() > getExpireAt();
	}

	@Override
	protected void toString(StringBuilder sb) {
		sb.append("at(");
		sb.append(expireAt);
		sb.append(")");
	}

	@Override
	public void touch() {
	}

}
