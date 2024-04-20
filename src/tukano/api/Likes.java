package tukano.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Likes {

	@Id
	String shortId;
	@Id
	String userId;

	public Likes() {
	}

	/**
	 * @param shortId
	 * @param userId
	 */
	public Likes(String shortId, String userId) {
		this.shortId = shortId;
		this.userId = userId;
	}

	/**
	 * @return the shortId
	 */
	public String getShortId() {
		return shortId;
	}

	/**
	 * @param shortId the shortId to set
	 */
	public void setShortId(String shortId) {
		this.shortId = shortId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

}