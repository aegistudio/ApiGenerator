package net.aegistudio.api.xmldom;

import org.w3c.dom.Element;

import net.aegistudio.api.Type;

public class XmlType implements Type {
	public final Element element;
	public XmlType(Element element) {
		this.element = element;
	}

	@Override
	public String name() {
		return element.getAttribute("type");
	}

	@Override
	public boolean variant() {
		return element.getAttribute("variant")
				.toLowerCase().equals("true");
	}
}
