package tukano.impl.clients.rest;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import org.glassfish.jersey.client.ClientConfig;


import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import tukano.api.java.Users;
import tukano.api.rest.RestUsers;

public class UsersClient implements Users{

    final URI serverURI;
    final Client client;
    final ClientConfig config;

    final WebTarget target;

    Logger LOGGER = Logger.getLogger(UsersClient.class.getName());

    public UsersClient(URI serverURI){
        this.serverURI = serverURI;
        this.config = new ClientConfig();
        this.client = ClientBuilder.newClient(config);
        this.target = client.target(serverURI).path(RestUsers.PATH);
    }

    @Override
    public Result<String> createUser(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public Result<User> getUser(String userId, String pwd) {
        Response r = target.path(userId)
        .queryParam(RestUsers.PWD, pwd).request()
        .accept(MediaType.APPLICATION_JSON)
        .get();

        var status = r.getStatus();

        if( status != Status.OK.getStatusCode() )
            return Result.error( getErrorCodeFrom(status));
        else
            return Result.ok( r.readEntity( User.class ));}

    @Override
    public Result<User> updateUser(String userId, String pwd, User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public Result<User> deleteUser(String userId, String pwd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchUsers'");
    }

    public static ErrorCode getErrorCodeFrom(int status) {
		return switch (status) {
		case 200, 209 -> ErrorCode.OK;
		case 409 -> ErrorCode.CONFLICT;
		case 403 -> ErrorCode.FORBIDDEN;
		case 404 -> ErrorCode.NOT_FOUND;
		case 400 -> ErrorCode.BAD_REQUEST;
		case 500 -> ErrorCode.INTERNAL_ERROR;
		case 501 -> ErrorCode.NOT_IMPLEMENTED;
		default -> ErrorCode.INTERNAL_ERROR;
		};
	}

    @Override
    public Result<Void> checkPassword(String userId, String pwd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkPassword'");
    }

    @Override
    public Result<Void> userExists(String userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'userExists'");
    }
    
}
