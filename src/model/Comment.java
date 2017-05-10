/**
 * 
 */
package model;

import java.time.LocalDateTime;

/**
 * @author Janty Azmat
 *
 */
public class Comment {
	// Fields
	private User meUser;
	private String meTxt;
	private LocalDateTime meDateTime;
	private Goal meContiner; // The comment container. If this is null (maybe changes) this this comment is a chat page comment

	public Comment(User theUser, String theText, LocalDateTime theDateTime, Goal theContainer) {
		this.meUser = theUser;
		this.meTxt = theText;
		this.meDateTime = theDateTime;
		this.meContiner = theContainer;
	}

	/**
	 * Returns the comment's user.
	 * @return	the comment's user
	 */
	public User getUser() {
		return meUser;
	}

	/**
	 * Returns the comment's text.
	 * @return	the comment's text
	 */
	public String getText() {
		return meTxt;
	}

//	/**
//	 * A method to set the comment's text.
//	 * @param the new comment's text.
//	 */
//	public void setText(String newText) {
//		this.meTxt = newText;
//	}

	/**
	 * Returns the comment's date and time.
	 * @return	the comment's date and time
	 */
	public LocalDateTime getDateTime() {
		return meDateTime;
	}

	/**
	 * Returns the goal that contains this comment, or 'null' if the main chat page contains the comment.
	 * @return	the comment's container
	 */
	public Goal getMeContiner() {
		return meContiner;
	}

//	/**
//	 * Returns the office this comment belongs to.
//	 * @return	 the office this comment belongs to
//	 */
//	public SimpleEntry<String, String> getOffice() {
//		return this.meUser.getOffice();
//	}

	/**
	 * Returns the office this comment belongs to.
	 * @return	 the office this comment belongs to
	 */
	public String getOfficeUUID() {
		return this.meUser.getOfficeUUID();
	}
}
