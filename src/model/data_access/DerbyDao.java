/**
 * 
 */
package model.data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;

import org.apache.derby.jdbc.EmbeddedDriver;

import model.Goal;
import model.GoalGroup;
import model.User;

/**
 * @author Janty Azmat
 */
public class DerbyDao implements IDao {
	// Constants
	private static final boolean me_IS_MULTI_CONNECTION = false;
	private static final String me_CONNECTION_STRING = "jdbc:derby:CoWorkersDB";
	private static final String me_OFFICES_TABLE = "offices"; // Users' table
	private static final String me_USERS_TABLE = "users"; // Users' table
	// Fields
	private Connection meConn;
	private String meLastFailMsg;

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
			tmpStm.addBatch("CREATE TABLE " + me_USERS_TABLE + "(email varchar(50) PRIMARY KEY, sha1_password char(40) NOT NULL, user_name varchar(50), user_type varchar(10) NOT NULL, office char(36), FOREIGN KEY(office) REFERENCES offices(uuid))");
			tmpStm.addBatch("INSERT INTO " + me_OFFICES_TABLE + " VALUES('FC8A70BA-DBF1-453D-8989-FD8A6A86CF59', 'Växjö Office')");
			tmpStm.addBatch("INSERT INTO " + me_OFFICES_TABLE + " VALUES('2223FCCE-2D55-4D65-8335-933FD8B3E623', 'Ljungby Office')");
			tmpStm.addBatch("INSERT INTO " + me_OFFICES_TABLE + " VALUES('A2A142C4-F24A-407D-9C7F-6C3FD7846284', 'Älmhult Office')");
			//tmpStm.addBatch("");
			tmpStm.executeBatch();
		}
//		this.addOffice(new SimpleEntry<String, String>("FC8A70BA-DBF1-453D-8989-FD8A6A86CF59", "Växjö Office"));
//		this.addOffice(new SimpleEntry<String, String>("2223FCCE-2D55-4D65-8335-933FD8B3E623", "Ljungby Office"));
//		this.addOffice(new SimpleEntry<String, String>("A2A142C4-F24A-407D-9C7F-6C3FD7846284", "Älmhult Office"));
//		this.addUser(new User("Dudy Dude", "aa@bb.cc", "A056C8D05AE9AC6CA180BC991B93B7FFE37563E0", new SimpleEntry<String, String>("2223FCCE-2D55-4D65-8335-933FD8B3E623", "Ljungby Office")));
//		return true;
	}

//	public void backupDataBase() {
//		PreparedStatement tmpStm = null;
//		try {
//			tmpStm = this.meConn.prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)");
//			tmpStm.setString(1, "xxxxbkup");
//			tmpStm.execute();
//		} catch (SQLException e) {
//			this.meLastFailMsg = "Error while backing up database";
//		}
//	}


	/**
	 * A method to get the message describing the last failure.
	 * @return	the message describing the last failure
	 */
	public String getLastFailureMessage() {
		return meLastFailMsg;
	}

	@Override
	public synchronized void close() throws SQLException {
		this.meConn = DriverManager.getConnection("jdbc:derby:;shutdown=true");
	}

	@Override
	public boolean checkCredentials(String theEmail, String sha1Password) {
		// TODO Auto-generated method stub
		return (theEmail.equals("aa@bb.cc") && sha1Password.equals("A056C8D05AE9AC6CA180BC991B93B7FFE37563E0")); // SHA-1 for 'qqq'
	}

	@Override
	public List<GoalGroup> getAllGoalGroups() {
		LinkedList<GoalGroup> outGoals = new LinkedList<>();
		outGoals.add(new GoalGroup("1st Group", "1stcat"));
		outGoals.getFirst().addGoal(new Goal("First Goal", "It's a Goal", 30, "1stcat"));
		outGoals.getFirst().addGoal(new Goal("Second Goal", "It's a Goal too", 90, "1stcat"));
		outGoals.add(new GoalGroup("2nd Group", "2ndcat"));
		outGoals.getLast().addGoal(new Goal("Third Goal", "A Goal Man..", 60, "2ndcat"));
		return outGoals;
	}

	@Override
	public synchronized boolean addOffice(SimpleEntry<String, String> newOffice) throws SQLException {
		Connection tmpConn;
		if (me_IS_MULTI_CONNECTION) {
			tmpConn = this.createConnection(me_CONNECTION_STRING);
		} else {
			tmpConn = this.meConn;
		}
		try (PreparedStatement tmpStm = tmpConn.prepareStatement("INSERT INTO " + me_OFFICES_TABLE + " VALUES('"
																	+ newOffice.getKey() + "', '" + newOffice.getValue() + "')")) {
			if (tmpStm.executeUpdate() > 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public synchronized boolean updateOffice(SimpleEntry<String, String> updatedOffice) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public synchronized boolean deleteOffice(String officeUUID) {
		// TODO Auto-generated method stub
		return false;
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
				"INSERT INTO " + me_USERS_TABLE + " VALUES('" + newUser.getEmail() + "', '" + newUser.getSha1Passwrod() + "', '"
				+ newUser.getFullName() + "', '" + newUser.getUserType().toString() + "', '" + newUser.getOfficeUUID() + "')")) {
			tmpStm.executeUpdate();
		}
	}

	@Override
	public synchronized boolean updateUser(User updatedUser) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public synchronized boolean deleteUser(User userUUID) {
		// TODO Auto-generated method stub
		return false;
	}
}
