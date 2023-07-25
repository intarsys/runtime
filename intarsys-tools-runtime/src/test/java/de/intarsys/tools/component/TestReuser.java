package de.intarsys.tools.component;

import org.junit.Assert;
import org.junit.Test;

public class TestReuser {

	@Test
	public void testDifferentID() {
		ReferenceCounted<Foo> reuser1;
		ReferenceCounted<Foo> reuser2;
		ReferenceCounted<Foo> reuser3;
		Foo foo1;
		Foo foo2;
		Foo foo3;
		//
		reuser1 = ReferenceCounted.get(Foo.class, "schnick", () -> new Foo(), (x) -> x.close());
		reuser1.acquire();
		foo1 = reuser1.get();
		reuser2 = ReferenceCounted.get(Foo.class, "schnack", () -> new Foo(), (x) -> x.close());
		reuser2.acquire();
		foo2 = reuser2.get();
		reuser3 = ReferenceCounted.get(Foo.class, "schnuck", () -> new Foo(), (x) -> x.close());
		reuser3.acquire();
		foo3 = reuser3.get();
		Assert.assertTrue(reuser1 != reuser2);
		Assert.assertTrue(reuser2 != reuser3);
		Assert.assertTrue(foo1 != foo2);
		Assert.assertTrue(foo2 != foo3);
		Assert.assertTrue(reuser1.getCounter() == 1);
		Assert.assertTrue(reuser2.getCounter() == 1);
		Assert.assertTrue(reuser3.getCounter() == 1);
		Assert.assertTrue(!foo1.isClosed());
		Assert.assertTrue(!foo2.isClosed());
		Assert.assertTrue(!foo3.isClosed());
		reuser1.release(null);
		reuser2.release(null);
		reuser3.release(null);
		try {
			reuser1.release(null);
			Assert.fail("must fail");
		} catch (Exception e) {
			//
		}
		try {
			reuser2.release(null);
			Assert.fail("must fail");
		} catch (Exception e) {
			//
		}
		try {
			reuser3.release(null);
			Assert.fail("must fail");
		} catch (Exception e) {
			//
		}
		Assert.assertTrue(reuser1.getCounter() == 0);
		Assert.assertTrue(reuser2.getCounter() == 0);
		Assert.assertTrue(reuser3.getCounter() == 0);
		Assert.assertTrue(foo1.isClosed());
		Assert.assertTrue(foo2.isClosed());
		Assert.assertTrue(foo3.isClosed());
	}

	@Test
	public void testSameID() {
		ReferenceCounted<Foo> reuser1;
		ReferenceCounted<Foo> reuser2;
		ReferenceCounted<Foo> reuser3;
		Foo foo1;
		Foo foo2;
		Foo foo3;
		//
		reuser1 = ReferenceCounted.get(Foo.class, "schnick", () -> new Foo(), (x) -> x.close());
		reuser1.acquire();
		foo1 = reuser1.get();
		reuser2 = ReferenceCounted.get(Foo.class, "schnick", () -> new Foo(), (x) -> x.close());
		reuser2.acquire();
		foo2 = reuser2.get();
		reuser3 = ReferenceCounted.get(Foo.class, "schnick", () -> new Foo(), (x) -> x.close());
		reuser3.acquire();
		foo3 = reuser3.get();
		Assert.assertTrue(reuser1 == reuser2);
		Assert.assertTrue(reuser2 == reuser3);
		Assert.assertTrue(foo1 == foo2);
		Assert.assertTrue(foo2 == foo3);
		Assert.assertTrue(reuser1.getCounter() == 3);
		Assert.assertTrue(!foo1.isClosed());
		reuser1.release(null);
		Assert.assertTrue(reuser1.getCounter() == 2);
		Assert.assertTrue(!foo1.isClosed());
		reuser1.release(null);
		Assert.assertTrue(reuser1.getCounter() == 1);
		Assert.assertTrue(!foo1.isClosed());
		reuser1.release(null);
		Assert.assertTrue(reuser1.getCounter() == 0);
		Assert.assertTrue(foo1.isClosed());
		try {
			reuser1.release(null);
			Assert.fail("must fail");
		} catch (Exception e) {
			//
		}
	}

}
