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

/**
 * @author Janty Azmat
 *
 */
@XmlRootElement(name = "Page")
@XmlAccessorType(XmlAccessType.NONE)
public class XmlBuilder {
	// Constants
	private static final String me_EMAIL_TXTBOX_ID = "txtBoxEmail";
	private static final String me_PASS_TXTBOX_ID = "txtBoxPass";

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
	private static class Group extends PageElement {
		// Fields
		@XmlElements({
			@XmlElement(name="LabledTextBox", type=LabledTextBox.class),
			@XmlElement(name="LabledTextValue", type=LabledTextValue.class),
			@XmlElement(name="DropBox", type=DropBox.class),
			@XmlElement(name="Group", type=Group.class),
			@XmlElement(name="SideMenu", type=SideMenu.class),
			@XmlElement(name="Button", type=Button.class),
			@XmlElement(name="Link", type=Link.class),
			@XmlElement(name="Link", type=Link.class),
			@XmlElement(name="XmlGoal", type=XmlGoal.class)
		})
		private List<PageElement> meElems;

		// Constructor
		public Group(String theTitle) {
			super(theTitle);
			this.meElems = new LinkedList<PageElement>();
		}

		// Constructor
		public Group(GoalGroup theGoalGroup) {
			super(theGoalGroup.getName());
			this.meElems = new LinkedList<PageElement>();
			theGoalGroup.getGoals().stream().forEach(g -> this.meElems.add(new XmlGoal(g.getName(), g.getPercentage(), g.getUUID())));
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

		// Constructor
		public LabledTextBox(String theTitle, TextBoxType boxType, String boxID) {
			super(theTitle);
			this.meBoxType = boxType;
			this.meID = boxID;
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
	private static class DropBox extends PageElement {
		// Fields
		@XmlElement(name="DropValues")
		private List<SimpleEntry<String, String>> meVals;

		// Constructor
		public DropBox(String defaultValue, List<SimpleEntry<String, String>> dropValues) {
			super(defaultValue);
			this.meVals = dropValues;
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

	// Fields
	@XmlElements({
		@XmlElement(name="LabledTextBox", type=LabledTextBox.class),
		@XmlElement(name="LabledTextValue", type=LabledTextValue.class),
		@XmlElement(name="DropBox", type=DropBox.class),
		@XmlElement(name="Group", type=Group.class),
		@XmlElement(name="SideMenu", type=SideMenu.class),
		@XmlElement(name="Button", type=Button.class),
		@XmlElement(name="Link", type=Link.class),
		@XmlElement(name="Link", type=Link.class),
		@XmlElement(name="XmlGoal", type=XmlGoal.class)
	})
	private List<PageElement> mePage;
	//@XmlTransient
	private Marshaller meMar;

	// Constructor
	public XmlBuilder() throws JAXBException {
		this.mePage = new LinkedList<PageElement>();
		this.meMar = JAXBContext.newInstance(XmlBuilder.class).createMarshaller();
		this.meMar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	}

	public String buildMainPage(List<GoalGroup> theGoalGroups) throws JAXBException {
		SideMenu tmpMenu = new SideMenu();
		tmpMenu.addLink(new Link("Chat", "GET_CHAT"));
		this.mePage.add(tmpMenu);
		theGoalGroups.stream().forEach(gr -> this.mePage.add(new Group(gr)));
		return this.createXml();
	}

	public String buildLoginPage() throws JAXBException {
		Group tmpGrp = new Group("Valid Credentials Needed");
		tmpGrp.addElement(new LabledTextBox("Login Email", LabledTextBox.TextBoxType.EMAIL, XmlBuilder.me_EMAIL_TXTBOX_ID));
		tmpGrp.addElement(new LabledTextBox("Login Password", LabledTextBox.TextBoxType.PASSWORD, XmlBuilder.me_PASS_TXTBOX_ID));
		tmpGrp.addElement(new Button("Login", "GET_SHA1", null));
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

	public String buildGoalPage(Goal theGoal, boolean isAdmin) throws JAXBException {
		SideMenu tmpMenu = new SideMenu();
		Group tmpGrp = new Group("Goal Page");
		tmpMenu.addLink(new Link("Chat", "GET_CHAT"));
		tmpMenu.addLink(new Link("Main Page", "GET_MAIN"));
		this.mePage.add(tmpMenu);
		tmpGrp.addElement(new Button("Back To Main Page", "GET_MAIN", null));
		if (isAdmin)
			tmpGrp.addElement(new Button("Edit Goal", "GET_GOALEDIT", theGoal.getUUID()));
		tmpGrp.addElement(new LabledTextValue("Goal Name:", theGoal.getName()));
		tmpGrp.addElement(new LabledTextValue("Goal Description:", theGoal.getDescription()));
		tmpGrp.addElement(new LabledTextValue("Goal Group:", theGoal.getGroupUUID()));
		tmpGrp.addElement(new XmlGoal(theGoal.getName(), theGoal.getPercentage()));
		this.mePage.add(tmpGrp);
		return this.createXml();
	}

	public String buildGoalEditPage(Goal theGoal) throws JAXBException {
		SideMenu tmpMenu = new SideMenu();
		Group tmpGrp = new Group("Goal Page");
		tmpMenu.addLink(new Link("Chat", "GET_CHAT"));
		tmpMenu.addLink(new Link("Main Page", "GET_MAIN"));
		this.mePage.add(tmpMenu);
		tmpGrp.addElement(new Button("Cancel Editing Goal", "GET_GOAL", theGoal.getUUID()));
		this.mePage.add(tmpGrp);
		return this.createXml();
	}

	public String buildUserEditPage() throws JAXBException {
		this.mePage.add(new SideMenu());
		// TODO: Add other page parts
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

//	/**
//	 * This method is for XML purpose only, and returns the inside page list.
//	 * @return	the inside page list
//	 */
//	public LinkedList<PageElement> getMePage() {
//		return this.mePage;
//	}
}
