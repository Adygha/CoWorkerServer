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
	private String meGroupUUID;
	private int mePcent;

	// Constructor for newly created goal
	public Goal(String goalName, String goalDescription, int goalPercentage, String groupUUID) {
		this.meUUID = UUID.randomUUID().toString();
		this.meName = goalName;
		this.meDesc = goalDescription;
		this.meGroupUUID = groupUUID;
		this.mePcent = goalPercentage;
	}

	// Constructor for a goal that we already know the ID
	public Goal(String goalUUID, String goalName, String goalDescription, int goalPercentage, String groupUUID) {
		this.meUUID = goalUUID;
		this.meName = goalName;
		this.meDesc = goalDescription;
		this.meGroupUUID = groupUUID;
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
		return meGroupUUID;
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
}
