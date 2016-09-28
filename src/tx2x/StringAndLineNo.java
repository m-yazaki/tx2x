package tx2x;

public class StringAndLineNo {

	private String m_sLine;
	private int m_nLineNo;

	public StringAndLineNo(String line, int lineno) {
		m_sLine = line;
		m_nLineNo = lineno;
	}

	public String getLine() {
		return m_sLine;
	}

	public int getLineNo() {
		return m_nLineNo;
	}

}
