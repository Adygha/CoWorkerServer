/**
 * 
 */
package model;

import java.io.StringWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import model.User.UserType;

/**
 * @author Janty Azmat
 *
 */
@XmlRootElement(name = "Page")
@XmlAccessorType(XmlAccessType.NONE)
class XmlBuilder {
	// Constants
	private static final String me_TXTBOX_EMAIL_ID = "txtEmail";
	private static final String me_TXTBOX_PASS_ID = "txtPass";
	private static final String me_TXTBOX_NAME_ID = "txtName";
	private static final String me_DROPBOX_USERTYPE_ID = "txtUserType";
	private static final String me_DROPBOX_OFFICE_ID = "txtOffice";
	private static final String me_TXTBOX_GOALNAME_ID = "txtGoalName";
	private static final String me_TXTAREA_GOALDESC_ID = "txtGoalDesc";
	private static final String me_DROPBOX_GOALGROUP_ID = "txtGoalGrp";
	private static final String me_TXTBOX_GOALPERC_ID = "txtGoalPerc";

	@XmlAccessorType(XmlAccessType.NONE)
	private static abstract class PageElement {
		// Fields
		@XmlElement(name="Title")
		private String meTitle;

		// Constructor
		public PageElement(String theTitle) {
			this.meTitle = theTitle;
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class Link extends PageElement {
		// Fields
		@XmlElement(name="Command")
		private String meCommand;

		// Constructor
		public Link(String theTitle, String theCommand) {
			super(theTitle);
			this.meCommand = theCommand;
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class Button extends PageElement {
		// Fields
		@XmlElement(name="Command")
		private String meCommand;
		@XmlElement(name="CommandParam")
		private String meCommParam;

		// Constructor
		public Button(String theTitle, String theCommand, String commandParam) {
			super(theTitle);
			this.meCommand = theCommand;
			this.meCommParam = commandParam;
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class XmlGoal extends PageElement {
		// Fields
		@XmlElement(name="Percentage")
		private int mePer;
		@XmlElement(name="Command")
		private String meCommand;
//		@XmlElement(name="Description")
//		private String meDecr;
		@XmlElement(name="IsPage")
		private boolean meIsPage;
//		@XmlElement(name="GroupUUID")
//		private String meGroupUUID;

		// Constructor as main page goal
		public XmlGoal(String goalName, int goalPercentage,  String theCommand) {
			super(goalName);
			//this.meIsPage = false;
			this.mePer = goalPercentage;
			this.meCommand = theCommand;
		}

		// Constructor for edit goal page percentage
		public XmlGoal(String goalName, int goalPercentage) {
			super(goalName);
			this.meIsPage = true;
			this.mePer = goalPercentage;
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class XmlUser extends PageElement {
		// Fields
		@XmlElement(name="Email")
		private String meEmail;

		public XmlUser(String eMail, String userName) {
			super(userName);
			this.meEmail = eMail;
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class Group extends PageElement {
		// Fields
		@XmlElements({
			@XmlElement(name="LabledTextBox", type=LabledTextBox.class),
			@XmlElement(name="LabledTextArea", type=LabledTextArea.class),
			@XmlElement(name="LabledTextValue", type=LabledTextValue.class),
			@XmlElement(name="DropBox", type=LabledDropBox.class),
			@XmlElement(name="Group", type=Group.class),
			@XmlElement(name="SideMenu", type=SideMenu.class),
			@XmlElement(name="Button", type=Button.class),
			@XmlElement(name="Link", type=Link.class),
			@XmlElement(name="Link", type=Link.class),
			@XmlElement(name="XmlGoal", type=XmlGoal.class),
			@XmlElement(name="XmlUser", type=XmlUser.class)
		})
		private List<PageElement> meElems;

		// Constructor
		public Group(String theTitle) {
			super(theTitle);
			this.meElems = new LinkedList<PageElement>();
		}

		// Constructor for goal-group
		public Group(GoalGroup goalGroup) {
			super(goalGroup.getName());
			this.meElems = new LinkedList<PageElement>();
			goalGroup.getGoals().stream().forEach(g -> this.meElems.add(new XmlGoal(g.getName(), g.getPercentage(), g.getUUID())));
		}

		// Constructor for user-group
		public Group(List<User> userList) {
			super("All Users:");
			this.meElems = new LinkedList<PageElement>();
			userList.stream().forEach(u -> this.meElems.add(new XmlUser(u.getEmail(), u.getFullName())));
		}

		/**
		 * Adds an element to this group.
		 * @param	the new element to add
		 */
		public void addElement(PageElement theElement) {
			this.meElems.add(theElement);
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class LabledTextBox extends PageElement {

		private static enum TextBoxType {
			TEXT,
			EMAIL,
			PASSWORD
		}

		// Fields
		@XmlElement(name="ID")
		private String meID;
		@XmlElement(name="TextBoxType")
		private TextBoxType meBoxType;
		@XmlElement(name="CurrentText")
		private String meCurTxt;

		// Constructor
		public LabledTextBox(String theTitle, TextBoxType boxType, String boxID, String currentText) {
			super(theTitle);
			this.meBoxType = boxType;
			this.meID = boxID;
			this.meCurTxt = currentText;
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class LabledTextArea extends PageElement {
		// Fields
		@XmlElement(name="ID")
		private String meID;
		@XmlElement(name="CurrentText")
		private String meCurTxt;

		public LabledTextArea(String theTitle, String theID, String currentText) {
			super(theTitle);
			this.meID = theID;
			this.meCurTxt = currentText;
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class LabledTextValue extends PageElement {
		// Fields
		@XmlElement(name="TextValueType")
		private String meTxt;

		// Constructor
		public LabledTextValue(String theTitle, String theText) {
			super(theTitle);
			this.meTxt = theText;
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class LabledDropBox extends PageElement {
		// Fields
		@XmlElement(name="DropValues")
		private List<XmlEntry> meVals;
		@XmlElement(name="SelectedValue")
		private String meVal;
		@XmlElement(name="ID")
		private String meID;
		@XmlElement(name="IsEditable")
		private boolean meIsEditable;

		// Constructor
		public LabledDropBox(String theTitle, List<SimpleEntry<String, String>> dropValues, String selectedValue, String boxID, boolean isEditable) {
			super(theTitle);
			this.meVals = new LinkedList<XmlEntry>();
			dropValues.stream().forEach(se -> this.meVals.add(new XmlEntry(se)));
			this.meVal = selectedValue;
			this.meID = boxID;
			this.meIsEditable = isEditable;
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class SideMenu extends PageElement {
		// Fields
		@XmlElement(name="Link")
		private LinkedList<Link> meLinks;

		// Constructor
		public SideMenu() {
			super("SideMenu");
			this.meLinks = new LinkedList<Link>();
			this.meLinks.push(new Link("Logout", "GET_LOGIN"));
		}

		/**
		 * Adds a link to this SideMenu.
		 * @param	the new link to add
		 */
		public void addLink(Link theLink) {
			this.meLinks.addFirst(theLink);
		}
	}

	// An entry class for XML purposes
	@XmlAccessorType(XmlAccessType.NONE)
	private static class XmlEntry {
		// Fields
		@XmlElement(name="Key")
		private String meKey;
		@XmlElement(name="Value")
		private String meValue;

		public XmlEntry(SimpleEntry<String, String> theEntry) {
			this.meKey = theEntry.getKey();
			this.meValue = theEntry.getValue();
		}
	}


	// Fields
	@XmlElements({
		@XmlElement(name="LabledTextBox", type=LabledTextBox.class),
		@XmlElement(name="LabledTextArea", type=LabledTextArea.class),
		@XmlElement(name="LabledTextValue", type=LabledTextValue.class),
		@XmlElement(name="DropBox", type=LabledDropBox.class),
		@XmlElement(name="Group", type=Group.class),
		@XmlElement(name="SideMenu", type=SideMenu.class),
		@XmlElement(name="Button", type=Button.class),
		@XmlElement(name="Link", type=Link.class),
		@XmlElement(name="Link", type=Link.class),
		@XmlElement(name="XmlGoal", type=XmlGoal.class),
		@XmlElement(name="XmlUser", type=XmlUser.class)
	})
	private List<PageElement> mePage;
	private Marshaller meMar;

	// Constructor
	public XmlBuilder() throws JAXBException {
		this.mePage = new LinkedList<PageElement>();
		this.meMar = JAXBContext.newInstance(XmlBuilder.class).createMarshaller();
		this.meMar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	}


	public String buildMainPage(List<GoalGroup> theGoalGroups, List<User> theUsers, List<SimpleEntry<String, String>> theOffices, UserType userType) throws JAXBException {
		SideMenu tmpMenu = new SideMenu();
		this.mePage.add(tmpMenu);
		Group tmpGrp;
		switch (userType) {
			case SUPER:
				tmpGrp = new Group("Change Password:");
				tmpGrp.addElement(new LabledTextBox("New Password", LabledTextBox.TextBoxType.PASSWORD, me_TXTBOX_PASS_ID, ""));
				tmpGrp.addElement(new LabledTextBox("Confirm Password", LabledTextBox.TextBoxType.PASSWORD, me_TXTBOX_PASS_ID + "Conf", ""));
				tmpGrp.addElement(new Button("Change Password", "GET_SHA1", null));
				Group tmpOffGrp = new Group("Manage Offices");
				Group tmpUsrGrp = new Group(theUsers);
				tmpOffGrp.addElement(new LabledDropBox("Offices", theOffices, theOffices.get(0).getKey(), me_DROPBOX_OFFICE_ID, true));
				tmpOffGrp.addElement(new Button("Add New Office", "CREATE_OFFICE", null));
				tmpOffGrp.addElement(new Button("Update Office", "UPDATE_OFFICE", null));
				tmpOffGrp.addElement(new Button("Delete Office", "DELETE_OFFICE", null));
				this.mePage.add(tmpGrp);
				this.mePage.add(tmpOffGrp);
				this.mePage.add(tmpUsrGrp);
				break;
			case ADMIN:
				tmpMenu.addLink(new Link("Create Goal", "GET_GOALEDIT"));
				tmpMenu.addLink(new Link("Goal Groups", "GET_GROUPOFFICE"));
			case COWORKER:
				tmpMenu.addLink(new Link("Account Settings", "GET_USER"));
				tmpMenu.addLink(new Link("Chat", "GET_CHAT"));
				theGoalGroups.stream().forEach(gr -> this.mePage.add(new Group(gr)));
				break;
			case NEW:
				tmpGrp = new Group("Welcome To Your New Account");
				tmpGrp.addElement(new LabledTextValue("Account is not yet active.", "Please check your email to activate your account."));
				this.mePage.add(tmpGrp);
		}
		return this.createXml();
	}

	public String buildLoginPage() throws JAXBException {
		Group tmpGrp = new Group("Valid Credentials Needed");
		tmpGrp.addElement(new LabledTextBox("Login Email", LabledTextBox.TextBoxType.EMAIL, XmlBuilder.me_TXTBOX_EMAIL_ID, ""));
		tmpGrp.addElement(new LabledTextBox("Login Password", LabledTextBox.TextBoxType.PASSWORD, XmlBuilder.me_TXTBOX_PASS_ID, ""));
		tmpGrp.addElement(new Button("Login", "GET_SHA1", null));
		tmpGrp.addElement(new Button("Register A New Account", "GET_USER", null));
		this.mePage.add(tmpGrp);
		return this.createXml();
	}

	public String buildRegisterPage(List<SimpleEntry<String, String>> theOffices) throws JAXBException {
		Group tmpGrp = new Group("Please Fill Your Account Information");
		tmpGrp.addElement(new LabledTextBox("Account Email", LabledTextBox.TextBoxType.EMAIL, me_TXTBOX_EMAIL_ID, ""));
		tmpGrp.addElement(new LabledTextBox("Account Password", LabledTextBox.TextBoxType.PASSWORD, me_TXTBOX_PASS_ID, ""));
		tmpGrp.addElement(new LabledTextBox("Nickname Or Full Name", LabledTextBox.TextBoxType.TEXT, me_TXTBOX_NAME_ID, ""));
		tmpGrp.addElement(new LabledDropBox("Choose Office", theOffices, null, me_DROPBOX_OFFICE_ID, false));
		tmpGrp.addElement(new Button("Register New Account", "CREATE_USER", null));
		tmpGrp.addElement(new Button("Go To Login Page", "GET_LOGIN", null));
		this.mePage.add(tmpGrp);
		return this.createXml();
	}

	public String buildChatPage() throws JAXBException {
		SideMenu tmpMenu = new SideMenu();
		tmpMenu.addLink(new Link("Main Page", "GET_MAIN"));
		this.mePage.add(tmpMenu);
		// TODO: Add other page parts
		return this.createXml();
	}

	public String buildGoalPage(Goal theGoal, String groupName, boolean isAdmin) throws JAXBException {
		SideMenu tmpMenu = new SideMenu();
		Group tmpGrp = new Group("Goal Page");
		tmpMenu.addLink(new Link("Chat", "GET_CHAT"));
		tmpMenu.addLink(new Link("Main Page", "GET_MAIN"));
		this.mePage.add(tmpMenu);
		tmpGrp.addElement(new Button("Back To Main Page", "GET_MAIN", null));
		if (isAdmin) {
			tmpGrp.addElement(new Button("Edit Goal", "GET_GOALEDIT", theGoal.getUUID()));
			tmpGrp.addElement(new Button("Delete Goal", "DELETE_GOAL", theGoal.getUUID()));
		}
		tmpGrp.addElement(new LabledTextValue("Goal Name:", theGoal.getName()));
		tmpGrp.addElement(new LabledTextValue("Goal Description:", theGoal.getDescription()));
		tmpGrp.addElement(new LabledTextValue("Goal Group:", groupName));
		tmpGrp.addElement(new XmlGoal(theGoal.getName(), theGoal.getPercentage()));
		this.mePage.add(tmpGrp);
		return this.createXml();
	}

	public String buildGoalEditPage(Goal theGoal, List<SimpleEntry<String, String>> goalGroups) throws JAXBException {
		SideMenu tmpMenu = new SideMenu();
		Group tmpGrp = new Group("Goal Edit Page");
		tmpMenu.addLink(new Link("Chat", "GET_CHAT"));
		tmpMenu.addLink(new Link("Main Page", "GET_MAIN"));
		this.mePage.add(tmpMenu);
		tmpGrp.addElement(new Button("Cancel Editing Goal", theGoal == null ? "GET_MAIN" : "GET_GOAL", theGoal == null ? "" : theGoal.getUUID()));
		tmpGrp.addElement(new LabledTextBox("Goal Name:", LabledTextBox.TextBoxType.TEXT, me_TXTBOX_GOALNAME_ID,
											theGoal == null ? "" : theGoal.getName()));
		tmpGrp.addElement(new LabledTextArea("Goal Description:", me_TXTAREA_GOALDESC_ID, theGoal == null ? "" : theGoal.getDescription()));
		tmpGrp.addElement(new LabledDropBox("Goal Group:", goalGroups, theGoal == null ? "" : theGoal.getGroupUUID(), me_DROPBOX_GOALGROUP_ID, false));
		tmpGrp.addElement(new LabledTextBox("Goal Percentage:", LabledTextBox.TextBoxType.TEXT, me_TXTBOX_GOALPERC_ID, theGoal == null ? "0" : 
											String.valueOf(theGoal.getPercentage())));
		tmpGrp.addElement(new Button(theGoal == null ? "Create Goal" : "Save Goal Data",
							theGoal == null ? "CREATE_GOAL" : "UPDATE_GOAL", theGoal == null ? null : theGoal.getUUID()));
		this.mePage.add(tmpGrp);
		return this.createXml();
	}

	public String buildUserEditPage(
			User theUser, List<SimpleEntry<String, String>> theOffices, List<String> userTypes, boolean isSuper) throws JAXBException {
		SideMenu tmpMenu = new SideMenu();
		Group tmpGrp = new Group("User Edit Page");
		tmpMenu.addLink(new Link("Main Page", "GET_MAIN"));
		tmpGrp.addElement(new Button("Cancel Edit User Data", "GET_MAIN", null));
		if (isSuper)
			tmpGrp.addElement(new Button("Delete User", "DELETE_USER", theUser.getUUID()));
		tmpGrp.addElement(new LabledTextValue("Account Email", theUser.getEmail()));
		tmpGrp.addElement(new LabledTextBox("Account Password", LabledTextBox.TextBoxType.PASSWORD, me_TXTBOX_PASS_ID, ""));
		tmpGrp.addElement(new LabledTextBox("Nickname Or Full Name", LabledTextBox.TextBoxType.TEXT, me_TXTBOX_NAME_ID, theUser.getFullName()));
		if (isSuper) {
			List<SimpleEntry<String, String>> tmpEntrys = new LinkedList<SimpleEntry<String, String>>();
			userTypes.stream().forEach(str -> tmpEntrys.add(new SimpleEntry<String, String>(str, str)));
			tmpGrp.addElement(new LabledDropBox("Choose User Type", tmpEntrys, theUser.getUserType().toString(), me_DROPBOX_USERTYPE_ID, false));
		} else {
			tmpMenu.addLink(new Link("Chat", "GET_CHAT")); // Maybe no deed
		}
		tmpGrp.addElement(new LabledDropBox("Choose Office", theOffices, theUser.getOfficeUUID(), me_DROPBOX_OFFICE_ID, false));
		tmpGrp.addElement(new Button("Save User Data", "UPDATE_USER", theUser.getEmail()));
		this.mePage.add(tmpMenu);
		this.mePage.add(tmpGrp);
		return this.createXml();
	}

	public String buildGroupPage(List<SimpleEntry<String, String>> goalGroups) throws JAXBException {
		SideMenu tmpMenu = new SideMenu();
		Group tmpGrp = new Group("Managing Goal Groups And Offices Page");
		Group tmpGoalGrp = new Group("Manage Goal Groups");
		tmpMenu.addLink(new Link("Main Page", "GET_MAIN"));
		tmpGrp.addElement(new Button("Cancel And Go To Main Page", "GET_MAIN", null));
		tmpGoalGrp.addElement(new LabledDropBox("Goal Groups", goalGroups, goalGroups.get(0).getKey(), me_DROPBOX_GOALGROUP_ID, true));
		tmpGoalGrp.addElement(new Button("Add New Goal Group", "CREATE_GROUP", null));
		tmpGoalGrp.addElement(new Button("Update Goal Group", "UPDATE_GROUP", null));
		tmpGoalGrp.addElement(new Button("Delete Goal Group", "DELETE_GROUP", null));
		tmpGrp.addElement(tmpGoalGrp);;
		this.mePage.add(tmpMenu);
		this.mePage.add(tmpGrp);
		return this.createXml();
	}

	// For the messages without login
	public String buildInfoPage(String theTitle, String theInfo) throws JAXBException {
		Group tmpGrp = new Group("Information Page");
		tmpGrp.addElement(new LabledTextValue(theTitle, theInfo));
		tmpGrp.addElement(new Button("Go To Login Page", "GET_LOGIN", null));
		this.mePage.add(tmpGrp);
		return this.createXml();
	}

	public String buildSingleElement(String elementName, String elementData) throws JAXBException {
		StringWriter outXml = new StringWriter();
		JAXBElement<String> tmpRoot = new JAXBElement<String>(new QName(elementName), String.class, elementData);
		this.meMar.marshal(tmpRoot, outXml);
		return outXml.toString();
	}

	// A private method to get the XML representation of the created page
	private String createXml() throws JAXBException {
	//private String createXml(String rootName) throws JAXBException {
		StringWriter outXml = new StringWriter();
		//JAXBElement<XmlBuilder> tmpRoot = new JAXBElement<XmlBuilder>(new QName(rootName), XmlBuilder.class, this);
		//this.meMar.marshal(tmpRoot, outXml);
		this.meMar.marshal(this, outXml);
		this.mePage.clear();
		return outXml.toString();
	}
}
