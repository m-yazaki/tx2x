package tx2x;

import tx2x_core.CellInfo;
import tx2x_core.TableManager;

public class TableWriter {
	/* •\‚ÌƒXƒ^ƒCƒ‹ */
	private String m_sStyle;

	/* •\‚Ì‚‚³E• */
	private int m_nHeight;
	private int m_nWidth;

	/* ƒwƒbƒ_[s‚Ìs” */
	private int m_nHeader;

	/* ’–Ú‚µ‚Ä‚¢‚éƒZƒ‹À•W */
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
		// è‡”š’¼Œã‚Ì•\‚Ìê‡‚ÍAlsManager.m_sStepCaption‚Éè‡”š‚ª“ü‚Á‚Ä‚­‚é
		String nextStyle; // •\‚ÌŒã‚ÌƒXƒ^ƒCƒ‹‚ğæ“¾‚·‚é
		for (int iNextStyle = 1;; iNextStyle++) {
			String cu = lsManager.getLongStyleFromArrayList(nLsIndex
					+ iNextStyle);
			if (cu.lastIndexOf(longStyle) != 0) {
				nextStyle = cu;
				break;
			}
		}
		String tBeforeSpace, tAfterSpace;
		if ((longStyle.equals("yè‡zyè‡zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy•\z") == false)
				&& (lsManager.m_sStepCaption.equals("") == false)
				&& (lsManager.m_sStepCaption.equals(" ") == false)) {
			/**
			 * è‡“à‚Ì•\‚Ìƒwƒbƒ_ˆ—
			 */

			// •\‚Ì‘OƒAƒL
			if (lsManager.getLongStyleFromArrayList(nLsIndex - 2).equals(
					"yè‡•ªŠòzyè‡•ªŠòz")) {
				tBeforeSpace = "2.834645669291339";
			} else {
				tBeforeSpace = "5.669291338582678";
			}

			// •\‚ÌŒã‚ëƒAƒL
			if (nextStyle.equals("yè‡zyè‡zy¦zy¦z")
					|| nextStyle.equals("yè‡zyè‡zy¦0zy¦0z")
					|| nextStyle.equals("yè‡•ªŠòzyè‡•ªŠòz") || nextStyle.equals("")) {
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
		 * è‡“à‚Ì•\‚Å‚Í‚È‚¢ê‡‚Ìƒwƒbƒ_ˆ—
		 */
		if (nextStyle.equals("ymemozymemoz")) {
			tAfterSpace = "0";
		} else {
			tAfterSpace = "2.834645669291339";
		}

		/* TableStart ‚Ìn‚Ü‚è */
		String header = "<TableStyle:" + m_sStyle + "><TableStart:" + m_nHeight
				+ "," + m_nWidth + ":" + (m_nHeader)
				+ ":0<tCellDefaultCellType:Text>";
		header += ">";
		/* TableStart ‚ÌI‚í‚è */

		if (longStyle.equals("y•\z")) {
			switch (m_nWidth) {
			default:
				for (int i = 0; i < m_nWidth; i++) {
					header += "<ColStart:<tColAttrWidth:"
							+ (238.16141732283399 / m_nWidth) + ">>";
				}
			}
		} else {
			System.out.println("ƒZƒ‹•‚ª–¢’è‹`‚Ì•\‚ª‚ ‚è‚Ü‚·B\n@longStyle:" + longStyle
					+ "\n@m_nWidth:" + m_nWidth);
		}
		return header;
	}

	public String getCellHeader(LongStyleManager lsManager)
			throws ArrayIndexOutOfBoundsException {
		m_nX++;
		String header = "<CellStyle:\\[None\\]><StylePriority:0><CellStart:";

		if (m_nWidth < m_nX) {
			System.out.println("“à•”ƒGƒ‰[F‰¡•‚ğ’´‚¦‚½w’è‚É‚È‚Á‚Ä‚¢‚Ü‚·B");
			System.out.println("m_nHeight:" + m_nHeight + ", m_nWidth:"
					+ m_nWidth);
			System.out.println("m_nY:" + m_nY + ", m_nX:" + m_nX);
			System.out.println("indesign.txt‚Åuš,šv‚ğŒŸõ‚µ‚Ä‚­‚¾‚³‚¢B");
			header += "š,š";
		} else if (m_nCellSize[m_nY - 1][m_nX - 1] != null) {
			header += m_nCellSize[m_nY - 1][m_nX - 1].getHeight() + ","
					+ m_nCellSize[m_nY - 1][m_nX - 1].getWidth();
			/* Îü‚Ìˆ— */
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
				header += "<tCellDiagnolWeight:0.28346456692913385>";
			}
		} else {
			header += "1,1";
		}
		// —]”’‚Ìˆ—
		// ‚È‚µ

		// ƒAƒ~‚Ìˆ—
		// ‚È‚µ

		// Œrü
		// ‚È‚µ

		// 1s–ÚƒIƒtƒZƒbƒgi1:ƒAƒZƒ“ƒgA2:ƒLƒƒƒbƒvƒnƒCƒgA3:s‘—‚èA4:XƒnƒCƒgA5:ŒÅ’èj@ƒfƒtƒHƒ‹ƒg‚Í3
		// ‚È‚µ

		// VerticalJustification
		// ‚È‚µ

		if (lsManager.getLongStyle().equals("yè‡zyè‡zy•\zyszyƒZƒ‹z")) {
			if (m_nX == 1) {
				header += "<tCellAttrLeftStrokeTint:100><tCellLeftStrokeOverprint:0>";
			} else {
				header += "";
			}
		}

		// c‘g”Å
		// ‚È‚µ

		if (lsManager.getLongStyle().equals("yè‡zyè‡zy•\zyszyƒZƒ‹z")) {
			if (m_nX == 1) {
				header += "<tCellLeftStrokeGapTint:100><tCellLeftStrokeGapColor:Paper>";
			} else {
				header += "";
			}
		}

		// header‚ğ•Â‚¶‚é
		header += ">";
		return header;
	}

	public String getRowHeader(LongStyleManager lsManager) {
		String rowHeader = "";
		m_nY++;
		m_nX = 0;
		rowHeader += "<RowStart:<tRowAttrHeight:10><tRowAttrMinRowSize:10><tRowAutoGrow:1><tRowKeeps:1>>";
		String longStyle = lsManager.getLongStyle();
		if ((longStyle.equals("yè‡zyè‡zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy•\zysz") == false)
				&& (lsManager.m_sStepCaption.equals("") == false)
				&& (lsManager.m_sStepCaption.equals(" ") == false)) {
			rowHeader += "<CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:0><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellFillColor:None><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0.5><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tTextCellFirstLineOffset:3><tTextCellVerticalJustification:0><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tTextCellVerticalComposition:1><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>>";
			if (m_nY == 1) {
				rowHeader += "<ParaStyle:step-title01>"
						+ lsManager.m_sStepCaption;
			}
			rowHeader += "<CellEnd:>";
		}
		return rowHeader;
	}

}
