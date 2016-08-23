/**
 * LongStyle（【お知らせ】【箇条書き】【箇条書き】【本文】【本文】）を元に、InDesignタグ付きテキストを生成する
 * ついでに特殊文字の置換も行っています。
 */
package tx2x.indesign;

// 定数クラス
import static tx2x.Constants.MM;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tx2x.IntermediateTextTreeWalker;
import tx2x.Tx2x;
import tx2x.Tx2xOptions;
import tx2x.Utils;
import tx2x.core.ControlText;
import tx2x.core.IntermediateText;
import tx2x.core.TableManager;

public class LongStyleManagerInDesign extends tx2x.LongStyleManager {
	private static final String KOKOMADE_INDENT_CHAR = String.valueOf((char) 7); // ここまでインデント文字
	private boolean m_bMac;

	// 未定義のスタイル（dummy000）を管理するための変数
	private int m_nDummyCounter;
	private Hashtable<String, String> m_cDummyStyleHashTable;

	/**
	 * 手順表組みを制御するための変数
	 */
	private int m_nStepTableWidth = 0;

	// type = 0x1: 画面あり
	// type = 0x2: 手順数字直後に表あり
	int m_nStepTableType = 0;

	// type = 0x1のときは、画面キャプションを保持
	// type = 0x2のときは、ステップ番号を保持
	String m_sStepCaption = "";

	// ステップ番号を保持
	String m_sStepNumber = "";

	int m_nPrevStepTableWidth = 0;

	// type = 0x1: 画面あり
	// type = 0x2: 手順数字直後に表あり
	int m_nPrevStepTableType = 0;

	String m_sPrevStepCaption = "";

	LongStyleManagerInDesign(boolean bMac) {
		m_nDummyCounter = 0;
		m_cDummyStyleHashTable = new Hashtable<String, String>();
		m_bMac = bMac;
	}

	public String getInDesignStyle(IntermediateText iText, int nLsIndex, IntermediateTextTreeWalker cTreeWalker)
			throws IOException {
		setPrevLongStyle();
		String longStyle = getLongStyle();

		if (iText instanceof ControlText) {
			// ControlTextの場合は本文が無い
		} else {
			// iTextの本文を処理。【初期操作】以外の全段落共通
			String text = getEscapeText(iText.getText());
			text = text.replaceAll("\\\\<CharStyle:([^>]*)\\\\>", "<CharStyle:$1>");
			// text = text.replaceAll("\\\\<CharStyle:\\\\>", "<CharStyle:>");
			if (m_bMac) {
				// text = text.replaceAll("-", "<2013>"); // 大丈夫じゃなかった
				// text = text.replaceAll("(<[^>]+)<2013>([^>]+>)", "$1-$2"); //
				// タグの中の-は、<2013>ではダメ

				if (text.matches(".*キー】.*")) {
					Tx2x.appendWarn("不明なキーが見つかりました。：" + text);
				}

				if (!Tx2xOptions.getInstance().getBoolean("InDesignCS5")) {
					// ①～⑳
					text = text.replace("①", "★○1★");
					text = text.replace("②", "★○2★");
					text = text.replace("③", "★○3★");
					text = text.replace("④", "★○4★");
					text = text.replace("⑤", "★○5★");
					text = text.replace("⑥", "★○6★");
					text = text.replace("⑦", "★○7★");
					text = text.replace("⑧", "★○8★");
					text = text.replace("⑨", "★○9★");
					text = text.replace("⑩", "★○10★");
					text = text.replace("⑪", "★○11★");
					text = text.replace("⑫", "★○12★");
					text = text.replace("⑬", "★○13★");
					text = text.replace("⑭", "★○14★");
					text = text.replace("⑮", "★○15★");
					text = text.replace("⑯", "★○16★");
					text = text.replace("⑰", "★○17★");
					text = text.replace("⑱", "★○18★");
					text = text.replace("⑲", "★○19★");
					text = text.replace("⑳", "★○20★");
				}
			}

			{ // ※0～※999（桁数制限無し）
				Pattern pattern = Pattern.compile("★(※[0-9]*)★");
				Matcher matcher = pattern.matcher(text);
				while (matcher.find()) {
					text = text.replaceFirst("★※[0-9]*★", "<CharStyle:small-up>" + matcher.group(1) + "<CharStyle:>");
					matcher = pattern.matcher(text);
				}
			}

			// ★（R）★
			text = text.replace("★（R）★", "<CharStyle:small-up><00AE><CharStyle:>");
			// ★（C）★
			text = text.replace("★（C）★", "<cOTFContAlt:0><00A9><cOTFContAlt:>");
			// ★TM★
			text = text.replace("★TM★", "<cOTFContAlt:0><2122><cOTFContAlt:>");

			// ～
			// text = text.replace("～", "<FF5E>");
			// text = text.replace("－", "<2212>");

			text = text.replace("▼P.", "<25B6>P.");
			text = text.replace(" ▼ ", " <25B6> ");

			{ // 操作n
				Pattern pattern = Pattern.compile("操作([０-９]+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find()) {
					text = matcher.replaceFirst(
							"操作<2009><CharStyle:step\\\\_number02><cOTFContAlt:0><cOTFeatureList:nalt\\\\,7>"
									+ Utils.zenkakuNumberToHankakuNumber(matcher.group(1))
									+ "<cOTFContAlt:><cOTFeatureList:><cOTFContAlt:0><2009><cOTFContAlt:><CharStyle:>");
				}
			}

			text = text.replace("★ここまでインデント★", KOKOMADE_INDENT_CHAR);

			{
				if (text.matches("(\\\\<img src=\"[^\"]+\" ?/?\\\\>)")) {
					text = text.replaceAll("(\\\\<img src=\"[^\"]+\" ?/?\\\\>)", "★★★$1★★★");
				} else {
					text = text.replaceAll("(\\\\<img src=\"[^\"]+\")( ?/?\\\\>)", "★★★$1 inline=\"yes\"$2★★★");
				}
			}

			{
				Pattern pattern = Pattern.compile("\\\\<a href=\"([^\"]+)\"\\\\>([^<]+)\\\\</a\\\\>");
				Matcher matcher = pattern.matcher(text);
				while (matcher.find()) {
					text = text.replaceAll("\\\\<a href=\"([^\"]+)\"\\\\>([^<]+)\\\\</a\\\\>",
							"<CharStyle:Hyperlink><Hyperlink:=<HyperlinkName:" + normalizeTag(matcher.group(2))
									+ "><HyperlinkDest:" + normalizeTag(matcher.group(1))
									+ "><HyperlinkDestKey:1><CharStyleRef:ハイパーリンク><HyperlinkLength:"
									+ matcher.group(2).length()
									+ "><HyperlinkStartOffset:0><Hidden:0><BrdrVisible:0><BrdrWidth:Thin><BrdrHilight:None><BrdrStyle:Solid><BrdrColor:0\\\\,0\\\\,0>>$2<CharStyle:>");
				}
			}

			iText.setText(text);
		}

		String ret = "";

		// 標準的なチェック（それぞれ独立しているので順不同）

		if (longStyle.equals("【章】【章】【章】")) {
			iText.setText(iText.getText().replaceFirst("【章】", ""));
			return "<ParaStyle:大見出し>";
		}

		if (longStyle.equals("【■】【■】【■】")) {
			iText.setText(iText.getText().replaceFirst("■", ""));
			return "<ParaStyle:大見出し>";
		}

		if (longStyle.equals("【章サブ】【章サブ】【章サブ】")) {
			return "<ParaStyle:大見出しサブ>";
		}

		if (longStyle.equals("【節】【節】【節】")) {
			iText.setText(iText.getText().replaceFirst("【節】", ""));
			return "<ParaStyle:中見出し>";
		}

		if (longStyle.equals("【項】【項】【項】")) {
			iText.setText(iText.getText().replaceFirst("【項】", ""));
			return "<ParaStyle:小見出し>";
		}

		if (longStyle.equals("【本文】【本文】【本文】")) {
			String text = iText.getText();
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:太字>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);
			return ret + "<ParaStyle:本文>";
		}

		if (longStyle.equals("【画面】【画面】【画面】")) {
			return "<ParaStyle:画面>";
		}

		if (longStyle.matches("(【1\\.?】【1\\.?】【表】【行】【セル】)?【箇条書き[●・]】【箇条書き[●・]】【箇条書き[●・]】")) {
			String text = iText.getText();
			text = text.replaceFirst("^[●・]", "•");
			iText.setText(text);
			if (isFirstCell(cTreeWalker)) {
				return "<ParaStyle:表ヘッダー-バレット>";
			} else {
				return "<ParaStyle:バレット>";
			}
		}

		if (longStyle.equals("【箇条書き・】【箇条書き・】【本文】【本文】【本文】")) {
			return "<ParaStyle:バレット補足>";
		}

		if (longStyle.equals("【箇条書き・】【箇条書き・】【コード】【コード】")) {
			if (iText.getText().startsWith("▼")) {
				iText.setText(null);
				return "<ParaStyle:バレット補足><TableStyle:\\[Basic Table\\]><TableStart:1,1:0:0<tCellDefaultCellType:Text>><ColStart:<tColAttrWidth:471.1811023620237>><RowStart:<tRowAttrHeight:44.83464566929133>><CellStyle:\\[None\\]><StylePriority:2><CellStart:1,1<tCellFillColor:Black><tCellAttrFillTint:20>>";
			} else {
				iText.setText("");
				return "<CellEnd:><RowEnd:><TableEnd:>";
			}
		}

		if (longStyle.equals("【表】")) {
			return "<ParaStyle:本文>";
		}

		if (longStyle.equals("【表】【行】【セル：ヘッダー】【本文】【本文】【本文】")) {
			iText.setText(iText.getText().replace("【ヘッダー】", ""));
			return "<ParaStyle:表ヘッダー>";
		}

		if (longStyle.matches("【表】【行】【セル】【本文】【本文】【本文】")) {
			return "<ParaStyle:表本文>";
		}

		if (longStyle.matches("【1\\.?】【1\\.?】【表】【行】【セル】【本文】【本文】【本文】")) {
			if (isFirstCell(cTreeWalker)) {
				return "<ParaStyle:表ヘッダー>";
			} else {
				return "<ParaStyle:表本文>";
			}
		}

		if (longStyle.equals("【メモ】【メモ】")) {
			if (iText.getText().equals("▲")) {
				iText.setText("");
				return "<CellEnd:><RowEnd:><TableEnd:>";
			} else {
				iText.setText(null);

				// FIXME: 改行しないように伝えたい
				return "<ParaStyle:本文><TableStyle:メモ><TableStart:1,1:0:0<tCellDefaultCellType:Text>><ColStart:<tColAttrWidth:238.52362204733453>><RowStart:<tRowAttrHeight:167.89844595841532><tRowAttrMinRowSize:167.89844595841532>><CellStyle:メモ><StylePriority:1><CellStart:1,1>";
			}
		}

		if (longStyle.equals("【メモ】【箇条書き・】【箇条書き・】")) {
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			iText.setText(text);
			return "<ParaStyle:バレット>";
		}

		if (longStyle.equals("【メモ】【箇条書き・】【箇条書き・】【手順】【手順】")) {
			// 手順数字の置き換え
			String text = iText.getText();
			text = text.replaceFirst("^０", ""); // "０\t"は削除する
			Pattern pattern = Pattern.compile("^([０-９]+)");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				text = matcher.replaceFirst(Utils.zenkakuNumberToHankakuNumber(matcher.group(1)) + ".");
			}
			iText.setText(text);
			return "<ParaStyle:バレット補足バレット>";
		}

		if (longStyle.equals("【メモ】【箇条書き・】【箇条書き・】【手順】【手順】【本文】【本文】")) {
			return "<ParaStyle:バレット補足バレット>\t";
		}

		if (longStyle.compareTo("【メモ】【箇条書き・】【箇条書き・】【箇条書き・】【箇条書き・】") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			iText.setText(text);
			return "<ParaStyle:バレット補足バレット>";
		}

		if (longStyle.compareTo("【メモ】【箇条書き・】【箇条書き・】【箇条書き・】【箇条書き・】【本文】【本文】") == 0) {
			return "<ParaStyle:バレット補足バレット>\t";
		}

		if (longStyle.equals("【手順】【手順】") || longStyle.equals("【付録】【手順】【手順】")) {
			// 手順数字の置き換え
			String text = iText.getText();
			String sStepNumber = text.substring(0, text.indexOf("\t"));
			text = text.replaceFirst("^０", ""); // "０\t"は削除する
			Pattern pattern = Pattern.compile("^([０-９]+)");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				text = matcher.replaceFirst(Utils.zenkakuNumberToHankakuNumber(matcher.group(1)) + ".");
			}

			iText.setText(text);

			// 表組みにするか判断
			// 手順の開始

			// 手順の場合はこの行を表組みにするか検討する
			// System.out.println("この行を表組みにするか検討します。");
			m_nStepTableType = 0;
			if (iText.getText().matches(".*	【画面.*")) {
				// 画面あり
				m_nStepTableType |= 0x1;
				m_nStepTableWidth = 3;
				m_sStepCaption = iText.getText().substring(1 + iText.getText().indexOf("】《"));
				iText.setText(iText.getText().substring(0, iText.getText().indexOf("	【画面")));
			}
			if (iText.getText().equals("\t") || iText.getText().matches(
					"<CharStyle:step\\\\_number01><cOTFContAlt:0><cOTFeatureList:nalt\\\\,7>[0-9]+<cOTFContAlt:><cOTFeatureList:><CharStyle:>\\t")) {
				// "[０-９]+\\t")) {

				// 直後に表組みがある操作文
				m_nStepTableType |= 0x2;
				m_nStepTableWidth = 0;
				m_sStepCaption = iText.getText().substring(0, iText.getText().indexOf("\t"));
				if (m_sStepCaption.equals("")) {
					m_sStepCaption = " ";
				}
			}
			if (m_nStepTableType == 0) {
				m_nStepTableType = 0;
				m_nStepTableWidth = 0;
				m_sStepCaption = "";
			}

			if (m_nPrevStepTableType == 0x1) {
				// 画面あり手順の終了
				double tCellAttrTopInset = 2 * MM;
				if (m_sStepNumber.equals("１")) {
					tCellAttrTopInset = 0;
				}
				ret += "<CellEnd:><CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:" + tCellAttrTopInset
						+ "><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>><CellEnd:><CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:"
						+ tCellAttrTopInset
						+ "><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>><ParaStyle:body00>"
						+ Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:-space\\_2mm>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:table-cap01>" + m_sPrevStepCaption + "<CellEnd:><RowEnd:>";
				m_sPrevStepCaption = "";

				if (m_nStepTableType != m_nPrevStepTableType) {
					// System.out
					// .println("列数:" + m_nStepTableWidth + "の表組みにしました。");
					ret += "<TableEnd:>" + Tx2x.getTaggedTextCRLF(m_bMac);
					m_nStepTableType = 0;
				}
			}

			m_sStepNumber = sStepNumber; // 反映する（遅延）
			// 出力する
			if (m_nStepTableType == 0) {
				// System.out.println("表組みにしません。");
			} else if (m_nStepTableType == 0x1) {
				// 画面ありなだけ。
				if (m_nStepTableType != m_nPrevStepTableType) {
					// TableStart
					// その前にtBeforeSpaceを決める
					double dBeforeSpace;
					if (getPrevLongStyle().equals("【節】【節】")) {
						dBeforeSpace = 0.5 * MM;
					} else {
						dBeforeSpace = 0;
					}

					ret += "<ParaStyle:body00><TableStart:1,3:0:0<tCellDefaultCellType:Text><tBeforeSpace:"
							+ dBeforeSpace + "><tAfterSpace:0>><ColStart:<tColAttrWidth:" + 56.5 * MM
							+ ">><ColStart:<tColAttrWidth:" + 1.5 * MM + ">><ColStart:<tColAttrWidth:" + 19 * MM + ">>";
				}
				// RowStart
				double dCellAttrTopInset = 2 * MM;
				if (m_sStepNumber.equals("１")) {
					dCellAttrTopInset = 0;
				}
				ret += "<RowStart:<tRowAttrHeight:" + 2 * MM + "><tRowAttrMinRowSize:" + 2 * MM
						+ ">><CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:" + dCellAttrTopInset
						+ "><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>>";
			} else if (m_nStepTableType == 0x2) {
				iText.setText("");

				m_nPrevStepTableType = m_nStepTableType;
				m_nPrevStepTableWidth = m_nStepTableWidth;
				m_sPrevStepCaption = m_sStepCaption;
				return "";
			}

			ret += "<ParaStyle:リスト>";

			m_nPrevStepTableType = m_nStepTableType;
			m_nPrevStepTableWidth = m_nStepTableWidth;
			m_sPrevStepCaption = m_sStepCaption;
			return ret;
		}

		if (longStyle.equals("【手順】【手順】【箇条書き・】【箇条書き・】")) {
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			iText.setText(text);
			return "<ParaStyle:リスト補足バレット>";
		}

		if (longStyle.matches("【1\\.?】【1\\.?】【1\\.?】")) {
			return "<ParaStyle:手順>";
		}

		if (longStyle.compareTo("【1.】【1.】【本文】【本文】【本文】") == 0) {
			return "<ParaStyle:手順補足>";
		}

		if (longStyle.compareTo("【1.】【1.】【箇条書き・】【箇条書き・】【箇条書き・】") == 0) {
			iText.setText(iText.getText().replaceFirst("・", "•"));
			return "<ParaStyle:手順バレット>";
		}

		if (longStyle.matches("【1\\.?】【1\\.?】【表】")) {
			return "<ParaStyle:手順補足>";
		}

		if (longStyle.matches("【1\\.?】【1\\.?】【表】【行】【セル】【①】【①】【①】")) {
			return "<ParaStyle:表手順丸数字>";
		}

		if (longStyle.matches("【1\\.?】【1\\.?】【表】【行】【セル】【指】【指】【指】")) {
			iText.setText(iText.getText().replaceFirst("^【指】\t", "☞"));
			return "<ParaStyle:その他の手順-タイトル>";
		}

		if (longStyle.matches("【1\\.?】【1\\.?】【表】【行】【セル】【指】【指】【①】【①】【①】")) {
			return "<ParaStyle:その他の手順-手順>";
		}

		if (longStyle.matches("【1\\.?】【1\\.?】【表】【行】【セル】【指】【指】【箇条書き●】【箇条書き●】【箇条書き●】")) {
			return "<ParaStyle:その他の手順-補足>";
		}

		if (longStyle.equals("【Step 1】【Step 1】")) {
			String text = "<CharStyle:ナンバーステップ>" + iText.getText();
			text = text.replaceFirst("\t", "<CharStyle:>\t");
			iText.setText(text);
			return "<ParaStyle:ステップ>";
		}

		if (longStyle.matches("(【1.】【1.】)?【ヒント】【本文】【本文】【本文】") == true) {
			return "<ParaStyle:ヒント本文>";
		}

		if (longStyle.matches("(【ヒント】|【注意】)【コード】【コード】") == true) {
			if (iText.getText().startsWith("▼")) {
				iText.setText(null);
				return "<ParaStyle:本文><TableStyle:\\[Basic Table\\]><TableStart:1,1:0:0<tCellDefaultCellType:Text>><ColStart:<tColAttrWidth:473.8478740157481>><RowStart:<tRowAttrHeight:11.83464566929134>><CellStyle:\\[None\\]><StylePriority:2><CellStart:1,1<tCellFillColor:Black><tCellAttrFillTint:20>>";
			} else {
				iText.setText("");
				return "<CellEnd:><RowEnd:><TableEnd:>";
			}
		}

		if (longStyle.equals("【ヒント】【箇条書き・】【箇条書き・】【箇条書き・】")) {
			iText.setText(iText.getText().replaceFirst("・", "•"));
			return "<ParaStyle:ヒント-バレット>";
		}

		if (longStyle.equals("【注意】【本文】【本文】【本文】")) {
			return "<ParaStyle:注意本文>";
		}

		if (longStyle.equals("【HACK】【HACK】【HACK】")) {
			iText.setText(iText.getText().replaceFirst("【HACK[^】]+】", ""));
			return "<ParaStyle:本文><TableStyle:\\[Basic Table\\]><TableStart:2,2:0:0<tCellDefaultCellType:Text><tOuterLeftStrokeWeight:0><tOuterLeftStrokeType:None><tOuterRightStrokeWeight:0><tOuterRightStrokeType:None><tOuterTopStrokeWeight:0><tOuterTopStrokeType:None><tOuterBottomStrokeWeight:0><tOuterBottomStrokeType:None>><ColStart:<tColAttrWidth:44.220472440763785>><ColStart:<tColAttrWidth:437.6692913385828>><RowStart:<tRowAttrHeight:44.22047244094489><tRowAttrMinRowSize:44.22047244094489><tRowAttrMaxRowSize:566.9291338582677>><CellStyle:\\[None\\]><StylePriority:0><CellStart:1,1<tCellFillColor:C\\=80 M\\=44 Y\\=15 K\\=0><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0.7086614173228347><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:None><tcRightStrokeType:None><tcTopStrokeType:None><tcBottomStrokeType:None><tTextCellVerticalJustification:1><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>><ParaStyle:HACK><pTextAlignment:Center>HACK"
					+ Tx2x.getTaggedTextCRLF(m_bMac)
					+ "<pTextAlignment:><ParaStyle:HACK-No><pTextAlignment:Center>#1<pTextAlignment:><CellEnd:><CellStyle:\\[None\\]><StylePriority:0><CellStart:2,1<tCellAttrTopInset:2><tCellAttrBottomInset:2><tCellAttrLeftStrokeWeight:0.7086614173228347><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:None><tcRightStrokeType:None><tcTopStrokeType:None><tcBottomStrokeType:None><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper><tPageItemCellAttrLeftInset:0><tPageItemCellAttrTopInset:0><tPageItemCellAttrRightInset:0><tPageItemCellAttrBottomInset:0>><ParaStyle:HACK-見出し>";
		}

		if (longStyle.equals("【HACK】【HACK】【本文】【本文】【本文】")) {
			return "<ParaStyle:本文>";
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】【本文】") == true) {
			return "<ParaStyle:コード>";
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】【本文】【本文】【本文】") == true) {
			return "<ParaStyle:コード>    ";
		}

		if (longStyle.equals("【コード】【本文】【本文】【本文】【本文】【本文】【本文】【本文】")) {
			return "<ParaStyle:コード>        ";
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】")) {
			iText.setText(iText.getText().replaceFirst("^::", ""));
			return "<ParaStyle:用語-見出し>";
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【本文】【本文】【本文】")) {
			return "<ParaStyle:用語-本文>";
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】")) {
			iText.setText(iText.getText().replaceFirst("^::", ""));
			return "<ParaStyle:用語-見出し2>";
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【本文】【本文】【本文】")) {
			return "<ParaStyle:用語-本文2>";
		}

		if (longStyle.equals("【――】【――】【――】")) {
			iText.setText(iText.getText().replaceFirst("----\t", "――"));
			return "<ParaStyle:執筆者>";
		}

		// 以降、ダミースタイルの処理
		return dummyStyle(longStyle);// + longStyle;
		// throw new IOException("Unknown Style:" + longStyle);
	}

	private boolean isFirstCell(IntermediateTextTreeWalker cTreeWalker) {
		IntermediateText cCurrentNode = cTreeWalker.getCurrentNode();

		// cParentNodesには、cCurrentNodeから、cCurrentNodeが属するセルまでのルート（親ノードたち）を入れる
		Stack<IntermediateText> cParentNodes = new Stack<IntermediateText>();
		ControlText cParentNode = null;
		while (cParentNode == null || cParentNode.getStyle().getStyleName().equals("【Root】") == false) {
			cParentNode = cTreeWalker.parentNode();
			cParentNodes.push(cParentNode);
			if (cParentNode.getStyle().getStyleName().equals("【行】")) {
				break; // 行まで遡ったらbreak;
			}
		}
		// ここに、先頭セルだけで行う処理を記述する
		ControlText cText = (ControlText) (cParentNodes.lastElement());
		boolean bRet;
		if (cText.getChildList().get(0) == cParentNodes.get(cParentNodes.size() - 2)) {
			bRet = true;
		} else {
			bRet = false;
		}

		// ここまででm_cTreeWalkerがセルを指しているので、m_cTreeWalkerを戻す
		IntermediateText cRoute;
		do {
			if (cParentNodes.isEmpty()) {
				// この中にいるぞ!
				while (cTreeWalker.getCurrentNode() != cCurrentNode) {
					cTreeWalker.nextSibling();
				}
				break; // 発見
			}
			cRoute = cParentNodes.pop();
			while (cTreeWalker.getCurrentNode() != cRoute) {
				cTreeWalker.nextSibling();
			}
			// 辿ってきた道を発見
			cTreeWalker.firstChild();
		} while (true);

		return bRet;
	}

	private String normalizeTag(String src) {
		return src.replaceAll("([:/\\?=&%])", "\\\\\\\\$1");
	}

	private String getEscapeText(String text) {
		String ret = text;
		ret = ret.replaceAll("\\\\", "\\\\\\\\");
		ret = ret.replaceAll(">", "\\\\>");
		ret = ret.replaceAll("<", "\\\\<");
		return ret;
	}

	private String dummyStyle(String longStyle) {
		String style = m_cDummyStyleHashTable.get(longStyle);
		if (style != null) {
			return style;
		}
		DecimalFormat df = new DecimalFormat();
		df.applyLocalizedPattern("0000");
		style = "<ParaStyle:dummy" + df.format(m_nDummyCounter) + ">";
		System.out.println(longStyle + "は、" + style + "として出力されました。");
		m_cDummyStyleHashTable.put(longStyle, style);
		m_nDummyCounter++;
		return style;
	}
}
