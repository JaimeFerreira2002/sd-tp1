package tukano.impl.blobs;

import tukano.impl.server_factory.RESTServer;
import tukano.impl.users.RestUsersResource;

public class BlobsServer {
    public static final String SERVICE = "blobs";
    public static final int PORT = 8082;

    /*
     * Start the Blobs Server
     */
    public static void main(String[] args) {
        System.out.println("Starting Users Server");
        
        // Start the server
        
        new RESTServer(SERVICE, PORT).startServer(RestUsersResource.class);
    }
    
}
