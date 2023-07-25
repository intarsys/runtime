package de.intarsys.tools.infoset;

public class Child implements IElementSerializable {

	private String a;

	private int b;

	public Child(String a, int b) {
		super();
		this.a = a;
		this.b = b;
	}

	public String getA() {
		return a;
	}

	public int getB() {
		return b;
	}

	@Override
	public void serialize(IElement element) throws ElementSerializationException {
		element.setAttributeValue("a", getA());
		element.setAttributeValue("b", String.valueOf(getB()));
	}

	public void setA(String a) {
		this.a = a;
	}

	public void setB(int b) {
		this.b = b;
	}

}
