package tx2x.xhtml;

public class Style_TagInfo {
	/**
	 * 各変数の意味合いは以下の通り。
	 *
	 * <pre>
	 * <ol>：m_sBigBlockOpenInfo
	 * <li>：m_sSmallBlockOpenInfo
	 * <p>：m_sLineOpenInfo
	 * 文章：m_sLine
	 * </p>：m_sLineCloseInfo
	 * </li>：m_sSmallBlockCloseinfo
	 * </ol>：m_sBigBlockCloseInfo
	 * </pre>
	 */
	private String m_sOpenInfo;
	private String m_sLine;
	private String m_sCloseInfo;
	private String m_sAdditionalIndent;
	static public Style_TagInfo NULL_STYLE_TAGINFO = new Style_TagInfo("", "", "", "");

	public Style_TagInfo(String sAdditionalIndent, String sOpenInfo, String sLine, String sCloseInfo) {
		m_sOpenInfo = sOpenInfo;
		m_sLine = sLine;
		m_sCloseInfo = sCloseInfo;
		m_sAdditionalIndent = sAdditionalIndent;
	}

	public String getOpenInfo() {
		return m_sOpenInfo;
	}

	public String getLine() {
		return m_sLine;
	}

	public String getCloseInfo() {
		return m_sCloseInfo;
	}

	public void setLine(String text) {
		m_sLine = text;
	}

	public String getHTMLTagIndent() {
		return m_sAdditionalIndent;
	}
}
