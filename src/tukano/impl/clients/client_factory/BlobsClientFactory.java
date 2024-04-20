package tukano.impl.clients.client_factory;

import java.net.URI;

import tukano.api.java.Blobs;
import tukano.api.java.Shorts;
import tukano.impl.clients.rest.ShortsClient;
import tukano.impl.discovery.Discovery;

/**
 * Class to create a new shorts client
 */

public class BlobsClientFactory extends ClientFactory{

    /**
	 * @param service_URI
	 * @return
	 */
	public Shorts getClient() {

		URI serverURI = Discovery.getInstance().knownUrisOf(Blobs.NAME, 1)[0];
		
		try {
			return new ShortsClient(serverURI);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


    
}
