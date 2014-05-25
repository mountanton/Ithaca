package gr.forth.ics.isl.ithaca.connection;

import gr.forth.ics.isl.ithaca.Resources;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import virtuoso.sesame2.driver.VirtuosoRepository;

/**
 * @author Mike Mountantonakis (mountanton@csd.uoc.gr)
 * @author Pavlos Fafalios (fafalios@csd.uoc.gr)
 */
public class ConnectToVirtuoso {

	private String virtuosoHost;
	private String virtuosoPort;
	private String virtuosoUserName;
	private String virtuosoPassword;
	private VirtuosoRepository repository;
	public static RepositoryConnection conn;

	/**
	 * Constructor Initializes the variables which are necessary for connecting
	 * (host,port,username,password)
	 */
	public ConnectToVirtuoso() {
		this.virtuosoHost = Resources.virtuosoHost.replaceAll("http://", "");
		this.virtuosoPort = Resources.virtuosoPort;
		this.virtuosoUserName = Resources.virtuosoUsername;
		this.virtuosoPassword = Resources.virtuosoPassword;

	}

	/**
	 * This method starts the connection to the virtuoso repository (openrdf)
	 *
	 * @throws RepositoryException
	 */
	public void startConnection() throws RepositoryException {

		this.repository = new VirtuosoRepository("jdbc:virtuoso://"
				+ this.virtuosoHost + ":" + this.virtuosoPort
				+ "/charset=UTF-8/log_enable=2",
				this.virtuosoUserName, this.virtuosoPassword);
		ConnectToVirtuoso.conn = this.repository.getConnection();
	}

	/**
	 * Returns the virtuoso Repository Connection (openrdf)
	 *
	 * @return the virtuoso Repository Connection
	 */
	public static RepositoryConnection getConnection() {
		return ConnectToVirtuoso.conn;
	}

	/**
	 * Returns the virtuoso Repository (openrdf)
	 *
	 * @return the virtuoso Repository
	 */
	public VirtuosoRepository getRepository() {
		return this.repository;
	}

	/**
	 * Terminates openRDF Connection
	 *
	 * @throws WarehouseControllerException
	 */
	public void terminateConnection() {
		if (ConnectToVirtuoso.conn != null) {
			try {
				ConnectToVirtuoso.conn.close();
			} catch (RepositoryException ex) {
			}
		}

	}
}
