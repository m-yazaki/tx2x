package tx2x.word;

import tx2x_core.CellInfo;
import tx2x_core.TableManager;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

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
		Dispatch cell = Dispatch.call(m_oTable, "Cell", m_nY, m_nX)
				.getDispatch();
		Dispatch.call(cell, "Select");
		CellInfo[][] nCellSize = m_tManager.getCellSize();
		if (nCellSize[m_nY - 1][m_nX - 1] != null) {
			/* 斜線の処理 */
			if (nCellSize[m_nY - 1][m_nX - 1].isDiagonalLine()) {
				if (nCellSize[m_nY - 1][m_nX - 1].isLeftTopLine()) {
					// With Selection.Cells
					// With .Borders(wdBorderDiagonalDown)
					// .LineStyle = wdLineStyleSingle
					// .LineWidth = wdLineWidth050pt
					// .Color = wdColorAutomatic
					// End With
					// End With
					Dispatch oCell = Dispatch
							.call(m_oTable, "Cell", m_nY, m_nX).toDispatch();
					Dispatch oBorders = Dispatch
							.call(oCell, "Borders", -7 /* wdBorderDiagonalDown */)
							.toDispatch();
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

	public void write(Dispatch oDocument, Dispatch oSelection) {
		/*
		 * Set myTable = ActiveDocument.Tables.Add(Selection.Range, 2, 5,
		 * wdWord9TableBehavior)
		 *
		 * '文字の設定 myTable.Cell(2, 1).Select Selection.TypeText Text:="２行目の、はじめ"
		 */
		Dispatch oTables = Dispatch.call(oDocument, "Tables").toDispatch();
		Variant oRange = Dispatch.call(oSelection, "Range");
		m_oTable = Dispatch.call(oTables, "Add", oRange,
				m_tManager.getHeight(), m_tManager.getWidth()).toDispatch();
	}

}
