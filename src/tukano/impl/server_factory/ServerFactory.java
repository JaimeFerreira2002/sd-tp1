package tukano.impl.server_factory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import tukano.impl.discovery.Discovery;

// import utils.discovery.Discovery;

/**
 * Abstract class representing a server.
 *
 * @param <R> The type of resource served by the server.
 */
public abstract class ServerFactory {

	private static Logger LOG = Logger.getLogger(ServerFactory.class.getName());

	/**
	 * Static block to be executed when the class is loaded into the JVM to set the
	 * system to prefer IPv4 stack over IPv6 when both are available.
	 */
	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	/**
	 * Format string for constructing the REST server URI
	 */
	private static final String SERVER_URI_FMT = "http://%s:%s/%s";

	/**
	 * Variables of the server
	 */
	protected String uri_path;
	protected String serviceName;
	protected int port;

	/**
	 * Constructor for the Server class.
	 *
	 * @param server_ctx  Constant representing the context path for the REST API.
	 * @param serviceName The name of the service.
	 * @param port        The port number on which the server will listen.
	 * @param resource    The class of the resource managed by the server.
	 */
	public ServerFactory(String server_ctx, String serviceName, int port) {
		this.serviceName = serviceName;
		this.port = port;
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			this.uri_path = String.format(SERVER_URI_FMT, ip, port, server_ctx);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		announceURI();
	}

	/**
	 * Method to perform discovery.
	 */
	 private void announceURI() {
	 	Discovery discovery = Discovery.getInstance();
		LOG.info(uri_path + " announced");
	 	discovery.announce(serviceName, uri_path);
	 }

}