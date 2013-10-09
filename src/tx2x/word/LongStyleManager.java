/**
 * LongStyle（【お知らせ】【箇条書き】【箇条書き】）を元に、InDesignタグ付きテキストを生成する
 * ついでに特殊文字の置換も行っています。
 */
package tx2x.word;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tx2x.Tx2xOptions;
import tx2x_core.IntermediateText;
import tx2x_core.Style;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class LongStyleManager {
	LinkedList<Style> m_StyleLinkedList; // スタイル情報をpush/popする
	String m_sPrevLongStyle; // 直前の長いスタイル名

	// 未定義のスタイル（dummy000）を管理するための変数
	private Hashtable<String, String> m_cDummyStyleHashTable;

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

	private ArrayList<String> m_cLongStyleArrayList;

	LongStyleManager(Dispatch oSelection) {
		m_StyleLinkedList = new LinkedList<Style>();
		m_sPrevLongStyle = "";
		m_cDummyStyleHashTable = new Hashtable<String, String>();
		m_cLongStyleArrayList = new ArrayList<String>();
	}

	public String getTargetStyle(IntermediateText iText, int nLsIndex)
			throws IOException {
		String longStyle = getLongStyle();
		if (iText.getStyle() == null) {
			longStyle += "【本文】";
		}

		// iTextの本文を処理。【初期操作】以外の全段落共通
		{
			String text = iText.getText();
			iText.setText(text);
		}

		// 標準的なチェック（それぞれ独立しているので順不同）

		if (longStyle.equals("【章】【章】")) {
			m_sPrevLongStyle = longStyle;
			return "見出し 1";
		}

		if (longStyle.equals("【章サブ】【章サブ】")) {
			m_sPrevLongStyle = longStyle;
			return "見出し 1サブ";
		}

		if (longStyle.equals("【HACK】【HACK】")) {
			m_sPrevLongStyle = longStyle;
			return "HACK";
		}

		if (longStyle.equals("【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "本文";
		}

		if (longStyle.equals("【箇条書き・】【箇条書き・】")) {
			m_sPrevLongStyle = longStyle;
			iText.setText(iText.getText().replaceFirst("・\t", ""));
			return "箇条書き";
		}

		if (longStyle.equals("【表】")) {
			m_sPrevLongStyle = longStyle;
			return "本文";
		}

		if (longStyle.equals("【表】【行】【セル】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "本文";
		}

		if (longStyle.equals("【ヒント】【ヒント】")) {
			m_sPrevLongStyle = longStyle;
			if (iText.getText().equals("▼ヒント")) {
				return "ヒント-ヘッダー";
			} else {
				return "ヒント-フッター";
			}
		}

		if (longStyle.equals("【ヒント】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "ヒント-本文";
		}

		// 以降、ダミースタイルの処理
		m_sPrevLongStyle = longStyle;
		String style = m_cDummyStyleHashTable.get(m_sPrevLongStyle);
		if (style != null) {
			return style;
		}
		DecimalFormat df = new DecimalFormat();
		df.applyLocalizedPattern("0000");
		style = "標準";
		iText.setText("■未定義スタイル:" + m_sPrevLongStyle + iText.getText());
		System.out.println(m_sPrevLongStyle + "は、" + style + "スタイルで出力されました。");
		m_cDummyStyleHashTable.put(m_sPrevLongStyle, style);
		return style;
	}

	public void addStyle(Style style) {
		m_StyleLinkedList.add(style);
	}

	public void removeLastStyle() {
		m_StyleLinkedList.removeLast();
	}

	public String getLongStyle() {
		String longStyle = "";
		Iterator<Style> it2 = m_StyleLinkedList.iterator();

		// longStyleの取得
		while (it2.hasNext()) {
			Style r2 = it2.next();
			if (r2 == null) {
				longStyle += "【本文】";
			} else {
				longStyle += r2.getStyleName();
			}
		}
		return longStyle;
	}

	public void setPrevLongStyle(String prevLongStyle) {
		m_sPrevLongStyle = prevLongStyle;
	}

	public void addLongStyleToArrayList() {
		m_cLongStyleArrayList.add(getLongStyle());
	}

	public String getLongStyleFromArrayList(int nLsIndex) {
		if (nLsIndex == m_cLongStyleArrayList.size()) {
			return ""; // スタイルなし
		}
		return m_cLongStyleArrayList.get(nLsIndex);
	}

	public String getPrevLongStyle() {
		return m_sPrevLongStyle;
	}

	public static String zenkakuNumberToHankakuNumber(String s) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c >= '０' && c <= '９') {
				sb.setCharAt(i, (char) (c - '０' + '0'));
			}
		}
		return sb.toString();
	}

	/*
	 * Wordデータを作成するセクション
	 */
	public void writeTargetData(Dispatch oSelection, String sWordStyle,
			IntermediateText iText, int nLsIndex) {
		String longStyle = getLongStyle();
		if (iText.getStyle() == null) {
			longStyle += "【本文】";
		}

		// 特別な処理が必要な場合
		if (longStyle.equals("【HACK】【HACK】")) {
			Dispatch oDocument = Dispatch.call(oSelection, "Document")
					.toDispatch();
			Dispatch oTables = Dispatch.call(oDocument, "Tables").toDispatch();
			Variant oRange = Dispatch.call(oSelection, "Range");
			Dispatch oTable = Dispatch.call(oTables, "Add", oRange, 1, 2)
					.toDispatch();

			// ヒントアイコン
			Dispatch oInlineShapes = Dispatch.get(oSelection, "InlineShapes")
					.toDispatch();
			Dispatch oInlineShape = Dispatch.call(
					oInlineShapes,
					"AddPicture",
					Tx2xOptions.getInstance().getString("tx2x_folder_name")
							+ "\\hack.png", "False", "True").toDispatch();
			Dispatch oShape = Dispatch.call(oInlineShape, "ConvertToShape")
					.toDispatch();
			Dispatch.call(oShape, "ZOrder", 5 /* msoSendBehindText */);
			Dispatch.call(oShape, "IncrementTop", -5.669291 /*
															 * MillimetersToPoints(
															 * 2)
															 */); // 2mm 上へ移動
			Dispatch.call(oShape, "IncrementLeft", -5.669291 /*
															 * MillimetersToPoints(
															 * 2)
															 */); // 2mm 左へ移動

			Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1).getDispatch();
			Dispatch.put(oCell, "PreferredWidthType", 3 /* wdPreferredWidthPoints */);
			Dispatch.put(oCell, "PreferredWidth", 42.51968 /*
															 * MillimetersToPoints
															 * (15)
															 */);
			// Dispatch.call(oCell, "Select");

			Dispatch.call(oSelection, "MoveStart", 12 /* wdCell */, 0); // 左へ移動
			Dispatch.put(oSelection, "Text", "HACK");
			Dispatch.put(oSelection, "Style", "HACK");
			Dispatch.call(oSelection, "MoveRight"); // 右へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			Pattern p = Pattern.compile("【HACK (.*)】(.*)");
			Matcher matcher = p.matcher(iText.getText());
			matcher.find();
			Dispatch.put(oSelection, "Text", matcher.group(1));
			Dispatch.put(oSelection, "Style", "HACK-No");

			// HACKリード文のエリア
			oCell = Dispatch.call(oTable, "Cell", 1, 2).getDispatch();
			Dispatch.put(oCell, "PreferredWidthType", 3 /* wdPreferredWidthPoints */);
			Dispatch.put(oCell, "PreferredWidth", 388.063 /*
														 * MillimetersToPoints
														 * (xx)
														 */);
			Dispatch.call(oCell, "Select");
			Dispatch.call(oSelection, "MoveLeft"); // 左へ移動（カーソルを立てる）
			Dispatch.put(oSelection, "Text", matcher.group(2));
			Dispatch.put(oSelection, "Style", "HACK-見出し");
			Dispatch.call(oSelection, "MoveRight"); // 右へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行

			return;
		} else if (longStyle.equals("【ヒント】【ヒント】")) {
			// ActiveDocument.Tables.Add Range:=Selection.Range, NumRows:=1,
			// NumColumns:= 2, DefaultTableBehavior:=wdWord9TableBehavior,
			// AutoFitBehavior:= wdAutoFitFixed
			// With Selection.Tables(1)
			// .ApplyStyleHeadingRows = True
			// .ApplyStyleLastRow = False
			// .ApplyStyleFirstColumn = True
			// .ApplyStyleLastColumn = False
			// .ApplyStyleRowBands = True
			// .ApplyStyleColumnBands = False
			// End With
			// Selection.MoveRight Unit:=wdCharacter, Count:=1, Extend:=wdExtend
			// Selection.Cells.PreferredWidthType = wdPreferredWidthPoints
			// Selection.Cells.PreferredWidth = MillimetersToPoints(15)
			// Selection.EscapeKey
			// Selection.Style = ActiveDocument.Styles("ヒント（枠）")
			// Selection.InlineShapes.AddPicture
			// FileName:="D:\test-text\hint.png", _
			// LinkToFile:=False, SaveWithDocument:=True
			// Selection.TypeParagraph
			// Selection.TypeBackspace
			// Selection.MoveRight Unit:=wdCell
			if (iText.getText().equals("▼ヒント")) {
				Dispatch oDocument = Dispatch.call(oSelection, "Document")
						.toDispatch();
				Dispatch oTables = Dispatch.call(oDocument, "Tables")
						.toDispatch();
				Variant oRange = Dispatch.call(oSelection, "Range");
				Dispatch oTable = Dispatch.call(oTables, "Add", oRange, 1, 2)
						.toDispatch();

				// ヒントアイコン
				Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1)
						.getDispatch();
				Dispatch.put(oCell, "PreferredWidthType", 3 /* wdPreferredWidthPoints */);
				Dispatch.put(oCell, "PreferredWidth", 42.51968 /*
																 * MillimetersToPoints
																 * (15)
																 */);
				Dispatch.call(oCell, "Select");
				Dispatch oInlineShapes = Dispatch.get(oSelection,
						"InlineShapes").toDispatch();
				Dispatch.call(oInlineShapes, "AddPicture", Tx2xOptions
						.getInstance().getString("tx2x_folder_name")
						+ "\\hint.png", "False", "True");

				// ヒント本文
				oCell = Dispatch.call(oTable, "Cell", 1, 2).getDispatch();
				Dispatch.put(oCell, "PreferredWidthType", 3 /* wdPreferredWidthPoints */);
				Dispatch.put(oCell, "PreferredWidth", 388.063 /*
															 * MillimetersToPoints
															 * (15)
															 */);
				Dispatch.call(oCell, "Select");
				Dispatch.call(oSelection, "MoveLeft"); // 左へ移動（カーソルを立てる）
				try { // Wordの更新を待つ
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				// iText.getText().equals("▲")
				// 以下の2行で不要な改行を削除し、表の次の行にカーソルを移動する
				Dispatch.call(oSelection, "TypeBackspace");
				Dispatch.call(oSelection, "MoveDown"); // 下へ移動
			}
			return;
		}

		// 標準的な処理
		Dispatch.put(oSelection, "Text", iText.getText());
		Dispatch.put(oSelection, "Style", sWordStyle);

		Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動

		if (nLsIndex > 2
				&& m_cLongStyleArrayList.get(nLsIndex - 2).equals(
						"【HACK】【HACK】")) {
			Dispatch.call(oSelection, "MoveDown"); // 下へ移動
		} else {
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
		}
	}
}
