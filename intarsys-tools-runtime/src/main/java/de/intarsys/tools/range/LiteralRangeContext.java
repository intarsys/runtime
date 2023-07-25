package de.intarsys.tools.range;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;

public class LiteralRangeContext implements IRangeContext {

	public static LiteralRangeContext create(IArgs args) {
		return new LiteralRangeContext(args);
	}

	private final int current;

	private final int max;

	private final int min;

	public LiteralRangeContext(IArgs args) {
		this(
				ArgTools.getIntStrict(args, "min", 0),
				ArgTools.getIntStrict(args, "max", 0),
				ArgTools.getIntStrict(args, "current", 0));
	}

	public LiteralRangeContext(int min, int max, int current) {
		super();
		this.current = current;
		this.max = max;
		this.min = min;
	}

	@Override
	public int getCurrent() {
		return current;
	}

	@Override
	public int getMax() {
		return max;
	}

	@Override
	public int getMin() {
		return min;
	}

}
