package tukano.impl.clients.client_factory;

import java.net.URI;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import tukano.impl.discovery.Discovery;



public abstract class ClientFactory {

	protected static int MIN_URIs_ENTRIES = 1;

	protected static final String SERVER_REST_URI_SUFFIX = "/rest";

	protected static int CACHE_EXPIRATION_TIME = 10;
	protected static TimeUnit TIME_UNIT = TimeUnit.MINUTES;

	protected URI getServer_URI(String serviceName, int minURIsEntries) {
		URI[] uris = getServer_URI_List(serviceName, minURIsEntries);
		return uris[new Random().nextInt(uris.length)];
	}

	protected URI[] getServer_URI_List(String serviceName, int minURIsEntries) {
		Discovery discovery = Discovery.getInstance();
		URI[] uris = discovery.knownUrisOf(serviceName, minURIsEntries);
		if (uris.length == 0)
			throw new IllegalStateException("No URIs found for service " + serviceName);
		return uris;
	}

}