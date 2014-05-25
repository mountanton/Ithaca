package gr.forth.ics.isl.ithaca.triplify;

import gr.forth.ics.isl.ithaca.Prefixes;
import gr.forth.ics.isl.ithaca.Resources;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mike Mountantonakis (mountanton@csd.uoc.gr)
 * @author Pavlos Fafalios (fafalios@csd.uoc.gr)
 */
public class TriplifyCSV implements Triplify {

	public Map<Integer, String> matchColumn_Id_Label = new TreeMap<>(); // this map matches the number of the column with the label of the column
	public Map<String, CSVProperties> matchingProperties = new TreeMap<>();// this map matches a label of a column with the features of the column
	public String uriSuffix; //the suffix of the uri for every subject of every triple
	public String fileN;// the fileName

	/**
	 * The constructor which initializes the features of all csv columns
	 */
	public TriplifyCSV() {
		this.setPropertiesMatchings(Resources.CSVPropertiesFilePath, "properties");
	}

	/**
	 * This function creates the triples w.r.t. the data of a csv file
	 *
	 * @param filename the name of the csv file that one want to triplify
	 */
	@Override
	public void createTriplesFromFile(String filename) {
		try {
			fileN = filename;
			this.setPropertiesMatchings(Resources.CSVFilesURIFilePath, "suffix");
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(new FileInputStream("resources\\datasets\\csv\\" + filename), "ISO-8859-7"));
			Set<String> y = new HashSet<>();
			String line;
			String triple = "<" + Prefixes.homerFull + filename + ">    a   void:Dataset .\n";
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("resources/output/" + filename.replace(".csv", ".ttl")), "UTF8"));
			String firstTriple = "";
			int count = 0;
			bw.write(Prefixes.prefixes);
			bw.write(triple);
			while ((line = in.readLine()) != null) {
				if (count == 0) {
					this.setColumnsProperties(line);
					count++;
					continue;
				}
				count++;
				String[] array = line.replace("\"", "").split(";");
				triple = "void:inDataset <" + Prefixes.homerFull + filename + "> ;\n";
				for (int i = 0; i < array.length; i++) {
					if (matchColumn_Id_Label.get(i).equals(this.uriSuffix)) {
						firstTriple = "<" + Prefixes.homerFull + filename.replace(".csv", "_") + array[i].replace(" ", "_") + "> a " + Prefixes.homer + filename.replace(".csv", "") + " ;\n";
					}
					if (this.matchColumn_Id_Label.get(i) != null && !array[i].trim().equals("")) {
						String column = this.matchColumn_Id_Label.get(i);
						CSVProperties cp = this.matchingProperties.get(column);
						if (cp != null) {
							if (cp.getOntologyProperty().contains(":")) {
								triple += cp.getOntologyProperty() + " ";
							} else {
								triple += Prefixes.homer + cp.getOntologyProperty() + " ";
							}
							if (cp.getType().equals("URI")) {
								if (cp.getOntologyProperty().equals("prefecture")) {
									triple += this.getPrefecture(array[i]);
								} else {
									triple += "<" + Prefixes.homerFull + array[i].replace(" ", "_") + ">";
								}
							} else if (cp.getType().equals("FULL_URI")) {
								String http = "";
								if (array[i].startsWith("www")) {
									http = "http://";
								}
								triple += "<" + http + array[i].replace(" ", "_") + ">";
							} else {
								if (!cp.getLanguage().equals("-")) {
									triple += " \"" + array[i] + "\"" + "@" + cp.getLanguage();
								} else if (!cp.getXSD().equals("-")) {
									if (cp.getXSD().equals("boolean")) {
										triple += " \"" + getBooleanValue(array[i]) + "\"" + "^^xsd:" + cp.getXSD();
									} else {
										triple += " \"" + array[i].replace(",", ".") + "\"" + "^^xsd:" + cp.getXSD();
									}
								} else {
									triple += " \"" + array[i].replace(",", ".") + "\"";
								}
							}
							triple += " ;\n";
						}
					}
				}
				triple += " .\n";
				String allTriples = firstTriple + triple;
				bw.write(allTriples);
			}
			System.out.println("CSV File: " + filename + " [Triplified]");
			bw.close();
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(TriplifyCSV.class.getName()).log(Level.SEVERE, null, ex);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(TriplifyCSV.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(TriplifyCSV.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * This function matches the id of a csv column with the label of a csv
	 * column
	 *
	 * @param line the first line of a csv file
	 */
	public void setColumnsProperties(String line) {
		String[] arrayLine = line.split(";");
		for (int i = 0; i < arrayLine.length; i++) {
			this.matchColumn_Id_Label.put(i, arrayLine[i]);
		}
	}

	/**
	 * This function stores the matchings of csv files
	 *
	 * @param filename the name of the file which contains the properties
	 * @param choice the choice of the type of the file that this function reads
	 * (properties if one want to match a property with the label of a
	 * column,suffix if one want to store a column as the suffix of the uri for
	 * every subject of every triple)
	 */
	private void setPropertiesMatchings(String filename, String choice) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));
			String line;
			int count = 0;
			while ((line = in.readLine()) != null) {

				if (count == 0) {
					count++;
					continue;
				}
				switch (choice) {
					case "properties":
						String[] arrayLine = line.split(",");
						CSVProperties cp = new CSVProperties();
						cp.setOntologyProperty(arrayLine[0]);
						cp.setCsvLabel(arrayLine[1]);
						cp.setLanguage(arrayLine[2]);
						cp.setType(arrayLine[3]);
						cp.setXSD(arrayLine[4].trim());
						this.matchingProperties.put(arrayLine[1], cp);
						break;
					case "suffix":
						arrayLine = line.split(",");
						if (arrayLine[0].equals(fileN)) {
							this.uriSuffix = arrayLine[1];
						}
						break;
				}
			}
			in.close();
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(TriplifyCSV.class.getName()).log(Level.SEVERE, null, ex);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(TriplifyCSV.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(TriplifyCSV.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * This function returns a boolean value for a csv cell
	 *
	 * @param cell the value of a csv cell
	 * @return 1 if the cell value is yes or true or 1 , 0 otherwise
	 */
	public int getBooleanValue(String cell) {
		if (cell.trim().contains("ΝΑΙ") || cell.trim().contains("NAI")) {
			return 1;
		} else if (cell.trim().equals("ΟΧΙ")) {
			return 0;
		}

		return Integer.parseInt(cell);
	}

	/**
	 * This function returns the prefecture for a place in Crete
	 *
	 * @param cell the value of a csv cell
	 * @return the prefecture where a place belongs
	 */
	public String getPrefecture(String cell) {
		String triple = "";
		if (cell.toUpperCase().contains("ΗΡ")) {
			triple = Prefixes.homer + "Herakleion";
		} else if (cell.toUpperCase().contains("ΧΑΝ")) {
			triple = Prefixes.homer + "Chania";
		} else if (cell.toUpperCase().contains("ΡΕΘ")) {
			triple = Prefixes.homer + "Rethymno";
		} else if (cell.toUpperCase().contains("ΛΑΣ")) {
			triple = Prefixes.homer + "Lasithi";
		} else {
			triple = "<" + Prefixes.homerFull + cell + ">";
		}
		return triple;
	}
}
