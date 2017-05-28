/**
 * 
 */
package model.data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;
import org.apache.derby.jdbc.EmbeddedDriver;
import model.Goal;
import model.GoalGroup;
import model.User;
import model.User.UserType;

/**
 * A class to create a data access object that uses Derby in an embedded environment.
 * @author Janty Azmat
 */
public class DerbyDao implements IDao {
	// Constants
	private static final String me_SUPER_EMAIL = "coworkers@gmx.com";
	private static final String me_SUPER_SHA1PASS = "F335A24386FBBD8DBEDC3C9C272C16F9F871DF5F"; // SHA-1 for 'qazqazqaz' password
	private static final boolean me_IS_MULTI_CONNECTION = false;
	private static final String me_CONNECTION_STRING = "jdbc:derby:CoWorkersDB";
	private static final String me_OFFICES_TABLE = "offices"; // Users' table
	private static final String me_GOALGROUPS_TABLE = "goal_groups"; // Goal-groups' table
	private static final String me_GOALS_TABLE = "goals"; // Goal-groups' table
	private static final String me_USERS_TABLE = "users"; // Users' table
	// Fields
	private Connection meConn;
	//private String meLastFailMsg;

	// Constructor
	public DerbyDao() throws SQLException {
		DriverManager.registerDriver(new EmbeddedDriver());
		if (me_IS_MULTI_CONNECTION) {
			Connection tmpConn = this.createConnection(me_CONNECTION_STRING);
			tmpConn.close();
		} else {
			this.meConn = this.createConnection(me_CONNECTION_STRING);
		}
	}


	private Connection createConnection(String conStr) throws SQLException {
		Connection outConn;
		try {
			outConn = DriverManager.getConnection(conStr);
		} catch (SQLException e) {
			if (e.getErrorCode() == 40000 && e.getSQLState().equals("XJ004")) {
				outConn = this.createConnection(conStr + ";create=true");
				this.initializeDatabase(outConn);
			} else {
				throw e;
			}
		}
		return outConn;
	}

	// A method to setup a newly created database with tables.
	private void initializeDatabase(Connection theConnection) throws SQLException {
		try (Statement tmpStm = theConnection.createStatement()) {
			tmpStm.addBatch("CREATE TABLE " + me_OFFICES_TABLE + "(uuid char(36) PRIMARY KEY, office varchar(50) UNIQUE)"); // The offices table
			tmpStm.addBatch("CREATE TABLE " + me_USERS_TABLE + "(uuid char(36) UNIQUE NOT NULL, email varchar(50) UNIQUE NOT NULL, sha1_password char(40) NOT NULL,"
					+ " user_name varchar(50), user_type varchar(10) NOT NULL, office char(36), PRIMARY KEY(uuid, email), FOREIGN KEY(office) REFERENCES " + me_OFFICES_TABLE + "(uuid))");
			tmpStm.addBatch("CREATE TABLE " + me_GOALGROUPS_TABLE + "(uuid char(36) PRIMARY KEY, name varchar(50) UNIQUE)"); // Groups table
			tmpStm.addBatch("CREATE TABLE " + me_GOALS_TABLE + "(uuid char(36) PRIMARY KEY, name varchar(50) NOT NULL, description varchar(1024), percent smallint NOT NULL, goal_group char(36), FOREIGN KEY(goal_group) REFERENCES " + me_GOALGROUPS_TABLE + "(uuid))"); // Goals table
			tmpStm.addBatch("INSERT INTO " + me_OFFICES_TABLE + " VALUES('FC8A70BA-DBF1-453D-8989-FD8A6A86CF59', 'Växjö Office')");
			tmpStm.addBatch("INSERT INTO " + me_USERS_TABLE + " VALUES('1133FCCE-2D55-4D65-8335-933FD8B3E623', '" + me_SUPER_EMAIL + "', '" + me_SUPER_SHA1PASS + "', 'Super User', 'SUPER', 'FC8A70BA-DBF1-453D-8989-FD8A6A86CF59')");

			tmpStm.addBatch("INSERT INTO " + me_OFFICES_TABLE + " VALUES('2223FCCE-2D55-4D65-8335-933FD8B3E623', 'Ljungby Office')");
			tmpStm.addBatch("INSERT INTO " + me_OFFICES_TABLE + " VALUES('A2A142C4-F24A-407D-9C7F-6C3FD7846284', 'Älmhult Office')");
			tmpStm.addBatch("INSERT INTO " + me_USERS_TABLE + " VALUES('2233FCCE-2D55-4D65-8335-933FD8B3E623', 'aa@bb.cc', 'A056C8D05AE9AC6CA180BC991B93B7FFE37563E0', 'me', 'ADMIN', '2223FCCE-2D55-4D65-8335-933FD8B3E623')");
			tmpStm.addBatch("INSERT INTO " + me_USERS_TABLE + " VALUES('4433FCCE-2D55-4D65-8335-933FD8B3E623', 'bb@bb.cc', 'A056C8D05AE9AC6CA180BC991B93B7FFE37563E0', 'me', 'COWORKER', '2223FCCE-2D55-4D65-8335-933FD8B3E623')");
			tmpStm.addBatch("INSERT INTO " + me_USERS_TABLE + " VALUES('5533FCCE-2D55-4D65-8335-933FD8B3E623', 'cc@bb.cc', 'A056C8D05AE9AC6CA180BC991B93B7FFE37563E0', 'me', 'NEW', '2223FCCE-2D55-4D65-8335-933FD8B3E623')");
			tmpStm.addBatch("INSERT INTO " + me_GOALGROUPS_TABLE + " VALUES('3333FCCE-2D55-4D65-8335-933FD8B3E623', 'Customer Relations Goals')");
			tmpStm.addBatch("INSERT INTO " + me_GOALGROUPS_TABLE + " VALUES('4444FCCE-2D55-4D65-8335-933FD8B3E623', 'Accounting Goals')");
			tmpStm.addBatch("INSERT INTO " + me_GOALGROUPS_TABLE + " VALUES('5555FCCE-2D55-4D65-8335-933FD8B3E623', 'Fiscal Year Goals')");
			tmpStm.addBatch("INSERT INTO " + me_GOALS_TABLE + " VALUES('AAA142C4-F24A-407D-9C7F-6C3FD7846284', 'Collecting 10000 Customer Emails', 'The target this year is to collect 10000 customer emails (SPAM THE UNIVERSE).', 20, '3333FCCE-2D55-4D65-8335-933FD8B3E623')");
			tmpStm.addBatch("INSERT INTO " + me_GOALS_TABLE + " VALUES('BBB142C4-F24A-407D-9C7F-6C3FD7846284', 'Get Customer Feedback', 'The target this year is to collect 10000 customers feedbacks of the survey.', 45, '3333FCCE-2D55-4D65-8335-933FD8B3E623')");
			tmpStm.addBatch("INSERT INTO " + me_GOALS_TABLE + " VALUES('CCC142C4-F24A-407D-9C7F-6C3FD7846284', 'Get Opinions About New Decoration', 'Get at least 100 customers opinions on the new office decoration.', 82, '3333FCCE-2D55-4D65-8335-933FD8B3E623')");
			tmpStm.addBatch("INSERT INTO " + me_GOALS_TABLE + " VALUES('DDD142C4-F24A-407D-9C7F-6C3FD7846284', 'Get All Departments'' Fiscal Year', 'Get all departments fiscal year before march.', 83, '5555FCCE-2D55-4D65-8335-933FD8B3E623')");
			//tmpStm.addBatch("");
			tmpStm.executeBatch();
		}
	}

//	public void backupDataBase() {
//		PreparedStatement tmpStm = null;
//		try {
//			tmpStm = this.meConn.prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)");
//			tmpStm.setString(1, "xxxBkup");
//			tmpStm.execute();
//		} catch (SQLException e) {
//			this.meLastFailMsg = "Error while backing up database";
//		}
//	}

	@Override
	public synchronized void close() throws SQLException {
		this.meConn = DriverManager.getConnection("jdbc:derby:;shutdown=true");
	}

	@Override
	public boolean checkCredentials(String theEmail, String sha1Password) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("SELECT sha1_password FROM " + me_USERS_TABLE + " WHERE email='" + theEmail + "'"); ResultSet tmpRes = tmpStm.executeQuery()) {
			if (tmpRes.next()) {
				return (sha1Password.equals(tmpRes.getString(1))); // SHA-1 for 'qqq'
			} else {
				return false;
			}
		}
	}

	@Override
	public List<GoalGroup> getGoalGroup(boolean isWithGoals) throws SQLException {
		Connection tmpConn;
		LinkedList<GoalGroup> outGoals = new LinkedList<GoalGroup>();
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("SELECT * FROM " + me_GOALGROUPS_TABLE); ResultSet tmpRes = tmpStm.executeQuery()) {
			while (tmpRes.next()) {
				GoalGroup tmpGrp = new GoalGroup(tmpRes.getString(1), tmpRes.getString(2));
				if (isWithGoals) {
					try (PreparedStatement tmpGoalStm = tmpConn.prepareStatement("SELECT * FROM " + me_GOALS_TABLE + " WHERE goal_group='" + tmpGrp.getUUID() + "'"); ResultSet tmpGoalRes = tmpGoalStm.executeQuery()) {
						while (tmpGoalRes.next())
							tmpGrp.addGoal(new Goal(tmpGoalRes.getString(1), tmpGoalRes.getString(2), tmpGoalRes.getString(3), tmpGoalRes.getShort(4), tmpGoalRes.getString(5)));
					}
				}
				outGoals.add(tmpGrp);
			}
		}
		return outGoals;
	}

	@Override
	public GoalGroup getGoalGroup(String goalUUID) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("SELECT * FROM " + me_GOALGROUPS_TABLE + " WHERE uuid='" + goalUUID + "'"); ResultSet tmpRes = tmpStm.executeQuery()) {
			if (tmpRes.next())
				return new GoalGroup(tmpRes.getString(1), tmpRes.getString(2));
		}
		return null;
	}

	@Override
	public void addGoalGroup(GoalGroup theGroup) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("INSERT INTO " + me_GOALGROUPS_TABLE + " VALUES('"
																+ theGroup.getUUID() + "', '" + theGroup.getName() + "')")) {
			tmpStm.executeUpdate();
		}
	}

	@Override
	public void updateGoalGroup(GoalGroup updatedGroup) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("UPDATE " + me_GOALGROUPS_TABLE + " SET name='"
																+ updatedGroup.getName() + "' WHERE uuid='" + updatedGroup.getUUID() + "'")) {
			tmpStm.executeUpdate();
		}
	}

	@Override
	public void deleteGoalGroup(String groupUUID) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("DELETE FROM " + me_GOALGROUPS_TABLE + " WHERE uuid='" + groupUUID + "'")) {
			tmpStm.executeUpdate();
		}
	}

	@Override
	public List<SimpleEntry<String, String>> getOffice() throws SQLException {
		List<SimpleEntry<String, String>> outOffices = new LinkedList<SimpleEntry<String, String>>();
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("SELECT * FROM " + me_OFFICES_TABLE); ResultSet tmpRes = tmpStm.executeQuery()) {
			while (tmpRes.next())
				outOffices.add(new SimpleEntry<String, String>(tmpRes.getString(1), tmpRes.getString(2)));
		}
		return outOffices;
	}

	@Override
	public synchronized void addOffice(SimpleEntry<String, String> newOffice) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("INSERT INTO " + me_OFFICES_TABLE + " VALUES('"
																+ newOffice.getKey() + "', '" + newOffice.getValue() + "')")) {
			tmpStm.executeUpdate();
		}
	}

	@Override
	public synchronized void updateOffice(SimpleEntry<String, String> updatedOffice) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("UPDATE " + me_OFFICES_TABLE + " SET office='"
																+ updatedOffice.getValue() + "' WHERE uuid='" + updatedOffice.getKey() + "'")) {
			tmpStm.executeUpdate();
		}
	}

	@Override
	public synchronized void deleteOffice(String officeUUID) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("DELETE FROM " + me_OFFICES_TABLE + " WHERE uuid='" + officeUUID + "'")) {
			tmpStm.executeUpdate();
		}
	}

	@Override
	public Goal getGoal(String goalUUID) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("SELECT * FROM " + me_GOALS_TABLE + " WHERE uuid='" + goalUUID + "'"); ResultSet tmpRes = tmpStm.executeQuery()) {
			if (tmpRes.next())
				return new Goal(tmpRes.getString(1), tmpRes.getString(2), tmpRes.getString(3), tmpRes.getShort(4), tmpRes.getString(5));
		}
		return null;
	}


	@Override
	public synchronized void addGoal(Goal newGoal) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement(
				"INSERT INTO " + me_GOALS_TABLE + " VALUES('" + newGoal.getUUID() + "', '" + newGoal.getName() + "', '"
				+ newGoal.getDescription() + "', " + newGoal.getPercentage() + ", '" + newGoal.getGroupUUID() + "')")) {
			tmpStm.executeUpdate();
		}
	}


	@Override
	public synchronized void updateGoal(Goal updatedGoal) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("UPDATE " + me_GOALS_TABLE + " SET name='" + updatedGoal.getName()
																+ "', description='" + updatedGoal.getDescription()
																+ "', percent=" + updatedGoal.getPercentage()
																+ ", goal_group='" + updatedGoal.getGroupUUID()
																+ "' WHERE uuid='" + updatedGoal.getUUID() + "'")) {
			tmpStm.executeUpdate();
		}
	}


	@Override
	public synchronized void deleteGoal(String goalUUID) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("DELETE FROM " + me_GOALS_TABLE + " WHERE uuid='" + goalUUID + "'")) {
			tmpStm.executeUpdate();
		}
	}

	@Override
	public List<User> getUser() throws SQLException {
		List<User> outList = new LinkedList<User>();
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("SELECT * FROM " + me_USERS_TABLE + " WHERE email!='" + me_SUPER_EMAIL + "'"); ResultSet tmpRes = tmpStm.executeQuery()) {
			while (tmpRes.next())
				outList.add(new User(tmpRes.getString(1), tmpRes.getString(2), tmpRes.getString(3), tmpRes.getString(4), UserType.valueOf(tmpRes.getString(5)), tmpRes.getString(6)));
		}
		return outList;
	}

	@Override
	public User getUser(String byKey, boolean isKeyEmail) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("SELECT * FROM " + me_USERS_TABLE + " WHERE " + (isKeyEmail ? "email='" : "uuid='") + byKey + "'"); ResultSet tmpRes = tmpStm.executeQuery()) {
			if (tmpRes.next())
				return new User(tmpRes.getString(1), tmpRes.getString(2), tmpRes.getString(3), tmpRes.getString(4), UserType.valueOf(tmpRes.getString(5)), tmpRes.getString(6));
		}
		return null;
	}

	@Override
	public synchronized void addUser(User newUser) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement(
				"INSERT INTO " + me_USERS_TABLE + " VALUES('" + newUser.getUUID() + "', '" + newUser.getEmail() + "', '"
				+ newUser.getSha1Passwrod() + "', '" + newUser.getFullName() + "', '" + newUser.getUserType().toString()
				+ "', '" + newUser.getOfficeUUID() + "')")) {
			tmpStm.executeUpdate();
		}
	}

	@Override
	public synchronized void updateUser(User updatedUser) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("UPDATE " + me_USERS_TABLE + " SET email='" + updatedUser.getEmail()
																+ "', sha1_password='" + updatedUser.getSha1Passwrod()
																+ "', user_name='" + updatedUser.getFullName()
																+ "', user_type='" + updatedUser.getUserType().toString()
																+ "', office='" + updatedUser.getOfficeUUID()
																+ "' WHERE uuid='" + updatedUser.getUUID() + "'")) {
			tmpStm.executeUpdate();
		}
	}

	@Override
	public synchronized void deleteUser(String userUUID) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("DELETE FROM " + me_USERS_TABLE + " WHERE uuid='" + userUUID + "'")) {
			tmpStm.executeUpdate();
		}
	}
}
