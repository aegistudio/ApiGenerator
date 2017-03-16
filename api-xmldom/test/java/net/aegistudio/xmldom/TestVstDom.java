package net.aegistudio.xmldom;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import net.aegistudio.api.xmldom.XmlDocument;

public class TestVstDom extends XmlDomTest {
	protected final XmlDocument xmlDom;
	public TestVstDom() throws IOException, 
		SAXException, ParserConfigurationException {
		super("vst.xml");
		xmlDom = new XmlDocument(super.document);
	}
	
	// Root node properties.
	public @Test void testRoot() {
		assertEquals(xmlDom.namespace(), new String[]{
				"net", "aegistudio", "vst"});
		assertEquals(xmlDom.clientHost(), "VstClient");
		assertEquals(xmlDom.serverHost(), "VstServer");
	}
	
	// Metafile properties.
	public @Test void testMetafile() {
		assertEquals(xmlDom.values().length, 2);
		assertEquals(xmlDom.functions().length, 2);
		assertEquals(xmlDom.interfaces().length, 2);
		assertEquals(xmlDom.callbacks().length, 0);
	}
	
	// Global functions.
	public @Test void testGlobal() {
		// Init.
		assertEquals(xmlDom.functions()[0].name(), "init");
		assertEquals(xmlDom.functions()[0].result().name(), "Plugin");
		assertEquals(xmlDom.functions()[0].parameters().length, 1);
		assertEquals(xmlDom.functions()[0].parameters()[0].name(), "dllPath");
		assertEquals(xmlDom.functions()[0].parameters()[0].type().name(), "string");
		
		// Destroy.
		assertEquals(xmlDom.functions()[1].result(), null);
		assertEquals(xmlDom.functions()[1].name(), "destroy");
		assertEquals(xmlDom.functions()[1].parameters().length, 1);
		assertEquals(xmlDom.functions()[1].parameters()[0].name(), "vstHandle");
		assertEquals(xmlDom.functions()[1].parameters()[0].type().name(), "Plugin");
	}
	
	// Value tests for midi event.
	public @Test void testMidiEvent() {
		assertEquals(xmlDom.values()[0].name(), "MidiEvent");
		assertEquals(xmlDom.values()[0].fields().length, 2);
	}
	
	// Value tests for frame.
	public @Test void testFrame() {
		assertEquals(xmlDom.values()[1].name(), "Frame");
		assertEquals(xmlDom.values()[1].fields().length, 1);
		assertEquals(xmlDom.values()[1].fields()[0].name(), "data");
		assertEquals(xmlDom.values()[1].fields()[0].type().name(), "double");
		assertEquals(xmlDom.values()[1].fields()[0].type().variant(), true);
	}
	
	// Interface test for plugin.
	public @Test void testPlugin() {
		assertEquals(xmlDom.interfaces()[1].name(), "Plugin");
		assertEquals(xmlDom.interfaces()[1].methods().length, 3);
		assertEquals(xmlDom.interfaces()[1]
				.methods()[0].name(), "listParameters");
		assertEquals(xmlDom.interfaces()[1]
				.methods()[0].result().name(), "Parameter");
		assertEquals(xmlDom.interfaces()[1]
				.methods()[0].result().variant(), true);
	}
}
