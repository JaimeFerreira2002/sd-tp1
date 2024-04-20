package tukano.impl.shorts;

import tukano.impl.server_factory.RESTServer;

public class ShortsServer {

    public static final String SERVICE = "shorts";
    public static final int PORT = 8081;

    /*
     * Start the Shorts Server
     */
    public static void main(String[] args) {
        System.out.println("Starting Shorts Server");
        // Start the server
        new RESTServer(SERVICE, PORT).startServer(RestShortsResource.class);
    }
    
}
