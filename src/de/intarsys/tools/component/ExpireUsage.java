package de.intarsys.tools.component;

/**
 * Expire after expireMax usages without any exception. This condition can't be
 * reset.
 * <p>
 * expireMax <= 0 will never expire.
 * 
 */
public class ExpireUsage extends ExpirationPredicate {

	final private int expireMax;

	private int usage = 0;

	public ExpireUsage(int max) {
		super();
		this.expireMax = max;
	}

	@Override
	public ExpirationPredicate copy() {
		return new ExpireUsage(getExpireMax());
	}

	public int getExpireMax() {
		return expireMax;
	}

	@Override
	public String getType() {
		return "usage";
	}

	public int getUsage() {
		return usage;
	}

	@Override
	public long getValue() {
		return getExpireMax();
	}

	@Override
	public boolean isExpired() {
		if (getExpireMax() <= 0) {
			return false;
		}
		return getUsage() >= getExpireMax();
	}

	@Override
	protected void toString(StringBuilder sb) {
		sb.append("usage(");
		sb.append(expireMax);
		sb.append(")");
	}

	@Override
	public void touch() {
		usage++;
	}
}
