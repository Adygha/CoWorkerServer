/**
 * 
 */
package model;

/**
 * A class that represents a raw request.
 * @author Janty Azmat
 */
public class RawRequest {

	/**
	 * An enumeration to specify the type of a raw requests (not HTTP).
	 * @author Janty Azmat
	 */
	public static enum RawRequestType {
		GET,
		CREATE,
		UPDATE,
		DELETE,
		BAD
	}

	public static enum RawRequestTarget {
		LOGIN,
		MAIN,
		CHAT,
		GOAL,
		GOALEDIT,
		USER,
		REGISTER,
		SHA1
		//NONE
	}

	// Fields
	//private boolean meIsCredOk;
	private RawRequestType meType;
	private RawRequestTarget meTarget;
	private String meUserName;
	private String meSha1Pass;
	private String mePayload;

	// Constructor
	public RawRequest(String reqString) {
		String[] tmpArr = reqString.split(" ", 3);
		String[] tmpCred = tmpArr[1].split(":", 2);
		String[] tmpType = tmpArr[0].split("_");
		if (tmpCred.length == 2) {
			this.meUserName = tmpCred[0].startsWith("/") ? tmpCred[0].substring(1) : tmpCred[0];
			this.meSha1Pass = tmpCred[1];
		} else {
			this.meUserName = "null";
			this.meSha1Pass = "null";
		}
		switch (tmpType[0]) {
			case "GET":
				this.meType = RawRequestType.GET;
				break;
			case "CREATE":
				this.meType = RawRequestType.CREATE;
				break;
			case "UPDATE":
				this.meType = RawRequestType.UPDATE;
				break;
			case "DELETE":
				this.meType = RawRequestType.DELETE;
				break;
			default:
				this.meType = RawRequestType.BAD;
		}
		switch (tmpType[1]) {
			case "LOGIN":
				if (this.meType == RawRequestType.GET) {
					this.meTarget = RawRequestTarget.LOGIN;
				} else {
					this.meType = RawRequestType.BAD;
					//this.meTarget = RawRequestTarget.NONE;
				}
				break;
			case "MAIN":
				if (this.meType == RawRequestType.GET) {
					this.meTarget = RawRequestTarget.MAIN;
				} else {
					this.meType = RawRequestType.BAD;
					//this.meTarget = RawRequestTarget.NONE;
				}
				break;
			case "CHAT":
				this.meTarget = RawRequestTarget.CHAT;
				break;
			case "GOAL":
				this.meTarget = RawRequestTarget.GOAL;
				break;
			case "GOALEDIT":
				this.meTarget = RawRequestTarget.GOALEDIT;
				break;
			case "USER":
				this.meTarget = RawRequestTarget.USER;
				break;
			case "REGISTER":
				if (this.meType == RawRequestType.GET) {
					this.meTarget = RawRequestTarget.REGISTER;
				} else {
					this.meType = RawRequestType.BAD;
					//this.meTarget = RawRequestTarget.NONE;
				}
				break;
			case "SHA1":
				if (this.meType == RawRequestType.GET) {
					this.meTarget = RawRequestTarget.SHA1;
				} else {
					this.meType = RawRequestType.BAD;
					//this.meTarget = RawRequestTarget.NONE;
				}
				break;
		}
		this.setPayloadData(tmpArr[2]);
	}


	// Privately set the payload data from the request string
	private void setPayloadData(String reqString) {
		if (reqString.contains("boundary=")) { // To extract according the boundary
			String tmpStr = reqString.substring(reqString.indexOf("boundary=") + 9); // Extract partially
			String tmpBnd = "\r\n--" + tmpStr.substring(0, tmpStr.indexOf("\r\n")); // Get the boundary value
			tmpStr = tmpStr.substring(tmpStr.indexOf(tmpBnd)  + tmpBnd.length(), tmpStr.lastIndexOf(tmpBnd)); // Extract between bounds
			tmpStr = tmpStr.substring(tmpStr.indexOf("\r\n\r\n") + 4); // Final extract
			this.mePayload = tmpStr;
		} else { // Then it's POST (small texts)
			//this.mePayload = reqString.substring(reqString.indexOf("\r\n\r\n") + 4).replaceAll("(?<!\r)\n", "\r\n");//Replace LF with CRLF for Windows readability
			this.mePayload = reqString.substring(reqString.indexOf("\r\n\r\n") + 4);
		}
	}


	/**
	 * Returns the type of this raw request.
	 * @return	the type of this raw request
	 */
	public RawRequestType getType() {
		return meType;
	}

	/**
	 * Returns the target of this raw request. The target is either a page, user, goal, chat, or SHA-1 data
	 * @return	the target of this raw request
	 */
	public RawRequestTarget getTarget() {
		return meTarget;
	}

	/**
	 * Returns the requester's user-name.
	 * @return	the requester's user-name
	 */
	public String getUserName() {
		return meUserName;
	}

	/**
	 * Returns the requester's password in SHA-1 format.
	 * @return	the requester's SHA-1 password
	 */
	public String getSha1Pass() {
		return meSha1Pass;
	}

	/**
	 * Returns the payload data (if any) that this request contains.
	 * @return	the request's payload data
	 */
	public String getPayloadData() {
		return mePayload;
	}
}
