package tx2x.core;

import tx2x.StringAndLineNo;

/*
 * スタイル情報とテキストを保持するクラス
 *
 * ArrayListに挿入することを前提とする。
 */
public class IntermediateText {
	Style m_cAStyle; // 一致したスタイルを保持
	String m_sLine; // テキスト本文
	Object m_cOption; // オプション
	private int m_nLineNumber;

	public IntermediateText(Style style, StringAndLineNo cStringAndLineNo) {
		super();
		setStyle(style);
		setText(cStringAndLineNo.getLine());
		setLineNumber(cStringAndLineNo.getLineNo());
	}

	public Style getStyle() {
		return m_cAStyle;
	}

	public String getText() {
		return m_sLine;
	}

	public String getChildText() {
		return getText();
	}

	public void setText(String line) {
		m_sLine = line;
	}

	public void setStyle(Style newStyle) {
		m_cAStyle = newStyle;
	}

	public String getDebugText() {
		return "(iText)" + m_cAStyle.getStyleName() + ":" + m_sLine + "\n";
	}

	public void setOption(Object newOption) {
		m_cOption = newOption;
	}

	public Object getOption() {
		return m_cOption;
	}

	private void setLineNumber(int nLineNumber) {
		m_nLineNumber = nLineNumber;
	}

	public int getLineNumber() {
		return m_nLineNumber;
	}
}
