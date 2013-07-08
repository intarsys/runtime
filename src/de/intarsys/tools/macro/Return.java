package de.intarsys.tools.macro;

public class Return extends MacroControlFlow {

	final private Object result;

	public Return(Object result) {
		this.result = result;
	}

	public Object getResult() {
		return result;
	}

}
