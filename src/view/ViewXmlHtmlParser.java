/**
 * 
 */
package view;

import java.util.LinkedList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
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
	private static final String me_GET_SUPERPAGE_FUNC = "getSuper()";
	private static final String me_GET_USEREDIT_FUNC = "getUserEdit()";
	private static final String me_GET_REGISTER_FUNC = "getRegister()";
	private static final String me_GET_SHA1_FUNC = "saveCred()";
	//private static final String me_EMAIL_TXTBOX_ID = "txtBoxEmail";
	//private static final String me_PASS_TXTBOX_ID = "txtBoxPass";


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
					outStr.append(ViewXmlHtmlParser.me_GET_LOGIN_FUNC);
					break;
				case "GET_MAIN":
					outStr.append(ViewXmlHtmlParser.me_GET_MAIN_FUNC);
					break;
				case "GET_CHAT":
					outStr.append(ViewXmlHtmlParser.me_GET_CHAT_FUNC);
					break;
//				case "GET_GOAL":
//					outStr.append(ViewXmlHtmlParser.me_GET_GOAL_FUNC);
//					break;
				case "GET_GOALEDIT":
					outStr.append(ViewXmlHtmlParser.me_GET_GOALEDIT_FUNC);
					break;
				case "GET_USER":
					outStr.append(ViewXmlHtmlParser.me_GET_USEREDIT_FUNC);
					break;
				case "GET_REGISTER":
					outStr.append(ViewXmlHtmlParser.me_GET_REGISTER_FUNC);
					break;
				case "GET_SHA1":
					outStr.append(ViewXmlHtmlParser.me_GET_SHA1_FUNC);
					break;
				default:
					outStr.append(this.meCommand);
			}
			outStr.append("'>");
			outStr.append(super.meTitle.replace(" ", "&nbsp"));
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
					outStr.append(ViewXmlHtmlParser.me_GET_LOGIN_FUNC);
					break;
				case "GET_MAIN":
					outStr.append(ViewXmlHtmlParser.me_GET_MAIN_FUNC);
					break;
				case "GET_CHAT":
					outStr.append(ViewXmlHtmlParser.me_GET_CHAT_FUNC);
					break;
				case "GET_GOAL":
					outStr.append(ViewXmlHtmlParser.me_GET_GOAL_FUNC);
					outStr.append(this.meCommParam);
					outStr.append("\")");
					break;
				case "GET_GOALEDIT":
					outStr.append(ViewXmlHtmlParser.me_GET_GOALEDIT_FUNC);
					outStr.append(this.meCommParam);
					outStr.append("\")");
					break;
				case "GET_SUPERPAGE":
					outStr.append(ViewXmlHtmlParser.me_GET_SUPERPAGE_FUNC);
					break;
				case "GET_USER":
					outStr.append(ViewXmlHtmlParser.me_GET_USEREDIT_FUNC);
					break;
				case "GET_REGISTER":
					outStr.append(ViewXmlHtmlParser.me_GET_REGISTER_FUNC);
					break;
				case "GET_SHA1":
					outStr.append(ViewXmlHtmlParser.me_GET_SHA1_FUNC);
					break;
				default:
					outStr.append(this.meCommand);
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
				outStr.append("<fieldset><legend>Percentage</legend><div class='progressBar'><span class='percentage'>");
				outStr.append(this.mePer);
				outStr.append("%</span><div style='width: ");
				outStr.append(mePer);
				outStr.append("%;' class='progressBarIn'></div></fieldset>");
			} else {
				outStr.append("<br /><a href='javascript:");
				outStr.append(ViewXmlHtmlParser.me_GET_GOAL_FUNC);
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

		@Override
		public String getHtml() {
			StringBuilder outStr = new StringBuilder(64);
			outStr.append("<br />");
			outStr.append(super.meTitle);
			outStr.append("<br /><input type='");
			outStr.append(this.meBoxType.toLowerCase());
			outStr.append("' id='");
			outStr.append(this.meID);
			outStr.append("' class='txtBox' /><br />");
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
			outStr.append("<fieldset><legend>");
			outStr.append(super.meTitle);
			outStr.append("</legend>");
			outStr.append(this.meTxt);
			outStr.append("</fieldset>");
			return outStr.toString();
		}
	}

	@XmlAccessorType(XmlAccessType.NONE)
	private static class DropBox extends PageElement {
		// Fields
		@XmlElement(name="DropValues")
		private List<SimpleEntry<String, String>> meVals;

		@Override
		public String getHtml() {
			StringBuilder outStr = new StringBuilder(64);
			if (this.meVals == null)
				this.meVals = new LinkedList<SimpleEntry<String, String>>();
			outStr.append("<select class='txtBox'>");
			for (SimpleEntry<String, String> ent : this.meVals) {
				outStr.append("<option value='");
				outStr.append(ent.getKey());
				outStr.append("'>");
				outStr.append(ent.getValue());
				outStr.append("</option>");
			}
			outStr.append("</select><br />");
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
			outStr.append("</div><span style='font-size:30px;cursor:pointer;' onclick='openNav()'>&#9776; MENU</span><br /><br /><br />");
			return outStr.toString();
		}
	}

	// Fields
	//@XmlElement//(name = "PageElement")
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

	public String getHtml() {
		StringBuilder outStr = new StringBuilder(8192);
		if (this.mePage == null)
			this.mePage = new LinkedList<PageElement>();
		this.mePage.stream().forEach(pe -> outStr.append(pe.getHtml()));
		return outStr.toString();
	}
}
