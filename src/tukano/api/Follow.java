package tukano.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Follow {
	@Id
	String followed;
	@Id
	String follower;

	public Follow() {
	}


	public Follow(String followed, String follower) {
		this.followed = followed;
		this.follower = follower;
	}

	/**
	 * @return the followed
	 */
	public String getFollowed() {
		return followed;
	}

	/**
	 * @param followed the followed to set
	 */
	public void setFollowed(String followed) {
		this.followed = followed;
	}

	/**
	 * @return the follower
	 */
	public String getFollower() {
		return follower;
	}

	/**
	 * @param follower the follower to set
	 */
	public void setFollower(String follower) {
		this.follower = follower;
	}

}