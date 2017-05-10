/**
 * 
 */
package model;

import java.util.UUID;

/**
 * @author Janty Azmat
 */
public class Goal {
	// Fields
	private String meUUID;
	private String meName;
	private String meDesc;
	private String meCatUUID;
	private int mePcent;
	//private LinkedList<Comment> meCmnts;

	public Goal(String goalName, String goalDecription, int goalPercentage, String categoryUUID) {
		this.meUUID = UUID.randomUUID().toString();
		this.meName = goalName;
		this.meDesc = goalDecription;
		this.meCatUUID = categoryUUID;
		this.mePcent = goalPercentage;
	}

	/**
	 * Returns the goal name.
	 * @return	the goal name
	 */
	public String getName() {
		return meName;
	}

	/**
	 * Returns the goal description.
	 * @return	the goal description
	 */
	public String getDescription() {
		return meDesc;
	}

	/**
	 * Returns the goal category's UUID.
	 * @return	the goal category's UUID
	 */
	public String getGroupUUID() {
		return meCatUUID;
	}

	/**
	 * Returns the goal percentage.
	 * @return	the goal percentage
	 */
	public int getPercentage() {
		return mePcent;
	}

	/**
	 * Returns the goal UUID.
	 * @return	the goal UUID
	 */
	public String getUUID() {
		return meUUID;
	}

//	/**
//	 * Returns a list of the goal's comments.
//	 * @return	the goal's comments
//	 */
//	public List<Comment> getMeCmnts() {
//		return meCmnts;
//	}
}
