/**
 * 
 */
package model.data_access;

import java.sql.SQLException;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import model.GoalGroup;
import model.User;

/**
 * @author Janty Azmat
 */
public interface IDao extends AutoCloseable {

	boolean checkCredentials(String theEmail, String sha1Password);
	boolean addOffice(SimpleEntry<String, String> newOffice) throws SQLException;
	boolean updateOffice(SimpleEntry<String, String> updatedOffice);
	boolean deleteOffice(String officeUUID);
	void addUser(User newUser) throws SQLException;
	boolean updateUser(User updatedUser);
	boolean deleteUser(User userUUID);
	void close() throws SQLException;

	public List<GoalGroup> getAllGoalGroups();
}
