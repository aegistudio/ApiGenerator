package net.aegistudio.api.xmldom;

import org.w3c.dom.Element;

import net.aegistudio.api.Method;
import net.aegistudio.api.Type;
import net.aegistudio.api.Value;

public class XmlNamedType implements Value.Field, Method.Parameter {
	protected final XmlType type;
	protected final Element element;
	
	public XmlNamedType(Element element) {
		this.type = new XmlType(element);
		this.element = element;
	}

	@Override
	public Type type() {
		return type;
	}

	@Override
	public String name() {
		return element.getAttribute("name");
	}
}
