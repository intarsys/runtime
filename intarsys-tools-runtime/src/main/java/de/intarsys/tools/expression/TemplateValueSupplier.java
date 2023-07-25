package de.intarsys.tools.expression;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Supplier;

import de.intarsys.tools.functor.Args;

public class TemplateValueSupplier implements Supplier<String> {

	private String template;
	private boolean urlEncode;

	public TemplateValueSupplier() {
		super();
	}

	@Override
	public String get() {
		TaggedStringEvaluator evaluator = TaggedStringEvaluator.decorateLenient(ExpressionEvaluator.get(Mode.UNTRUSTED),
				new ConstantResolver(null));

		try {
			Object result = evaluator.evaluate(getTemplate(), Args.create());
			if (result == null) {
				return null;
			}

			if (result instanceof String) {
				String value = (String) result;
				if (isUrlEncode()) {
					value = URLEncoder.encode(value, "ASCII");
				}
				return value;
			}

			throw new IllegalArgumentException("Expression does not evaluate to a String value");
		} catch (EvaluationException e) {
			throw new IllegalArgumentException("Expression evaluation exception", e);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Error encoding value", e);
		}
	}

	public String getTemplate() {
		return template;
	}

	public boolean isUrlEncode() {
		return urlEncode;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setUrlEncode(boolean urlEncode) {
		this.urlEncode = urlEncode;
	}

}
