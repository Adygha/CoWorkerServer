/**
 * 
 */
package model.data_access;

import java.sql.SQLException;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import model.Goal;
import model.GoalGroup;
import model.User;

/**
 * An interface the data access object that this server uses.
 * @author Janty Azmat
 */
public interface IDao extends AutoCloseable {

	boolean checkCredentials(String theEmail, String sha1Password) throws SQLException;
	void addOffice(SimpleEntry<String, String> newOffice) throws SQLException;
	void updateOffice(SimpleEntry<String, String> updatedOffice) throws SQLException;
	void deleteOffice(String officeUUID) throws SQLException;
	Goal getGoal(String goalUUID);
	void addGoal(String goalUUID, String goalName, String goalDecription, int goalPercentage, String groupUUID);
	void updateGoal(Goal updatedGoal);
	void deleteGoal(String goalUUID);
	User getUser(String eMail) throws SQLException;
	void addUser(User newUser) throws SQLException;
	boolean updateUser(User updatedUser);
	boolean deleteUser(String userUUID);
	void close() throws SQLException;

	public List<GoalGroup> getAllGoalGroups(boolean isWithGoals) throws SQLException;
}
