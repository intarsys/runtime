package de.intarsys.tools.component;

/**
 * Expire if predefined idle time in milliseconds "expireAfter" has expired
 * before we experience another "touch".
 * <p>
 * expireAfter <= 0 will never expire.
 * 
 */
public class ExpireTimeout extends ExpirationPredicate {

	private long touched;

	final private long expireAfter;

	public ExpireTimeout(long expireAfter) {
		super();
		this.expireAfter = expireAfter;
		touch();
	}

	@Override
	public ExpirationPredicate copy() {
		return new ExpireTimeout(getExpireAfter());
	}

	public long getExpireAfter() {
		return expireAfter;
	}

	public long getTouched() {
		return touched;
	}

	@Override
	public String getType() {
		return "timeout";
	}

	@Override
	public long getValue() {
		return getExpireAfter();
	}

	@Override
	public boolean isExpired() {
		if (getExpireAfter() <= 0) {
			return false;
		}
		return System.currentTimeMillis() > getTouched() + getExpireAfter();
	}

	@Override
	protected void toString(StringBuilder sb) {
		sb.append("timeout(");
		sb.append(expireAfter);
		sb.append(")");
	}

	@Override
	public void touch() {
		touched = System.currentTimeMillis();
	}
}
