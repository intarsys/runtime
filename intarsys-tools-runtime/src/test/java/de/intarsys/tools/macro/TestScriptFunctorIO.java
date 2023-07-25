package de.intarsys.tools.macro;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.ElementFactory;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementSerializable;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.string.TestTools;
import junit.framework.TestCase;

public class TestScriptFunctorIO extends TestCase {

	class TestFunctor implements IFunctor, IElementSerializable {
		private Object result;
		private boolean executed;

		public TestFunctor(Object result) {
			this.result = result;
		}

		public Object getResult() {
			return result;
		}

		public boolean isExecuted() {
			return executed;
		}

		@Override
		public Object perform(IFunctorCall call) throws FunctorException {
			executed = true;
			return result;
		}

		@Override
		public void serialize(IElement element) throws ElementSerializationException {
			element.setAttributeValue("result", String.valueOf(result));
		}
	}

	protected void checkSource(MacroBlock block, String name) throws ElementSerializationException, IOException {
		String source;
		IElement element = ElementFactory.get().createElement("script");
		block.serialize(element);
		source = element.toString();
		assertThat(TestTools.canonicalize(getSource(name)), equalTo(TestTools.canonicalize(source)));
	}

	protected String getSource(String name) throws IOException {
		InputStream is = getClass().getResourceAsStream(name);
		return StreamTools.getString(is, "UTF-8");
	}

	public void testBlockAssign() throws Exception {
		MacroBlock block;
		String source;
		TestFunctor b1;
		TestFunctor b2;
		TestFunctor b3;
		TestFunctor e1;
		TestFunctor e2;
		TestFunctor e3;
		TestFunctor f1;
		TestFunctor f2;
		TestFunctor f3;
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		//
		block = new MacroBlock();
		block.addBlockExpression(new MacroAssign(b1, "rb1"));
		checkSource(block, "s2assign.script");
		//
		block = new MacroBlock();
		block.addBlockExpression(new MacroAssign(b1, "rb1"));
		block.addBlockExpression(new MacroAssign(b2, "rb2"));
		block.addBlockExpression(new MacroAssign(b3, "rb3"));
		checkSource(block, "s3assign.script");
		//
		block = new MacroBlock();
		block.addBlockExpression(new MacroAssign(b1, "rb1"));
		block.addBlockExpression(new MacroAssign(b2, "rb2"));
		block.addBlockExpression(new MacroAssign(b3, "rb3"));
		block.addFinallyExpression(new MacroAssign(f1, "rf1"));
		block.addFinallyExpression(new MacroAssign(f2, "rf2"));
		block.addFinallyExpression(new MacroAssign(f3, "rf3"));
		checkSource(block, "s4assign.script");
		//
		block = new MacroBlock();
		block.addBlockExpression(new MacroAssign(b1, "rb1"));
		block.addBlockExpression(new MacroAssign(b2, "rb2"));
		block.addBlockExpression(new MacroAssign(b3, "rb3"));
		block.addErrorExpression(new MacroAssign(e1, "re1"));
		block.addErrorExpression(new MacroAssign(e2, "re2"));
		block.addErrorExpression(new MacroAssign(e3, "re3"));
		block.addFinallyExpression(new MacroAssign(f1, "rf1"));
		block.addFinallyExpression(new MacroAssign(f2, "rf2"));
		block.addFinallyExpression(new MacroAssign(f3, "rf3"));
		checkSource(block, "s5assign.script");
	}

	public void testBlockSimple() throws Exception {
		MacroBlock block;
		String source;
		TestFunctor b1;
		TestFunctor b2;
		TestFunctor b3;
		TestFunctor e1;
		TestFunctor e2;
		TestFunctor e3;
		TestFunctor f1;
		TestFunctor f2;
		TestFunctor f3;
		b1 = new TestFunctor("b1");
		b2 = new TestFunctor("b2");
		b3 = new TestFunctor("b3");
		e1 = new TestFunctor("e1");
		e2 = new TestFunctor("e2");
		e3 = new TestFunctor("e3");
		f1 = new TestFunctor("f1");
		f2 = new TestFunctor("f2");
		f3 = new TestFunctor("f3");
		//
		block = new MacroBlock();
		checkSource(block, "s1.script");
		//
		block = new MacroBlock();
		block.addBlockExpression(b1);
		checkSource(block, "s2.script");
		//
		block = new MacroBlock();
		block.addBlockExpression(b1);
		block.addBlockExpression(b2);
		block.addBlockExpression(b3);
		checkSource(block, "s3.script");
		//
		block = new MacroBlock();
		block.addBlockExpression(b1);
		block.addBlockExpression(b2);
		block.addBlockExpression(b3);
		block.addFinallyExpression(f1);
		block.addFinallyExpression(f2);
		block.addFinallyExpression(f3);
		checkSource(block, "s4.script");
		//
		block = new MacroBlock();
		block.addBlockExpression(b1);
		block.addBlockExpression(b2);
		block.addBlockExpression(b3);
		block.addErrorExpression(e1);
		block.addErrorExpression(e2);
		block.addErrorExpression(e3);
		block.addFinallyExpression(f1);
		block.addFinallyExpression(f2);
		block.addFinallyExpression(f3);
		checkSource(block, "s5.script");
	}
}
