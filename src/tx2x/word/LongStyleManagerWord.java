/**
 * LongStyle（【お知らせ】【箇条書き】【箇条書き】）を元に、InDesignタグ付きテキストを生成する
 * ついでに特殊文字の置換も行っています。
 */
package tx2x.word;

// 定数クラス
import static tx2x.Constants.MM;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import tx2x.LongStyleManager;
import tx2x.Tx2xOptions;
import tx2x.core.ControlText;
import tx2x.core.IntermediateText;

public class LongStyleManagerWord extends LongStyleManager {
	// 未定義のスタイル（dummy000）を管理するための変数
	private Hashtable<String, String> m_cDummyStyleHashTable;
	private boolean m_bDebugMode;

	LongStyleManagerWord() {
		super();
		m_cDummyStyleHashTable = new Hashtable<String, String>();
		m_bDebugMode = Tx2xOptions.getInstance().getBoolean("debug");
	}

	public String getTargetStyle(IntermediateText iText, int nLsIndex) throws IOException {
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

		if (m_sCurrentLongStyle.equals("【章】【章】【章】")) {
			return "見出し 1";
		}

		if (m_sCurrentLongStyle.equals("【章サブ】【章サブ】【章サブ】")) {
			return "見出し 1サブ";
		}

		if (m_sCurrentLongStyle.equals("【節】【節】【節】")) {
			return "見出し 2";
		}

		if (m_sCurrentLongStyle.equals("【項】【項】【項】")) {
			return "見出し 3";
		}

		if (m_sCurrentLongStyle.equals("【HACK】【HACK】【HACK】")) {
			return "HACK-見出し";
		}

		if (m_sCurrentLongStyle.equals("【HACK】【HACK】【本文】【本文】【本文】")) {
			return "本文";
		}

		if (m_sCurrentLongStyle.equals("【コード】【コード】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【コード】【本文】【本文】【本文】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【コード】【本文】【本文】【本文】【本文】【本文】")) {
			iText.setText("\t" + iText.getText());
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【コード】【本文】【本文】【本文】【本文】【本文】【本文】【本文】")) {
			iText.setText("\t\t" + iText.getText());
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【本文】【本文】【本文】")) {
			return "本文";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き・】【箇条書き・】【箇条書き・】")) {
			return "箇条書き";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き・】【箇条書き・】【コード】【コード】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き・】【箇条書き・】【コード】【本文】【本文】【本文】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き・】【箇条書き・】【コード】【本文】【本文】【本文】【本文】【本文】")) {
			iText.setText("\t" + iText.getText());
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き・】【箇条書き・】【コード】【コード】【本文】【本文】【本文】【本文】")) {
			iText.setText("\t\t" + iText.getText());
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【1.】")) {
			return "段落番号";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【本文】【本文】【本文】")) {
			return "段落番号-本文";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【箇条書き・】【箇条書き・】【箇条書き・】")) {
			return "箇条書き2";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【ヒント】【ヒント】")) {
			if (iText.getText().equals("▼ヒント")) {
				return "ヒント-ヘッダー";
			} else {
				return "ヒント-フッター";
			}
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【ヒント】【本文】【本文】【本文】")) {
			return "ヒント-本文";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【コード】【コード】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【コード】【本文】【本文】【本文】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【1.】【1.】【コード】【本文】【本文】【本文】【本文】【本文】")) {
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

		if (m_sCurrentLongStyle.equals("【表】【行】【セル：ヘッダー】【本文】【本文】【本文】")) {
			iText.setText(iText.getText().replaceFirst("【ヘッダー】", ""));
			return "本文";
		}

		if (m_sCurrentLongStyle.equals("【表】【行】【セル】【本文】【本文】【本文】")) {
			return "本文";
		}

		if (m_sCurrentLongStyle.equals("【ヒント】【ヒント】")) {
			if (iText.getText().equals("▼ヒント")) {
				return "ヒント-ヘッダー";
			} else {
				return "ヒント-フッター";
			}
		}

		if (m_sCurrentLongStyle.equals("【ヒント】【本文】【本文】【本文】")) {
			return "ヒント-本文";
		}

		if (m_sCurrentLongStyle.equals("【ヒント】【箇条書き・】【箇条書き・】【箇条書き・】")) {
			return "ヒント-箇条書き";
		}

		if (m_sCurrentLongStyle.equals("【ヒント】【コード】【コード】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【ヒント】【コード】【本文】【本文】【本文】")) {
			return "コード";
		}

		if (m_sCurrentLongStyle.equals("【注意】【コード】【コード】")) {
			return "注意-コード";
		}

		if (m_sCurrentLongStyle.equals("【注意】【コード】【本文】【本文】【本文】")) {
			return "注意-コード";
		}

		if (m_sCurrentLongStyle.equals("【注意】【注意】")) {
			if (iText.getText().equals("▼注意")) {
				return "注意-ヘッダー";
			} else {
				return "注意-フッター";
			}
		}

		if (m_sCurrentLongStyle.equals("【注意】【本文】【本文】【本文】")) {
			return "注意-本文";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】")) {
			iText.setText(iText.getText().replaceFirst("^::", ""));
			return "用語";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【本文】【本文】【本文】")) {
			return "用語-本文";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】")) {
			iText.setText(iText.getText().replaceFirst("^::", ""));
			return "用語2";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【本文】【本文】【本文】")) {
			return "用語2-本文";
		}

		if (m_sCurrentLongStyle.equals("【画面】【画面】【画面】")) {
			return "本文";
		}

		if (m_sCurrentLongStyle.equals("【――】【――】【――】")) {
			return "――";
		}

		// 以降、ダミースタイルの処理
		String style = m_cDummyStyleHashTable.get(getPrevLongStyle());
		if (style != null) {
			return style;
		}
		DecimalFormat df = new DecimalFormat();
		df.applyLocalizedPattern("0000");
		style = "標準";
		iText.setText("■未定義スタイル:" + m_sCurrentLongStyle + iText.getText());
		System.out.println(m_sCurrentLongStyle + "は、" + style + "スタイルで出力されました。");
		m_cDummyStyleHashTable.put(m_sCurrentLongStyle, style);
		return style;
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
	public void writeTargetIntermediateText(Dispatch oSelection, String sWordStyle, IntermediateText iText,
			int nLsIndex) {
		String longStyle = getLongStyle();
		if (iText.getStyle() == null) {
			longStyle += "【本文】";
		}
		if (m_bDebugMode) {
			System.out.println(longStyle);
		}

		if (longStyle.equals("【画面】【画面】【画面】")) {
			Dispatch oDocument = Dispatch.call(oSelection, "Document").toDispatch();
			Dispatch oTables = Dispatch.call(oDocument, "Tables").toDispatch();
			Variant oRange = Dispatch.call(oSelection, "Range");
			Dispatch oTable = Dispatch.call(oTables, "Add", oRange, 1, 1).toDispatch();

			Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1).getDispatch();
			setCellWidth(oCell, 148 * MM);

			// 左端からのインデント
			Dispatch oRows = Dispatch.get(oTable, "Rows").toDispatch();
			Dispatch.put(oRows, "LeftIndent", 2 * MM);

			// 罫線
			for (int i = -4; i <= -1; i++) {
				/* -1:wdBorderTop */
				/* -2:wdBorderLeft */
				/* -3:wdBorderBottom */
				/* -4:wdBorderRight */
				Dispatch oBorder = Dispatch.call(oTable, "Borders", i).toDispatch();
				Dispatch.put(oBorder, "LineStyle", 1 /* wdLineStyleSingle */);
			}

			// 余白
			Dispatch.put(oTable, "TopPadding", 2 * MM);
			Dispatch.put(oTable, "BottomPadding", 2 * MM);
			Dispatch.put(oTable, "LeftPadding", 2 * MM);
			Dispatch.put(oTable, "RightPadding", 2 * MM);

			Dispatch.call(oCell, "Select");
			Dispatch oInlineShapes = Dispatch.get(oSelection, "InlineShapes").toDispatch();
			Pattern p = Pattern.compile("^<img src=\"([^\"]+)");
			Matcher matcher = p.matcher(iText.getText());
			matcher.find();
			try {
				Dispatch.call(oInlineShapes, "AddPicture",
						Tx2xOptions.getInstance().getString("tx2x_folder_name") + "\\" + matcher.group(1), "False",
						"True");
			} catch (ComFailException e) {
				System.out.println("---------- error ----------\n" + e.getLocalizedMessage() + "ファイル名："
						+ matcher.group(1) + "\n---------------------------");
			}

			try { // Wordの更新を待つ
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Dispatch oParagraphFormat = Dispatch.get(oSelection, "ParagraphFormat").toDispatch();
			Dispatch.put(oParagraphFormat, "Alignment", 1 /* wdAlignParagraphCenter */);
			Dispatch.call(oSelection, "MoveDown"); // 下へ移動（カーソルを立てる）
			return;
		} else if (longStyle.equals("【1.】【1.】【1.】")) {
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
		} else if (longStyle.equals("【箇条書き・】【箇条書き・】【箇条書き・】")) {
			Dispatch.put(oSelection, "Style", sWordStyle);
			Dispatch.put(oSelection, "Text", iText.getText().replaceFirst("^・", "●"));
			Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			return;
		} else if (longStyle.equals("【1.】【1.】【箇条書き・】【箇条書き・】【箇条書き・】")) {
			Dispatch.put(oSelection, "Style", sWordStyle);
			Dispatch.put(oSelection, "Text", iText.getText().replaceFirst("^・", "●"));
			Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			return;
		} else if (longStyle.equals("【ヒント】【箇条書き・】【箇条書き・】【箇条書き・】")) {
			Dispatch.put(oSelection, "Style", sWordStyle);
			Dispatch.put(oSelection, "Text", iText.getText().replaceFirst("^・", "●"));
			Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			return;
		} else if (longStyle.equals("【――】【――】【――】")) {
			Dispatch.put(oSelection, "Style", sWordStyle);
			Dispatch.put(oSelection, "Text", iText.getText().replaceFirst("^-+\t", "―― "));
			Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			return;
		} else if (longStyle.equals("【章】【章】【章】") || longStyle.equals("【節】【節】【節】") || longStyle.equals("【項】【項】【項】")) {
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

		if (getPrevLongStyle().equals("【HACK】【HACK】")) {
			// Dispatch.call(oSelection, "MoveDown"); // 下へ移動
		} else if (m_sCurrentLongStyle.matches("【表】【行】【セル(：ヘッダー)?】【本文】【本文】【本文】")) {
			// Dispatch.call(oSelection, "MoveDown"); // 下へ移動
		} else {
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
		}
	}

	private void writeHintParts(Dispatch oSelection, double dLeftIndent) {
		Dispatch oDocument = Dispatch.call(oSelection, "Document").toDispatch();
		Dispatch oTables = Dispatch.call(oDocument, "Tables").toDispatch();
		Variant oRange = Dispatch.call(oSelection, "Range");
		Dispatch oTable = Dispatch.call(oTables, "Add", oRange, 1, 2).toDispatch();

		// 左端からのインデント
		Dispatch oRows = Dispatch.get(oTable, "Rows").toDispatch();
		Dispatch.put(oRows, "LeftIndent", dLeftIndent);

		// ヒントアイコン
		Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1).getDispatch();
		setCellWidth(oCell, 15 * MM);

		Dispatch.call(oCell, "Select");
		Dispatch.put(oSelection, "Style", "本文");
		Dispatch oInlineShapes = Dispatch.get(oSelection, "InlineShapes").toDispatch();
		Dispatch.call(oInlineShapes, "AddPicture",
				Tx2xOptions.getInstance().getString("tx2x_folder_name") + "\\hint.png", "False", "True");

		// ヒント本文
		oCell = Dispatch.call(oTable, "Cell", 1, 2).getDispatch();
		setCellWidth(oCell, 136.9 * MM - dLeftIndent);

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
