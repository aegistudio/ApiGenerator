package net.aegistudio.api.javagen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import net.aegistudio.api.Document;
import net.aegistudio.api.gen.HandleInterfacing;
import net.aegistudio.api.gen.HostInterfacing;
import net.aegistudio.api.gen.Interfacing;
import net.aegistudio.api.gen.PseudoContext;
import net.aegistudio.api.xmldom.XmlDocument;

public class TestVstGenerating {
	protected final XmlDocument dom;
	public TestVstGenerating() throws IOException, 
		SAXException, ParserConfigurationException {
		
		this.dom = XmlDocument.read(getClass()
				.getResourceAsStream("/vst.xml"));
	}
	
	public @Test void testValue() throws IOException {
		PseudoContext context = new PseudoContext(System.out);
		JavaValueGenerator generator = new JavaValueGenerator();
		generator.generate(context, dom);
	}

	private Interfacing[] allInterface(Document document, boolean client) {
		List<Interfacing> interfacingList = new ArrayList<>();
		interfacingList.add(new HostInterfacing(document, client));
		Arrays.stream(document.interfaces())
			.map(HandleInterfacing::new)
			.forEach(interfacingList::add);
		Arrays.stream(document.callbacks())
			.map(HandleInterfacing::new)
			.forEach(interfacingList::add);
		return interfacingList.toArray(new Interfacing[0]);
	}
	
	public @Test void testLocal() throws IOException {
		PseudoContext context = new PseudoContext(System.out);
		JavaLocalGenerator generator = new JavaLocalGenerator() {

			@Override
			protected Interfacing[] perspect(Document document) {
				return allInterface(document, false);
			}
			
		};
		generator.generate(context, dom);
	}
	
	public @Test void testRemotel() throws IOException {
		PseudoContext context = new PseudoContext(System.out);
		JavaRemoteGenerator generator = new JavaRemoteGenerator() {

			@Override
			protected Interfacing[] perspect(Document document) {
				return allInterface(document, true);
			}
			
		};
		generator.generate(context, dom);
	}
}
