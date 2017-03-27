package net.aegistudio.api.xmldom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import net.aegistudio.api.Document;
import net.aegistudio.api.Interface;
import net.aegistudio.api.Method;
import net.aegistudio.api.Namespace;
import net.aegistudio.api.Value;

public class XmlDocument implements Document {
	protected final org.w3c.dom.Document dom;
	
	protected final Interface[] interfaces, callbacks;
	protected final Value[] values;
	protected final Method[] functions;
	
	public static XmlDocument read(InputStream domInputStream) 
			throws IOException, SAXException, ParserConfigurationException {
		return new XmlDocument(DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(domInputStream));
	}
	
	public XmlDocument(org.w3c.dom.Document dom) {
		this.dom = dom;
		if(!dom.getDocumentElement().getTagName()
				.toLowerCase().equals("api"))
			throw new IllegalArgumentException("Invalid API XML Document.");
		
		List<Interface> interfaces = new ArrayList<>();
		List<Interface> callbacks = new ArrayList<>();
		List<Value> values = new ArrayList<>();
		List<Method> functions = new ArrayList<>();
		
		XmlTagClassifier classifier = new XmlTagClassifier();
		classifier.register("interface", 
				XmlInterface.create("interface"), interfaces::add);
		classifier.register("function", 
				XmlMethod.create("function"), functions::add);
		classifier.register("value", XmlValue::new, values::add);
		classifier.register("callback", 
				XmlInterface.create("callback"), callbacks::add);
		classifier.nullError("api");
		classifier.classify(dom.getDocumentElement());
		
		this.interfaces = interfaces.toArray(new Interface[0]);
		this.callbacks = callbacks.toArray(new Interface[0]);
		this.values = values.toArray(new Value[0]);
		this.functions = functions.toArray(new Method[0]);
	}
	
	@Override
	public Namespace namespace() {
		return new Namespace(dom.getDocumentElement()
				.getAttribute("namespace"));
	}

	@Override
	public String clientHost() {
		String clientHost = dom.getDocumentElement()
				.getAttribute("clientHost");
		return clientHost.length() == 0? 
				"ApiClient" : clientHost;
	}

	@Override
	public String serverHost() {
		String serverHost = dom.getDocumentElement()
				.getAttribute("serverHost");
		return serverHost.length() == 0?
				"ApiServer" : serverHost;
	}

	@Override
	public Interface[] interfaces() {
		return interfaces;
	}

	@Override
	public Interface[] callbacks() {
		return callbacks;
	}

	@Override
	public Value[] values() {
		return values;
	}

	@Override
	public Method[] functions() {
		return functions;
	}
}
