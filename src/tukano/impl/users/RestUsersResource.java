package tukano.impl.users;

import java.util.List;

import jakarta.inject.Singleton;
import tukano.api.User;
import tukano.api.java.Users;
import tukano.api.rest.RestUsers;
import tukano.impl.rest.RESTResource;

@Singleton
public class RestUsersResource extends RESTResource implements RestUsers {

	protected final Users impl;


	public RestUsersResource() {
		this.impl = new JavaUsers();
	}

	@Override
	public String createUser(User user) {
		return resultOrThrow(impl.createUser(user));
	}

	@Override
	public User getUser(String userId, String pwd) {
		return resultOrThrow(impl.getUser(userId, pwd));
	}

	@Override
	public List<User> searchUsers(String pattern) {
		return resultOrThrow(impl.searchUsers(pattern));
	}

	@Override
	public User updateUser(String userId, String pwd, User user) {
		return resultOrThrow(impl.updateUser(userId, pwd, user));
	}

	@Override
	public User deleteUser(String userId, String pwd) {
		return resultOrThrow(impl.deleteUser(userId, pwd));
	}

	@Override
	public void verifyPassword(String userId, String pwd) {
		resultOrThrow(impl.checkPassword(userId, pwd));
	}

	@Override
	public void userExists(String userId) {
		resultOrThrow(impl.userExists(userId));
	}


}