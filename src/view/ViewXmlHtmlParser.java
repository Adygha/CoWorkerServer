/**
 * 
 */
package view;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

/**
 * A class to build HTML content off data from raw-request's XML data
 * @author Janty Azmat
 */
@XmlAccessorType(XmlAccessType.NONE)
class ViewXmlHtmlParser {
	// Constants
	private static final String me_GET_LOGIN_FUNC = "getLogin()";
	private static final String me_GET_MAIN_FUNC = "getMain()";
	private static final String me_GET_CHAT_FUNC = "getChat()";
	private static final String me_GET_GOAL_FUNC = "getGoal(\"";
	private static final String me_GET_GOALEDIT_FUNC = "getGoalEdit(\"";
	//private static final String me_GET_SUPERPAGE_FUNC = "getSuper()";
	private static final String me_GET_USER_FUNC = "getUser(\"";
	//private static final String me_GET_REGISTER_FUNC = "getRegister()";
	private static final String me_GET_GROUPOFFICE_FUNC = "getGroupOffice()";
	private static final String me_GET_SHA1_FUNC = "saveCred(true)";
	private static final String me_CREATE_USER_FUNC = "createUser()";
	private static final String me_CREATE_GOAL_FUNC = "createUpdateGoal()";
	private static final String me_CREATE_OFFICE_FUNC = "createOffice()";
	private static final String me_CREATE_GROUP_FUNC = "createGroup()";
	private static final String me_UPDATE_USER_FUNC = "updateUser(\"";
	private static final String me_UPDATE_GOAL_FUNC = "createUpdateGoal(\"";
	private static final String me_UPDATE_OFFICE_FUNC = "updateOffice()";
	private static final String me_UPDATE_GROUP_FUNC = "updateGroup()";
	private static final String me_DELETE_USER_FUNC = "deleteUser(\"";
	private static final String me_DELETE_GOAL_FUNC = "deleteGoal(\"";
	private static final String me_DELETE_OFFICE_FUNC = "deleteOffice()";
	private static final String me_DELETE_GROUP_FUNC = "deleteGroup()";


	@XmlAccessorType(XmlAccessType.NONE)
	private static abstract class PageElement {
		// Fields
		@XmlElement(name="Title")
		private String meTitle;


		public abstract String getHtml();
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class Link extends PageElement {
		// Fields
		@XmlElement(name="Command")
		private String meCommand;

		@Override
		public String getHtml() {
			StringBuilder outStr = new StringBuilder(64);
			outStr.append("<a href='javascript:");
			switch (this.meCommand) {
				case "GET_LOGIN":
					outStr.append(me_GET_LOGIN_FUNC);
					break;
				case "GET_MAIN":
					outStr.append(me_GET_MAIN_FUNC);
					break;
				case "GET_CHAT":
					outStr.append(me_GET_CHAT_FUNC);
					break;
//				case "GET_GOAL":
//					outStr.append(me_GET_GOAL_FUNC);
//					break;
				case "GET_GOALEDIT":
					outStr.append(me_GET_GOALEDIT_FUNC);
					outStr.append("\")");
					break;
				case "GET_USER":
					outStr.append(me_GET_USER_FUNC);
					outStr.append("\")");
					break;
//				case "GET_REGISTER":
//					outStr.append(me_GET_REGISTER_FUNC);
//					break;
				case "GET_GROUPOFFICE":
					outStr.append(me_GET_GROUPOFFICE_FUNC);
					break;
//				case "GET_SHA1":
//					outStr.append(me_GET_SHA1_FUNC);
//					break;
				default:
					outStr.append(this.meCommand); // TODO: Better remove
			}
			outStr.append("'>");
			outStr.append(super.meTitle.replace(" ", "&nbsp")); // Just to avoid linebreaks in links
			outStr.append("</a>");
			return outStr.toString();
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class Button extends PageElement {
		// Fields
		@XmlElement(name="Command")
		private String meCommand;
		@XmlElement(name="CommandParam")
		private String meCommParam;

		@Override
		public String getHtml() {
			StringBuilder outStr = new StringBuilder(64);
			outStr.append("<br /><button type='button' class='generalButton' onclick='");
			switch (this.meCommand) {
				case "GET_LOGIN":
					outStr.append(me_GET_LOGIN_FUNC);
					break;
				case "GET_MAIN":
					outStr.append(me_GET_MAIN_FUNC);
					break;
				case "GET_CHAT":
					outStr.append(me_GET_CHAT_FUNC);
					break;
				case "GET_GOAL":
					outStr.append(me_GET_GOAL_FUNC);
					outStr.append(this.meCommParam);
					outStr.append("\")");
					break;
				case "GET_GOALEDIT":
					outStr.append(me_GET_GOALEDIT_FUNC);
					outStr.append(this.meCommParam);
					outStr.append("\")");
					break;
//				case "GET_SUPERPAGE":
//					outStr.append(me_GET_SUPERPAGE_FUNC);
//					break;
				case "GET_USER":
					outStr.append(me_GET_USER_FUNC);
					outStr.append("\")");
					break;
//				case "GET_REGISTER":
//					outStr.append(me_GET_REGISTER_FUNC);
//					break;
				case "GET_GROUPOFFICE":
					outStr.append(me_GET_GROUPOFFICE_FUNC);
					break;
				case "GET_SHA1":
					outStr.append(me_GET_SHA1_FUNC);
					break;
				case "CREATE_USER":
					outStr.append(me_CREATE_USER_FUNC);
					break;
				case "CREATE_GOAL":
					outStr.append(me_CREATE_GOAL_FUNC);
					break;
				case "CREATE_OFFICE":
					outStr.append(me_CREATE_OFFICE_FUNC);
					break;
				case "CREATE_GROUP":
					outStr.append(me_CREATE_GROUP_FUNC);
					break;
				case "UPDATE_USER":
					outStr.append(me_UPDATE_USER_FUNC);
					outStr.append(this.meCommParam);
					outStr.append("\")");
					break;
				case "UPDATE_GOAL":
					outStr.append(me_UPDATE_GOAL_FUNC);
					outStr.append(this.meCommParam);
					outStr.append("\")");
					break;
				case "UPDATE_OFFICE":
					outStr.append(me_UPDATE_OFFICE_FUNC);
					break;
				case "UPDATE_GROUP":
					outStr.append(me_UPDATE_GROUP_FUNC);
					break;
				case "DELETE_USER":
					outStr.append(me_DELETE_USER_FUNC);
					outStr.append(this.meCommParam);
					outStr.append("\")");
					break;
				case "DELETE_GOAL":
					outStr.append(me_DELETE_GOAL_FUNC);
					outStr.append(this.meCommParam);
					outStr.append("\")");
					break;
				case "DELETE_OFFICE":
					outStr.append(me_DELETE_OFFICE_FUNC);
					break;
				case "DELETE_GROUP":
					outStr.append(me_DELETE_GROUP_FUNC);
					break;
				default:
					outStr.append(this.meCommand); // TODO: Better remove
			}
			outStr.append("'>");
			outStr.append(super.meTitle);
			outStr.append("</button><br />");
			return outStr.toString();
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

		@Override
		public String getHtml() {
			StringBuilder outStr = new StringBuilder(256);
			if (this.meIsPage) {
				outStr.append("<br /><fieldset><legend>Percentage</legend><div class='progressBar'><span class='percentage'>");
				outStr.append(this.mePer);
				outStr.append("%</span><div style='width: ");
				outStr.append(mePer);
				outStr.append("%;' class='progressBarIn'></div></fieldset>");
			} else {
				outStr.append("<br /><a href='javascript:");
				outStr.append(me_GET_GOAL_FUNC);
				outStr.append(this.meCommand);
				outStr.append("\")' style='text-decoration: none;'><fieldset class='progressLink'>");
				outStr.append(super.meTitle);
				outStr.append("<br /><div class='progressBar'><span class='percentage'>");
				outStr.append(mePer);
				outStr.append("%</span><div style='width: ");
				outStr.append(mePer);
				outStr.append("%;' class='progressBarIn'></div></fieldset></a>");
			}
			return outStr.toString();
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class XmlUser extends PageElement {
		// Fields
		@XmlElement(name="Email")
		private String meEmail;

		@Override
		public String getHtml() {
			StringBuilder outStr = new StringBuilder(256);
			outStr.append("<br /><a href='javascript:");
			outStr.append(me_GET_USER_FUNC);
			outStr.append(this.meEmail);
			outStr.append("\")' style='text-decoration: none;'><fieldset class='progressLink'><legend>");
			outStr.append(super.meTitle);
			outStr.append("</legend>");
			outStr.append(this.meEmail);
			outStr.append("</fieldset></a>");
			return outStr.toString();
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

		@Override
		public String getHtml() {
			StringBuilder outStr = new StringBuilder(512);
			if (this.meElems == null)
				this.meElems = new LinkedList<PageElement>();
			outStr.append("<fieldset class='group'><legend>");
			outStr.append(super.meTitle);
			outStr.append("</legend>");
			this.meElems.stream().forEach(pe -> outStr.append(pe.getHtml()));
			outStr.append("</fieldset>");
			return outStr.toString();
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class LabledTextBox extends PageElement {
		// Fields
		@XmlElement(name="ID")
		private String meID;
		@XmlElement(name="TextBoxType")
		private String meBoxType;
		@XmlElement(name="CurrentText")
		private String meCurTxt;

		@Override
		public String getHtml() {
			StringBuilder outStr = new StringBuilder(64);
			outStr.append("<br /><fieldset><legend>");
			outStr.append(super.meTitle);
			outStr.append("</legend><input type='");
			outStr.append(this.meBoxType.toLowerCase());
			outStr.append("' value='");
			outStr.append(this.meCurTxt);
			outStr.append("' id='");
			outStr.append(this.meID);
			outStr.append("' class='txtBox' /></fieldset>");
			return outStr.toString();
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class LabledTextArea extends PageElement {
		// Fields
		@XmlElement(name="ID")
		private String meID;
		@XmlElement(name="CurrentText")
		private String meCurTxt;

		@Override
		public String getHtml() {
			StringBuilder outStr = new StringBuilder(64);
			outStr.append("<br /><fieldset><legend>");
			outStr.append(super.meTitle);
			outStr.append("</legend><textarea id='");
			outStr.append(this.meID);
			outStr.append("' class='txtBox' style='resize: none; overflow-y: scroll;' overflow-y>");
			outStr.append(this.meCurTxt);
			outStr.append("</textarea></fieldset>");
			return outStr.toString();
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class LabledTextValue extends PageElement {
		// Fields
		@XmlElement(name="TextValueType")
		private String meTxt;

		@Override
		public String getHtml() {
			StringBuilder outStr = new StringBuilder(64);
			outStr.append("<br /><fieldset><legend>");
			outStr.append(super.meTitle);
			outStr.append("</legend>");
			outStr.append(this.meTxt);
			outStr.append("</fieldset>");
			return outStr.toString();
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

		@Override
		public String getHtml() {
			StringBuilder outStr = new StringBuilder(64);
			if (this.meVals == null)
				this.meVals = new LinkedList<XmlEntry>();
			outStr.append("<br /><fieldset><legend>");
			outStr.append(super.meTitle);
			if (this.meIsEditable) {
				outStr.append("</legend><select onchange='this.nextElementSibling.value=this.options[this.selectedIndex].text;' id='");
			} else {
				outStr.append("</legend><select id='");
			}
			outStr.append(this.meID);
			outStr.append("' class='txtBox'>");
			for (XmlEntry ent : this.meVals) {
				outStr.append("<option value='");
				outStr.append(ent.getKey());
				outStr.append(ent.getKey().equals(this.meVal) ? "' selected>" : "'>");
				if (this.meIsEditable && ent.getKey().equals(this.meVal))
					this.meVal = ent.getValue();
				outStr.append(ent.getValue());
				outStr.append("</option>");
			}
			if (this.meIsEditable) {
				outStr.append("</select><input type='text' class='txtBox' id='");
				outStr.append(this.meID);
				outStr.append("Text' value='");
				outStr.append(this.meVal);
				outStr.append("' /></fieldset>");
			} else {
				outStr.append("</select></fieldset>");
			}
			return outStr.toString();
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class SideMenu extends PageElement {
		// Fields
		@XmlElement(name="Link")
		private LinkedList<Link> meLinks;

		@Override
		public String getHtml() {
			StringBuilder outStr = new StringBuilder(512);
			if (this.meLinks == null)
				this.meLinks = new LinkedList<Link>();
			outStr.append("<div id='sideMenu' class='sidenav'><a href='javascript:void(0)' class='closebtn' onclick='closeNav()'>&times;</a>");
			this.meLinks.stream().forEach(pe -> outStr.append(pe.getHtml()));
			outStr.append("</div><span class='menuSpan' onclick='openNav()'>&#9776; MENU</span><br /><br /><br />");
			return outStr.toString();
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

		public String getKey() {
			return meKey;
		}

		public String getValue() {
			return meValue;
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


	public String getHtml() {
		StringBuilder outStr = new StringBuilder(8192);
		if (this.mePage == null)
			this.mePage = new LinkedList<PageElement>();
		this.mePage.stream().forEach(pe -> outStr.append(pe.getHtml()));
		return outStr.toString();
	}
}
