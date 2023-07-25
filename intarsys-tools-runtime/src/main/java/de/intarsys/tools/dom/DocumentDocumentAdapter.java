package de.intarsys.tools.dom;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.IStringEvaluatorAccess;
import de.intarsys.tools.infoset.IDocument;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.xml.TransformerTools;

/**
 * Adapt a W3C document do {@link IDocument}
 *
 */
public class DocumentDocumentAdapter implements IDocument, IStringEvaluatorAccess {

	private final Document document;

	private IStringEvaluator stringEvaluator;

	public DocumentDocumentAdapter(Document document) {
		super();
		this.document = document;
	}

	@Override
	public String asXML() {
		try (StringWriter output = new StringWriter()) {
			TransformerTools
					.createSecureTransformerFactory()
					.newTransformer()
					.transform(new DOMSource(document), new StreamResult(output));
			return output.toString();
		} catch (TransformerException | IOException e) {
			return "<error>";
		}
	}

	public Document getDocument() {
		return document;
	}

	@Override
	public IElement getRootElement() {
		return new ElementElementAdapter(document.getDocumentElement(), stringEvaluator);
	}

	@Override
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

	@Override
	public void setStringEvaluator(IStringEvaluator evaluator) {
		this.stringEvaluator = evaluator;
	}

	@Override
	public String toString() {
		return asXML();
	}
}
