package tukano.impl.users;

import java.util.logging.Logger;

import tukano.impl.server_factory.RESTServer;

//This class defines the entry point for the Users server API

public class UsersServer {

    public static final String SERVICE = "users";
    public static final int PORT = 8080;

   private static Logger LOG = Logger.getLogger(UsersServer.class.getName());

    /*
     * Start the Users Server
     */
    public static void main(String[] args) {
        System.out.println("Starting Users Server");
        
        // Start the server
        
        new RESTServer(SERVICE, PORT).startServer(RestUsersResource.class);
    }
    
}
