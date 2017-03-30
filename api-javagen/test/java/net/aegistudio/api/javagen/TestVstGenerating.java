package net.aegistudio.api.javagen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import net.aegistudio.api.Document;
import net.aegistudio.api.gen.Context;
import net.aegistudio.api.gen.FileContext;
import net.aegistudio.api.gen.HandleInterfacing;
import net.aegistudio.api.gen.HostInterfacing;
import net.aegistudio.api.gen.Interfacing;
import net.aegistudio.api.xmldom.XmlDocument;

public class TestVstGenerating {
	protected final XmlDocument dom;
	public TestVstGenerating() throws IOException, 
		SAXException, ParserConfigurationException {
		
		this.dom = XmlDocument.read(getClass()
				.getResourceAsStream("/vst.xml"));
	}
	
	protected Context makeContext(String preface) {
		//context = new PseudoContext(System.out);
		return new FileContext("target/test-output/" + preface);
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
	
	public @Test void testValue() throws IOException {
		JavaValueGenerator generator = new JavaValueGenerator();
		generator.generate(makeContext("value"), dom);
	}
	
	public @Test void testLocal() throws IOException {
		JavaLocalGenerator generator = new JavaLocalGenerator(
				dom -> this.allInterface(dom, false));
		generator.generate(makeContext("local"), dom);
	}
	
	public @Test void testRemote() throws IOException {
		JavaRemoteGenerator generator = new JavaRemoteGenerator(
				dom -> this.allInterface(dom, true));
		generator.generate(makeContext("remote"), dom);
	}
}
