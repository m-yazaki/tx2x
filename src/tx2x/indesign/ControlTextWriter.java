package tx2x.indesign;

import static tx2x.Constants.MM;

import java.io.IOException;

import tx2x.Tx2xOptions;
import tx2x.core.ControlText;

/*
 * ControlTextをWordデータに書き出す
 */
public class ControlTextWriter {
	static private boolean m_bMac = Tx2xOptions.getInstance().getBoolean("mac");

	public void writeBigBlockOpenInfo(InDesignTT_FileWriter fwInDesign, LongStyleManagerInDesign lsManager,
			ControlText cText) throws IOException {
		String longStyle = lsManager.getLongStyle();
		if (longStyle.equals("【ヒント】")) {
			writeHintParts(fwInDesign, "本文", 480.8897637793466);
			return;
		} else if (longStyle.equals("【1.】【1.】【ヒント】")) {
			writeHintParts(fwInDesign, "手順補足", 452.54330708643306);
			return;
		} else if (longStyle.equals("【注意】")) {
			writeNoteParts(fwInDesign, 0, 136.9 * MM);
			return;
		} else if (longStyle.equals("【HACK】")) {
			return;
		} else if (longStyle.equals("【コード】")) {
			writeCodeParts(fwInDesign, "本文", 481.1811023620236, "Black");
			return;
		} else if (longStyle.equals("【ヒント】【コード】")) {
			writeCodeParts(fwInDesign, "本文", 473.8478740157481, "Black");
			return;
		} else if (longStyle.equals("【1.】【1.】【コード】")) {
			writeCodeParts(fwInDesign, "手順補足", 452.83464566911033, "Black");
			return;
		} else if (longStyle.equals("【箇条書き・】【箇条書き・】【コード】")) {
			writeCodeParts(fwInDesign, "バレット補足", 471.1811023620237, "Black");
			return;
		} else if (longStyle.equals("【注意】【コード】")) {
			writeCodeParts(fwInDesign, "本文", 473.8478740157481, "C\\=21 M\\=79 Y\\=100 K\\=0");
			return;
		}
		return;
	}

	private void writeCodeParts(InDesignTT_FileWriter fwInDesign, String sStyle, double dWidth, String sColor)
			throws IOException {
		fwInDesign.write("<ParaStyle:" + sStyle
				+ "><TableStyle:\\[Basic Table\\]><TableStart:1,1:0:0<tCellDefaultCellType:Text>><ColStart:<tColAttrWidth:"
				+ dWidth
				+ ">><RowStart:<tRowAttrHeight:22.83464566929134>><CellStyle:\\[None\\]><StylePriority:0><CellStart:1,1<tCellFillColor:"
				+ sColor + "><tCellAttrFillTint:20>>", false, m_bMac);
	}

	public void writeSmallBlockOpenInfo() {
		return;
	}

	public void writeLineOpenInfo() {
		return;
	}

	public void writeLine() {
		return;
	}

	public void writeLineCloseInfo() {
		return;
	}

	public void writeSmallBlockCloseInfo() {
		return;
	}

	public void writeBigBlockCloseInfo(InDesignTT_FileWriter fwInDesign, LongStyleManagerInDesign lsManager,
			ControlText cText) throws IOException {
		String longStyle = lsManager.getLongStyle();
		if (longStyle.equals("【ヒント】") || longStyle.equals("【1.】【1.】【ヒント】") || longStyle.equals("【注意】")
				|| longStyle.equals("【コード】") || longStyle.equals("【ヒント】【コード】") || longStyle.equals("【注意】【コード】")
				|| longStyle.equals("【1.】【1.】【コード】") || longStyle.equals("【箇条書き・】【箇条書き・】【コード】")) {
			fwInDesign.write("<CellEnd:><RowEnd:><TableEnd:>", true, m_bMac);
		}

		// 直前が【HACK】【HACK】【本文】【本文】で、現在が【HACK】【HACK】【で始まらない場合
		if (longStyle.equals("【HACK】【HACK】【本文】【本文】")) {
			fwInDesign.write(
					"<CellEnd:><RowEnd:><RowStart:<tRowAttrHeight:3><tRowAttrMinRowSize:3><tRowAttrMaxRowSize:566.9291338582677>><CellStyle:\\[None\\]><StylePriority:0><CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:0><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellFillColor:None><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0.7086614173228347><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:None><tcRightStrokeType:None><tcTopStrokeType:None><tcBottomStrokeType:None><tTextCellVerticalJustification:1><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>><ParaStyle:最小段落> <CellEnd:><CellStyle:\\[None\\]><StylePriority:0><CellStart:1,1<tCellAttrTopInset:2><tCellAttrBottomInset:2><tCellAttrLeftStrokeWeight:0.7086614173228347><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:None><tcRightStrokeType:None><tcTopStrokeType:None><tcBottomStrokeType:None><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper><tPageItemCellAttrLeftInset:0><tPageItemCellAttrTopInset:0><tPageItemCellAttrRightInset:0><tPageItemCellAttrBottomInset:0>><CellEnd:><RowEnd:><TableEnd:>",
					true, m_bMac);
		}

	}

	private void writeHintParts(InDesignTT_FileWriter fwInDesign, String sStyle, double dLeftWidth) throws IOException {
		fwInDesign.write(
				"<ParaStyle:" + sStyle
						+ "><TableStyle:\\[Basic Table\\]><TableStart:2,1:0:0<tCellDefaultCellType:Text><tOuterLeftStrokeWeight:1><tCellOuterLeftStrokeColor:C\\=80 M\\=44 Y\\=15 K\\=0><tOuterRightStrokeWeight:1><tCellOuterRightStrokeColor:C\\=80 M\\=44 Y\\=15 K\\=0><tOuterTopStrokeWeight:1><tCellOuterTopStrokeColor:C\\=80 M\\=44 Y\\=15 K\\=0><tOuterBottomStrokeWeight:1><tCellOuterBottomStrokeColor:C\\=80 M\\=44 Y\\=15 K\\=0><tOuterLeftStrokeTint:50><tOuterRightStrokeTint:50><tOuterTopStrokeTint:50><tOuterBottomStrokeTint:50>><ColStart:<tColAttrWidth:"
						+ dLeftWidth
						+ ">><RowStart:<tRowAttrHeight:12.015625><tRowAttrMinRowSize:9>><CellStyle:\\[None\\]><StylePriority:1><CellStart:1,1<tCellAttrLeftInset:2.834645669291339><tCellAttrTopInset:2><tCellAttrRightInset:2.834645669291339><tCellAttrBottomInset:2><tCellFillColor:C\\=80 M\\=44 Y\\=15 K\\=0><tCellAttrFillTint:50><tCellAttrBottomStrokeWeight:0><tCellBottomStrokeColor:Black><tcBottomStrokeType:Solid><tTextCellFirstLineOffset:1><tCellAttrBottomStrokeTint:100><tCellBottomStrokeOverprint:0><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:None><tCellRightStrokeGapColor:None><tCellTopStrokeGapColor:None><tCellBottomStrokeGapColor:None><tCellBottomStrokeGapOverprint:0>><ParaStyle:本文>ヒント<CellEnd:><RowEnd:><RowStart:<tRowAttrHeight:25.015625><tRowAttrMinRowSize:9>><CellStyle:\\[None\\]><StylePriority:1><CellStart:1,1<tCellAttrLeftInset:2.834645669291339><tCellAttrTopInset:4><tCellAttrRightInset:2.834645669291339><tCellAttrBottomInset:2><tCellAttrTopStrokeWeight:0><tCellTopStrokeColor:Black><tcTopStrokeType:Solid><tTextCellFirstLineOffset:1><tCellAttrTopStrokeTint:100><tCellTopStrokeOverprint:0><tCellTopStrokeGapTint:100><tCellLeftStrokeGapColor:None><tCellRightStrokeGapColor:None><tCellTopStrokeGapColor:None><tCellBottomStrokeGapColor:None><tCellTopStrokeGapOverprint:0>>",
				false, m_bMac);
	}

	private void writeNoteParts(InDesignTT_FileWriter fwInDesign, double dLeftIndent, double dBodyWidth)
			throws IOException {
		fwInDesign.write(
				"<ParaStyle:><cSize:9.000000><cFont:メイリオ><TableStyle:\\[Basic Table\\]><TableStart:2,1:0:0<tCellDefaultCellType:Text><tOuterLeftStrokeWeight:1><tCellOuterLeftStrokeColor:C\\=80 M\\=44 Y\\=15 K\\=0><tOuterRightStrokeWeight:1><tCellOuterRightStrokeColor:C\\=80 M\\=44 Y\\=15 K\\=0><tOuterTopStrokeWeight:1><tCellOuterTopStrokeColor:C\\=80 M\\=44 Y\\=15 K\\=0><tOuterBottomStrokeWeight:1><tCellOuterBottomStrokeColor:C\\=80 M\\=44 Y\\=15 K\\=0><tOuterLeftStrokeTint:50><tOuterRightStrokeTint:50><tOuterTopStrokeTint:50><tOuterBottomStrokeTint:50>><ColStart:<tColAttrWidth:480.8897637793466>><RowStart:<tRowAttrHeight:12.015625><tRowAttrMinRowSize:9>><CellStyle:\\[None\\]><StylePriority:1><CellStart:1,1<tCellAttrLeftInset:2.834645669291339><tCellAttrTopInset:2><tCellAttrRightInset:2.834645669291339><tCellAttrBottomInset:2><tCellFillColor:C\\=21 M\\=79 Y\\=100 K\\=0><tCellAttrFillTint:50><tCellAttrLeftStrokeWeight:1><tCellAttrRightStrokeWeight:1><tCellAttrTopStrokeWeight:1><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:C\\=21 M\\=79 Y\\=100 K\\=0><tCellTopStrokeColor:C\\=21 M\\=79 Y\\=100 K\\=0><tCellRightStrokeColor:C\\=21 M\\=79 Y\\=100 K\\=0><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tTextCellFirstLineOffset:1><tCellAttrLeftStrokeTint:50><tCellAttrRightStrokeTint:50><tCellAttrTopStrokeTint:50><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:None><tCellRightStrokeGapColor:None><tCellTopStrokeGapColor:None><tCellBottomStrokeGapColor:None><tCellLeftStrokeGapOverprint:0><tCellRightStrokeGapOverprint:0><tCellTopStrokeGapOverprint:0><tCellBottomStrokeGapOverprint:0>><ParaStyle:本文>注意<CellEnd:><RowEnd:><RowStart:<tRowAttrHeight:25.015625><tRowAttrMinRowSize:9>><CellStyle:\\[None\\]><StylePriority:1><CellStart:1,1<tCellAttrLeftInset:2.834645669291339><tCellAttrTopInset:4><tCellAttrRightInset:2.834645669291339><tCellAttrBottomInset:2><tCellAttrLeftStrokeWeight:1><tCellAttrRightStrokeWeight:1><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:1><tCellLeftStrokeColor:C\\=21 M\\=79 Y\\=100 K\\=0><tCellTopStrokeColor:Black><tCellRightStrokeColor:C\\=21 M\\=79 Y\\=100 K\\=0><tCellBottomStrokeColor:C\\=21 M\\=79 Y\\=100 K\\=0><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tTextCellFirstLineOffset:1><tCellAttrLeftStrokeTint:50><tCellAttrRightStrokeTint:50><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:50><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:None><tCellRightStrokeGapColor:None><tCellTopStrokeGapColor:None><tCellBottomStrokeGapColor:None><tCellLeftStrokeGapOverprint:0><tCellRightStrokeGapOverprint:0><tCellTopStrokeGapOverprint:0><tCellBottomStrokeGapOverprint:0>>",
				false, m_bMac);
	}
}
