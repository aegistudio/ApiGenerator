package net.aegistudio.api.cppgen;

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
	
	protected Context makeContext(boolean clientSide) {
		//context = new PseudoContext(System.out);
		return new FileContext("target/test-output/" + 
				(clientSide? "client/" : "server/"));
	}

	private Interfacing[] allInterface(Document document, 
			boolean client, boolean remote) {
		List<Interfacing> interfacingList = new ArrayList<>();
		
		if(!(remote ^ client))
			interfacingList.add(new HostInterfacing(document, client));
		
		if(!(remote ^ client))
			Arrays.stream(document.interfaces())
				.map(HandleInterfacing::new)
				.forEach(interfacingList::add);
		if(remote ^ client)
			Arrays.stream(document.callbacks())
				.map(HandleInterfacing::new)
				.forEach(interfacingList::add);
		return interfacingList.toArray(new Interfacing[0]);
	}
	
	public @Test void testValue() throws IOException {
		CppValueGenerator clientGenerator = new CppValueGenerator(false);
		clientGenerator.generate(makeContext(false), dom);
		
		CppValueGenerator serverGenerator = new CppValueGenerator(true);
		serverGenerator.generate(makeContext(true), dom);
	}
	
	public @Test void testLocal() throws IOException {
		CppLocalGenerator clientGenerator = new CppLocalGenerator(
				false, document -> this.allInterface(document, false, false));
		clientGenerator.generate(makeContext(false), dom);
		
		CppLocalGenerator serverGenerator = new CppLocalGenerator(
				true, document -> this.allInterface(document, true, false));
		serverGenerator.generate(makeContext(true), dom);
	}
	
	public @Test void testRemote() throws IOException {
		CppRemoteGenerator clientGenerator = new CppRemoteGenerator(
				false, document -> this.allInterface(document, false, true));
		clientGenerator.generate(makeContext(false), dom);
		
		CppRemoteGenerator serverGenerator = new CppRemoteGenerator(
				true, document -> this.allInterface(document, true, true));
		serverGenerator.generate(makeContext(true), dom);
	}
}
