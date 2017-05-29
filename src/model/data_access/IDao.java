/**
 * 
 */
package model.data_access;

import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import model.Comment;
import model.Goal;
import model.GoalGroup;
import model.User;

/**
 * An interface the data access object that this server uses.
 * @author Janty Azmat
 */
public interface IDao extends AutoCloseable {

	void close() throws SQLException; // Added here to restrict to only SQLException
	boolean checkCredentials(String theEmail, String sha1Password) throws SQLException;
	List<GoalGroup> getGoalGroup(boolean isWithGoals) throws SQLException;
	GoalGroup getGoalGroup(String goalUUID) throws SQLException;
	void addGoalGroup(GoalGroup theGroup) throws SQLException;
	void updateGoalGroup(GoalGroup updatedGroup) throws SQLException;
	void deleteGoalGroup(String groupUUID) throws SQLException;
	List<SimpleEntry<String, String>> getOffice() throws SQLException;
	void addOffice(SimpleEntry<String, String> newOffice) throws SQLException;
	void updateOffice(SimpleEntry<String, String> updatedOffice) throws SQLException;
	void deleteOffice(String officeUUID) throws SQLException;
	Goal getGoal(String goalUUID) throws SQLException;
	void addGoal(Goal newGoal) throws SQLException;
	void updateGoal(Goal updatedGoal) throws SQLException;
	void deleteGoal(String goalUUID) throws SQLException;
	List<User> getUser() throws SQLException;
	User getUser(String byKey, boolean isKeyEmail) throws SQLException;
	void addUser(User newUser) throws SQLException;
	void updateUser(User updatedUser) throws SQLException;
	void deleteUser(String userUUID) throws SQLException;
	List<Comment> getComment(String goalUUID) throws SQLException;
	void addComment(Comment newComment) throws SQLException;
	void editComment(Comment updatedComment);
	void deleteComment(String commentUUID);
}
