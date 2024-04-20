package tukano.impl.users;

import java.util.List;

import java.util.logging.Logger;

import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import tukano.api.java.Users;
import tukano.impl.hibernate.Hibernate;

public class JavaUsers implements Users{

    private static Logger LOG = Logger.getLogger(JavaUsers.class.getName());


    @Override
    public Result<String> createUser(User user) {
        LOG.info("Creating user: " + user.getUserId());
        

        //Check validity of the parameters
        if (user.getUserId() == null || user.getPwd() == null || user.getEmail() == null || user.getDisplayName() == null) {
            LOG.info("Invalid parameters.");
            return Result.error(ErrorCode.BAD_REQUEST);
        }

        try{
            //Get the hibernate instance
            Hibernate hibernate = Hibernate.getInstance();

            //get the current user from database
            List<User> users = hibernate.sql(String.format("SELECT * FROM User u WHERE u.userId = '%s'", user.getUserId()), User.class);
            System.out.println("users size:" + users.size());

            //Check if the user already exists
            if (users.size() > 0) {
                LOG.info("User already exists.");
                return Result.error(ErrorCode.CONFLICT);
            }

            //Persist/save the user
            hibernate.persist(user);
            return Result.ok(user.getUserId());

        }catch(Exception e){
            LOG.severe("Error creating user: " + e.getMessage());
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }


    }

    @Override
    public Result<User> getUser(String userId, String pwd) {
        LOG.info("Getting user: " + userId);

        //Check validity of the parameters
        if (userId == null || pwd == null) {
            LOG.info("Invalid parameters.");
            LOG.info("password " + pwd);

            return Result.error(ErrorCode.BAD_REQUEST);
        }

        try{
            //Get the hibernate instance
            Hibernate hibernate = Hibernate.getInstance();

            //get the current user from database
            List<User> users = hibernate.sql(String.format("SELECT * FROM User u WHERE u.userId = '%s'", userId), User.class);

            //Check if the user exists
            if (users.size() == 0) {
                LOG.info("User does not exist.");
                return Result.error(ErrorCode.NOT_FOUND);
            }

            //Check if the password is correct
            if (!users.get(0).getPwd().equals(pwd)) {
                LOG.info("Incorrect password.");
                return Result.error(ErrorCode.FORBIDDEN);
            }

            return Result.ok(users.get(0));

        }catch(Exception e){
            LOG.severe("Error getting user: " + e.getMessage());
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<User> updateUser(String userId, String pwd, User user) {
        LOG.info("Updating user: " + userId);

        //Check validity of the parameters
        if (userId == null || pwd == null || user == null || (user.getUserId() != null && !userId.equals(user.getUserId()))) {
            LOG.info("Invalid parameters.");
            return Result.error(ErrorCode.BAD_REQUEST);
        }

        try{
            //Get the hibernate instance
            Hibernate hibernate = Hibernate.getInstance();

            //get the current user from database
            List<User> users = hibernate.sql(String.format("SELECT * FROM User u WHERE u.userId = '%s'", userId), User.class);

            //Check if the user exists
            if (users.size() == 0) {
                LOG.info("User does not exist.");
                return Result.error(ErrorCode.NOT_FOUND);
            }

            User oldUser = users.get(0);	//Get the old user

            //Check if the password is correct
            if (!oldUser.getPwd().equals(pwd)) {
                LOG.info("Incorrect password.");
                return Result.error(ErrorCode.FORBIDDEN);
            }

            //if the new user has null fields we keep the old ones
            User updatedUser = new User(oldUser.getUserId(), user.getPwd() != null ? user.getPwd() : pwd,
				user.getEmail() != null ? user.getEmail() : oldUser.getEmail(),
				user.getDisplayName() != null ? user.getDisplayName() : oldUser.getDisplayName());


            //Update the user
            hibernate.update(updatedUser);
            return Result.ok(updatedUser);

        }catch(Exception e){
            LOG.severe("Error updating user: " + e.getMessage());
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<User> deleteUser(String userId, String pwd) {
        LOG.info("Deleting user: " + userId);

        //Check validity of the parameters
        if (userId == null || pwd == null) {
            LOG.info("Invalid parameters.");
            return Result.error(ErrorCode.BAD_REQUEST);
        }

        try{
            //Get the hibernate instance
            Hibernate hibernate = Hibernate.getInstance();

            //get the current user from database
            List<User> users = hibernate.sql(String.format("SELECT * FROM User u WHERE u.userId = '%s'", userId), User.class);

            //Check if the user exists
            if (users.size() == 0) {
                LOG.info("User does not exist.");
                return Result.error(ErrorCode.NOT_FOUND);
            }

            //Check if the password is correct
            if (!users.get(0).getPwd().equals(pwd)) {
                LOG.info("Incorrect password.");
                return Result.error(ErrorCode.FORBIDDEN);
            }

            //Delete the user
            hibernate.delete(users.get(0));
            return Result.ok(users.get(0));

        }catch(Exception e){
            LOG.severe("Error deleting user: " + e.getMessage());
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        LOG.info("Searching users: " + pattern);

        //Check validity of the parameters
        if (pattern == null) {
            LOG.info("Invalid parameters.");
            return Result.error(ErrorCode.BAD_REQUEST);
        }

        try{
            //Get the hibernate instance
            Hibernate hibernate = Hibernate.getInstance();

            //get the current user from database
            List<User> users = hibernate.sql(String.format("SELECT * FROM User u WHERE LOWER(u.userId) LIKE LOWER('%%%s%%')", pattern), User.class);

            return Result.ok(users);

        }catch(Exception e){
            LOG.severe("Error searching users: " + e.getMessage());
            return Result.error(ErrorCode.INTERNAL_ERROR);
        }
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
