package edu.upenn.cis.anntool.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class TipsterParserBak extends DefaultHandler {

	public static void invokeParse(File f) throws IOException, SAXException,
			ParserConfigurationException {

		SequenceInputStream xmlData = new SequenceInputStream(
				(new Vector<InputStream>(Arrays.asList(
						new ByteArrayInputStream("<root>\n".getBytes()),
						new FileInputStream(f), new ByteArrayInputStream(
								"</root>\n".getBytes())))).elements());

		SAXParserFactory.newInstance().newSAXParser().parse(xmlData,
				new TipsterParserBak());
	}

	StringBuffer accumulator = new StringBuffer(); // Accumulate parsed text
	Map<String, String> xmlMap = new HashMap<String, String>();

	// When the parser encounters plain text (not XML elements), it calls
	// this method, which accumulates them in a string buffer
	public void characters(char[] buffer, int start, int length) {
		accumulator.append(buffer, start, length);
	}

	// Every time the parser encounters the beginning of a new element, it
	// calls this method, which resets the string buffer
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		accumulator.setLength(0);
		if (localName.equals("DOC")) {
			xmlMap.clear();
		}
	}

	// When the parser encounters the end of an element, it calls this method
	public void endElement(String name) {
		//System.out.println(xmlMap);
		xmlMap.put(name, accumulator.toString());
	}

	/** This method is called when warnings occur */
	public void warning(SAXParseException exception) {
		System.err.println("WARNING: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
	}

	/** This method is called when errors occur */
	public void error(SAXParseException exception) {
		System.err.println("ERROR: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
	}

	/** This method is called when non-recoverable errors occur. */
	public void fatalError(SAXParseException exception) throws SAXException {
		System.err.println("FATAL: line " + exception.getLineNumber() + ": "
				+ exception.getMessage());
		throw (exception);
	}
}