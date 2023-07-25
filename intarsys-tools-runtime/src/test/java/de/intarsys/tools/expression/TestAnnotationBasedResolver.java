package de.intarsys.tools.expression;

import org.junit.Assert;
import org.junit.Test;

import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;

public class TestAnnotationBasedResolver {

	public class TargetAny {
		private String value;

		protected TargetAny(String value) {
			super();
			this.value = value;
		}

		@ResolveProperty(property = "diedel")
		public String getDiedel() {
			return value;
		}

		@ResolveAny()
		public String getDoedel(String var, IArgs args) {
			return "var" + value;
		}
	}

	public class TargetProperty {
		private String value;

		protected TargetProperty(String value) {
			super();
			this.value = value;
		}

		@ResolveProperty(property = "diedel")
		public String getDiedel() {
			return value;
		}

	}

	@Test
	public void testResolveAny() throws Exception {
		ReflectiveResolver resolver;
		TargetAny target;
		String template;
		String result;
		//
		target = new TargetAny("test");
		resolver = new ReflectiveResolver(target);
		template = "diedel";
		result = (String) resolver.evaluate(template, Args.create());
		Assert.assertEquals("test", result);
		//
		target = new TargetAny("test");
		resolver = new ReflectiveResolver(target);
		template = "doedel";
		result = (String) resolver.evaluate(template, Args.create());
		Assert.assertEquals("vartest", result);
	}

	@Test
	public void testResolveProperty() throws Exception {
		ReflectiveResolver resolver;
		TargetProperty target;
		String template;
		String result;
		//
		target = new TargetProperty("test");
		resolver = new ReflectiveResolver(target);
		template = "diedel";
		result = (String) resolver.evaluate(template, Args.create());
		Assert.assertEquals("test", result);
		//
		target = new TargetProperty("test");
		resolver = new ReflectiveResolver(target);
		template = "doedel";
		try {
			result = (String) resolver.evaluate(template, Args.create());
			Assert.fail();
		} catch (Exception e) {
			//
		}
	}

}
