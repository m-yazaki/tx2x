package tx2x.word;

import tx2x.core.CellInfo;
import tx2x.core.TableManager;

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
		Dispatch oCell = Dispatch.call(m_oTable, "Cell", m_nY, m_nX)
				.getDispatch();
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

	public void write(Dispatch oSelection) {
		Dispatch oDocument = Dispatch.call(oSelection, "Document").toDispatch();
		Dispatch oTables = Dispatch.call(oDocument, "Tables").toDispatch();
		Variant oRange = Dispatch.call(oSelection, "Range");
		m_oTable = Dispatch.call(oTables, "Add", oRange,
				m_tManager.getHeight(), m_tManager.getWidth()).toDispatch();

		// 標準的な外枠
		Dispatch.call(m_oTable, "Select");
		Dispatch oBorder = Dispatch
				.call(oSelection, "Borders", -1 /* wdBorderTop */).toDispatch();
		Dispatch.put(oBorder, "LineStyle", 1 /* Options.DefaultBorderLineStyle */);
		Dispatch.put(oBorder, "LineWidth", 4 /* Options.DefaultBorderLineWidth */);
		Dispatch.put(oBorder, "Color", -16777216 /* Options.DefaultBorderColor */);
		oBorder = Dispatch.call(oSelection, "Borders", -3 /* wdBorderBottom */)
				.toDispatch();
		Dispatch.put(oBorder, "LineStyle", 1 /* Options.DefaultBorderLineStyle */);
		Dispatch.put(oBorder, "LineWidth", 4 /* Options.DefaultBorderLineWidth */);
		Dispatch.put(oBorder, "Color", -16777216 /* Options.DefaultBorderColor */);
		oBorder = Dispatch.call(oSelection, "Borders", -6 /* wdBorderVertical */)
				.toDispatch();
		Dispatch.put(oBorder, "LineStyle", 1 /* Options.DefaultBorderLineStyle */);
		Dispatch.put(oBorder, "LineWidth", 4 /* Options.DefaultBorderLineWidth */);
		Dispatch.put(oBorder, "Color", -16777216 /* Options.DefaultBorderColor */);

	}

}
