package gr.forth.ics.isl.ithaca;

import gr.forth.ics.isl.ithaca.connection.ConnectToVirtuoso;
import gr.forth.ics.isl.ithaca.download.DownloadDBPediaTriples;
import gr.forth.ics.isl.ithaca.triplify.TriplifyCSV;
import gr.forth.ics.isl.ithaca.triplify.TriplifyMetadata;
import gr.forth.ics.isl.ithaca.triplify.TriplifyXML;
import gr.forth.ics.isl.ithaca.warehouse.VirtuosoUploader;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.repository.RepositoryException;

/**
 * @author Mike Mountantonakis (mountanton@csd.uoc.gr)
 * @author Pavlos Fafalios (fafalios@csd.uoc.gr)
 */
public class Ithaca {

	private static void initialize() {
		new XMLReader().initialize();
	}

	public static void executeController() {
		if (Resources.downloadSources.equals("yes")) {
			new DownloadDBPediaTriples().downloadData();
		}
		if (Resources.triplifyCSV.equals("yes")) {
			TriplifyCSV tcsv = new TriplifyCSV();
			File startingFolder = new File("resources\\datasets\\csv");
			for (File f : startingFolder.listFiles()) {
				tcsv.createTriplesFromFile(f.getName());
			}
		}
		if (Resources.triplifyXML.equals("yes")) {
			TriplifyXML txml = new TriplifyXML();
			File startingFolder = new File("resources\\datasets\\xml");
			for (File f : startingFolder.listFiles()) {
				txml.createTriplesFromFile(f.getName());
			}
		}
		if (Resources.triplifyMetadataXML.equals("yes")) {
			TriplifyMetadata tmet = new TriplifyMetadata("xml");

			File startingFolder = new File("resources\\datasets\\metadata\\xml");
			for (File f : startingFolder.listFiles()) {
				tmet.createTriplesFromFile(f.getName());
			}
		}
		if (Resources.triplifyMetadataCSV.equals("yes")) {
			TriplifyMetadata tmet = new TriplifyMetadata("csv");
			File startingFolder = new File("resources\\datasets\\metadata\\csv");
			for (File f : startingFolder.listFiles()) {
				tmet.createTriplesFromFile(f.getName());
			}
		}
		if (Resources.importFiles.equals("yes")) {
			ConnectToVirtuoso connection = new ConnectToVirtuoso();

			try {
				connection.startConnection();
				VirtuosoUploader vu = new VirtuosoUploader();
				vu.selectDataForUploading();
				connection.terminateConnection();
			} catch (RepositoryException ex) {
				Logger.getLogger(VirtuosoUploader.class.getName()).log(Level.SEVERE, null, ex);
			}


		}
	}

	/**
	 * Main Method
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Ithaca.initialize();
		Ithaca.executeController();
	}
}
