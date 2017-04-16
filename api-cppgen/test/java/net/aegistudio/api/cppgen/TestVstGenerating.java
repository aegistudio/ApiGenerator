package net.aegistudio.api.cppgen;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import net.aegistudio.api.gen.Context;
import net.aegistudio.api.gen.FileContext;
import net.aegistudio.api.xmldom.XmlDocument;

public class TestVstGenerating {
	protected final XmlDocument dom;
	public TestVstGenerating() throws IOException, 
		SAXException, ParserConfigurationException {
		
		this.dom = XmlDocument.read(getClass()
				.getResourceAsStream("/vst.xml"));
	}
	
	protected Context makeContext(String preface, boolean clientSide) {
		//context = new PseudoContext(System.out);
		return new FileContext("target/test-output/" + 
				(clientSide? "client/" : "server/") + preface);
	}

	/*
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
	*/
	
	public @Test void testValue() throws IOException {
		CppValueGenerator clientGenerator = new CppValueGenerator(false);
		clientGenerator.generate(makeContext("value", false), dom);
		
		CppValueGenerator serverGenerator = new CppValueGenerator(true);
		serverGenerator.generate(makeContext("value", true), dom);
	}
	
	/*
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
	*/
}
