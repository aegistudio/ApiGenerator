package net.aegistudio.api.xmldom;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.w3c.dom.Element;

import net.aegistudio.api.Interface;
import net.aegistudio.api.Method;

public class XmlInterface implements Interface {
	protected final Element element;
	protected final Method[] methods;
	public XmlInterface(String name, Element node) {
		this.element = node;
		List<Method> methods = new ArrayList<>();
		
		XmlTagClassifier classifier = new XmlTagClassifier();
		classifier.register("method", 
				XmlMethod.create("method"), methods::add);
		classifier.nullError(name);
		classifier.classify(element);
		
		this.methods = methods.toArray(new Method[0]);
	}

	public static Function<Element, XmlInterface> create(String name) {
		return element -> new XmlInterface(name, element);
	}
	
	@Override
	public String name() {
		return element.getAttribute("name");
	}

	@Override
	public Method[] methods() {
		return methods;
	}
}
