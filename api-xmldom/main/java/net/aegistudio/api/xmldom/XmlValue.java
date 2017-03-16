package net.aegistudio.api.xmldom;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import net.aegistudio.api.Value;

public class XmlValue implements Value {
	public final Element element;
	public final Field[] fields; 
	public XmlValue(Element element) {
		this.element = element;
		
		List<Field> fields = new ArrayList<>();
		
		XmlTagClassifier classifier = new XmlTagClassifier();
		classifier.register("field", XmlNamedType::new, fields::add);
		classifier.nullError("value");
		classifier.classify(element);
		
		this.fields = fields.toArray(new Field[0]);
	}

	@Override
	public String name() {
		return element.getAttribute("name");
	}

	@Override
	public Field[] fields() {
		return fields;
	}
}
