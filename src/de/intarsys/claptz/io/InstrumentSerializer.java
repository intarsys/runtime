package de.intarsys.claptz.io;

import java.io.IOException;
import java.util.Iterator;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.intarsys.claptz.impl.StandardExtension;
import de.intarsys.claptz.impl.StandardInstrument;
import de.intarsys.claptz.impl.StandardInstrumentPrerequisite;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.IStringEvaluatorAccess;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.serialize.SerializationContext;
import de.intarsys.tools.serialize.xml.XMLSerializer;

public class InstrumentSerializer extends XMLSerializer {

	public InstrumentSerializer(SerializationContext context) {
		super(context);
	}

	public InstrumentSerializer(SerializationContext context,
			boolean createDocument) {
		super(context, createDocument);
	}

	protected void serialize(IElement element, ContentHandler handler)
			throws SAXException {
		IStringEvaluator oldValue = null;
		try {
			if (element instanceof IStringEvaluatorAccess) {
				oldValue = ((IStringEvaluatorAccess) element)
						.getStringEvaluator();
				((IStringEvaluatorAccess) element).setStringEvaluator(null);
			}
			AttributesImpl attrs = new AttributesImpl();
			for (Iterator<String> it = element.attributeNames(); it.hasNext();) {
				String name = it.next();
				addAttribute(attrs, name, element.attributeTemplate(name));
			}
			doStartElement(handler, element.getName(), attrs);
			for (Iterator<IElement> it = element.elementIterator(); it
					.hasNext();) {
				IElement childElement = it.next();
				serialize(childElement, handler);
			}
			doEndElement(handler, element.getName());
		} finally {
			if (element instanceof IStringEvaluatorAccess) {
				((IStringEvaluatorAccess) element).setStringEvaluator(oldValue);
			}
		}
	}

	@Override
	public void serialize(Object object, ContentHandler handler)
			throws SAXException, IOException {
		serialize((StandardInstrument) object, handler);
	}

	protected void serialize(StandardInstrument instrument,
			ContentHandler handler) throws SAXException, IOException {
		AttributesImpl attrs;
		attrs = new AttributesImpl();
		addAttribute(attrs, "id", instrument.getId());
		doStartElement(handler, "instrument", attrs);
		//
		attrs = new AttributesImpl();
		doStartElement(handler, "requires", attrs);
		for (StandardInstrumentPrerequisite prerequisite : instrument
				.getPrerequisites()) {
			attrs = new AttributesImpl();
			addAttribute(attrs, "instrument", prerequisite.getInstrumentId());
			if (prerequisite.getAbsentAction() != null) {
				addAttribute(attrs, "absent", prerequisite.getAbsentAction());
			}
			doStartElement(handler, "prerequisite", attrs);
			doEndElement(handler, "prerequisite");
		}
		doEndElement(handler, "requires");
		//
		for (StandardExtension extension : instrument.getExtensions()) {
			serialize(extension.getElement(), handler);
		}
		//
		doEndElement(handler, "instrument");
	}

}
