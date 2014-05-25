package gr.forth.ics.isl.ithaca;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

/**
 * @author Mike Mountantonakis (mountanton@csd.uoc.gr)
 * @author Pavlos Fafalios (fafalios@csd.uoc.gr)
 */
public class XMLReader {

	/**
	 * It checks if the configuration xml file is validate and after that sets
	 * to the variables user's choices
	 */
	public void initialize() {
		try {

			if (validateXMLSchema("config/config.xsd", "config/config.xml") == false) {
				return;
			}
			File fXmlFile = new File("config/config.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			this.setVirtuoso((Element) doc.getElementsByTagName("virtuoso").item(0));
			this.setActions((Element) doc.getElementsByTagName("actions").item(0));
		} catch (ParserConfigurationException | SAXException | IOException e) {
		}
	}

	/**
	 * It sets the actions which user wants to take place
	 *
	 * @param eElement the parent element
	 */
	public void setActions(Element eElement) {
		Resources.downloadSources = eElement.getElementsByTagName("downloadSources").item(0).getTextContent();
		Resources.downloadQuery = eElement.getElementsByTagName("downloadQuery").item(0).getTextContent().replace("&lt;", "<").replace("&gt;", ">");
		Resources.downloadFileName = eElement.getElementsByTagName("downloadFileName").item(0).getTextContent();
		Resources.importFiles = eElement.getElementsByTagName("importFiles").item(0).getTextContent();
		Resources.triplifyXML = eElement.getElementsByTagName("triplifyXML").item(0).getTextContent();
		Resources.triplifyCSV = eElement.getElementsByTagName("triplifyCSV").item(0).getTextContent();
		Resources.triplifyMetadataXML = eElement.getElementsByTagName("triplifyMetadataXML").item(0).getTextContent();
		Resources.triplifyMetadataCSV = eElement.getElementsByTagName("triplifyMetadataCSV").item(0).getTextContent();
		Resources.CSVFilesURIFilePath = eElement.getElementsByTagName("CSVFilesURIFilePath").item(0).getTextContent();
		Resources.MetadataFilePath = eElement.getElementsByTagName("MetadataFilePath").item(0).getTextContent();
		Resources.CSVPropertiesFilePath = eElement.getElementsByTagName("CSVPropertiesFilePath").item(0).getTextContent();
	}

	/**
	 * It sets user's choices about Virtuoso
	 *
	 * @param eElement parent element
	 */
	public void setVirtuoso(Element eElement) {

		Resources.virtuosoHost = eElement.getElementsByTagName("host").item(0).getTextContent();
		Resources.virtuosoPort = eElement.getElementsByTagName("port").item(0).getTextContent();
		Resources.virtuosoUsername = eElement.getElementsByTagName("username").item(0).getTextContent();
		Resources.virtuosoPassword = eElement.getElementsByTagName("password").item(0).getTextContent();
		Resources.virtuosoGraph = eElement.getElementsByTagName("graph").item(0).getTextContent();

		if (!Resources.virtuosoGraph.endsWith("/") && !Resources.virtuosoGraph.endsWith("#")) {
			Resources.virtuosoGraph += "/";
		}

	}

	/**
	 * This method checks if an xml file is valid according to an xml schema
	 *
	 * @param xsdPath the path of the xsd schema file
	 * @param xmlPath the path of the xml file
	 */
	public static boolean validateXMLSchema(String xsdPath, String xmlPath) {

		try {
			SchemaFactory factory =
					SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new File(xsdPath));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new File(xmlPath)));
		} catch (SAXException e) {
			System.out.println("Exception: " + e.getMessage());
			return false;
		} catch (IOException ex) {
		}
		return true;
	}
}
