/**
 * 
 */
package model;

import java.time.LocalDateTime;

/**
 * @author Janty Azmat
 *
 */
class Comment {
	// Fields
	private User meUser;
	private String meTxt;
	private LocalDateTime meDateTime;
	private Goal meContainer; // The comment container. If this is null (maybe changes) this this comment is a chat page comment

	public Comment(User theUser, String theText, LocalDateTime theDateTime, Goal theContainer) {
		this.meUser = theUser;
		this.meTxt = theText;
		this.meDateTime = theDateTime;
		this.meContainer = theContainer;
	}

	/**
	 * Returns the comment's user.
	 * @return	the comment's user
	 */
	public User getUser() {
		return this.meUser;
	}

	/**
	 * Returns the comment's text.
	 * @return	the comment's text
	 */
	public String getText() {
		return this.meTxt;
	}

	/**
	 * Returns the comment's date and time.
	 * @return	the comment's date and time
	 */
	public LocalDateTime getDateTime() {
		return this.meDateTime;
	}

	/**
	 * Returns the goal that contains this comment, or 'null' if the main chat page contains the comment.
	 * @return	the comment's container
	 */
	public Goal getContainer() {
		return this.meContainer;
	}

	/**
	 * Returns the office this comment belongs to.
	 * @return	 the office this comment belongs to
	 */
	public String getOfficeUUID() {
		return this.meUser.getOfficeUUID();
	}
}
