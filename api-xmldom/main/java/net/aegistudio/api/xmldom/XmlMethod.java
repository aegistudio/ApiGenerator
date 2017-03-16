package net.aegistudio.api.xmldom;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.w3c.dom.Element;

import net.aegistudio.api.Method;
import net.aegistudio.api.Type;

public class XmlMethod implements Method {
	protected final Element element;
	protected Type result;
	protected final Parameter[] parameters;
	public XmlMethod(String tag, Element node) {
		this.element = node;
		
		XmlTagClassifier classifier = new XmlTagClassifier();
		List<Parameter> parameters = new ArrayList<>();
		classifier.register("return", this::setResult);
		classifier.register("parameter", 
				XmlNamedType::new, parameters::add);
		classifier.nullError(tag);
		classifier.classify(element);
		this.parameters = parameters.toArray(new Parameter[0]);
	}

	public static Function<Element, XmlMethod> create(String name) {
		return element -> new XmlMethod(name, element);
	}
	
	private void setResult(Element element) {
		result = new XmlType(element);
	}
	
	@Override
	public String name() {
		return element.getAttribute("name");
	}

	@Override
	public Type result() {
		return result;
	}

	@Override
	public Parameter[] parameters() {
		return parameters;
	}
}
