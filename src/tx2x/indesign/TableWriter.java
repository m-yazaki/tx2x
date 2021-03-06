package tx2x.indesign;

import tx2x.Tx2x;
import tx2x.core.CellInfo;
import tx2x.core.TableManager;

import static tx2x.Constants.*; // 定数クラス

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
		m_nHeader = tManager.getHeaderLines();
		m_nCellSize = tManager.getCellSize();
	}

	public String getHeader(LongStyleManagerInDesign lsManager, int nLsIndex) {
		String longStyle = lsManager.getLongStyle();
		m_nX = 0;
		m_nY = 0;
		// 手順数字直後の表の場合は、lsManager.m_sStepCaptionに手順数字が入ってくる
		String nextStyle; // 表の後のスタイルを取得する
		for (int iNextStyle = 1;; iNextStyle++) {
			String cu = lsManager.getLongStyleFromArrayList(nLsIndex + iNextStyle);
			if (cu.lastIndexOf(longStyle) != 0) {
				nextStyle = cu;
				break;
			}
		}
		double tBeforeSpace, tAfterSpace;
		if ((longStyle.equals("【手順】【手順】【箇条書き・】【箇条書き・】【表】") == false) && (lsManager.m_sStepCaption.equals("") == false)
				&& (lsManager.m_sStepCaption.equals(" ") == false)) {
			/**
			 * 手順内の表のヘッダ処理
			 */

			// 表の前アキ
			if (lsManager.getLongStyleFromArrayList(nLsIndex - 2).equals("【手順分岐】【手順分岐】")) {
				tBeforeSpace = 1 * MM;
			} else {
				tBeforeSpace = 2 * MM;
			}

			// 表の後ろアキ
			if (nextStyle.equals("【手順】【手順】【※】【※】") || nextStyle.equals("【手順】【手順】【※0】【※0】")
					|| nextStyle.equals("【手順分岐】【手順分岐】") || nextStyle.equals("")) {
				tAfterSpace = 1 * MM;
			} else {
				tAfterSpace = 0;
			}
			String header = "<TableStart:" + m_nHeight + "," + (m_nWidth + 1) + ":" + (m_nHeader)
					+ ":0<tCellDefaultCellType:Text><tOuterLeftStrokeWeight:0.5><tCellOuterLeftStrokeColor:Black><tOuterLeftStrokeType:Solid><tOuterRightStrokeWeight:0.5><tCellOuterRightStrokeColor:Black><tOuterRightStrokeType:Solid><tOuterTopStrokeWeight:0.5><tCellOuterTopStrokeColor:Black><tOuterTopStrokeType:Solid><tOuterBottomStrokeWeight:0.5><tCellOuterBottomStrokeColor:Black><tOuterBottomStrokeType:Solid>"
					+ "<tBeforeSpace:" + tBeforeSpace + "><tAfterSpace:" + tAfterSpace
					+ "><tRowStrokePatFirstColor:Black><tRowStrokePatSecondColor:Black><tTableRowStrokePatternFirstCount:1><tTableRowStrokePatternSecondCount:1><tRowStrokePatternFirstType:Solid><tRowStrokePatternSecondType:Solid><tColStrokePatFirstColor:Black><tColStrokePatSecondColor:Black><tTableColStrokePatternFirstCount:1><tTableColStrokePatternSecondCount:1><tColStrokePatternFirstType:Solid><tColStrokePatternSecondType:Solid><tRowStrokePatternFirstWeight:0.5><tRowStrokePatternSecondWeight:0.5><tColStrokePatternFirstWeight:0.5><tColStrokePatternSecondWeight:0.5><tOuterLeftStrokeTint:100><tOuterRightStrokeTint:100><tOuterTopStrokeTint:100><tOuterBottomStrokeTint:100><tTableRowStrokePatternFirstTint:100><tTableRowStrokePatternSecondTint:100><tTableColStrokePatternFirstTint:100><tTableColStrokePatternSecondTint:100>>";
			if (m_nWidth == 1) {
				header += "<ColStart:<tColAttrWidth:" + 5 * MM + ">><ColStart:<tColAttrWidth:203.7027559055118>>";
			} else if (m_nWidth == 2) {
				header += "<ColStart:<tColAttrWidth:" + 5 * MM + ">><ColStart:<tColAttrWidth:" + 20 * MM + ">>"
						+ "<ColStart:<tColAttrWidth:147.00984251968504>>";
			} else if (m_nWidth == 3) {
				header += "<ColStart:<tColAttrWidth:" + 5 * MM + ">><ColStart:<tColAttrWidth:" + 13 * MM + ">>"
						+ "<ColStart:<tColAttrWidth:" + 13 * MM + ">>"
						+ "<ColStart:<tColAttrWidth:130.39370078740154>>";
			}
			return header;
		}
		/**
		 * 手順内の表ではない場合のヘッダ処理
		 */
		if (nextStyle.equals("【memo】【memo】")) {
			tAfterSpace = 0;
		} else {
			tAfterSpace = 1 * MM;
		}

		/* TableStart の始まり */
		String header = "<TableStyle:" + m_sStyle + "><TableStart:" + m_nHeight + "," + m_nWidth + ":" + (m_nHeader)
				+ ":0<tCellDefaultCellType:Text>";
		header += ">";
		/* TableStart の終わり */

		if (longStyle.equals("【表】")) {
			switch (m_nWidth) {
			default:
				for (int i = 0; i < m_nWidth; i++) {
					header += "<ColStart:<tColAttrWidth:" + (238.16141732283399 / m_nWidth) + ">>";
				}
			}
		} else if (longStyle.equals("【1】【1】【表】")) {
			switch (m_nWidth) {
			case 2:
				header += "<ColStart:<tColAttrWidth:119.08070866141699>>";
				header += "<ColStart:<tColAttrWidth:333.75393700769325>>";
				break;
			default:
				for (int i = 0; i < m_nWidth; i++) {
					header += "<ColStart:<tColAttrWidth:" + (238.16141732283399 / m_nWidth) + ">>";
				}
			}
		} else {
			System.out.println("セル幅が未定義の表があります。" + Tx2x.getMessageCRLF() + "　longStyle:" + longStyle
					+ Tx2x.getMessageCRLF() + "　m_nWidth:" + m_nWidth);
		}
		return header;
	}

	public String getCellHeader(LongStyleManagerInDesign lsManager) throws ArrayIndexOutOfBoundsException {
		String longStyle = lsManager.getLongStyle();
		m_nX++;
		String header;
		if (longStyle.equals("【1】【1】【表】【行】【セル】")) {
			if (m_nX == 1) {
				header = "<CellStyle:表ヘッダー><StylePriority:0><CellStart:";
			} else {
				header = "<CellStyle:表本文><StylePriority:0><CellStart:";
			}
		} else {
			header = "<CellStyle:\\[None\\]><StylePriority:0><CellStart:";
		}
		if (m_nWidth < m_nX) {
			System.out.println("内部エラー：横幅を超えた指定になっています。");
			System.out.println("m_nHeight:" + m_nHeight + ", m_nWidth:" + m_nWidth);
			System.out.println("m_nY:" + m_nY + ", m_nX:" + m_nX);
			System.out.println("indesign.txtで「★,★」を検索してください。");
			header += "★,★";
		} else if (m_nCellSize[m_nY - 1][m_nX - 1] != null) {
			header += m_nCellSize[m_nY - 1][m_nX - 1].getHeight() + "," + m_nCellSize[m_nY - 1][m_nX - 1].getWidth();
			/* 斜線の処理 */
			if (m_nCellSize[m_nY - 1][m_nX - 1].isDiagonalLine()) {
				header += "<tCellDiagnolAdornment:1>";
				if (m_nCellSize[m_nY - 1][m_nX - 1].isLeftTopLine()) {
					header += "<tCellLeftTopLine:1>";
				} else {
					header += "<tCellLeftTopLine:0>";
				}
				if (m_nCellSize[m_nY - 1][m_nX - 1].isRightTopLine()) {
					header += "<tCellRightTopLine:1>";
				} else {
					header += "<tCellRightTopLine:0>";
				}
				header += "<tCellDiagnolWeight:" + 0.1 * MM + ">";
			}
		} else {
			header += "1,1";
		}
		// 余白の処理
		// なし

		// アミの処理
		// なし

		// 罫線
		// なし

		// 1行目オフセット（1:アセント、2:キャップハイト、3:行送り、4:Xハイト、5:固定） デフォルトは3
		// なし

		// VerticalJustification
		// なし

		if (lsManager.getLongStyle().equals("【手順】【手順】【表】【行】【セル】")) {
			if (m_nX == 1) {
				header += "<tCellAttrLeftStrokeTint:100><tCellLeftStrokeOverprint:0>";
			} else {
				header += "";
			}
		}

		// 縦組版
		// なし

		if (lsManager.getLongStyle().equals("【手順】【手順】【表】【行】【セル】")) {
			if (m_nX == 1) {
				header += "<tCellLeftStrokeGapTint:100><tCellLeftStrokeGapColor:Paper>";
			} else {
				header += "";
			}
		}

		// headerを閉じる
		header += ">";
		return header;
	}

	public String getRowHeader(LongStyleManagerInDesign lsManager) {
		String rowHeader = "";
		m_nY++;
		m_nX = 0;
		rowHeader += "<RowStart:<tRowAttrHeight:10><tRowAttrMinRowSize:10><tRowAutoGrow:1><tRowKeeps:1>>";
		return rowHeader;
	}

}
