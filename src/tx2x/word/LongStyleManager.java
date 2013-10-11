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
import tx2x.core.IntermediateText;
import tx2x.core.Style;

import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class LongStyleManager {
	private static final double MillimetersToPoints_2 = 5.669291;
	private static final double MillimetersToPoints_10 = 28.34646;
	private static final double MillimetersToPoints_15 = 42.51968;
	private static final double MillimetersToPoints_20 = 56.69291;
	private static final double MillimetersToPoints_120 = 340.1575;
	private static final double MillimetersToPoints_124_8 = 353.7638;
	private static final double MillimetersToPoints_134_8 = 382.1103;
	private static final double MillimetersToPoints_136_9 = 388.063;
	private static final double MillimetersToPoints_148 = 419.5276;
	LinkedList<Style> m_StyleLinkedList; // スタイル情報をpush/popする
	String m_sCurrentLongStyle; // 現在の長いスタイル名
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
		m_sCurrentLongStyle = getLongStyle();
		if (iText.getStyle() == null) {
			m_sCurrentLongStyle += "【本文】";
		}

		// iTextの本文を処理。【初期操作】以外の全段落共通
		{
			String text = iText.getText();
			iText.setText(text);
		}

		// 標準的なチェック（それぞれ独立しているので順不同）

		if (m_sCurrentLongStyle.equals("【章】【章】")) {
			return "見出し 1";
		}

		if (m_sCurrentLongStyle.equals("【章サブ】【章サブ】")) {
			return "見出し 1サブ";
		}

		if (m_sCurrentLongStyle.equals("【節】【節】")) {
			return "見出し 2";
		}

		if (m_sCurrentLongStyle.equals("【項】【項】")) {
			return "見出し 3";
		}

		if (m_sCurrentLongStyle.equals("【HACK】【HACK】")) {
			return "HACK";
		}

		if (m_sCurrentLongStyle.equals("【コード】【コード】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【コード】【本文】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【コード】【コード】【本文】【本文】")) {
			iText.setText("\t" + iText.getText());
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【コード】【コード】【本文】【本文】【本文】【本文】")) {
			iText.setText("\t\t" + iText.getText());
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【本文】【本文】")) {
			return "本文";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き・】【箇条書き・】")) {
			return "箇条書き";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き・】【箇条書き・】【コード】【コード】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き・】【箇条書き・】【コード】【本文】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き・】【箇条書き・】【コード】【コード】【本文】【本文】")) {
			iText.setText("\t" + iText.getText());
			return "コード";
		}

		if (m_sCurrentLongStyle
				.equals("【箇条書き・】【箇条書き・】【コード】【コード】【本文】【本文】【本文】【本文】")) {
			iText.setText("\t\t" + iText.getText());
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】")) {
			return "段落番号";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【本文】【本文】")) {
			return "段落番号-本文";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【箇条書き・】【箇条書き・】")) {
			return "箇条書き2";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【ヒント】【ヒント】")) {
			if (iText.getText().equals("▼ヒント")) {
				return "ヒント-ヘッダー";
			} else {
				return "ヒント-フッター";
			}
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【ヒント】【本文】")) {
			return "ヒント-本文";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【コード】【コード】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【コード】【本文】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【コード】【コード】【本文】【本文】")) {
			iText.setText("\t" + iText.getText());
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【コード】【コード】【本文】【本文】【本文】【本文】")) {
			iText.setText("\t\t" + iText.getText());
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【表】")) {
			return "本文";
		}

		if (m_sCurrentLongStyle.equals("【表】【行】【セル：ヘッダー】【本文】")) {
			iText.setText(iText.getText().replaceFirst("【ヘッダー】", ""));
			return "本文";
		}

		if (m_sCurrentLongStyle.equals("【表】【行】【セル】【本文】")) {
			return "本文";
		}

		if (m_sCurrentLongStyle.equals("【ヒント】【ヒント】")) {
			if (iText.getText().equals("▼ヒント")) {
				return "ヒント-ヘッダー";
			} else {
				return "ヒント-フッター";
			}
		}

		if (m_sCurrentLongStyle.equals("【ヒント】【本文】")) {
			return "ヒント-本文";
		}

		if (m_sCurrentLongStyle.equals("【ヒント】【箇条書き・】【箇条書き・】")) {
			return "ヒント-箇条書き";
		}

		if (m_sCurrentLongStyle.equals("【ヒント】【コード】【コード】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【ヒント】【コード】【本文】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【注意】【コード】【コード】")) {
			return "注意-コード";
		}

		if (m_sCurrentLongStyle.equals("【注意】【コード】【本文】")) {
			return "注意-コード";
		}

		if (m_sCurrentLongStyle.equals("【注意】【注意】")) {
			if (iText.getText().equals("▼注意")) {
				return "注意-ヘッダー";
			} else {
				return "注意-フッター";
			}
		}

		if (m_sCurrentLongStyle.equals("【注意】【本文】")) {
			return "注意-本文";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き（用語）】【箇条書き（用語）】")) {
			iText.setText(iText.getText().replaceFirst("^::", ""));
			return "用語";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【本文】【本文】")) {
			return "用語-本文";
		}

		if (m_sCurrentLongStyle
				.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】")) {
			iText.setText(iText.getText().replaceFirst("^::", ""));
			return "用語2";
		}

		if (m_sCurrentLongStyle
				.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【本文】【本文】")) {
			return "用語2-本文";
		}

		if (m_sCurrentLongStyle.equals("【画面】【画面】")) {
			return "本文";
		}

		if (m_sCurrentLongStyle.equals("【――】【――】")) {
			return "――";
		}

		// 以降、ダミースタイルの処理
		String style = m_cDummyStyleHashTable.get(m_sPrevLongStyle);
		if (style != null) {
			return style;
		}
		DecimalFormat df = new DecimalFormat();
		df.applyLocalizedPattern("0000");
		style = "標準";
		iText.setText("■未定義スタイル:" + m_sCurrentLongStyle + iText.getText());
		System.out
				.println(m_sCurrentLongStyle + "は、" + style + "スタイルで出力されました。");
		m_cDummyStyleHashTable.put(m_sCurrentLongStyle, style);
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

	public void setPrevLongStyle() {
		m_sPrevLongStyle = m_sCurrentLongStyle;
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
		if (longStyle.equals("【コード】【コード】")) {
			if (iText.getText().equals("▼コード")) {
				// 表を作る
				Dispatch oDocument = Dispatch.call(oSelection, "Document")
						.toDispatch();
				Dispatch oTables = Dispatch.call(oDocument, "Tables")
						.toDispatch();
				Variant oRange = Dispatch.call(oSelection, "Range");
				Dispatch oTable = Dispatch.call(oTables, "Add", oRange, 1, 1)
						.toDispatch();

				// 幅を調節
				Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1)
						.getDispatch();
				setCellWidth(oCell, MillimetersToPoints_134_8);

				// 左端からのインデント
				Dispatch oRows = Dispatch.get(oTable, "Rows").toDispatch();
				Dispatch.put(oRows, "LeftIndent", MillimetersToPoints_15);

				// 背景：RGB(236, 240, 241)
				Dispatch oShading = Dispatch.get(oTable, "Shading")
						.toDispatch();
				Dispatch.put(oShading, "BackgroundPatternColor", 15855852);

				// 罫線
				for (int i = -4; i <= -1; i++) {
					/* -1:wdBorderTop */
					/* -2:wdBorderLeft */
					/* -3:wdBorderBottom */
					/* -4:wdBorderRight */
					Dispatch oBorder = Dispatch.call(oTable, "Borders", i)
							.toDispatch();
					Dispatch.put(oBorder, "LineStyle", 4 /* wdLineStyleDashLargeGap */);
				}
				return;
			} else {
				// 表から抜け出す
				Dispatch.call(oSelection, "TypeBackspace"); // Backspace（不要な改行を削除）
				Dispatch.call(oSelection, "MoveRight", 1 /* wdCharacter */, 2); // 右へ2つ移動
				return;
			}
		} else if (longStyle.equals("【ヒント】【コード】【コード】")) {
			if (iText.getText().equals("▼コード")) {
				// 表を作る
				Dispatch oDocument = Dispatch.call(oSelection, "Document")
						.toDispatch();
				Dispatch oTables = Dispatch.call(oDocument, "Tables")
						.toDispatch();
				Variant oRange = Dispatch.call(oSelection, "Range");
				Dispatch oTable = Dispatch.call(oTables, "Add", oRange, 1, 1)
						.toDispatch();

				// 幅を調節
				Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1)
						.getDispatch();
				setCellWidth(oCell, MillimetersToPoints_120);

				// 左端からのインデント
				Dispatch oRows = Dispatch.get(oTable, "Rows").toDispatch();
				Dispatch.put(oRows, "LeftIndent", MillimetersToPoints_10);

				// 背景：RGB(236, 240, 241)
				Dispatch oShading = Dispatch.get(oTable, "Shading")
						.toDispatch();
				Dispatch.put(oShading, "BackgroundPatternColor", 15855852);

				// 罫線
				for (int i = -4; i <= -1; i++) {
					/* -1:wdBorderTop */
					/* -2:wdBorderLeft */
					/* -3:wdBorderBottom */
					/* -4:wdBorderRight */
					Dispatch oBorder = Dispatch.call(oTable, "Borders", i)
							.toDispatch();
					Dispatch.put(oBorder, "LineStyle", 4 /* wdLineStyleDashLargeGap */);
				}
				return;
			} else {
				// 表から抜け出す
				Dispatch.call(oSelection, "TypeBackspace"); // Backspace（不要な改行を削除）
				Dispatch.call(oSelection, "MoveRight", 1 /* wdCharacter */, 2); // 右へ2つ移動
				return;
			}
		} else if (longStyle.equals("【注意】【コード】【コード】")) {
			if (iText.getText().equals("▼コード")) {
				// 表を作る
				Dispatch oDocument = Dispatch.call(oSelection, "Document")
						.toDispatch();
				Dispatch oTables = Dispatch.call(oDocument, "Tables")
						.toDispatch();
				Variant oRange = Dispatch.call(oSelection, "Range");
				Dispatch oTable = Dispatch.call(oTables, "Add", oRange, 1, 1)
						.toDispatch();

				// 幅を調節
				Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1)
						.getDispatch();
				setCellWidth(oCell, MillimetersToPoints_120);

				// 左端からのインデント
				Dispatch oRows = Dispatch.get(oTable, "Rows").toDispatch();
				Dispatch.put(oRows, "LeftIndent", MillimetersToPoints_10);

				// 背景：RGB(236, 240, 241)
				Dispatch oShading = Dispatch.get(oTable, "Shading")
						.toDispatch();
				Dispatch.put(oShading, "BackgroundPatternColor", 14939133);

				// 罫線
				for (int i = -4; i <= -1; i++) {
					/* -1:wdBorderTop */
					/* -2:wdBorderLeft */
					/* -3:wdBorderBottom */
					/* -4:wdBorderRight */
					Dispatch oBorder = Dispatch.call(oTable, "Borders", i)
							.toDispatch();
					Dispatch.put(oBorder, "LineStyle", 4 /* wdLineStyleDashLargeGap */);
				}
				return;
			} else {
				// 表から抜け出す
				Dispatch.call(oSelection, "TypeBackspace"); // Backspace（不要な改行を削除）
				Dispatch.call(oSelection, "MoveRight", 1 /* wdCharacter */, 2); // 右へ2つ移動
				return;
			}
		} else if (longStyle.equals("【1.】【1.】【コード】【コード】")
				|| longStyle.equals("【箇条書き・】【箇条書き・】【コード】【コード】")) {
			if (iText.getText().equals("▼コード")) {
				// 表を作る
				Dispatch oDocument = Dispatch.call(oSelection, "Document")
						.toDispatch();
				Dispatch oTables = Dispatch.call(oDocument, "Tables")
						.toDispatch();
				Variant oRange = Dispatch.call(oSelection, "Range");
				Dispatch oTable = Dispatch.call(oTables, "Add", oRange, 1, 1)
						.toDispatch();

				// 幅を調節
				Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1)
						.getDispatch();
				setCellWidth(oCell, MillimetersToPoints_124_8);

				// 左端からのインデント
				Dispatch oRows = Dispatch.get(oTable, "Rows").toDispatch();
				Dispatch.put(oRows, "LeftIndent", MillimetersToPoints_20);

				// 背景：RGB(236, 240, 241)
				Dispatch oShading = Dispatch.get(oTable, "Shading")
						.toDispatch();
				Dispatch.put(oShading, "BackgroundPatternColor", 15855852);

				// 罫線
				for (int i = -4; i <= -1; i++) {
					/* -1:wdBorderTop */
					/* -2:wdBorderLeft */
					/* -3:wdBorderBottom */
					/* -4:wdBorderRight */
					Dispatch oBorder = Dispatch.call(oTable, "Borders", i)
							.toDispatch();
					Dispatch.put(oBorder, "LineStyle", 4 /* wdLineStyleDashLargeGap */);
				}
				return;
			} else {
				// 表から抜け出す
				Dispatch.call(oSelection, "TypeBackspace"); // Backspace（不要な改行を削除）
				Dispatch.call(oSelection, "MoveRight", 1 /* wdCharacter */, 2); // 右へ2つ移動
				return;
			}
		} else if (longStyle.equals("【HACK】【HACK】")) {
			Dispatch oDocument = Dispatch.call(oSelection, "Document")
					.toDispatch();
			Dispatch oTables = Dispatch.call(oDocument, "Tables").toDispatch();
			Variant oRange = Dispatch.call(oSelection, "Range");
			Dispatch oTable = Dispatch.call(oTables, "Add", oRange, 1, 2)
					.toDispatch();

			// HACKグレーアイコン
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
			Dispatch.call(oShape, "IncrementTop", -MillimetersToPoints_2); // 2mm上へ移動
			Dispatch.call(oShape, "IncrementLeft", -MillimetersToPoints_2); // 2mm左へ移動

			Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1).getDispatch();
			setCellWidth(oCell, MillimetersToPoints_15);

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
			setCellWidth(oCell, MillimetersToPoints_136_9);

			Dispatch.call(oCell, "Select");
			Dispatch.call(oSelection, "MoveLeft"); // 左へ移動（カーソルを立てる）
			Dispatch.put(oSelection, "Text", matcher.group(2));
			Dispatch.put(oSelection, "Style", "HACK-見出し");
			Dispatch.call(oSelection, "MoveRight"); // 右へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行

			return;
		} else if (longStyle.equals("【ヒント】【ヒント】")) {
			if (iText.getText().equals("▼ヒント")) {
				writeHintParts(oSelection, 0);
			} else { // iText.getText().equals("▲")
				// 不要な改行を削除し、表の次の行にカーソルを移動する
				Dispatch.call(oSelection, "TypeBackspace");
				Dispatch.call(oSelection, "MoveRight", 1 /* wdCharacter */, 2); // 右へ2つ移動
			}
			return;
		} else if (longStyle.equals("【1.】【1.】【ヒント】【ヒント】")) {
			if (iText.getText().equals("▼ヒント")) {
				writeHintParts(oSelection, MillimetersToPoints_15);
			} else { // iText.getText().equals("▲")
				// 不要な改行を削除し、表の次の行にカーソルを移動する
				Dispatch.call(oSelection, "TypeBackspace");
				Dispatch.call(oSelection, "MoveRight", 1 /* wdCharacter */, 2); // 右へ2つ移動
			}
			return;
		} else if (longStyle.equals("【注意】【注意】")) {
			if (iText.getText().equals("▼注意")) {
				writeNoteParts(oSelection, 0, MillimetersToPoints_136_9);
			} else { // iText.getText().equals("▲")
				// 不要な改行を削除し、表の次の行にカーソルを移動する
				Dispatch.call(oSelection, "TypeBackspace");
				Dispatch.call(oSelection, "MoveRight", 1 /* wdCharacter */, 2); // 右へ2つ移動
			}
			return;
		} else if (longStyle.equals("【画面】【画面】")) {
			Dispatch oDocument = Dispatch.call(oSelection, "Document")
					.toDispatch();
			Dispatch oTables = Dispatch.call(oDocument, "Tables").toDispatch();
			Variant oRange = Dispatch.call(oSelection, "Range");
			Dispatch oTable = Dispatch.call(oTables, "Add", oRange, 1, 1)
					.toDispatch();

			Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1).getDispatch();
			setCellWidth(oCell, MillimetersToPoints_148);

			// 左端からのインデント
			Dispatch oRows = Dispatch.get(oTable, "Rows").toDispatch();
			Dispatch.put(oRows, "LeftIndent", MillimetersToPoints_2);

			// 罫線
			for (int i = -4; i <= -1; i++) {
				/* -1:wdBorderTop */
				/* -2:wdBorderLeft */
				/* -3:wdBorderBottom */
				/* -4:wdBorderRight */
				Dispatch oBorder = Dispatch.call(oTable, "Borders", i)
						.toDispatch();
				Dispatch.put(oBorder, "LineStyle", 1 /* wdLineStyleSingle */);
			}

			// 余白
			Dispatch.put(oTable, "TopPadding", MillimetersToPoints_2);
			Dispatch.put(oTable, "BottomPadding", MillimetersToPoints_2);
			Dispatch.put(oTable, "LeftPadding", MillimetersToPoints_2);
			Dispatch.put(oTable, "RightPadding", MillimetersToPoints_2);

			Dispatch.call(oCell, "Select");
			Dispatch oInlineShapes = Dispatch.get(oSelection, "InlineShapes")
					.toDispatch();
			Pattern p = Pattern.compile("^<img src=\"([^\"]+)");
			Matcher matcher = p.matcher(iText.getText());
			matcher.find();
			try {
				Dispatch.call(oInlineShapes, "AddPicture", Tx2xOptions
						.getInstance().getString("tx2x_folder_name")
						+ "\\"
						+ matcher.group(1), "False", "True");
			} catch (ComFailException e) {
				System.out.println("---------- error ----------\n"
						+ e.getLocalizedMessage() + "ファイル名：" + matcher.group(1)
						+ "\n---------------------------");
			}

			try { // Wordの更新を待つ
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Dispatch oParagraphFormat = Dispatch.get(oSelection,
					"ParagraphFormat").toDispatch();
			Dispatch.put(oParagraphFormat, "Alignment", 1 /* wdAlignParagraphCenter */);
			Dispatch.call(oSelection, "MoveDown"); // 下へ移動（カーソルを立てる）
			return;
		} else if (longStyle.equals("【1.】【1.】")) {
			Pattern p = Pattern.compile("^([0-9]+\\.)(.*)");
			Matcher matcher = p.matcher(iText.getText());
			matcher.find();
			Dispatch.put(oSelection, "Style", sWordStyle);
			Dispatch.put(oSelection, "Text", "\t");
			Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
			Dispatch.put(oSelection, "Text", matcher.group(1));
			Dispatch oFont = Dispatch.get(oSelection, "Font").toDispatch();
			Dispatch.put(oFont, "Name", "Arial Narrow");
			Dispatch.put(oFont, "Size", 14);
			Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
			Dispatch.put(oSelection, "Text", matcher.group(2));
			oFont = Dispatch.get(oSelection, "Font").toDispatch();
			Dispatch.call(oFont, "Reset");
			Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			return;
		} else if (longStyle.equals("【箇条書き・】【箇条書き・】")) {
			Dispatch.put(oSelection, "Style", sWordStyle);
			Dispatch.put(oSelection, "Text",
					iText.getText().replaceFirst("^・", "●"));
			Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			return;
		} else if (longStyle.equals("【1.】【1.】【箇条書き・】【箇条書き・】")) {
			Dispatch.put(oSelection, "Style", sWordStyle);
			Dispatch.put(oSelection, "Text",
					iText.getText().replaceFirst("^・", "●"));
			Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			return;
		} else if (longStyle.equals("【ヒント】【箇条書き・】【箇条書き・】")) {
			Dispatch.put(oSelection, "Style", sWordStyle);
			Dispatch.put(oSelection, "Text",
					iText.getText().replaceFirst("^・", "●"));
			Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			return;
		} else if (longStyle.equals("【――】【――】")) {
			Dispatch.put(oSelection, "Style", sWordStyle);
			Dispatch.put(oSelection, "Text",
					iText.getText().replaceFirst("^-+\t", "―― "));
			Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			return;
		} else if (longStyle.equals("【節】【節】") || longStyle.equals("【項】【項】")
				|| longStyle.equals("【章】【章】")) {
			iText.setText(iText.getText().replaceFirst("【.】", ""));
		}

		// 標準的な処理
		Dispatch.put(oSelection, "Text", iText.getText());
		try {
			Dispatch.put(oSelection, "Style", sWordStyle);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Style: " + sWordStyle);
		}

		Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動

		if (m_sPrevLongStyle.equals("【HACK】【HACK】")) {
			Dispatch.call(oSelection, "MoveDown"); // 下へ移動
		} else if (m_sCurrentLongStyle.equals("【表】【行】【セル：ヘッダー】【本文】")
				|| m_sCurrentLongStyle.equals("【表】【行】【セル】【本文】")) {
			// Dispatch.call(oSelection, "MoveDown"); // 下へ移動
		} else {
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
		}
	}

	private void writeHintParts(Dispatch oSelection, double dLeftIndent) {
		Dispatch oDocument = Dispatch.call(oSelection, "Document").toDispatch();
		Dispatch oTables = Dispatch.call(oDocument, "Tables").toDispatch();
		Variant oRange = Dispatch.call(oSelection, "Range");
		Dispatch oTable = Dispatch.call(oTables, "Add", oRange, 1, 2)
				.toDispatch();

		// 左端からのインデント
		Dispatch oRows = Dispatch.get(oTable, "Rows").toDispatch();
		Dispatch.put(oRows, "LeftIndent", dLeftIndent);

		// ヒントアイコン
		Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1).getDispatch();
		setCellWidth(oCell, MillimetersToPoints_15);

		Dispatch.call(oCell, "Select");
		Dispatch.put(oSelection, "Style", "本文");
		Dispatch oInlineShapes = Dispatch.get(oSelection, "InlineShapes")
				.toDispatch();
		Dispatch.call(oInlineShapes, "AddPicture", Tx2xOptions.getInstance()
				.getString("tx2x_folder_name") + "\\hint.png", "False", "True");

		// ヒント本文
		oCell = Dispatch.call(oTable, "Cell", 1, 2).getDispatch();
		setCellWidth(oCell, MillimetersToPoints_136_9 - dLeftIndent);

		Dispatch.call(oCell, "Select");
		Dispatch.call(oSelection, "MoveLeft"); // 左へ移動（カーソルを立てる）
	}

	private void writeNoteParts(Dispatch oSelection, double dLeftIndent,
			double dBodyWidth) {
		Dispatch oDocument = Dispatch.call(oSelection, "Document").toDispatch();
		Dispatch oTables = Dispatch.call(oDocument, "Tables").toDispatch();
		Variant oRange = Dispatch.call(oSelection, "Range");
		Dispatch oTable = Dispatch.call(oTables, "Add", oRange, 1, 2)
				.toDispatch();

		// 左端からのインデント
		Dispatch oRows = Dispatch.get(oTable, "Rows").toDispatch();
		Dispatch.put(oRows, "LeftIndent", dLeftIndent);

		// ヒントアイコン
		Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1).getDispatch();
		setCellWidth(oCell, MillimetersToPoints_15);

		Dispatch.call(oCell, "Select");
		Dispatch.put(oSelection, "Style", "本文");
		Dispatch oInlineShapes = Dispatch.get(oSelection, "InlineShapes")
				.toDispatch();
		Dispatch.call(oInlineShapes, "AddPicture", Tx2xOptions.getInstance()
				.getString("tx2x_folder_name") + "\\note.png", "False", "True");

		// ヒント本文
		oCell = Dispatch.call(oTable, "Cell", 1, 2).getDispatch();
		setCellWidth(oCell, MillimetersToPoints_136_9 - dLeftIndent);

		Dispatch.call(oCell, "Select");
		Dispatch.call(oSelection, "MoveLeft"); // 左へ移動（カーソルを立てる）
	}

	private void setCellWidth(Dispatch oCell, double dWidth) {
		Dispatch.put(oCell, "Width", dWidth);
		// if (Tx2xOptions.getInstance().getBoolean("Visible") == true) {
		// // Wordの更新を待つ
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
	}
}
