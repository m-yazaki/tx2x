package tx2x.xhtml;

public class NavPointInfo {

	private String m_sText;
	private String m_sHref;
	private int m_nLevel;

	public NavPointInfo(String sText, String sHref, int nLevel) {
		m_sText = sText;
		m_sHref = sHref;
		m_nLevel = nLevel;
	}

	public String getText() {
		return m_sText;
	}

	public String getHref() {
		return m_sHref;
	}

	public int getLevel() {
		return m_nLevel;
	}
}
