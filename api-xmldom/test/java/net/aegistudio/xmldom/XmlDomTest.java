package net.aegistudio.xmldom;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlDomTest {
	Document document;
	public XmlDomTest(String whichDom) throws IOException, 
		SAXException, ParserConfigurationException {
		
		InputStream domInputStream = getClass()
				.getResourceAsStream("/" + whichDom);
		this.document = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder().parse(domInputStream);
	}
}
