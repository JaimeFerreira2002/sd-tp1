
package tukano.impl.clients.rest;

import java.net.URI;
import java.util.List;

import tukano.api.Short;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.java.Users;

public class ShortsClient implements Shorts {

	public ShortsClient(URI serverURI){

	}

	@Override
	public Result<Short> createShort(String userId, String password) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'createShort'");
	}

	@Override
	public Result<Void> deleteShort(String shortId, String password) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'deleteShort'");
	}

	@Override
	public Result<Short> getShort(String shortId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getShort'");
	}

	@Override
	public Result<List<String>> getShorts(String userId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getShorts'");
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'follow'");
	}

	@Override
	public Result<List<String>> followers(String userId, String password) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'followers'");
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'like'");
	}

	@Override
	public Result<List<String>> likes(String shortId, String password) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'likes'");
	}

	@Override
	public Result<List<String>> getFeed(String userId, String password) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getFeed'");
	}

	@Override
	public Result<Void> checkBlobId(String blobId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'checkBlobId'");
	}

}