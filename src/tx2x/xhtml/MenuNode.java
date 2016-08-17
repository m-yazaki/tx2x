package tx2x.xhtml;

import java.util.LinkedHashMap;

public class MenuNode {

	private String m_sManualName;
	private String m_sPartName;
	private String m_sPrevFilename;
	private String m_sNextFilename;
	private LinkedHashMap<String, String> m_cParents;

	public void setManualName(String sManualName) {
		m_sManualName = sManualName;
	}

	public void setPartName(String sPartName) {
		m_sPartName = sPartName;
	}

	public void setPrevFilename(String sPrevFilename) {
		m_sPrevFilename = sPrevFilename;
	}

	public void setNextFilename(String sNextFilename) {
		m_sNextFilename = sNextFilename;
	}

	public String getManualName_DUP() {
		return m_sManualName;
	}

	public String getPartName() {
		return m_sPartName;
	}

	public void setParents(LinkedHashMap<String, String> cParents) {
		m_cParents = cParents;
	}

	public LinkedHashMap<String, String> getParents() {
		return m_cParents;
	}

	public String getPrevFilename() {
		return m_sPrevFilename;
	}

	public String getNextFilename() {
		return m_sNextFilename;
	}

}
