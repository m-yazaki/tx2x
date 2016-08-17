package tx2x.core;

public class CellInfo {
	/* 斜線 */
	int m_nType;
	public static final int LeftTopLine = 0x1;
	public static final int RightTopLine = 0x2;
	/* 結合セル数 */
	int m_nWidth;
	int m_nHeight;
	private int m_nDiffX;
	private int m_nDiffY;
	/* 背景色 */
	private int m_nColor;
	/* 上下の揃えかた */
	private String m_sVerticalJustification;
	private CellInfo m_cParent;

	private boolean m_bHeader;
	private boolean m_bRowHeader;
	private boolean m_bColumnHeader;
	private String m_sCellStyle;

	public int getDiffX() {
		return m_nDiffX;
	}

	public int getDiffY() {
		return m_nDiffY;
	}

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

	public void setMerged(CellInfo cParent, int nDiffX, int nDiffY) {
		m_cParent = cParent;
		m_nDiffX = nDiffX;
		m_nDiffY = nDiffY;
	}

	public CellInfo isMerged() {
		return m_cParent;
	}

	public void setHeader(boolean bHeader) {
		m_bHeader = bHeader;
	}

	public boolean isHeader() {
		return m_bHeader;
	}

	public void setRowHeader(boolean bRowHeader) {
		m_bRowHeader = bRowHeader;
	}

	public boolean isRowHeader() {
		return m_bRowHeader;
	}

	public void setColumnHeader(boolean bColumnHeader) {
		m_bColumnHeader = bColumnHeader;
	}

	public boolean isColumnHeader() {
		return m_bColumnHeader;
	}

	public void setCellStyle(String sCellStyle) {
		m_sCellStyle = sCellStyle;
	}

	public String getCellStyle() {
		return m_sCellStyle;
	}
}
