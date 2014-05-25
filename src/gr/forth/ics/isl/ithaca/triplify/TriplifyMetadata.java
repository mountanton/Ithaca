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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mike Mountantonakis (mountanton@csd.uoc.gr)
 * @author Pavlos Fafalios (fafalios@csd.uoc.gr)
 */
public class TriplifyMetadata implements Triplify {

	Map<String, String> matchProperties = new HashMap<>(); //a map in order to match the ontology properties with the metadata properties
	String type; // the type of the file where the metadata belong (xml or csv)
	String inputFilePath; // the file path of the input file

	/**
	 * Constructor It calls the method setProperties in order to match the
	 * ontology properties with the metadata properties and initializes the
	 * variable type which shows the type of the file where the metadata belong
	 * (xml or csv)
	 *
	 * @param typeValue the type of the file
	 */
	public TriplifyMetadata(String typeValue) {
		this.setProperties();
		type = typeValue;
		if (type.equals("xml")) {
			inputFilePath = System.getProperty("user.dir").toString() + "\\resources\\datasets\\metadata\\xml\\";
		} else {
			inputFilePath = inputFilePath = System.getProperty("user.dir").toString() + "\\resources\\datasets\\metadata\\csv\\";
		}
	}
	/*
	 * This method match the ontology properties with the metadata properties
	 */

	private void setProperties() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(Resources.MetadataFilePath), "UTF8"));
			Set<String> y = new HashSet<String>();
			String line;
			while ((line = in.readLine()) != null) {
				String[] array = line.split("\t");
				matchProperties.put(array[0], array[1]);
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(TriplifyMetadata.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(TriplifyMetadata.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * This function creates the triples w.r.t. the data of a metadata file
	 *
	 * @param filename the name of the metadata file that one want to triplify
	 */
	@Override
	public void createTriplesFromFile(String filename) {
		try {
			boolean flag = true;
			String filepath = "resources/output/" + filename.replace(".metadata", "_metadata.ttl");
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "UTF8"));
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath + filename), "UTF8"));
			Set<String> y = new HashSet<String>();
			String line, triple;
			bw.write(Prefixes.prefixes);
			triple = "<" + Prefixes.homerFull + filename.replace(".metadata", "." + type) + "> a void:Dataset ; \n";
			while ((line = in.readLine()) != null) {
				String[] array = line.split("\t");
				if (array[0].contains("Locator")) {
					triple += this.matchProperties.get(array[0]) + " <" + array[1] + "> ; \n";
				} else if ((array[0].contains("Abstract") || (array[0].contains("Category")))) {
					String[] x = array[1].split(";");
					for (String str : x) {
						triple += this.matchProperties.get(array[0]) + " \"" + str + "\" ; \n";
					}
				} else {
					triple += this.matchProperties.get(array[0]) + " \"" + array[1] + "\" ; \n";
				}
			}
			triple += " .\n";
			bw.write(triple);
			bw.close();
			System.out.println(type.toUpperCase() + " Metadata File: " + filename + " [Triplified]");

		} catch (FileNotFoundException ex) {
			Logger.getLogger(TriplifyMetadata.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(TriplifyMetadata.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
