package de.intarsys.tools.expression;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.intarsys.tools.functor.Args;

public class TestNamespaceNotFound {

	@Test
	public void nested() throws Exception {

		MapResolver resolver = new MapResolver(true);
		resolver.put("string", "value"); //$NON-NLS-1$ //$NON-NLS-2$
		resolver.put("integer", Integer.valueOf(1)); //$NON-NLS-1$
		resolver.put("float", Float.valueOf(1.123456f)); //$NON-NLS-1$
		MapResolver container = new MapResolver(true);
		container.put("a", resolver);
		//
		assertThat(container.evaluate("a.string", Args.create()), is("value"));
		try {
			assertThat(container.evaluate("a.foo", Args.create()), is("value"));
			fail("should not resolve");
		} catch (NamespaceNotFound e) {
			fail("not expected");
		} catch (EvaluationException e) {
			//
		}
		try {
			assertThat(container.evaluate("b.value", Args.create()), is("value"));
			fail("should not resolve");
		} catch (NamespaceNotFound e) {
			//
		}
	}

	@Test
	public void scoped_nested() throws Exception {
		ScopedResolver root = new ScopedResolver();
		MapResolver resolver1 = new MapResolver(true);
		root.addResolver(resolver1);
		MapResolver child1 = new MapResolver(true);
		child1.put("1", "x"); //$NON-NLS-1$ //$NON-NLS-2$
		resolver1.put("a", child1); //$NON-NLS-1$ //$NON-NLS-2$
		MapResolver resolver2 = new MapResolver(true);
		root.addResolver(resolver2);
		MapResolver child2 = new MapResolver(true);
		child2.put("2", "y"); //$NON-NLS-1$ //$NON-NLS-2$
		resolver2.put("b", child2); //$NON-NLS-1$ //$NON-NLS-2$
		//
		assertThat(root.evaluate("a.1", Args.create()), is("x"));
		assertThat(root.evaluate("b.2", Args.create()), is("y"));
		try {
			assertThat(root.evaluate("c.1", Args.create()), is("y"));
			fail("should not resolve");
		} catch (NamespaceNotFound e) {
		} catch (EvaluationException e) {
			fail("not expected");
		}
		try {
			assertThat(root.evaluate("a.2", Args.create()), is("y"));
			fail("should not resolve");
		} catch (NamespaceNotFound e) {
			fail("not expected");
		} catch (EvaluationException e) {
		}
	}

	@Test
	public void scoped_single() throws Exception {
		ScopedResolver root = new ScopedResolver();
		MapResolver resolver1 = new MapResolver(true);
		resolver1.put("a", "x"); //$NON-NLS-1$ //$NON-NLS-2$
		root.addResolver(resolver1);
		MapResolver resolver2 = new MapResolver(true);
		resolver2.put("b", "y"); //$NON-NLS-1$ //$NON-NLS-2$
		root.addResolver(resolver2);
		//
		assertThat(root.evaluate("a", Args.create()), is("x"));
		assertThat(root.evaluate("b", Args.create()), is("y"));
		try {
			assertThat(root.evaluate("c", Args.create()), is("y"));
			fail("should not resolve");
		} catch (NamespaceNotFound e) {
			fail("not expected");
		} catch (EvaluationException e) {
			//
		}
	}

	@Test
	public void single_level() throws Exception {
		MapResolver resolver = new MapResolver(true);
		resolver.put("string", "value"); //$NON-NLS-1$ //$NON-NLS-2$
		resolver.put("integer", Integer.valueOf(1)); //$NON-NLS-1$
		resolver.put("float", Float.valueOf(1.123456f)); //$NON-NLS-1$
		//
		assertThat(resolver.evaluate("string", Args.create()), is("value"));
		try {
			assertThat(resolver.evaluate("foo", Args.create()), is("value"));
			fail("should not resolve");
		} catch (NamespaceNotFound e) {
			fail("not expected");
		} catch (EvaluationException e) {
			//
		}
	}

}
