package de.intarsys.tools.dom;

import org.w3c.dom.Attr;

import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.IStringEvaluatorAccess;
import de.intarsys.tools.expression.StringEvaluatorTools;
import de.intarsys.tools.expression.TaggedStringEvaluator;
import de.intarsys.tools.infoset.IAttribute;

/**
 * Adapt a W3C Attr to {@link IAttribute}
 * 
 */
public class AttributeAttributeAdapter implements IAttribute, IStringEvaluatorAccess {

	private final Attr attr;

	private IStringEvaluator stringEvaluator;

	private IStringEvaluator templateEvaluator;

	public AttributeAttributeAdapter(Attr attribute) {
		super();
		this.attr = attribute;
	}

	public AttributeAttributeAdapter(Attr attribute, IStringEvaluator evaluator) {
		super();
		this.attr = attribute;
		setStringEvaluator(evaluator);
	}

	protected Object evaluate(String value) {
		return StringEvaluatorTools.evaluateString(templateEvaluator, value);
	}

	public Attr getAttr() {
		return attr;
	}

	@Override
	public Object getData() {
		return evaluate(attr.getValue());
	}

	@Override
	public String getName() {
		return attr.getName();
	}

	@Override
	public IStringEvaluator getStringEvaluator() {
		return stringEvaluator;
	}

	@Override
	public String getTemplate() {
		return attr.getValue();
	}

	@Override
	public String getValue() {
		return String.valueOf(evaluate(attr.getValue()));
	}

	@Override
	public void setStringEvaluator(IStringEvaluator evaluator) {
		this.stringEvaluator = evaluator;
		if (stringEvaluator == null) {
			this.templateEvaluator = null;
		} else {
			this.templateEvaluator = TaggedStringEvaluator.decorate(evaluator);
		}
	}

	@Override
	public String toString() {
		return getName() + "=\"" + getTemplate() + "\"";
	}

}
