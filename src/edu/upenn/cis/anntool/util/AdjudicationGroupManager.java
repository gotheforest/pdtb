package edu.upenn.cis.anntool.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.org.apache.xpath.internal.XPathFactory;

import edu.upenn.cis.anntool.settings.Constants;

public class AdjudicationGroupManager {

	private Document doc;
	static final XPath xpath = javax.xml.xpath.XPathFactory.newInstance()
			.newXPath();
	private File adjudicationGroupsFile;
	private Element currentFileNode;

	public AdjudicationGroupManager(String annRoot) {
		try {
			adjudicationGroupsFile = new File(annRoot,
					Constants.adjudicationGroupsFilename);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			if (adjudicationGroupsFile.exists()) {
				doc = documentBuilderFactory.newDocumentBuilder().parse(
						adjudicationGroupsFile);
			} else {
				doc = documentBuilderFactory.newDocumentBuilder().newDocument();
				Element root = doc.createElement("AdjudicationGroups");
				doc.appendChild(root);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSecFile(String sec, String fil) {
		try {
			String expr = "//Section[@ID='" + sec + "']";
			Element section = (Element) xpath.evaluate(expr, doc,
					XPathConstants.NODE);
			if (section == null) {
				section = doc.createElement("Section");
				section.setAttribute("ID", sec);
				doc.getDocumentElement().appendChild(section);
			}
			expr = "./File[@ID='" + fil + "']";
			currentFileNode = (Element) xpath.evaluate(expr, section,
					XPathConstants.NODE);
			if (currentFileNode == null) {
				currentFileNode = doc.createElement("File");
				currentFileNode.setAttribute("ID", fil);
				section.appendChild(currentFileNode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getGroupNumber(Relation relation) {
		try {
			String expr = "./Root[@ID='" + relation.getRoot()
					+ "']/Relation[@ID='" + relation.getRelationID()
					+ "']/@GroupID";
			Double groupID = (Double) xpath.evaluate(expr, currentFileNode,
					XPathConstants.NUMBER);
			return Double.isNaN(groupID) ? -1 : groupID.intValue();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public void save(List<Relation> relationList) {
		try {
			for (; currentFileNode.getChildNodes().getLength() > 0; currentFileNode
					.removeChild(currentFileNode.getChildNodes().item(0))) {
			}
			add(relationList);
			adjudicationGroupsFile.getParentFile().mkdirs();
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(new DOMSource(doc), new StreamResult(
					adjudicationGroupsFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void add(List<Relation> relationList) {
		try {
			for (Relation relation : relationList) {
				if (!relation.isGhost()) {
					String expr = "./Root[@ID='" + relation.getRoot() + "']";
					Element rootNode = (Element) xpath.evaluate(expr,
							currentFileNode, XPathConstants.NODE);
					if (rootNode == null) {
						rootNode = doc.createElement("Root");
						rootNode.setAttribute("ID", relation.getRoot());
						currentFileNode.appendChild(rootNode);
					}
					Element relationNode = doc.createElement("Relation");
					relationNode.setAttribute("ID", relation.getRelationID());
					relationNode.setAttribute("GroupID",
							"" + relation.getGroupNumber());
					rootNode.appendChild(relationNode);
				}
				add(relation.getAdjudications());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
