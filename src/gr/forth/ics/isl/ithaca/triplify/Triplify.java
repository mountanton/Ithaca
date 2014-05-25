package gr.forth.ics.isl.ithaca.triplify;

/**
 * @author Mike Mountantonakis (mountanton@csd.uoc.gr)
 * @author Pavlos Fafalios (fafalios@csd.uoc.gr)
 */
public interface Triplify {

	/**
	 * It takes a file (csv,xml) and converts the data of the file to triples
	 *
	 * @param filename the name of the input file
	 */
	void createTriplesFromFile(String filename);
}
