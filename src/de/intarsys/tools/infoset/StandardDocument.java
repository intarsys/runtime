package de.intarsys.tools.infoset;

import java.io.StringWriter;

import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.IStringEvaluatorAccess;
import de.intarsys.tools.expression.TaggedStringEvaluator;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.stream.StreamTools;

public class StandardDocument implements IDocument, IStringEvaluatorAccess {

	private IStringEvaluator stringEvaluator;

	private IStringEvaluator templateEvaluator;

	private StandardElement rootElement;

	public String asXML() {
		StringWriter writer = null;
		try {
			writer = new StringWriter();
			XMLWriter xmlWriter = new XMLWriter(writer);
			xmlWriter.write(this);
			return writer.toString();
		} catch (Exception e) {
			return "<error>";
		} finally {
			StreamTools.close(writer);
		}
	}

	protected Object evaluate(String value) {
		if (templateEvaluator == null || value == null) {
			return value;
		}
		if (value.indexOf('$') >= 0) {
			try {
				return templateEvaluator.evaluate(value, Args.create());
			} catch (Exception e) {
				return "<error expanding '" + value + "'"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return value;
	}

	@Override
	public IElement getRootElement() {
		return rootElement;
	}

	public IStringEvaluator getStringEvaluator() {
		return stringEvaluator;
	}

	@Override
	public void setRootElement(IElement element) {
		this.rootElement = (StandardElement) element;
		((StandardElement) element).setDocument(this);
	}

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
		return asXML();
	}

}
