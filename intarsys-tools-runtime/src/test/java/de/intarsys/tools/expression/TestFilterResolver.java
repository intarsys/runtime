package de.intarsys.tools.expression;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;

public class TestFilterResolver {

	@Test
	public void testSimple() {
		MapResolver map = MapResolver.createStrict();
		map.put("var1", "value1");
		IStringEvaluator filter = new FilterResolver("local", map);
		TaggedStringEvaluator evaluator = TaggedStringEvaluator.decorate(filter);
		evaluator.setStrict(false);
		IArgs args = Args.create();
		args.put("x", "constant");
		args.put("y", "inline ${var1}");
		args.put("z1", "inline ${local.var1}");
		args.put("z2", "inline ${local.var2}");
		args = ArgTools.expandDeep(args, evaluator);
		assertThat(args.get("x"), is("constant"));
		assertThat(args.get("y"), is("inline ${var1}"));
		assertThat(args.get("z1"), is("inline value1"));
		assertThat(args.get("z2"), is("inline ${local.var2}"));
	}
}
