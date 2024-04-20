package tukano.impl.server_factory;

import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;


/**
 * Class representing a REST server.
 *
 * @param <R> The type of resource managed by the server.
 */
public class RESTServer extends ServerFactory {

	private static Logger Log = Logger.getLogger(RESTServer.class.getName());

	/**
	 * Constant representing the context path for the REST API.
	 */
	private static final String REST_CTX = "rest";

	/**
	 * Constructor for the ServerREST class.
	 *
	 * @param serviceName The name of the service.
	 * @param port        The port number on which the server will listen.
	 * @param resource    The class of the resource served by the server.
	 */
	public RESTServer(String serviceName, int port) {
		super(REST_CTX, serviceName, port);
	}

	public <R> void startServer( Class<R> resourceClass) {
		// Config REST Server
		ResourceConfig config = new ResourceConfig();
		config.register(resourceClass);
		// Create REST server
		JdkHttpServerFactory.createHttpServer(URI.create(uri_path), config);
		Log.info(String.format("%s Server ready @ %s\n", serviceName, uri_path));
	}

}