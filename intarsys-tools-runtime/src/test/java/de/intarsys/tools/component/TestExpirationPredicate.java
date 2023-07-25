package de.intarsys.tools.component;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ObjectCreationException;
import junit.framework.TestCase;

public class TestExpirationPredicate extends TestCase {

	public void testCreate() throws ObjectCreationException {
		ExpirationPredicate result;
		ExpirationPredicate value;
		ExpirationPredicate defaultValue;
		IArgs args;
		//
		args = Args.create();
		defaultValue = null;
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result == null);
		//
		args = Args.create();
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result == defaultValue);
		//
		args = Args.create();
		value = new ExpireNever();
		args.put("expire", value);
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result == value);
		//
		args = Args.create();
		value = new ExpireNever();
		args.put("expire", value);
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "expire", defaultValue);
		assertTrue(result == value);
		//
		args = Args.create();
		value = new ExpireNever();
		args.put("fooExpire", value);
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "fooExpire", defaultValue);
		assertTrue(result == value);
		//
		//
		args = Args.create();
		args.put("expireType", "after");
		args.put("expireValue", "1");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireAfter);
		assertTrue(((ExpireAfter) result).getValue() == 1);
		//
		args = Args.create();
		args.put("expireType", "at");
		args.put("expireValue", "2");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireAt);
		assertTrue(((ExpireAt) result).getValue() == 2);
		//
		args = Args.create();
		args.put("expireType", "timeout");
		args.put("expireValue", "3");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireTimeout);
		assertTrue(((ExpireTimeout) result).getValue() == 3);
		//
		args = Args.create();
		args.put("expireType", "usage");
		args.put("expireValue", "4");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireUsage);
		assertTrue(((ExpireUsage) result).getValue() == 4);
		//
		args = Args.create();
		args.put("expireType", "never");
		args.put("expireValue", "5");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireNever);
		//
		//
		args = Args.create();
		args.put("expireType", "after");
		args.put("expireValue", "1");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "expire", defaultValue);
		assertTrue(result instanceof ExpireAfter);
		assertTrue(((ExpireAfter) result).getValue() == 1);
		//
		args = Args.create();
		args.put("expireType", "at");
		args.put("expireValue", "2");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "expire", defaultValue);
		assertTrue(result instanceof ExpireAt);
		assertTrue(((ExpireAt) result).getValue() == 2);
		//
		args = Args.create();
		args.put("expireType", "timeout");
		args.put("expireValue", "3");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "expire", defaultValue);
		assertTrue(result instanceof ExpireTimeout);
		assertTrue(((ExpireTimeout) result).getValue() == 3);
		//
		args = Args.create();
		args.put("expireType", "usage");
		args.put("expireValue", "4");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "expire", defaultValue);
		assertTrue(result instanceof ExpireUsage);
		assertTrue(((ExpireUsage) result).getValue() == 4);
		//
		args = Args.create();
		args.put("expireType", "never");
		args.put("expireValue", "5");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "expire", defaultValue);
		assertTrue(result instanceof ExpireNever);
		//
		//
		args = Args.create();
		args.put("fooExpireType", "after");
		args.put("fooExpireValue", "1");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "fooExpire", defaultValue);
		assertTrue(result instanceof ExpireAfter);
		assertTrue(((ExpireAfter) result).getValue() == 1);
		//
		args = Args.create();
		args.put("fooExpireType", "at");
		args.put("fooExpireValue", "2");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "fooExpire", defaultValue);
		assertTrue(result instanceof ExpireAt);
		assertTrue(((ExpireAt) result).getValue() == 2);
		//
		args = Args.create();
		args.put("fooExpireType", "timeout");
		args.put("fooExpireValue", "3");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "fooExpire", defaultValue);
		assertTrue(result instanceof ExpireTimeout);
		assertTrue(((ExpireTimeout) result).getValue() == 3);
		//
		args = Args.create();
		args.put("fooExpireType", "usage");
		args.put("fooExpireValue", "4");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "fooExpire", defaultValue);
		assertTrue(result instanceof ExpireUsage);
		assertTrue(((ExpireUsage) result).getValue() == 4);
		//
		args = Args.create();
		args.put("fooExpireType", "never");
		args.put("fooExpireValue", "5");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "fooExpire", defaultValue);
		assertTrue(result instanceof ExpireNever);
		//
		//
		args = Args.create();
		args.put("fooExpire", "type =after; value = 1");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "fooExpire", defaultValue);
		assertTrue(result instanceof ExpireAfter);
		assertTrue(((ExpireAfter) result).getValue() == 1);
		//
		args = Args.create();
		args.put("fooExpire", "type =at; value = 2");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "fooExpire", defaultValue);
		assertTrue(result instanceof ExpireAt);
		assertTrue(((ExpireAt) result).getValue() == 2);
		//
		args = Args.create();
		args.put("fooExpire", "type =timeout; value = 3");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "fooExpire", defaultValue);
		assertTrue(result instanceof ExpireTimeout);
		assertTrue(((ExpireTimeout) result).getValue() == 3);
		//
		args = Args.create();
		args.put("fooExpire", "type =usage; value = 4");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "fooExpire", defaultValue);
		assertTrue(result instanceof ExpireUsage);
		assertTrue(((ExpireUsage) result).getValue() == 4);
		//
		args = Args.create();
		args.put("fooExpire", "type =never; value = 5");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, "fooExpire", defaultValue);
		assertTrue(result instanceof ExpireNever);
	}

	public void testCreateComposite() throws ObjectCreationException {
		ExpirationPredicate result;
		ExpirationPredicate value;
		ExpirationPredicate defaultValue;
		IArgs args;
		//
		args = Args.create();
		args.put("expireType", "or");
		args.put("expireValue",
				Args.createNamed( //
						"op1", Args.createNamed("type", "timeout", "value", "1"), //
						"op2", Args.createNamed("type", "usage", "value", "2"))); //
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireOr);
		value = ((ExpireOr) result).getOp1();
		assertTrue(((ExpireTimeout) value).getValue() == 1);
		value = ((ExpireOr) result).getOp2();
		assertTrue(((ExpireUsage) value).getValue() == 2);
		//
		args = Args.create();
		args.put("expire",
				Args.createNamed("type", "or", "value", //
						Args.createNamed( //
								"op1", Args.createNamed("type", "timeout", "value", "1"), //
								"op2", Args.createNamed("type", "usage", "value", "2")))); //
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireOr);
		value = ((ExpireOr) result).getOp1();
		assertTrue(((ExpireTimeout) value).getValue() == 1);
		value = ((ExpireOr) result).getOp2();
		assertTrue(((ExpireUsage) value).getValue() == 2);
		//
		args = Args.create();
		args.put("expireType", "and");
		args.put("expireValue", "op1.type=timeout;op1.value=1;op2.type=usage;op2.value=2");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireAnd);
		value = ((ExpireAnd) result).getOp1();
		assertTrue(((ExpireTimeout) value).getValue() == 1);
		value = ((ExpireAnd) result).getOp2();
		assertTrue(((ExpireUsage) value).getValue() == 2);
		//
		args = Args.create();
		args.put("expireType", "or");
		args.put("expireValue", "op1.type=timeout;op1.value=1;op2.type=usage;op2.value=2");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireOr);
		value = ((ExpireOr) result).getOp1();
		assertTrue(((ExpireTimeout) value).getValue() == 1);
		value = ((ExpireOr) result).getOp2();
		assertTrue(((ExpireUsage) value).getValue() == 2);
	}

	public void testCreateDeprecated() throws ObjectCreationException {
		ExpirationPredicate result;
		ExpirationPredicate value;
		ExpirationPredicate defaultValue;
		IArgs args;
		//
		args = Args.create();
		args.put("expireAfter", "1");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireAfter);
		assertTrue(((ExpireAfter) result).getValue() == 1);
		//
		args = Args.create();
		args.put("expireAt", "2");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireAt);
		assertTrue(((ExpireAt) result).getValue() == 2);
		//
		args = Args.create();
		args.put("expireTimeout", "3");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireTimeout);
		assertTrue(((ExpireTimeout) result).getValue() == 3);
		//
		args = Args.create();
		args.put("expireUsage", "4");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireUsage);
		assertTrue(((ExpireUsage) result).getValue() == 4);
		//
		args = Args.create();
		args.put("expireNever", "5");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireNever);
		//
		args = Args.create();
		args.put("expireUsage", "1");
		args.put("expireTimeout", "2");
		defaultValue = new ExpireNever();
		result = ExpirationPredicate.create(args, defaultValue);
		assertTrue(result instanceof ExpireOr);
		assertTrue(((ExpireOr) result).getOp1() instanceof ExpireTimeout);
		assertTrue(((ExpireOr) result).getOp2() instanceof ExpireUsage);
	}
}
