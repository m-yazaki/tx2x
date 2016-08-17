package tx2x;

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
	private String m_sBigBlockOpenInfo;
	private String m_sSmallBlockOpenInfo;
	private String m_sLineOpenInfo;
	private String m_sLine;
	private String m_sLineCloseInfo;
	private String m_sSmallBlockCloseInfo;
	private String m_sBigBlockCloseInfo;

	public Style_TagInfo(String sBigBlockOpenInfo, String sSmallBlockOpenInfo,
			String sLineOpenInfo, String sLine, String sLineCloseInfo,
			String sSmallBlockCloseInfo, String sBigBlockCloseInfo) {
		m_sBigBlockOpenInfo = sBigBlockOpenInfo;
		m_sSmallBlockOpenInfo = sSmallBlockOpenInfo;
		m_sLineOpenInfo = sLineOpenInfo;
		m_sLine = sLine;
		m_sLineCloseInfo = sLineCloseInfo;
		m_sSmallBlockCloseInfo = sSmallBlockCloseInfo;
		m_sBigBlockCloseInfo = sBigBlockCloseInfo;
	}

	public String getBigBlockOpenInfo() {
		return m_sBigBlockOpenInfo;
	}

	public String getSmallBlockOpenInfo() {
		return m_sSmallBlockOpenInfo;
	}

	public String getLineOpenInfo() {
		return m_sLineOpenInfo;
	}

	public String getLine() {
		return m_sLine;
	}

	public String getLineCloseInfo() {
		return m_sLineCloseInfo;
	}

	public String getSmallBlockCloseInfo() {
		return m_sSmallBlockCloseInfo;
	}

	public String getBigBlockCloseInfo() {
		return m_sBigBlockCloseInfo;
	}

	public void setLine(String text) {
		m_sLine = text;
	}

}
