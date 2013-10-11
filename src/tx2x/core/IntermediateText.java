package tx2x.core;

/*
 * スタイル情報とテキストを保持するクラス
 *
 * ArrayListに挿入することを前提とする。
 */
public class IntermediateText {
	Style m_cAStyle; // 一致したスタイルを保持
	String m_sLine; // テキスト本文

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

	public boolean hasChild() {
		return false;
	}

	public void setText(String line) {
		m_sLine = line;
	}

	public void setStyle(Style newStyle) {
		m_cAStyle = newStyle;
	}
}
