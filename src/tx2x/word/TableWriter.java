package tx2x.word;

import com.jacob.com.Dispatch;

import tx2x.Tx2x;
import tx2x_core.CellInfo;
import tx2x_core.TableManager;

public class TableWriter {
	/* 表のスタイル */
	private String m_sStyle;

	/* 表の高さ・幅 */
	private int m_nHeight;
	private int m_nWidth;

	/* ヘッダー行の行数 */
	private int m_nHeader;

	/* 注目しているセル座標 */
	private int m_nX;
	private int m_nY;
	private CellInfo[][] m_nCellSize;

	TableWriter(TableManager tManager) {
		m_sStyle = tManager.getStyle();
		m_nHeight = tManager.getHeight();
		m_nWidth = tManager.getWidth();
		m_nHeader = tManager.getHeader();
		m_nCellSize = tManager.getCellSize();
	}

	public String getHeader(LongStyleManager lsManager, int nLsIndex) {
		String longStyle = lsManager.getLongStyle();
		m_nX = 0;
		m_nY = 0;
		// 手順数字直後の表の場合は、lsManager.m_sStepCaptionに手順数字が入ってくる
		String nextStyle; // 表の後のスタイルを取得する
		for (int iNextStyle = 1;; iNextStyle++) {
			String cu = lsManager.getLongStyleFromArrayList(nLsIndex
					+ iNextStyle);
			if (cu.lastIndexOf(longStyle) != 0) {
				nextStyle = cu;
				break;
			}
		}
		String tBeforeSpace, tAfterSpace;
		if ((longStyle.equals("【手順】【手順】【箇条書き・】【箇条書き・】【表】") == false)
				&& (lsManager.m_sStepCaption.equals("") == false)
				&& (lsManager.m_sStepCaption.equals(" ") == false)) {
			/**
			 * 手順内の表のヘッダ処理
			 */

			// 表の前アキ
			if (lsManager.getLongStyleFromArrayList(nLsIndex - 2).equals(
					"【手順分岐】【手順分岐】")) {
				tBeforeSpace = "2.834645669291339";
			} else {
				tBeforeSpace = "5.669291338582678";
			}

			// 表の後ろアキ
			if (nextStyle.equals("【手順】【手順】【※】【※】")
					|| nextStyle.equals("【手順】【手順】【※0】【※0】")
					|| nextStyle.equals("【手順分岐】【手順分岐】") || nextStyle.equals("")) {
				tAfterSpace = "2.834645669291339";
			} else {
				tAfterSpace = "0";
			}
			String header = "<TableStart:"
					+ m_nHeight
					+ ","
					+ (m_nWidth + 1)
					+ ":"
					+ (m_nHeader)
					+ ":0<tCellDefaultCellType:Text><tOuterLeftStrokeWeight:0.5><tCellOuterLeftStrokeColor:Black><tOuterLeftStrokeType:Solid><tOuterRightStrokeWeight:0.5><tCellOuterRightStrokeColor:Black><tOuterRightStrokeType:Solid><tOuterTopStrokeWeight:0.5><tCellOuterTopStrokeColor:Black><tOuterTopStrokeType:Solid><tOuterBottomStrokeWeight:0.5><tCellOuterBottomStrokeColor:Black><tOuterBottomStrokeType:Solid>"
					+ "<tBeforeSpace:"
					+ tBeforeSpace
					+ "><tAfterSpace:"
					+ tAfterSpace
					+ "><tRowStrokePatFirstColor:Black><tRowStrokePatSecondColor:Black><tTableRowStrokePatternFirstCount:1><tTableRowStrokePatternSecondCount:1><tRowStrokePatternFirstType:Solid><tRowStrokePatternSecondType:Solid><tColStrokePatFirstColor:Black><tColStrokePatSecondColor:Black><tTableColStrokePatternFirstCount:1><tTableColStrokePatternSecondCount:1><tColStrokePatternFirstType:Solid><tColStrokePatternSecondType:Solid><tRowStrokePatternFirstWeight:0.5><tRowStrokePatternSecondWeight:0.5><tColStrokePatternFirstWeight:0.5><tColStrokePatternSecondWeight:0.5><tOuterLeftStrokeTint:100><tOuterRightStrokeTint:100><tOuterTopStrokeTint:100><tOuterBottomStrokeTint:100><tTableRowStrokePatternFirstTint:100><tTableRowStrokePatternSecondTint:100><tTableColStrokePatternFirstTint:100><tTableColStrokePatternSecondTint:100>>";
			if (m_nWidth == 1) {
				header += "<ColStart:<tColAttrWidth:14.173228346456694>>"
						+ "<ColStart:<tColAttrWidth:203.7027559055118>>";
			} else if (m_nWidth == 2) {
				header += "<ColStart:<tColAttrWidth:14.173228346456694>>"
						+ "<ColStart:<tColAttrWidth:56.69291338582676>>"
						+ "<ColStart:<tColAttrWidth:147.00984251968504>>";
			} else if (m_nWidth == 3) {
				header += "<ColStart:<tColAttrWidth:14.173228346456694>>"
						+ "<ColStart:<tColAttrWidth:36.85039370078741>>"
						+ "<ColStart:<tColAttrWidth:36.85039370078741>>"
						+ "<ColStart:<tColAttrWidth:130.39370078740154>>";
			}
			return header;
		}
		/**
		 * 手順内の表ではない場合のヘッダ処理
		 */
		if (nextStyle.equals("【memo】【memo】")) {
			tAfterSpace = "0";
		} else {
			tAfterSpace = "2.834645669291339";
		}

		/* TableStart の始まり */
		String header = "<TableStyle:" + m_sStyle + "><TableStart:" + m_nHeight
				+ "," + m_nWidth + ":" + (m_nHeader)
				+ ":0<tCellDefaultCellType:Text>";
		header += ">";
		/* TableStart の終わり */

		if (longStyle.equals("【表】")) {
			switch (m_nWidth) {
			default:
				for (int i = 0; i < m_nWidth; i++) {
					header += "<ColStart:<tColAttrWidth:"
							+ (238.16141732283399 / m_nWidth) + ">>";
				}
			}
		} else {
			System.out.println("セル幅が未定義の表があります。" + Tx2x.getMessageCRLF()
					+ "　longStyle:" + longStyle + Tx2x.getMessageCRLF()
					+ "　m_nWidth:" + m_nWidth);
		}
		return header;
	}

	public void selectNextCell(Dispatch oTable)
			throws ArrayIndexOutOfBoundsException {
		m_nX++;
		Dispatch cell = Dispatch.call(oTable, "Cell", m_nY, m_nX).getDispatch();
		Dispatch.call(cell, "Select");
		if (m_nCellSize[m_nY - 1][m_nX - 1] != null) {
			/* 斜線の処理 */
			if (m_nCellSize[m_nY - 1][m_nX - 1].isDiagonalLine()) {
				if (m_nCellSize[m_nY - 1][m_nX - 1].isLeftTopLine()) {
					// With Selection.Cells
					// With .Borders(wdBorderDiagonalDown)
					// .LineStyle = wdLineStyleSingle
					// .LineWidth = wdLineWidth050pt
					// .Color = wdColorAutomatic
					// End With
					// End With
					Dispatch oCell = Dispatch.call(oTable, "Cell", m_nY, m_nX)
							.toDispatch();
					Dispatch oBorders = Dispatch
							.call(oCell, "Borders", -7 /* wdBorderDiagonalDown */)
							.toDispatch();
					Dispatch.put(oBorders, "LineStyle", 1 /* wdLineStyleSingle */);
					Dispatch.put(oBorders, "LineWidth", 4 /* wdLineWidth050pt */);
					Dispatch.put(oBorders, "Color", -16777216 /* wdColorAutomatic */);
				} else {
				}
				if (m_nCellSize[m_nY - 1][m_nX - 1].isRightTopLine()) {
				} else {
				}
			}
		}
	}

	public void selectNextRow(Dispatch oTable) {
		m_nY++;
		m_nX = 0;
	}

}
