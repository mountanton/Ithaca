package gr.forth.ics.isl.ithaca.triplify;

import gr.forth.ics.isl.ithaca.Ithaca;
import gr.forth.ics.isl.ithaca.Prefixes;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Mike Mountantonakis (mountanton@csd.uoc.gr)
 * @author Pavlos Fafalios (fafalios@csd.uoc.gr)
 */
public class TriplifyXML implements Triplify {

	/**
	 * This function creates the triples w.r.t. the data of an XML file
	 *
	 * @param filename the name of an XML file that one want to triplify
	 */
	@Override
	public void createTriplesFromFile(String filename) {
		try {
			File fXmlFile = new File("resources/datasets/xml/" + filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			int index = 0;
			String filepath = "resources/output/" + filename.replace(".xml", ".ttl");
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "UTF8"));
			String triple = "<" + Prefixes.homerFull + filename + "> a void:Dataset .\n";
			bw.write(Prefixes.prefixes);
			bw.write(triple);
			Element tag = (Element) doc.getDocumentElement();
			while (tag.getElementsByTagName("property").item(index) != null) {
				Element property = (Element) tag.getElementsByTagName("property").item(index);
				Element name = (Element) property.getElementsByTagName("name").item(0);
				triple = "<" + Prefixes.homerFull + filename.replace(".xml", "_") + name.getTextContent().replace("\"", "").replace(" ", "_") + "> a <" + Prefixes.homerFull + filename.replace(".xml", "") + "> ;\n";
				triple += " void:inDataset" + "<" + Prefixes.homerFull + filename + ">;\n";
				triple += "rdfs:label \"" + name.getTextContent().replace("\"", "") + "\"@gr ;\n";
				String nam = name.getTextContent().replace("\"", "").toUpperCase();
				Element description = (Element) property.getElementsByTagName("description").item(0);
				if (!description.getTextContent().replace("καιnbsp;", "").trim().equals("")) {
					triple += "rdfs:comment \"" + description.getTextContent().replace("καιnbsp;", "").replace("\"", "'") + "\"@gr  ;\n";
				}
				String prefecture = this.getPrefecture(description.getTextContent().toUpperCase(), nam);
				if (!prefecture.equals("")) {
					triple += "<" + Prefixes.homerFull + "prefecture> <" + Prefixes.homerFull + prefecture + "> ;\n";
				}
				Element lat = (Element) property.getElementsByTagName("latitude").item(0);
				if (!lat.getTextContent().contains("null")) {
					triple += "geo:lat \"" + lat.getTextContent().replace(",", ".") + "\"^^xsd:double ;\n";
				}
				Element lng = (Element) property.getElementsByTagName("longitude").item(0);
				if (!lng.getTextContent().contains("null")) {
					triple += "geo:long \"" + lng.getTextContent().replace(",", ".") + "\"^^xsd:double ;\n";
				}
				triple += ".\n";
				index++;
				bw.write(triple);
			}
			bw.close();
			System.out.println("XML File: " + filename + " [Triplified]");
		} catch (SAXException | IOException | ParserConfigurationException ex) {
			Logger.getLogger(Ithaca.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * This function returns the prefecture for a place in Crete
	 *
	 * @param description the value of the element description
	 * @param name the value of the element name
	 * @return the prefecture where a place belongs
	 */
	public String getPrefecture(String description, String name) {
		String prefecture = "";
		if (description.contains("ΗΡΑΚΛΕ") || name.contains("ΗΡΑΚΛΕ")) {
			prefecture = "Herakleion";
		} else if (description.contains("ΧΑΝΙ") || name.contains("ΧΑΝΙ")) {
			prefecture = "Chania";
		} else if (description.contains("ΆΓΙΟ ΝΙΚ") || description.contains("΄ΆΓΙΟΣ ΝΙΚ") || description.contains("ΣΗΤ") || description.contains("ΠΕΤΡ")) {
			prefecture = "Lasithi";
		} else if (name.contains("ΆΓΙΟ ΝΙΚ") || name.toUpperCase().contains("ΆΓΙΟΣ ΝΙΚ") || name.contains("ΣΗΤ") || name.contains("ΠΕΤΡ")) {
			prefecture = "Lasithi";
		} else if (description.contains("ΡΈΘ") || name.contains("ΡΈΘ") || description.contains("ΡΕΘ") || name.contains("ΡΕΘ")) {
			prefecture = "Rethymno";
		}
		return prefecture;
	}
}
