package gr.forth.ics.isl.ithaca.warehouse;

import gr.forth.ics.isl.ithaca.Resources;
import gr.forth.ics.isl.ithaca.connection.ConnectToVirtuoso;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

/**
 * @author Mike Mountantonakis (mountanton@csd.uoc.gr)
 * @author Pavlos Fafalios (fafalios@csd.uoc.gr)
 */
public class VirtuosoUploader {

	RDFFormat format;

	/*
	 * This function selects the files that will be uploaded to virtuoso
	 */
	public void selectDataForUploading() {
		File startDir = new File("resources/output/");
		for (File f : startDir.listFiles()) {
			uploadFile(f);
		}
	}

	/*
	 * This function  uploads a file to Virtuoso
	 * @param f the file that will be uploaded
	 */
	public void uploadFile(File f) {
		InputStreamReader in = null;
		try {
			URI graph = ConnectToVirtuoso.getConnection().getRepository().getValueFactory().createURI(Resources.virtuosoGraph);
			if (f.toString().endsWith("ttl") || f.toString().endsWith("nt")) {
				this.format = RDFFormat.TURTLE;
			} else if (f.toString().endsWith("owl") || f.toString().endsWith("rdf")) {
				this.format = RDFFormat.RDFXML;
			}
			System.out.print("IMPORTING file " + f.getAbsolutePath());
			in = new InputStreamReader(new FileInputStream(f), "UTF8");
			ConnectToVirtuoso.getConnection().add(in, "", this.format, graph);
			System.out.println(" [IMPORTED]");
		} catch (IOException ex) {
			Logger.getLogger(VirtuosoUploader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (RepositoryException ex) {
			Logger.getLogger(VirtuosoUploader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (RDFParseException ex) {
			Logger.getLogger(VirtuosoUploader.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				Logger.getLogger(VirtuosoUploader.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
