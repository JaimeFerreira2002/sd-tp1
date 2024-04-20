package tukano.impl.clients.client_factory;

import java.net.URI;

import tukano.api.java.Users;
import tukano.impl.clients.rest.UsersClient;
import tukano.impl.discovery.Discovery;

/**
 * Class to create a new user client
 */

public class UserClientFactory extends ClientFactory{

    /**
	 * 
	 * @param service_URI
	 * @return
	 */
	public Users getClient() {

		URI serverURI = Discovery.getInstance().knownUrisOf(Users.NAME, 1)[0];
		
		try {
			return new UsersClient(serverURI);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


    
}
