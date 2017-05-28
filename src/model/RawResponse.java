/**
 * 
 */
package model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBException;
import model.User.UserType;
import model.data_access.IDao;

/**
 * A class that represents a raw response.
 * @author Janty Azmat
 */
public class RawResponse {
	// Constants
	private static final String me_WARN_ELEM = "WARNING";
	private static final String me_SHA1_ELEM = "SHA1";
	private static final String me_SMTP_SERVER = "mail.gmx.com";
	private static final String me_SMTP_PORT = "465";
	private static final String me_SUPER_EMAIL = "coworkers@gmx.com";
	private static final String me_SUPER_PASS = "qazqazqaz";
	private static final String me_SITE_LINK = "https://lans.ml:9999/";
	// Fields
	private String meData;
	private IDao meDao;
	private IModelObserver meObserver;
	private XmlBuilder meXmlBld;
	private boolean meIsCredOk;

	// Constructor
	public RawResponse(RawRequest theRequest, IDao theDao, IModelObserver theObserver) {
		this.meDao = theDao;
		this.meObserver = theObserver;
		User tmpUser = null;
		try {
			this.meXmlBld = new XmlBuilder();
			if (this.meObserver.requestCheckPaused()) { // Check if server is in maintenance (paused) mode
				this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Server is under maintenance. Please try again later.");
			} else {
				try {
					this.meIsCredOk = this.meDao.checkCredentials(theRequest.getUserEmail(), theRequest.getSha1Pass());
					if (this.meIsCredOk)
						tmpUser = this.meDao.getUser(theRequest.getUserEmail(), true);
				} catch (SQLException e) {
					this.meObserver.requestPrintErrWarn("Error while checking credentials." , false);
					this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Error connecting to database. Please try again later.");
					return;
				}
				switch (theRequest.getType()) {
					case GET:
						switch (theRequest.getTarget()) {
							case LOGIN:
								this.meData = this.meXmlBld.buildLoginPage();
								break;
							case MAIN:
								if (this.meIsCredOk) {
									try {
										switch (tmpUser.getUserType()) {
											case SUPER:
												this.meData = this.meXmlBld.buildMainPage(null, this.meDao.getUser(),
																						this.meDao.getOffice(), tmpUser.getUserType());
												break;
											case ADMIN:
											case COWORKER:
												this.meData = this.meXmlBld.buildMainPage(
														this.meDao.getGoalGroup(true), null, null, tmpUser.getUserType());
												break;
											case NEW:
												this.meData = this.meXmlBld.buildMainPage(null, null, null, tmpUser.getUserType());
										}
									} catch (SQLException e) {
										this.meObserver.requestPrintErrWarn("Error getting main page data from database.", false);
										this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
																					"Cannot connect to databese. Please try again later.");
									}
								} else {
									//this.meData = this.meXmlBld.buildLoginPage();
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
									try {
										Goal tmpGoal = this.meDao.getGoal(theRequest.getPayloadData());
										this.meData = this.meXmlBld.buildGoalPage(tmpGoal, this.meDao.getGoalGroup(
												tmpGoal.getGroupUUID()).getName(), tmpUser.getUserType() == UserType.ADMIN);
									} catch (SQLException e) {
										this.meObserver.requestPrintErrWarn("Error getting goal data from database.", false);
										this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
																					"Cannot get goal data from databese. Please try again later.");
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Credentials are not correct. Access denied.");
								}
								break;
							case GOALEDIT:
								if (this.meIsCredOk && tmpUser.getUserType() == UserType.ADMIN) {
									List<SimpleEntry<String, String>> tmpList = new LinkedList<SimpleEntry<String, String>>();
									try {
										this.meDao.getGoalGroup(false).stream().forEach(
																	g -> tmpList.add(new SimpleEntry<String, String>(g.getUUID(), g.getName())));
										this.meData = this.meXmlBld.buildGoalEditPage(theRequest.getPayloadData().equals("null") ?
												null : this.meDao.getGoal(theRequest.getPayloadData()), tmpList);
									} catch (SQLException e) {
										this.meObserver.requestPrintErrWarn("Error getting goals groups from database.", false);
										this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
																					"Cannot get goal data from database. Please try again later.");
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Only admins can add and edit goals.");
								}
								break;
							case USER:
								try {
									if (this.meIsCredOk) {
										switch (tmpUser.getUserType()) {
											case SUPER:
												List<String> tmpList = new LinkedList<String>();
												for (UserType ut : UserType.values()) {
													if (ut != UserType.SUPER)
														tmpList.add(ut.toString());
												}
												this.meData = this.meXmlBld.buildUserEditPage(this.meDao.getUser(theRequest.getPayloadData(),
																	true), this.meDao.getOffice(), tmpList, true);
												break;
											case ADMIN:
											case COWORKER:
												this.meData = this.meXmlBld.buildUserEditPage(tmpUser, this.meDao.getOffice(), null, false);
												break;
											case NEW:
												this.meData = this.meXmlBld.buildMainPage(null, null, null, tmpUser.getUserType());
										}
									} else {
										this.meData = this.meXmlBld.buildRegisterPage(this.meDao.getOffice());
									}
								} catch (SQLException e) {
									this.meObserver.requestPrintErrWarn("Error getting user data from database.", false);
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
																	"Cannot get user data from database. Please try again later.");
								}
								break;
							case GROUP:
								if (this.meIsCredOk && tmpUser.getUserType() == UserType.ADMIN) {
									List<SimpleEntry<String, String>> tmpList = new LinkedList<SimpleEntry<String, String>>();
									try {
									this.meDao.getGoalGroup(false).stream().forEach(
																	g -> tmpList.add(new SimpleEntry<String, String>(g.getUUID(), g.getName())));
									this.meData = this.meXmlBld.buildGroupPage(tmpList);
									} catch (SQLException e) {
										this.meObserver.requestPrintErrWarn("Error getting goals groups and offices from database.", false);
										this.meData = this.meXmlBld.buildSingleElement(
												me_WARN_ELEM, "Cannot get goal group and office data from database. Please try again later.");
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Only admins can manage goal-groups and offices.");
								}
								break;
							default: //case SHA1:
								this.meData = this.meXmlBld.buildSingleElement(me_SHA1_ELEM, this.createSha1Hash(theRequest.getPayloadData()));
								//break;
						}
						break;
					case CREATE:
						switch (theRequest.getTarget()) {
							case CHAT:
								this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Not Yet Implemented."); // TODO:
								break;
							case GOAL:
								if (this.meIsCredOk && tmpUser.getUserType() == UserType.ADMIN) {
									String[] tmpArr = theRequest.getPayloadData().split("\n", 4);
									try {
										this.meDao.addGoal(new Goal(tmpArr[0], tmpArr[3], Integer.parseInt(tmpArr[1]), tmpArr[2]));
										this.meData = this.meXmlBld.buildMainPage(this.meDao.getGoalGroup(true), null, null, tmpUser.getUserType());	
									} catch (NumberFormatException e) {
										this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "The percentage should be an integer [0 to 100].");
									} catch (SQLException e) {
										this.meObserver.requestPrintErrWarn("Error creating new goal in database.", false);
										this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
																					"Cannot create new goal in database. Please try again later.");
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Only admins can create goals.");
								}
								break;
							case OFFICE:
								if (this.meIsCredOk && tmpUser.getUserType() == UserType.SUPER) {
									try {
										this.meDao.addOffice(
												new SimpleEntry<String, String>(UUID.randomUUID().toString(), theRequest.getPayloadData()));
										this.meData = this.meXmlBld.buildMainPage(
												null, this.meDao.getUser(), this.meDao.getOffice(), tmpUser.getUserType());
									} catch (SQLException e) {
										if (e.getErrorCode() == 30000 && e.getSQLState().equals("23505")) {
											this.meData = this.meXmlBld.buildSingleElement(
													me_WARN_ELEM, "An office with the same name already exists. Please try another.");
										} else {
											this.meObserver.requestPrintErrWarn("Error creating new office in database.", false);
											this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
													"Cannot create new office in database. Please try again later.");
										}
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Only the Super can create offices.");
								}
								break;
							case GROUP:
								if (this.meIsCredOk && tmpUser.getUserType() == UserType.ADMIN) {
									List<SimpleEntry<String, String>> tmpList = new LinkedList<SimpleEntry<String, String>>();
									try {
										this.meDao.addGoalGroup(new GoalGroup(UUID.randomUUID().toString(), theRequest.getPayloadData()));
										this.meDao.getGoalGroup(false).stream().forEach(
												g -> tmpList.add(new SimpleEntry<String, String>(g.getUUID(), g.getName())));
										this.meData = this.meXmlBld.buildGroupPage(tmpList);
									} catch (SQLException e) {
										if (e.getErrorCode() == 30000 && e.getSQLState().equals("23505")) {
											this.meData = this.meXmlBld.buildSingleElement(
													me_WARN_ELEM, "A goal group with the same name already exists. Please try another.");
										} else {
											this.meObserver.requestPrintErrWarn("Error creating new goal group in database.", false);
											this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
													"Cannot create new goal group in database. Please try again later.");
										}
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Only admins can create goal groups.");
								}
								break;
							default: //case USER:// Only 'USER' will be in this case (the others are filtered in the request)
								String[] tmpArr = theRequest.getPayloadData().split("\n", 4);
								User tmpDupUser = new User(tmpArr[0], this.createSha1Hash(tmpArr[1]), tmpArr[2], tmpArr[3]);
								try {
									this.meDao.addUser(tmpDupUser);
									this.sendEmail(tmpArr[0], "Welcome from CoWorkers Server",
											"Welcome to your new account.\nPlease use this URL to activate your account:\n\n"
											+ me_SITE_LINK + tmpDupUser.getUUID());
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
											"Account created successfully. Please check your email to activate your account.");
								} catch (SQLException e) {
									if (e.getErrorCode() == 30000 && e.getSQLState().equals("23505")) { // The account already exists case
										try {
											tmpDupUser = this.meDao.getUser(tmpArr[0], true);
										} catch (SQLException ex) {/* Safe to ignore */}
										if (tmpDupUser != null && tmpDupUser.getUserType() == UserType.NEW) {
											try {
												this.sendEmail(tmpArr[0], "Welcome from CoWorkers Server",
														"Welcome to your new account\nPlease use this URL to activate your account:\n\n"
														+ me_SITE_LINK + tmpDupUser.getUUID());
												this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Email already exist. If you "
														+ "already created the account, check your email.");
											} catch (MessagingException e1) {
												this.meObserver.requestPrintErrWarn("Error sending email.", false);
												this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
																					"Cannot send activation email. Please try again later.");
											}
										} else {
											this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Email already exist. Try another.");
										}
									} else if (e.getErrorCode() == 30000 && e.getSQLState().equals("23503")) {
										this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
												"Cannot add user. The provided office does not exist. Please try again.");
									} else {
										this.meObserver.requestPrintErrWarn("Error adding user to database", false);
										this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
												"Error adding user. Please contact support.");
									}
								} catch (MessagingException e) {
									this.meObserver.requestPrintErrWarn("Error sending email.", false);
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
											"Cannot send activation email. Please try again later.");
								}
						}
						break;
					case UPDATE:
						switch (theRequest.getTarget()) {
							case CHAT:
								this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Not Yet Implemented."); // TODO:
								break;
							case GOAL:
								if (this.meIsCredOk && tmpUser.getUserType() == UserType.ADMIN) {
									String[] tmpArr = theRequest.getPayloadData().split("\n", 5);
									try {
										this.meDao.updateGoal(new Goal(tmpArr[0], tmpArr[1], tmpArr[4], Integer.parseInt(tmpArr[2]), tmpArr[3]));
										this.meData = this.meXmlBld.buildMainPage(this.meDao.getGoalGroup(true), null, null, tmpUser.getUserType());										
									} catch (NumberFormatException e) {
										this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "The percentage should be an integer [0 to 100].");
									} catch (SQLException e) {
										this.meObserver.requestPrintErrWarn("Error updating goal data to database.", false);
										this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
																					"Cannot update goal data to database. Please try again later.");
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Only admins can update goals.");
								}
								break;
							case OFFICE:
								if (this.meIsCredOk && tmpUser.getUserType() == UserType.SUPER) {
									String[] tmpArr = theRequest.getPayloadData().split("\n", 2);
									try {
										this.meDao.updateOffice(new SimpleEntry<String, String>(tmpArr[0], tmpArr[1]));
										this.meData = this.meXmlBld.buildMainPage(
												null, this.meDao.getUser(), this.meDao.getOffice(), tmpUser.getUserType());
									} catch (SQLException e) {
										if (e.getErrorCode() == 30000 && e.getSQLState().equals("23505")) {
											this.meData = this.meXmlBld.buildSingleElement(
													me_WARN_ELEM, "An office with the same name already exists. Please try another.");
										} else {
											this.meObserver.requestPrintErrWarn("Error updating office in database.", false);
											this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
													"Cannot update office in database. Please try again later.");
										}
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Only the Super can update offices.");
								}
								break;
							case GROUP:
								if (this.meIsCredOk && tmpUser.getUserType() == UserType.ADMIN) {
									String[] tmpArr = theRequest.getPayloadData().split("\n", 2);
									List<SimpleEntry<String, String>> tmpList = new LinkedList<SimpleEntry<String, String>>();
									try {
										this.meDao.updateGoalGroup(new GoalGroup(tmpArr[0], tmpArr[1]));
										this.meDao.getGoalGroup(false).stream().forEach(
												g -> tmpList.add(new SimpleEntry<String, String>(g.getUUID(), g.getName())));
										this.meData = this.meXmlBld.buildGroupPage(tmpList);
									} catch (SQLException e) {
										if (e.getErrorCode() == 30000 && e.getSQLState().equals("23505")) {
											this.meData = this.meXmlBld.buildSingleElement(
													me_WARN_ELEM, "A goal group with the same name already exists. Please try another.");
										} else {
											this.meObserver.requestPrintErrWarn("Error updating goal group in database.", false);
											this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
													"Cannot update goal group in database. Please try again later.");
										}
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Only admins can update goal groups.");
								}
								break;
							default: //case USER:// Only 'USER' will be in this case (the others are filtered in the request)
								try {
									if (this.meIsCredOk) {
										if (tmpUser.getUserType() == UserType.SUPER) {
											String[] tmpArr = theRequest.getPayloadData().split("\n", 5);
											if (tmpArr.length == 1) { // Just change self password case
												this.meDao.updateUser(new User(tmpUser.getUUID(), tmpUser.getEmail(), tmpArr[0],
														tmpUser.getFullName(), tmpUser.getUserType(), tmpUser.getOfficeUUID()));
												//this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Password updated.");
												this.meData = this.meXmlBld.buildLoginPage();
											} else { // Update other user
												User tmpDupUser = this.meDao.getUser(tmpArr[0], true);
												this.meDao.updateUser(new User(tmpDupUser.getUUID(), tmpDupUser.getEmail(),
													tmpArr[1].equals("null") ? tmpDupUser.getSha1Passwrod() : this.createSha1Hash(tmpArr[1]),
															tmpArr[2], UserType.valueOf(tmpArr[3]), tmpArr[4]));
												this.meData = this.meXmlBld.buildMainPage(null, this.meDao.getUser(), this.meDao.getOffice(),
																							tmpUser.getUserType());
											}
										} else {
											String[] tmpArr = theRequest.getPayloadData().split("\n", 4);
											this.meDao.updateUser(new User(tmpUser.getUUID(), tmpUser.getEmail(),
													tmpArr[1].equals("null") ? tmpUser.getSha1Passwrod() : this.createSha1Hash(tmpArr[1]),
															tmpArr[2], tmpUser.getUserType(), tmpArr[3]));
											if (tmpArr[1].equals("null")) {
												this.meData = this.meXmlBld.buildMainPage(
														this.meDao.getGoalGroup(true), null, null, tmpUser.getUserType());
											} else {
												this.meData = this.meXmlBld.buildLoginPage();
											}
										}
									} else {
										User tmpDupUser = this.meDao.getUser(theRequest.getUserEmail(), false); // It actually contains UUID
										if (tmpDupUser.getUserType() == UserType.NEW) { // Only update if new
											this.meDao.updateUser(new User(tmpDupUser.getUUID(), tmpDupUser.getEmail(),
															tmpDupUser.getSha1Passwrod(), tmpDupUser.getFullName(),
															UserType.COWORKER, tmpDupUser.getOfficeUUID()));
											this.meData = this.meXmlBld.buildInfoPage("Thank you for activating your account.",
																					"You can now normally view the main page.");
										} else {
											this.meData = this.meXmlBld.buildInfoPage("User already activated.",
																					"You can now normally view the main page.");
										}
									}
								} catch (SQLException e) {
									this.meObserver.requestPrintErrWarn("Error updating user data in database.", false);
									this.meData = this.meXmlBld.buildSingleElement(
											me_WARN_ELEM, "Cannot update user data now. Please try again later.");
								}
						}
						break;
					case DELETE:
						switch (theRequest.getTarget()) {
							case CHAT:
								this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Not Yet Implemented."); // TODO:
								break;
							case GOAL:
								if (this.meIsCredOk && tmpUser.getUserType() == UserType.ADMIN) {
									try {
										this.meDao.deleteGoal(theRequest.getPayloadData());
										this.meData = this.meXmlBld.buildMainPage(this.meDao.getGoalGroup(true), null, null, tmpUser.getUserType());
									} catch (SQLException e) {
										this.meObserver.requestPrintErrWarn("Error while deleting goal from database.", false);
										this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Cannot delete goal now. Please try again later.");
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Only admins can delete goals.");
								}
								break;
							case OFFICE:
								if (this.meIsCredOk && tmpUser.getUserType() == UserType.SUPER) {
									try {
										this.meDao.deleteOffice(theRequest.getPayloadData());
										this.meData = this.meXmlBld.buildMainPage(
												null, this.meDao.getUser(), this.meDao.getOffice(), tmpUser.getUserType());
									} catch (SQLException e) {
										if (e.getErrorCode() == 30000 && e.getSQLState().equals("23503")) {
											this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
													"The office already contains co-workers/users. The office cannot be deleted.");
										} else {
											this.meObserver.requestPrintErrWarn("Error deleting office from database.", false);
											this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
													"Cannot delete office from database. Please try again later.");
										}
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Only the Super can delete offices.");
								}
								break;
							case GROUP:
								if (this.meIsCredOk && tmpUser.getUserType() == UserType.ADMIN) {
									List<SimpleEntry<String, String>> tmpList = new LinkedList<SimpleEntry<String, String>>();
									try {
										this.meDao.deleteGoalGroup(theRequest.getPayloadData());
										this.meDao.getGoalGroup(false).stream().forEach(
												g -> tmpList.add(new SimpleEntry<String, String>(g.getUUID(), g.getName())));
										this.meData = this.meXmlBld.buildGroupPage(tmpList);
									} catch (SQLException e) {
										if (e.getErrorCode() == 30000 && e.getSQLState().equals("23503")) {
											this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
													"The goal group already contains goals. The goal group cannot be deleted.");
										} else {
											this.meObserver.requestPrintErrWarn("Error deleting goal group from database.", false);
											this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM,
													"Cannot delete goal group from database. Please try again later.");
										}
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Only admins can delete goal groups.");
								}
								break;
							default: //case USER:// Only 'USER' will be in this case (the others are filtered in the request)
								if (this.meIsCredOk && tmpUser.getUserType() == UserType.SUPER) {
									try {
										this.meDao.deleteUser(theRequest.getPayloadData());
										this.meData = this.meXmlBld.buildMainPage(null, this.meDao.getUser(), this.meDao.getOffice(),
																					tmpUser.getUserType());
									} catch (SQLException e) {
										this.meObserver.requestPrintErrWarn("Error while deleting user from database.", false);
										this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Cannot delete user now. Please try again later.");
									}
								} else {
									this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Only the Super can delete users.");
								}
						}
						break;
					case BAD:
						this.meData = this.meXmlBld.buildSingleElement(me_WARN_ELEM, "Request is invalid. Please check support.");
				}
			}
		} catch (JAXBException e) {
			this.meObserver.requestPrintErrWarn("Error while creating the XML builder, or generating XML data.", true);
		}
	} // RawResponse constructor

	public String getRawResponseData() {
		return this.meData;
	}

	private void sendEmail(String eMail, String theSubject, String theText) throws MessagingException {
		Properties tmpProps = new Properties();
		tmpProps.put("mail.smtp.host", me_SMTP_SERVER);
		tmpProps.put("mail.smtp.socketFactory.port", me_SMTP_PORT);
		tmpProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		tmpProps.put("mail.smtp.auth", "true");
		tmpProps.put("mail.smtp.port", me_SMTP_PORT);
		Message tmpMsg = new MimeMessage(Session.getDefaultInstance(tmpProps));
		tmpMsg.setFrom(new InternetAddress(me_SUPER_EMAIL));
		tmpMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(eMail));
		tmpMsg.setSubject(theSubject);
		tmpMsg.setText(theText);
		Transport.send(tmpMsg, me_SUPER_EMAIL, me_SUPER_PASS);
	}

	private String createSha1Hash(String theData) {
		String outSha1 = null;
		try {
			outSha1 = new BigInteger(1, MessageDigest.getInstance("SHA-1").digest(theData.getBytes())).toString(16).toUpperCase();
		} catch (NoSuchAlgorithmException e) {/* Safe to ignore */}
		return outSha1;
	}
}
