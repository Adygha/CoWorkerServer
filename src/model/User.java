/**
 * 
 */
package model;

import java.util.UUID;

/**
 * A class that represents a user of the application.
 * @author Janty Azmat
 */
public class User {

	/**
	 * An enum to specify the type of a user.
	 * @author Janty Azmat
	 */
	public enum UserType {

		/**
		 * A super administrator
		 */
		SUPER,

		/**
		 * An Administrator
		 */
		ADMIN,

		/**
		 * A co-worker
		 */
		COWORKER,

		/**
		 * A newly registered but not validated co-worker
		 */
		NEW
	}

	// Fields
	private String meUUID;
	private String meEmail;
	private String meSha1Pass;
	private String meName;
	private UserType meType;
	private String meOfficeUUID;

	// Constructor (automates UUID and assigns the NEW type for creating new users)
	public User(String eMail, String sha1PassWord, String fullName, String officeUUID) {
		this(UUID.randomUUID().toString(), eMail, sha1PassWord, fullName, UserType.NEW, officeUUID);
	}

	// Constructor (for creating users already exist in database)
	public User(String theUUID, String eMail, String sha1PassWord, String fullName, UserType theType, String officeUUID) {
		this.meUUID = theUUID;
		this.meEmail = eMail;
		this.meSha1Pass = sha1PassWord;
		this.meName = fullName;
		this.meType = theType;
		this.meOfficeUUID = officeUUID;
	}


	/**
	 * Returns the UUID for this user object.
	 * @return	the UUID for this user object
	 */
	public String getUUID() {
		return this.meUUID;
	}

	/**
	 * Returns the email for this user object.
	 * @return	the email for this user object
	 */
	public String getEmail() {
		return this.meEmail;
	}

	/**
	 * Returns the new full name for this user object.
	 * @return	the full name for this user object
	 */
	public String getFullName() {
		return this.meName;
	}

	/**
	 * Returns the SHA1 password for this user object.
	 * @return	the SHA1 password for this user object
	 */
	public String getSha1Passwrod() {
		return this.meSha1Pass;
	}

	/**
	 * Returns the office this user object belongs to.
	 * @return	the office this user object belongs to
	 */
	public String getOfficeUUID() {
		return this.meOfficeUUID;
	}

	/**
	 * Returns the type of this user object.
	 * @return	the type of this user object
	 */
	public UserType getUserType() {
		return this.meType;
	}
}
