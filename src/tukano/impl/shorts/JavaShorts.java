package tukano.impl.shorts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import tukano.api.Follow;
import tukano.api.Likes;
import tukano.api.Short;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.java.Users;
import tukano.impl.clients.client_factory.UserClientFactory;
import tukano.impl.hibernate.Hibernate;

public class JavaShorts implements Shorts{

    Logger LOG = Logger.getLogger(JavaShorts.class.getName());

    @Override
    public Result<Short> createShort(String userId, String password) {
        LOG.info("Creating Short for user: " + userId);

        //check validity of the parameters
        if (userId == null || password == null) {
            LOG.info("Invalid parameters.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        try{
            //Get hybernate instance
            Hibernate hibernate = Hibernate.getInstance();
            
            //Check if user exists & get the current user form the users server
            Users userClient = new UserClientFactory().getClient();
            Result<User> userResponse = userClient.getUser(userId, password);

            if (!userResponse.isOK()) {
                LOG.info("User does not exist.");
                return Result.error(Result.ErrorCode.NOT_FOUND);
            }

            //check password
            if(!userResponse.value().getPwd().equals(password)){
                LOG.info("Iconrrect password");
                return Result.error(Result.ErrorCode.FORBIDDEN);
            }

            //Create the short
            Short newShort = new Short(UUID.randomUUID().toString(), userId, UUID.randomUUID().toString());

            //Update to database
            hibernate.persist(newShort);
            
            return Result.ok(newShort);


        }catch(Exception e){
            LOG.severe("Error creating short: " + e.getMessage());
            return Result.error(Result.ErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<Void> deleteShort(String shortId, String password) {
       LOG.info("Deleting Short: " + shortId);

         //Check validity of the parameters
            if (shortId == null || password == null) {
                LOG.info("Invalid parameters.");
                return Result.error(Result.ErrorCode.BAD_REQUEST);
            }

            try{
                //Get the hibernate instance
                Hibernate hibernate = Hibernate.getInstance();

                //get the current short from database
                List<Short> shorts = hibernate.sql(String.format("SELECT * FROM Short s WHERE s.shortId = '%s'", shortId), Short.class);

                //Check if the short exists
                if (shorts.size() == 0) {
                    LOG.info("Short does not exist.");
                    return Result.error(Result.ErrorCode.NOT_FOUND);
                }

                //get the owner user of the short
                String ownerId = shorts.get(0).getOwnerId();

                 //Get the owner user form the users server
                Users userClient = new UserClientFactory().getClient();
                Result<User> userResponse = userClient.getUser(ownerId, password);

                if (!userResponse.isOK()) {
                    LOG.info("User does not exist.");
                    return Result.error(Result.ErrorCode.NOT_FOUND);
                }
                User user = userResponse.value();

                //Check if the password is correct
                if (!user.getPwd().equals(password)) {
                    LOG.info("Password is not correct.");
                    return Result.error(Result.ErrorCode.FORBIDDEN);
                }

                //Delete the short
                hibernate.delete(shorts.get(0));
                return Result.ok();

            } catch (Exception e) {
                LOG.severe("Error deleting short: " + e.getMessage());
                return Result.error(Result.ErrorCode.INTERNAL_ERROR);
            }
    }

    @Override
    public Result<Short> getShort(String shortId) {
        LOG.info("Retrieving Short: " + shortId);

        //Check validity of the parameters
        if (shortId == null) {
            LOG.info("Invalid parameters.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        try{
            //Get the hibernate instance
            Hibernate hibernate = Hibernate.getInstance();

            //get the current short from database
            List<Short> shorts = hibernate.sql(String.format("SELECT * FROM Short s WHERE s.shortId = '%s'", shortId), Short.class);

            //Check if the short exists
            if (shorts.size() == 0) {
                LOG.info("Short does not exist.");
                return Result.error(Result.ErrorCode.NOT_FOUND);
            }
            

            return Result.ok(shorts.get(0));

        }catch(Exception e){
            LOG.severe("Error retrieving short: " + e.getMessage());
            return Result.error(Result.ErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<List<String>> getShorts(String userId) {
        LOG.info("Retrieving Shorts for user: " + userId);

        //Check validity of the parameters
        if (userId == null) {
            LOG.info("Invalid parameters.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        try{
            //Get the hibernate instance
            Hibernate hibernate = Hibernate.getInstance();

            //Check if the users exist 
            Users userClient = new UserClientFactory().getClient();
            Result<User> userResponse = userClient.getUser(userId, ".");

            if(userResponse.error().equals(Result.ErrorCode.NOT_FOUND)){
                LOG.info("User does not exist.");
                return Result.error(Result.ErrorCode.NOT_FOUND);
            }

            

            //Get the list of shortIds
            List<String> shortIds = hibernate.sql(String.format("SELECT s.shortId FROM Short s WHERE s.ownerId = '%s'", userId), String.class);

            return Result.ok(shortIds);

        }catch(Exception e){
            LOG.severe("Error retrieving shorts: " + e.getMessage());
            return Result.error(Result.ErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
        LOG.info("User: " + userId1 + " following user: " + userId2);

        //Check validity of the parameters
        if (userId1 == null || userId2 == null || password == null) {
            LOG.info("Invalid parameters.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        try{
            //Get the hibernate instance
            Hibernate hibernate = Hibernate.getInstance();

            
            //Get the follower user
            Users followerClient = new UserClientFactory().getClient();
            Result<User> followerResult = followerClient.getUser(userId1, password);

            //Check if the follower user exists
            if(!followerResult.isOK()){
                LOG.info("User doesnt exist");
                return Result.error(followerResult.error());
            }

               //check password
            if(!followerResult.value().getPwd().equals(password)){
                LOG.info("Iconrrect password");
                return Result.error(Result.ErrorCode.FORBIDDEN);
            }

            //get the followed user from database
            Users follwedUser = new UserClientFactory().getClient();
            Result<User> followedResult = follwedUser.getUser(userId2, ".");

            //Check if the followed user exists
            if(followedResult.error().equals(Result.ErrorCode.NOT_FOUND)){
                LOG.info("User does not exist.");
                return Result.error(Result.ErrorCode.NOT_FOUND);
            }

            //Check if the user is already following
            List<Follow> alreadyFollows = hibernate.sql(String.format("SELECT * FROM Follow f WHERE f.followed = '%s' AND f.follower = '%s'", userId2, userId1), Follow.class);
            
            if (!alreadyFollows.isEmpty() && isFollowing) {
                LOG.info("Already following user.");
                return Result.error(Result.ErrorCode.CONFLICT);
            }else if (isFollowing){
                LOG.info(userId1 + "Starting following " + userId2 + " user.");
                Follow follow = new Follow(userId2, userId1);
                hibernate.persist(follow);
            }else if(!alreadyFollows.isEmpty() && !isFollowing){
                LOG.info(userId1 + "Stoped following " + userId2 + " user.");
                Follow follow = alreadyFollows.get(0);
                hibernate.delete(follow);
            }

            return Result.ok();
            
    }catch(Exception e){
        LOG.severe("Error following user: " + e.getMessage());
        return Result.error(Result.ErrorCode.INTERNAL_ERROR);
    }

}

    @Override
    public Result<List<String>> followers(String userId, String password) {
        LOG.info("Retrieving followers for user: " + userId);

        //Check validity of the parameters
        if (userId == null || password == null) {
            LOG.info("Invalid parameters.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        try{
            //Get the hibernate instance
            Hibernate hibernate = Hibernate.getInstance();

            //Check if the users exist 
            Users userClient = new UserClientFactory().getClient();
            Result<User> userResponse = userClient.getUser(userId, password);

            if(userResponse.error().equals(Result.ErrorCode.NOT_FOUND)){
                LOG.info("User does not exist.");
                return Result.error(Result.ErrorCode.NOT_FOUND);
            }

            //check password
            if(!userResponse.value().getPwd().equals(password)){
                LOG.info("Iconrrect password");
                return Result.error(Result.ErrorCode.FORBIDDEN);
            }

            //Get the list of followers
            List<Follow> followers = hibernate.sql(String.format("SELECT * FROM Follow f WHERE f.followed = '%s'", userId), Follow.class);

            List<String> followersIds = new ArrayList<>();

            for (Follow user : followers) {
                followersIds.add(user.getFollower());
            }

            return Result.ok(followersIds);

    }catch(Exception e){
        LOG.severe("Error retrieving followers: " + e.getMessage());
        return Result.error(Result.ErrorCode.INTERNAL_ERROR);
    }
}

    @Override
    public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
        LOG.info("User: " + userId + " liking short: " + shortId);

        //Check validity of the parameters
        if (shortId == null || userId == null || password == null) {
            LOG.info("Invalid parameters.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        try{
            //Get the hibernate instance
            Hibernate hibernate = Hibernate.getInstance();

             //Check if the users exist and get the user
             Users userClient = new UserClientFactory().getClient();
             Result<User> userResponse = userClient.getUser(userId, password);
    
             if(userResponse.error().equals(Result.ErrorCode.NOT_FOUND)){
                 return Result.error(Result.ErrorCode.NOT_FOUND);
             }

            //check password
            if(!userResponse.value().getPwd().equals(password)){
                LOG.info("Incorrect password");
                return Result.error(Result.ErrorCode.FORBIDDEN);
            }

            //check if the short exists
            List<Short> shorts = hibernate.sql(String.format("SELECT * FROM Short s WHERE s.shortId = '%s'", shortId), Short.class);
            if(shorts.isEmpty()){
                LOG.info("Short doesnt exist");
                return Result.error(Result.ErrorCode.NOT_FOUND);
            }

            Short updatedShort = shorts.get(0);
            //check if user has already Liked this short
            List<Likes> userLikes = hibernate.sql(String.format("SELECT * FROM Likes l WHERE l.shortId = '%s' AND l.userId = '%s'", shortId, userId), Likes.class);
            
            if(userLikes.isEmpty()){
                if (isLiked){
                    Likes newLike = new Likes(shortId, userId);
                    hibernate.persist(newLike);
                    updatedShort.setTotalLikes(updatedShort.getTotalLikes() + 1);
                }
            }else {
                if (!isLiked) {
                    hibernate.delete(userLikes.get(0));
                    updatedShort.setTotalLikes(updatedShort.getTotalLikes() - 1);
                }else {
                    return Result.error(Result.ErrorCode.CONFLICT);
                }
            }

            hibernate.update(updatedShort);
            return Result.ok();

    }catch(Exception e){
        LOG.severe("Error liking short: " + e.getMessage());
        return Result.error(Result.ErrorCode.INTERNAL_ERROR);
    }

}

    @Override
    public Result<List<String>> likes(String shortId, String password) {
        LOG.info("Retrieving likes for short: " + shortId);

        //Check validity of the parameters
        if (shortId == null || password == null) {
            LOG.info("Invalid parameters.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        try{
            //Get the hibernate instance
            Hibernate hibernate = Hibernate.getInstance();

            //get the short from database
            List<Short> shorts = hibernate.sql(String.format("SELECT * FROM Short s WHERE s.shortId = '%s'", shortId), Short.class);

            //Check if the short exists
            if (shorts.isEmpty()) {
                LOG.info("Short does not exist.");
                return Result.error(Result.ErrorCode.NOT_FOUND);
            }

            //get the owner of the short
            String ownerId = shorts.get(0).getOwnerId();

             //Check if the user exist and get the user
             Users userClient = new UserClientFactory().getClient();
             Result<User> userResponse = userClient.getUser(ownerId, password);


            //check password
            if(!userResponse.value().getPwd().equals(password)){
                LOG.info("Incorrect password");
                return Result.error(Result.ErrorCode.FORBIDDEN);
            }

            //Get the list of likes
            List<String> likes = hibernate.sql(String.format("SELECT userId FROM Likes WHERE shortId = '%s'", shortId), String.class);

            return Result.ok(likes);

    }catch(Exception e){
        LOG.severe("Error retrieving likes: " + e.getMessage());
        return Result.error(Result.ErrorCode.INTERNAL_ERROR);
    }
}

    @Override
    public Result<List<String>> getFeed(String userId, String password) {
        LOG.info("Retrieving feed for user: " + userId);

        //Check validity of the parameters
        if (userId == null || password == null) {
            LOG.info("Invalid parameters.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        try{
            //Get the hibernate instance
            Hibernate hibernate = Hibernate.getInstance();


           //Check if the users exist and get the user
           Users userClient = new UserClientFactory().getClient();
           Result<User> userResponse = userClient.getUser(userId, password);

           LOG.info("User retrieved: " + userResponse.value().displayName());

           if(userResponse.error().equals(Result.ErrorCode.NOT_FOUND)){
               LOG.info("User does not exist.:" + userResponse.value());
               return Result.error(Result.ErrorCode.NOT_FOUND);
           }

            //Get feed
            List<String> feed = hibernate.sql(String.format("SELECT s.shortId FROM Short s "
			+ " LEFT OUTER JOIN Follow f ON s.ownerId = f.followed  WHERE s.ownerId = '%s' OR f.follower = '%s' "
			+ " ORDER BY s.timestamp DESC", userId, userId), String.class);

            return Result.ok(feed);
    }catch(Exception e){
        LOG.severe("Error retrieving feed: " + e.getMessage());
        return Result.error(Result.ErrorCode.INTERNAL_ERROR);
    }
}

    @Override
	public Result<Void> checkBlobId(String shortId) {
		LOG.info("Check blobId with: " + shortId);

         //Get the hibernate instance
         Hibernate hibernate = Hibernate.getInstance();

		//Check arguments validity
		if (shortId == null) {
			LOG.info("Invalid parameters");
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		}

		//Get short with shortID
		List<Short> shrt = hibernate.sql(String.format("SELECT * FROM Short WHERE shortId = '%s'", shortId), Short.class);

        if(shrt.isEmpty()){
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

		LOG.info("short exists.");
		return Result.ok();
	}
    
}
