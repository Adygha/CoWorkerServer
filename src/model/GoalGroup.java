/**
 * 
 */
package model;

import java.util.LinkedList;
import java.util.List;

/**
 * A class that represents a group of goals
 * @author Janty Azmat
 */
public class GoalGroup {
	// Fields
	private String meName;
	private String meUUID;
	private List<Goal> meGoals;

	public GoalGroup(String groupName, String groupUUID) {
		this.meName = groupName;
		this.meUUID = groupUUID;
		this.meGoals = new LinkedList<Goal>();
	}

	/**
	 * A method to add a goal to this group.
	 * @param	the new goal to add
	 */
	public void addGoal(Goal theGoal) {
		if (!theGoal.getGroupUUID().equals(this.meUUID))
			throw new IllegalArgumentException("The input goal does not belongto this group.");
		this.meGoals.add(theGoal);
	}

	/**
	 * Returns the group's name.
	 * @return	the group's name
	 */
	public String getName() {
		return meName;
	}

	/**
	 * Returns the group's UUID.
	 * @return	 the group's UUID
	 */
	public String getUUID() {
		return meUUID;
	}

	/**
	 * Returns the group's goal list.
	 * @return	the group's goal list
	 */
	public List<Goal> getGoals() {
		return meGoals;
	}
}
