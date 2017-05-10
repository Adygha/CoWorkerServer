/**
 * 
 */
package model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.xml.bind.JAXBException;

import model.data_access.IDao;

/**
 * A class that represents a raw response.
 * @author Janty Azmat
 */
public class RawResponse {
	// Constants
	private static final String me_WARN_ELEM = "WARNING";
	private static final String me_SHA1_ELEM = "SHA1";
	// Fields
	private String meData;
	private IDao meDao;
	private IModelObserver meObserver;
	private XmlBuilder meXmlBld;
	private boolean meIsCredOk;

	public RawResponse(RawRequest theRequest, IDao theDao, IModelObserver theObserver) {
		this.meDao = theDao;
		this.meObserver = theObserver;
		try {
			this.meXmlBld = new XmlBuilder();
			if (this.meObserver.requestCheckPaused()) { // Check if server is in maintenance (paused) mode
				this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Server is under maintenance. Please try again later.");
			} else {
				this.meIsCredOk = this.meDao.checkCredentials(theRequest.getUserName(), theRequest.getSha1Pass());
				switch (theRequest.getType()) {
					case GET:
						switch (theRequest.getTarget()) {
							case LOGIN:
								this.meData = this.meXmlBld.buildLoginPage();
								break;
							case MAIN:
								if (this.meIsCredOk) {
									this.meData = this.meXmlBld.buildMainPage(this.meDao.getAllGoalGroups());
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Credentials are not correct. Access denied.");
								}
								break;
							case CHAT:
								if (this.meIsCredOk) {
									this.meData = this.meXmlBld.buildChatPage();
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Credentials are not correct. Access denied.");
								}
								break;
							case GOAL:
								if (this.meIsCredOk) {
									this.meData = this.meXmlBld.buildGoalPage();
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Credentials are not correct. Access denied.");
								}
								break;
							case GOALEDIT:
								break;
							case USER:
								break;
							case REGISTER:
								break;
							case SHA1:
								try {
									this.meData = this.meXmlBld.buildSingleElement(
											me_SHA1_ELEM, new BigInteger(1, MessageDigest.getInstance("SHA-1").digest(
													theRequest.getPayloadData().getBytes())).toString(16).toUpperCase());
								} catch (NoSuchAlgorithmException e) {/* Safe to ignore */}
								break;
						}
						break;
					case CREATE:
						switch (theRequest.getTarget()) {
							case CHAT:
								break;
							case GOAL:
								break;
							default: //case USER:// Only 'USER' will be in this case (the others are filtered in the request)
								String[] tmpArr = theRequest.getPayloadData().split("\n", 4);
							try {
								this.meDao.addUser(new User(tmpArr[0], tmpArr[1], tmpArr[2], tmpArr[3]));
								this.meData = this.meXmlBld.buildLoginPage();
							} catch (SQLException e) {
								if (e.getErrorCode() == 30000 && e.getSQLState().equals("23505")) {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
											"Cannot add user. Email already exist. Please try again.");
								} else if (e.getErrorCode() == 30000 && e.getSQLState().equals("23503")) {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
											"Cannot add user. The provided office does not exist. Please try again.");
								} else {
									this.meObserver.requestPrintErrWarn("Error adding user to database", false);
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
											"Error adding user. Please contact support.");
								}
							}
						}
						break;
					case UPDATE:
						switch (theRequest.getTarget()) {
							case CHAT:
								break;
							case GOAL:
								break;
							case USER:
							default: //case USER:// Only 'USER' will be in this case (the others are filtered in the request)
						}
						break;
					case DELETE:
						switch (theRequest.getTarget()) {
							case CHAT:
								break;
							case GOAL:
								break;
							case USER:
							default: //case USER:// Only 'USER' will be in this case (the others are filtered in the request)
						}
						break;
					case BAD:
						this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Request is invalid. Please check support.");
				}
			}
		} catch (JAXBException e) {
			this.meObserver.requestPrintErrWarn("Error while creating the XML builder, or generating XML data.", true);
		}
	}

	public String getRawResponseData() {
		return this.meData;
	}
}
