package gr.forth.ics.isl.ithaca.download;

import gr.forth.ics.isl.ithaca.Resources;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mike Mountantonakis (mountanton@csd.uoc.gr)
 * @author Pavlos Fafalios (fafalios@csd.uoc.gr)
 */
public class DownloadDBPediaTriples {

	private int offset = 0;
	private int vima = 10000;
	private int i = 1;
	String xmlResult = "@prefix rdf:	<http://www.w3.org/1999/02/22-rdf-syntax-ns#> ."
			+ "\n" + "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ." + "\n"
			+ "@prefix owl:<http://www.w3.org/2002/07/owl#> ." + "\n";
	String endpoint = "http://dbpedia.org/sparql?query=";

	public void downloadData() {

		String filepath = "resources/output/" + Resources.downloadFileName + ".ttl";
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filepath), "UTF8"));
			bw.write(xmlResult);
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(DownloadDBPediaTriples.class.getName()).log(Level.SEVERE, null, ex);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(DownloadDBPediaTriples.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(DownloadDBPediaTriples.class.getName()).log(Level.SEVERE, null, ex);
		}
		while (true) {
			try {
				String query = Resources.downloadQuery;
				String queryPath = "";
				try {
					queryPath = endpoint + URLEncoder.encode(query, "utf-8");
				} catch (UnsupportedEncodingException ex) {
					Logger.getLogger(DownloadDBPediaTriples.class.getName()).log(Level.SEVERE, null, ex);
				}
				URL url = null;
				try {
					url = new URL(queryPath);
				} catch (MalformedURLException ex) {
					Logger.getLogger(DownloadDBPediaTriples.class.getName()).log(Level.SEVERE, null, ex);
				}
				URLConnection con = url.openConnection();
				con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
				con.setRequestProperty("ACCEPT", "text/csv");
				InputStream is = con.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, "utf-8");
				BufferedReader in = new BufferedReader(isr);

				try {
					String line;
					int count = 0;
					String data = "";
					while ((line = in.readLine()) != null) {
						count++;
						if (count == 1) {
							continue;
						}
						String[] parts = line.split(",");

						if (parts.length != 3) {
							if (parts.length > 3) {
								for (int j = 3; j < parts.length; j++) {
									parts[2] = parts[2] + " " + parts[j];
								}
							} else {
								count--;
								continue;
							}
						}
						if (parts[2].length() > 2) {
							if (parts[2].substring(1, parts[2].length() - 1).contains("'")) {

								String part = parts[2].substring(1, parts[2].length() - 1);
								part = part.replace("'", " ");
								parts[2] = part;
							}

							if (parts[2].substring(1, parts[2].length() - 1).contains("\\")) {

								String part = parts[2].substring(1, parts[2].length() - 1);
								part = part.replace("\\", " ");
								parts[2] = part;
							}

							if (parts[2].substring(1, parts[2].length() - 1).contains("\"")) {

								String part = parts[2].substring(1, parts[2].length() - 1);
								part = part.replace("\"", " ");
								parts[2] = part;
							}

							if (parts[2].substring(1, parts[2].length() - 1).contains(">") || parts[2].substring(1, parts[2].length() - 1).contains("<")) {
								String part = parts[2].substring(1, parts[2].length() - 1);
								part = part.replace(">", "&gt;");
								part = part.replace("<", "&lt;");
								parts[2] = part;
							}
						}

						if (parts[0].toLowerCase().startsWith("\"http")) {
							String p = parts[0].substring(1, parts[0].length() - 1);
							parts[0] = "<" + p + ">";
						} else if (parts[0].toLowerCase().startsWith("\"node")) {
							parts[0] = "_:" + parts[0].substring(1, parts[0].length() - 1);
							parts[0] = parts[0].replace("nodeID://", "");
						}

						if (parts[1].toLowerCase().startsWith("\"http")) {
							String p = parts[1].substring(1, parts[1].length() - 1);
							parts[1] = "<" + p + ">";
						}

						if (parts[2].toLowerCase().startsWith("\"http")) {
							String p = parts[2].substring(1, parts[2].length() - 1);
							parts[2] = "<" + p + ">";
						}
						if (parts[2].toLowerCase().startsWith("\"node")) {
							String p = parts[2].substring(1, parts[2].length() - 1);
							parts[2] = "_:" + p;
							parts[2] = parts[2].replace("nodeID://", "");
						}

						if (!parts[2].startsWith("\"") && !parts[2].startsWith("<")) {
							parts[2] = "\"" + parts[2] + "\"";
						}

						if (!parts[2].startsWith("\"") && parts[2].endsWith("\"")) {
							parts[2] = "\"" + parts[2];
						}

						if (parts[2].startsWith("\"") && !parts[2].endsWith("\"")) {
							parts[2] = parts[2] + "\"";
						}
						bw.write(parts[0] + "\t" + parts[1] + "\t" + parts[2] + " . \n");
					}
					if (count < vima - 1) {
						break;
					}
					in.close();
					isr.close();
					is.close();
				} catch (IOException ex) {
					System.out.println(ex.getMessage());
				}
				i++;
				offset += vima;
			} catch (IOException ex) {
				Logger.getLogger(DownloadDBPediaTriples.class.getName()).log(Level.SEVERE, null, ex);
			}

		}
		try {
			Thread.sleep(2000);
			System.out.println("File: " + Resources.downloadFileName + " [Created]");
			bw.close();
		} catch (IOException ex) {
			Logger.getLogger(DownloadDBPediaTriples.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InterruptedException ex) {
			Logger.getLogger(DownloadDBPediaTriples.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
