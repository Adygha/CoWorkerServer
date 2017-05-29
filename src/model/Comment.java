/**
 * 
 */
package model;

import java.time.LocalDateTime;

/**
 * @author Janty Azmat
 */
public class Comment {
	// Fields
	private String meUserUUID;
	private String meGoalUUID;
	private String meContent;
	private LocalDateTime meDateTime;
	private User meUser; // It will be null if the user is deleted

	public Comment(String userUUID, String goalUUID, String theContent, LocalDateTime theDateTime, User theUser) {
		this.meUserUUID = userUUID;
		this.meGoalUUID = goalUUID;
		this.meContent = theContent;
		this.meDateTime = theDateTime;
		this.meUser = theUser;
	}

	/**
	 * Returns the comment's user's ID.
	 * @return	the comment's user's ID
	 */
	public String getUserUUID() {
		return this.meUserUUID;
	}

	/**
	 * Returns the comment's goals's ID, or the 'null' string if the comment belongs to the chat page.
	 * @return	the comment's goals's ID or 'null' string.
	 */
	public String getGoalUUID() {
		return this.meGoalUUID;
	}

	/**
	 * Returns the comment's text content.
	 * @return	the comment's text content
	 */
	public String getContent() {
		return this.meContent;
	}

	/**
	 * Returns the comment's date and time.
	 * @return	the comment's date and time
	 */
	public LocalDateTime getDateTime() {
		return this.meDateTime;
	}

	/**
	 * Returns the comment's user.
	 * @return	the comment's user
	 */
	public User getUser() {
		return this.meUser;
	}
}
