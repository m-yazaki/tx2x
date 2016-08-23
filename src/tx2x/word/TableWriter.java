package tx2x.word;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import tx2x.core.CellInfo;
import tx2x.core.TableManager;

//定数クラス
import static tx2x.Constants.MM;

/*
 * TableManagerが管理している表を、ターゲットに正しく出力することを責務とする
 */
public class TableWriter {
	/* 注目しているセル座標 */
	private int m_nX;
	private int m_nY;

	private TableManager m_tManager;

	private Dispatch m_oTable;

	TableWriter(TableManager tManager) {
		m_tManager = tManager;
		m_nX = 0;
		m_nY = 0;
	}

	public void selectNextCell() throws ArrayIndexOutOfBoundsException {
		m_nX++;
		Dispatch oCell = Dispatch.call(m_oTable, "Cell", m_nY, m_nX).getDispatch();
		Dispatch.call(oCell, "Select");
		CellInfo[][] nCellSize = m_tManager.getCellSize();
		if (nCellSize[m_nY - 1][m_nX - 1] != null) {
			/* ヘッダーかどうか */
			if (nCellSize[m_nY - 1][m_nX - 1].isHeader()) {
				// 背景：RGB(236, 240, 241)
				Dispatch oShading = Dispatch.get(oCell, "Shading").toDispatch();
				Dispatch.put(oShading, "BackgroundPatternColor", 5258796);
			}
			/* 斜線の処理 */
			if (nCellSize[m_nY - 1][m_nX - 1].isDiagonalLine()) {
				if (nCellSize[m_nY - 1][m_nX - 1].isLeftTopLine()) {
					Dispatch oBorders = Dispatch.call(oCell, "Borders", -7 /* wdBorderDiagonalDown */).toDispatch();
					Dispatch.put(oBorders, "LineStyle", 1 /* wdLineStyleSingle */);
					Dispatch.put(oBorders, "LineWidth", 4 /* wdLineWidth050pt */);
					Dispatch.put(oBorders, "Color", -16777216 /* wdColorAutomatic */);
				} else {
				}
				if (nCellSize[m_nY - 1][m_nX - 1].isRightTopLine()) {

				} else {

				}
			}
		}
	}

	public void selectNextRow() {
		m_nY++;
		m_nX = 0;
	}

	public void write(Dispatch oSelection) {
		Variant oRange = Dispatch.call(oSelection, "Range");
		m_oTable = Tx2xDispatch
				.call(oSelection, "Document.Tables.Add", oRange, m_tManager.getHeight(), m_tManager.getWidth())
				.toDispatch();

		// 標準的な外枠
		Dispatch.call(m_oTable, "Select");
		int aBorders[] = { -1 /* wdBorderTop */, -5 /* wdBorderHorizontal */, -3 /* wdBorderBottom */,
				-6 /* wdBorderVertical */ };
		for (int i = 0; i < aBorders.length; i++) {
			Dispatch oBorder = Dispatch.call(oSelection, "Borders", aBorders[i]).toDispatch();
			Dispatch.put(oBorder, "LineStyle",
					1 /* Options.DefaultBorderLineStyle */);
			Dispatch.put(oBorder, "LineWidth",
					4 /* Options.DefaultBorderLineWidth */);
			Dispatch.put(oBorder, "Color",
					-16777216 /* Options.DefaultBorderColor */);
		}
	}

	public void setIndent(LongStyleManagerWord lsManager, int nLsIndex) {
		// スタイルによってインデントが異なる
		String longStyle = lsManager.getLongStyle();
		if (longStyle.matches("【1\\.?】【1\\.?】【表】")) {
			// 1列目の幅を調節
			Dispatch oCol = Dispatch.call(m_oTable, "Columns", 1).getDispatch();
			Tx2xDispatch.put(oCol, "Width", 63.3 * MM);
			Tx2xDispatch.put(oCol, "Shading.BackgroundPatternColor", -603923969);

			// 表全体のインデントを調節
			Tx2xDispatch.put(m_oTable, "Rows.LeftIndent", 12.5 * MM);

			// 1列目の背景を変更する
		} else {
			System.out.println("【警告】表のインデントが正しいことを確認してください。");
			System.out.println(longStyle);
			System.out.println(m_tManager.getCTableText().getDebugText());
		}
	}

}
