/**
 * 
 */
package model;

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
	private String meName;
	private String meEmail;
	private String meSha1Pass;
	private UserType meType;
	//private SimpleEntry<String, String> meOffice;
	private String meOfficeUUID;

//	public User(String fullName, String eMail, String sha1PassWord, SimpleEntry<String, String> theOffice) {
//		this(fullName, eMail, sha1PassWord, theOffice.getKey());
//	}

	public User(String fullName, String eMail, String sha1PassWord, String officeUUID) {
		this(fullName, eMail, sha1PassWord, UserType.NEW, officeUUID);
	}

	public User(String fullName, String eMail, String sha1PassWord, UserType theType, String officeUUID) {
		this.meName = fullName;
		this.meEmail = eMail;
		this.meSha1Pass = sha1PassWord;
		this.meOfficeUUID = officeUUID;
		this.meType = theType;
	}

	/**
	 * Returns the new full name for this user object.
	 * @return the full name for this user object
	 */
	public String getFullName() {
		return meName;
	}

//	/**
//	 * Sets the new full name for this user object.
//	 * @param fullName	the new full name
//	 */
//	public void setFullName(String fullName) {
//		this.meName = fullName;
//	}

	/**
	 * Returns the email for this user object.
	 * @return the email for this user object
	 */
	public String getEmail() {
		return meEmail;
	}

//	/**
//	 * Sets the new email for this user object.
//	 * @param eMail	the new email for this user object
//	 */
//	public void setEmail(String eMail) {
//		this.meEmail = eMail;
//	}

	/**
	 * Returns the SHA1 password for this user object.
	 * @return	the SHA1 password for this user object
	 */
	public String getSha1Passwrod() {
		return meSha1Pass;
	}

//	/**
//	 * Sets the new SHA1 password for this user object.
//	 * @param sha1Pass	the new SHA1 password
//	 */
//	public void setSha1Passwrod(String sha1Pass) {
//		this.meSha1Pass = sha1Pass;
//	}

//	/**
//	 * Returns the office this user object belongs to.
//	 * @return	the office this user object belongs to
//	 */
//	public SimpleEntry<String, String> getOffice() {
//		return meOffice;
//	}

	/**
	 * Returns the office this user object belongs to.
	 * @return	the office this user object belongs to
	 */
	public String getOfficeUUID() {
		return meOfficeUUID;
	}

//	/**
//	 * Sets the new office this user object belongs to.
//	 * @param theOffice	the new office
//	 */
//	public void setOffice(Entry<String, String> theOffice) {
//		this.meOffice = theOffice;
//	}

	/**
	 * Returns the type of this user object.
	 * @return the type of this user object
	 */
	public UserType getUserType() {
		return this.meType;
	}

//	/**
//	 * Sets the type of this user object.
//	 * @param userType	the new type
//	 */
//	public void setUserType(UserType userType) {
//		this.meType = userType;
//	}
}
