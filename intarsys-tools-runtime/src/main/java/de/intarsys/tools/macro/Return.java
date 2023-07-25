package de.intarsys.tools.macro;

public class Return extends MacroControlFlow {

	private final transient Object result;

	public Return(Object result) {
		this.result = result;
	}

	public Object getResult() {
		return result;
	}

}
