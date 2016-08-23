/**
 * ControlTextが【表】の場合の処理を担当する
 * 具体的にはm_nCellSize[][]を作成するのが目的である。
 * TableToXXXXXはm_nCellSize[][]を読みながら、適切なテキストを出力すること。
 */
package tx2x.core;

import java.util.ArrayList;
import java.util.Iterator;

import tx2x.IntermediateTextTreeWalker;
import tx2x.LongStyleManager;

public class TableManager {
	protected String m_sStyle;
	int m_nWidth;
	int m_nHeight;
	int m_nHeader;
	protected ControlText m_cTableText;
	CellInfo[][] m_cCellInfoArray;
	private boolean m_bDebugMode;
	private String m_sCaption = null;
	private String m_sColumnWidth;
	protected IntermediateTextTreeWalker m_cTreeWalker;

	public TableManager(ControlText cText, IntermediateTextTreeWalker cTreeWalker, boolean bDebugMode) {
		m_cTreeWalker = cTreeWalker;
		m_cTableText = (ControlText) m_cTreeWalker.getCurrentNode();

		m_nHeader = 0;
		m_bDebugMode = bDebugMode;

		String sTableInfo = m_cTableText.getChildList().get(0).getChildText();
		m_sStyle = "標準";
		if (sTableInfo.indexOf("style:") != -1) {
			m_sStyle = sTableInfo.replaceFirst(".*style:([^),]+).*", "$1");
		}
		if (sTableInfo.indexOf("caption:") != -1) {
			m_sCaption = sTableInfo.replaceFirst(".*caption:([^),]+).*", "$1");
		}
		if (sTableInfo.indexOf("columnwidth:") != -1) {
			m_sColumnWidth = sTableInfo.replaceFirst(".*columnwidth:([^),]+).*", "$1");
		}

		// Heightを取得
		ArrayList<IntermediateText> child = m_cTableText.getChildList();
		if (child == null) {
			m_nHeight = 0;
		} else {
			m_nHeight = child.size() - 2;
		}

		// Widthを取得
		m_nWidth = 0;
		Iterator<IntermediateText> it = m_cTableText.getChildList().iterator();
		while (it.hasNext()) {
			IntermediateText row = it.next();
			if (row instanceof ControlText == false)
				continue; // IntermediateTextは無視
			ControlText cRow = (ControlText) row;
			if (cRow.getStyle().getStyleName().equals("【行】")) {
				int nRowSize = cRow.getChildList().size();
				if (m_nWidth < nRowSize)
					m_nWidth = nRowSize;
			}
		}

		// nSpanのチェック（左と結合、上と結合、左上から斜線、右上から斜線、黒20%だけチェック、上下センター）
		int id = 0;
		int[][] nSpan = new int[m_nHeight][m_nWidth];
		m_cCellInfoArray = new CellInfo[m_nHeight][m_nWidth];
		for (int y = 0; y < m_nHeight; y++) {
			ControlText cRow = (ControlText) m_cTableText.getChildList().get(y + 1);
			for (int x = 0; x < m_nWidth; x++) {
				m_cCellInfoArray[y][x] = new CellInfo(1, 1);
				ArrayList<IntermediateText> cCells = cRow.getChildList();
				String sText; // セル内のテキスト
				IntermediateText iText = null;
				try {
					ControlText cCell = (ControlText) cCells.get(x);
					ControlText cCellText = (ControlText) cCell.getChildList().get(0);
					iText = cCellText.getChildList().get(0);
					sText = iText.getChildText();
				} catch (java.lang.IndexOutOfBoundsException e) {
					// DEBUG INFOを出して継続
					outputErrorMessage(e, cText);
					sText = "";
				}
				// }
				// String sText = cCell.getChild().get(0).getText();
				/* 結合処理 */
				if (sText.indexOf("【左と結合】") != -1) {
					try {
						nSpan[y][x] = nSpan[y][x - 1];
						m_cCellInfoArray[y][x].setColor(m_cCellInfoArray[y][x - 1].getColor());
						m_cCellInfoArray[y][x].setHeader(m_cCellInfoArray[y][x - 1].isHeader());
					} catch (ArrayIndexOutOfBoundsException e) {
						outputErrorMessage(e, cText);
					}
				} else if (sText.indexOf("【上と結合】") != -1) {
					try {
						nSpan[y][x] = nSpan[y - 1][x];
						m_cCellInfoArray[y][x].setColor(m_cCellInfoArray[y - 1][x].getColor());
					} catch (ArrayIndexOutOfBoundsException e) {
						outputErrorMessage(e, cText);
					}
				} else {
					nSpan[y][x] = id;
					id++;
				}
				/* 斜線処理 */
				if (sText.indexOf("【左上から斜線】") != -1) {
					m_cCellInfoArray[y][x].setDiagonalLine(CellInfo.LeftTopLine);
					iText.setText(sText.replaceFirst("【左上から斜線】", ""));
				} else if (sText.indexOf("【右上から斜線】") != -1) {
					m_cCellInfoArray[y][x].setDiagonalLine(CellInfo.RightTopLine);
					iText.setText(sText.replaceFirst("【右上から斜線】", ""));
				}
				/* グレー処理 */
				if (sText.indexOf("【黒20%】") != -1 || (m_sStyle.equals("手順内表") && x < m_nWidth - 1) // "手順内表"スタイルは、一番右の列は白。それ以外は黒20%
				) {
					m_cCellInfoArray[y][x].setColor(20);
				} else if (sText.indexOf("【黒15%】") != -1) {
					m_cCellInfoArray[y][x].setColor(15);
				} else if (sText.indexOf("【ヘッダー】") != -1) {
					m_cCellInfoArray[y][x].setHeader(true);
				} else if (sText.indexOf("【行ヘッダー】") != -1) {
					m_cCellInfoArray[y][x].setRowHeader(true);
				} else if (sText.indexOf("【行ヘッダー】") != -1) {
					m_cCellInfoArray[y][x].setColumnHeader(true);
				} else if (sText.indexOf("【背景グレー】") != -1) {
					m_cCellInfoArray[y][x].setColor(15);
				}
				/* 上下センター */
				if (sText.indexOf("【上下センター】") != -1) {
					m_cCellInfoArray[y][x].setVerticalJustification("中央");
				}
			}
		}

		// nSpanのチェック（右と結合、下と結合だけチェック）
		for (int y = m_nHeight - 1; y >= 0; y--) {
			ControlText cRow = (ControlText) m_cTableText.getChildList().get(y + 1);
			for (int x = m_nWidth - 1; x >= 0; x--) {
				ArrayList<IntermediateText> cCells = cRow.getChildList();
				ControlText cCell;
				String sFirstTextInTheCell; // セル内の初めの1行
				try {
					cCell = (ControlText) cCells.get(x);
					IntermediateText iText = cCell.getChildList().get(0);
					if (iText instanceof ControlText) {
						sFirstTextInTheCell = ((ControlText) iText).getChildList().get(0).getChildText();
					} else {
						sFirstTextInTheCell = cCell.getChildList().get(0).getText();
					}
				} catch (IndexOutOfBoundsException e) {
					// DEBUG INFOを出して継続
					outputErrorMessage(e, cText);
					cCell = null;
					sFirstTextInTheCell = "";
				}
				// }
				if (m_bDebugMode)
					System.out.println(sFirstTextInTheCell);
				if (sFirstTextInTheCell.equals("【右と結合】")) {
					if (m_nWidth <= x + 1) {
						System.out.println("【右と結合】できません");
						System.out.println("x + 1:" + (x + 1) + ", m_nWidth:" + m_nWidth);
					} else {
						nSpan[y][x] = nSpan[y][x + 1];
						m_cCellInfoArray[y][x].setDiagonalLine(m_cCellInfoArray[y][x + 1].getDiagonalLine());
						m_cCellInfoArray[y][x + 1].setDiagonalLine(0);
						m_cCellInfoArray[y][x].setColor(m_cCellInfoArray[y][x + 1].getColor());
						// m_cCellInfoArray[y][x + 1].setColor(0);
					}
				} else if (sFirstTextInTheCell.equals("【下と結合】")) {
					nSpan[y][x] = nSpan[y + 1][x];
					m_cCellInfoArray[y][x].setDiagonalLine(m_cCellInfoArray[y + 1][x].getDiagonalLine());
					m_cCellInfoArray[y + 1][x].setDiagonalLine(0);
				}
			}
		}

		boolean[] m_nCheck = new boolean[id];
		for (int i = 0; i < id; i++) {
			m_nCheck[i] = false;
		}

		int currentId = 0;

		for (int y = 0; y < m_nHeight; y++) {
			for (int x = 0; x < m_nWidth; x++) {
				int cellHeight = 1;
				int cellWidth = 1;
				currentId = nSpan[y][x];
				if (m_nCheck[currentId]) {
					continue;
				}
				// currentIdは未チェック
				m_nCheck[currentId] = true;
				// 幅をチェック
				for (int x2 = x; x2 < m_nWidth; x2++) {
					if (nSpan[y][x2] == nSpan[y][x]) {
						// 同じIDが振られていた
						cellWidth = x2 - x + 1; // 横幅ほぼ確定
						if (cellWidth > 1) {
							m_cCellInfoArray[y][x2].setMerged(m_cCellInfoArray[y][x], x2 - x,
									0 /* y - y */);
						}
					} else {
						break; // 横向きチェックおしまい
					}
				}
				// 高さをチェック
				for (int y2 = y + 1; y2 < m_nHeight; y2++) {
					if (nSpan[y2][x] == nSpan[y][x]) {
						// 同じIDが振られていた
						// 現在確認済みの最長幅の分だけ確認（枝切り）
						for (int x_delta = 0; x_delta < cellWidth; x_delta++) {
							if (nSpan[y2][x + x_delta] != nSpan[y][x]) {
								cellWidth = x_delta;
								break;
							} else {
								m_cCellInfoArray[y2][x + x_delta].setMerged(m_cCellInfoArray[y][x], x + x_delta - x,
										y2 - y);
							}
						}
						cellHeight = y2 - y + 1; // 高さ確定
						// if (cellWidth > 1) {
						// m_cCellInfoArray[y2][x].setMerged();
						// }
					} else {
						cellHeight = y2 - y; // 高さ確定
						break; // 高さチェックおしまい
					}
				}
				m_cCellInfoArray[y][x].setSize(cellWidth, cellHeight);
			}
		}

		if (m_sStyle.equals("NOHEADER") == false) {
			/*
			 * ヘッダ行の判別
			 */
			boolean exit = false; // ヘッダ行のチェックが終了しました。というフラグ
			ControlText cTable = cText;
			for (int y = 0; y < m_nHeight; y++) {
				IntermediateText iRow = cTable.getChildList().get(y);
				if (iRow instanceof ControlText) {
					ControlText cRow = (ControlText) iRow;
					int count = 0;
					for (int x = 0; x < m_nWidth; x++) {
						@SuppressWarnings("unused")
						ControlText cCell = (ControlText) cRow.getChildList().get(x);
						if (m_cCellInfoArray[y - 1][x].isHeader()) {
							// y = 0は、cTableのStyle情報などが入っているので、1つ飛ばしている

							// count += m_cCellInfoArray[y - 1][x].getWidth();
							count++; // セル幅を取得する必要がない？
						}
					}
					if (cRow.getChildList().size() != count) {
						exit = true; // ヘッダ行ではないものが見つかったので終了
						break;
					}
					m_nHeader++;
				}
				if (exit)
					break;
			}
		}
	}

	private void outputErrorMessage(ArrayIndexOutOfBoundsException e, ControlText cText) {
		System.out.println("【表書式警告】表の左端のセルで【左と結合】したり、上端のセルで【上と結合】したりしている可能性があります。");
		System.out.println("　DEBUG INFORMATION START (" + e + ")");
		ArrayList<IntermediateText> cRows = cText.getChildList();
		for (int y = 0; y < cRows.size(); y++) {
			IntermediateText row = cRows.get(y);
			if (row instanceof ControlText) {
				boolean bOutput = false;
				String sMESSAGE = "　==========:(1," + y + ")\n";
				ControlText cRow = (ControlText) row;
				ArrayList<IntermediateText> cCells = cRow.getChildList();
				for (int x = 0; x < cCells.size(); x++) {
					if (x != 0)
						sMESSAGE = sMESSAGE + "　-----:(" + (x + 1) + "," + y + ")\n";
					sMESSAGE = sMESSAGE + "　" + cCells.get(x).getText();
					if (y == 1 && cCells.get(x).getText().equals("【上と結合】")) {
						sMESSAGE = sMESSAGE + "←★ここでは【上と結合】できません。\n";
						bOutput = true;
					} else if (x == 0 && cCells.get(x).getText().equals("【左と結合】")) {
						sMESSAGE = sMESSAGE + "←★ここでは【左と結合】できません。\n";
						bOutput = true;
					} else {
						if (bOutput)
							sMESSAGE = sMESSAGE + "\n";
					}
				}
				if (bOutput)
					System.out.print(sMESSAGE);
			}
		}
		System.out.println("　DEBUG INFORMATION END");
	}

	private void outputErrorMessage(IndexOutOfBoundsException e, ControlText cText) {
		System.out.println("【表書式警告】表の各行のセル数が途中で変わっている可能性があります。区切り文字（=、-）の数にも注意してください。");
		System.out.println("　DEBUG INFORMATION START(" + e + ")");
		System.out.println(cText.getDebugText());
		int width_check = 0;
		ArrayList<IntermediateText> cRows = cText.getChildList();
		for (int y = 0; y < cRows.size(); y++) {
			IntermediateText row = cRows.get(y);
			if (row instanceof ControlText) {
				ControlText cRow = (ControlText) row;
				ArrayList<IntermediateText> cCells = cRow.getChildList();
				if (width_check != cCells.size()) {
					if (width_check != 0) {
						String sOutMessage = "　==========:(1," + y + ")";
						sOutMessage = sOutMessage + "［CHECK］横幅更新(" + width_check + "→" + +cCells.size() + ")";
						System.out.println(sOutMessage);

						for (int x = 0; x < cCells.size(); x++) {
							if (x != 0)
								System.out.println("　-----:(" + (x + 1) + "," + y + ")");
							System.out.println("　" + cCells.get(x).getText());
						}
					}
					width_check = cCells.size();
				}

			}
		}
		System.out.println("　DEBUG INFORMATION END");
		// e.printStackTrace();
	}

	public int getHeight() {
		return m_nHeight;
	}

	public int getWidth() {
		return m_nWidth;
	}

	public CellInfo[][] getCellSize() {
		return m_cCellInfoArray;
	}

	public int getHeaderLines() {
		return m_nHeader;
	}

	public String getStyle() {
		return m_sStyle;
	}

	public String getColumnWidth() {
		return m_sColumnWidth;
	}

	public ControlText getCTableText() {
		return m_cTableText;
	}

	public String getCaption() {
		return m_sCaption;
	}

	public boolean hasCaption() {
		return (m_sCaption != null);
	}

	public String getCellHeader(LongStyleManager lsManager, ControlText cText) throws ArrayIndexOutOfBoundsException {
		return null;
	}
}
