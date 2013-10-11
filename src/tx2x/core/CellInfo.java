package tx2x.core;

public class CellInfo {
	/* 斜線 */
	int m_nType;
	public static final int LeftTopLine = 0x1;
	public static final int RightTopLine = 0x2;
	/* 結合セル数 */
	int m_nWidth;
	int m_nHeight;
	/* 背景色 */
	private int m_nColor;
	/* 上下の揃えかた */
	private String m_sVerticalJustification;

	public CellInfo(int width, int height) {
		m_nWidth = width;
		m_nHeight = height;
		m_nType = 0x0;
		m_nColor = 0;
	}

	public int getWidth() {
		return m_nWidth;
	}

	public int getHeight() {
		return m_nHeight;
	}

	public void setDiagonalLine(int type) {
		m_nType = type;
	}

	public int getDiagonalLine() {
		return m_nType;
	}

	public void setSize(int width, int height) {
		m_nWidth = width;
		m_nHeight = height;
	}

	public boolean isDiagonalLine() {
		return (m_nType != 0x00);
	}

	// 左上から斜線である場合はtrue
	public boolean isLeftTopLine() {
		return ((m_nType & LeftTopLine) != 0x00);
	}

	// 右上から斜線である場合はtrue
	public boolean isRightTopLine() {
		return ((m_nType & RightTopLine) != 0x00);
	}

	public void setColor(int color) {
		m_nColor = color;
	}

	public int getColor() {
		return m_nColor;
	}

	public void setVerticalJustification(String sVerticalJustification) {
		m_sVerticalJustification = sVerticalJustification;
	}

	public String getVerticalJustification() {
		return m_sVerticalJustification;
	}

	public boolean isHeader() {
		return (m_nColor != 0);
	}
}
