package tx2x.core;

/*
 * スタイル情報とテキストを保持するクラス
 *
 * ArrayListに挿入することを前提とする。
 */
public class IntermediateText {
	Style m_cAStyle; // 一致したスタイルを保持
	String m_sLine; // テキスト本文
	Object m_cOption; // オプション

	public IntermediateText(Style style, String line) {
		super();
		setStyle(style);
		setText(line);
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
}
