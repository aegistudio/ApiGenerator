package net.aegistudio.api.xmldom;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Convert children nodes of an element
 * by mapping their tags.
 * 
 * @author aegistudio
 *
 */

public class XmlTagClassifier {
	protected final Map<String, Consumer<Element>> consumers = new TreeMap<>();
	
	public void register(String tag, Consumer<Element> element) {
		this.consumers.put(tag.toLowerCase(), element);
	}
	
	public <T> void register(String tag, Function<Element, T> element, Consumer<T> t) {
		register(tag, e -> t.accept(element.apply(e)));
	}
	
	protected Consumer<Element> nullConsumer = e -> {};
	public void nullRegister(Consumer<Element> element) {
		this.nullConsumer = element;
	}
	
	public void nullError(String current) {
		nullRegister(e -> {
			throw new IllegalArgumentException(
				"Unrecognized tag " + e.getTagName() + " under " + current + ".");
		});
	}
	
	public void classify(Element element) {
		NodeList children = element.getChildNodes();
		for(int i = 0; i < children.getLength(); i ++) {
			Node node = children.item(i);
			if(node.getNodeType() != Node.ELEMENT_NODE) continue;
			if(node.getParentNode() != element) continue;
			
			Element currentElement = (Element) node;
			String tagName = currentElement.getTagName().toLowerCase();
			
			Consumer<Element> elementConsumer = consumers.get(tagName);
			if(elementConsumer == null) elementConsumer = nullConsumer;
			elementConsumer.accept(currentElement);
		}
	}
}
