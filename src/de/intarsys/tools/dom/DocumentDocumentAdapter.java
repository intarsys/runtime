package de.intarsys.tools.dom;

import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.IStringEvaluatorAccess;
import de.intarsys.tools.expression.TaggedStringEvaluator;
import de.intarsys.tools.infoset.IDocument;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.stream.StreamTools;

/**
 * Adapt a W3C document do {@link IDocument}
 * 
 */
public class DocumentDocumentAdapter implements IDocument,
		IStringEvaluatorAccess {

	final private Document document;

	private IStringEvaluator stringEvaluator;

	private IStringEvaluator templateEvaluator;

	public DocumentDocumentAdapter(Document document) {
		super();
		this.document = document;
	}

	public String asXML() {
		StringWriter writer = null;
		try {
			writer = new StringWriter();
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			Result result = new StreamResult(writer);
			Source source = new DOMSource(document);
			transformer.transform(source, result);
			return writer.toString();
		} catch (TransformerException e) {
			return "<error>";
		} finally {
			StreamTools.close(writer);
		}
	}

	public Document getDocument() {
		return document;
	}

	@Override
	public IElement getRootElement() {
		return new ElementElementAdapter(document.getDocumentElement(),
				stringEvaluator);
	}

	public IStringEvaluator getStringEvaluator() {
		return stringEvaluator;
	}

	@Override
	public void setRootElement(IElement element) {
		Element w3cElement = ((ElementElementAdapter) element).getElement();
		Element tempElement = (Element) document.importNode(w3cElement, true);
		document.removeChild(document.getDocumentElement());
		document.appendChild(tempElement);
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
