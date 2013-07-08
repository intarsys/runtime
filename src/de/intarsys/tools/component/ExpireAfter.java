package de.intarsys.tools.component;

/**
 * Expire after the predefined period expireAfter without any exception. This
 * condition can't be reset.
 * 
 */
public class ExpireAfter extends ExpirationPredicate {

	final private long created = System.currentTimeMillis();

	final private long expireAfter;

	public ExpireAfter(long expireAfter) {
		super();
		this.expireAfter = expireAfter;
	}

	@Override
	public ExpirationPredicate copy() {
		return new ExpireAfter(getExpireAfter());
	}

	public long getCreated() {
		return created;
	}

	public long getExpireAfter() {
		return expireAfter;
	}

	@Override
	public String getType() {
		return "after";
	}

	@Override
	public long getValue() {
		return getExpireAfter();
	}

	@Override
	public boolean isExpired() {
		return System.currentTimeMillis() > getCreated() + getExpireAfter();
	}

	@Override
	protected void toString(StringBuilder sb) {
		sb.append("after(");
		sb.append(expireAfter);
		sb.append(")");
	}

	@Override
	public void touch() {
	}
}
