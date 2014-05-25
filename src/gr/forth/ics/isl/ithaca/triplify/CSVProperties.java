package gr.forth.ics.isl.ithaca.triplify;

/**
 * @author Mike Mountantonakis (mountanton@csd.uoc.gr)
 * @author Pavlos Fafalios (fafalios@csd.uoc.gr)
 */
public class CSVProperties {

	public String ontologyProperty; //The property which we will use to describe the value of the csv column
	public String csvLabel; //the label of the csv column 
	public String Language; //the language of the csv column
	public String Type; //the type of the column (URI or Literal)
	public String XSD; //the xsd type of the literal

	/**
	 * Returns the property that we will us in order to describe the value of
	 * the csv column
	 *
	 * @return the ontology property
	 */
	public String getOntologyProperty() {
		return ontologyProperty;
	}

	/**
	 * Sets the ontology property
	 *
	 * @param ontologyProperty the value of ontology property
	 */
	public void setOntologyProperty(String ontologyProperty) {
		this.ontologyProperty = ontologyProperty;
	}

	/**
	 * Returns the value of the csv label
	 *
	 * @return the value of the csv label
	 */
	public String getCsvLabel() {
		return csvLabel;
	}

	/**
	 * Sets the csv label
	 *
	 * @param csvLabel the value of the csv column label
	 */
	public void setCsvLabel(String csvLabel) {
		this.csvLabel = csvLabel;
	}

	/**
	 * Returns the language of the csv column (i.e. gr or en...)
	 *
	 * @return the language of the csv column label
	 */
	public String getLanguage() {
		return Language;
	}

	/**
	 * Sets the language of the csv column
	 *
	 * @param Language the value of the language of the csv column
	 */
	public void setLanguage(String Language) {
		this.Language = Language;
	}

	/**
	 * Returns the type of the value of the csv column
	 *
	 * @return the type of the csv column (Literal or URI)
	 */
	public String getType() {
		return Type;
	}

	/**
	 * Sets the type of the csv column
	 *
	 * @param Type the value of the type of the csv column
	 */
	public void setType(String Type) {
		this.Type = Type;
	}

	/**
	 * Returns the xml schema type of the csv column
	 *
	 * @return the xml schema type of the csv column
	 */
	public String getXSD() {
		return XSD;
	}

	/**
	 * Sets the value of the xml schema type of the csv column
	 *
	 * @param XSD the value of the xml schema type of the csv column
	 */
	public void setXSD(String XSD) {
		this.XSD = XSD;
	}
}
