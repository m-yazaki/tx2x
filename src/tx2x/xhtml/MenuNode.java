package tx2x.xhtml;

import java.util.ArrayList;

public class MenuNode {

	private String m_sManualName;
	private String m_sTitle;
	private String m_sFilename;
	private MenuNode m_cPartNode;
	private MenuNode m_sPrevNode;
	private MenuNode m_sNextNode;
	private ArrayList<MenuNode> m_cParents;

	public void setManualName(String sManualName) {
		m_sManualName = sManualName;
	}

	public void setPartNode(MenuNode sPartName) {
		m_cPartNode = sPartName;
	}

	public void setPrevFileNode(MenuNode sPrevFilename) {
		m_sPrevNode = sPrevFilename;
	}

	public void setNextFileNode(MenuNode sNextFilename) {
		m_sNextNode = sNextFilename;
	}

	public String getManualName() {
		return m_sManualName;
	}

	public MenuNode getPartNode() {
		return m_cPartNode;
	}

	public void setParents(ArrayList<MenuNode> cParents) {
		m_cParents = cParents;
	}

	public ArrayList<MenuNode> getParents() {
		return m_cParents;
	}

	public MenuNode getPrevNode() {
		return m_sPrevNode;
	}

	public MenuNode getNextNode() {
		return m_sNextNode;
	}

	public void setFilename(String sFilename) {
		m_sFilename = sFilename;
	}

	public void setTitle(String sTitle) {
		m_sTitle = sTitle;
	}

	public String getTitle() {
		return m_sTitle;
	}

	public String getFilename() {
		return m_sFilename;
	}

}
