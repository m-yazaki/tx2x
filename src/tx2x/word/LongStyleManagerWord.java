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
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import tx2x.IntermediateTextTreeWalker;
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

		if (m_sCurrentLongStyle.equals("【■】【■】【■】")) {
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

		if (m_sCurrentLongStyle.matches("(【1\\.?】【1\\.?】【表】【行】【セル】)?【箇条書き[●・]】【箇条書き[●・]】【箇条書き[●・]】")) {
			return "箇条書き";
		}

		if (m_sCurrentLongStyle.matches("(【1\\.?】【1\\.?】【表】【行】【セル】)?【指】【指】【指】")) {
			return "その他の手順-タイトル";
		}

		if (m_sCurrentLongStyle.matches("(【1\\.?】【1\\.?】【表】【行】【セル】)?【指】【指】【①】【①】【①】")) {
			return "その他の手順-手順";
		}

		if (m_sCurrentLongStyle.matches("(【1\\.?】【1\\.?】【表】【行】【セル】)?【指】【指】【箇条書き●】【箇条書き●】【箇条書き●】")) {
			return "その他の手順-補足";
		}

		if (m_sCurrentLongStyle.equals("【箇条書き・】【箇条書き・】【本文】【本文】【本文】")) {
			return "箇条書き-本文";
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

		if (m_sCurrentLongStyle.matches("【1\\.?】【1\\.?】【1\\.?】")) {
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

		if (m_sCurrentLongStyle.matches("(【1\\.?】【1\\.?】)?【表】【行】【セル】【本文】【本文】【本文】")) {
			return "本文";
		}

		if (m_sCurrentLongStyle.matches("(【1\\.?】【1\\.?】)?【表】【行】【セル】【①】【①】【①】")) {
			return "手順丸数字";
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
			int nLsIndex, IntermediateTextTreeWalker cTreeWalker) {
		String longStyle = getLongStyle();
		if (iText.getStyle() == null) {
			longStyle += "【本文】";
		}
		if (m_bDebugMode) {
			System.out.println(longStyle);
		}

		// 段落スタイルを設定するだけではない、特別なデザインが必要なもの
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
		} else if (longStyle.matches("【1\\.?】【1\\.?】【1\\.?】")) {
			Pattern p = Pattern.compile("^([0-9]+\\.?)(.*)");
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
		} else if (longStyle.matches("(【1.】【1.】|【ヒント】)?【箇条書き・】【箇条書き・】【箇条書き・】")) {
			Dispatch.put(oSelection, "Style", sWordStyle);
			typeText(oSelection, iText.getText().replaceFirst("^・", "●"));
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			return;
		} else if (longStyle.equals("【――】【――】【――】")) {
			Dispatch.put(oSelection, "Style", sWordStyle);
			Dispatch.put(oSelection, "Text", iText.getText().replaceFirst("^-+\t", "―― "));
			Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			return;
		} else if (longStyle.equals("【章】【章】【章】") || longStyle.equals("【節】【節】【節】") || longStyle.equals("【項】【項】【項】")) {
			iText.setText(iText.getText().replaceFirst("^【.】", ""));
		} else if (longStyle.equals("【■】【■】【■】")) {
			iText.setText(iText.getText().replaceFirst("^■", ""));
		} else if (longStyle.matches("(【1\\.?】【1\\.?】【表】【行】【セル】)?【指】【指】【指】")) {
			iText.setText(iText.getText().replaceFirst("^【指】\t", ""));
			Tx2xDispatch.call(oSelection, "InsertSymbol", 9758 /* 指 */, "ＭＳ 明朝", true);
		}

		// 標準的な処理
		try {
			Dispatch.put(oSelection, "Style", sWordStyle);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Style: " + sWordStyle);
		}
		typeText(oSelection, iText.getText());

		// Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動

		if (getPrevLongStyle().equals("【HACK】【HACK】")) {
			// Dispatch.call(oSelection, "MoveDown"); // 下へ移動
		} else if (m_sCurrentLongStyle.matches("【表】【行】【セル(：ヘッダー)?】【本文】【本文】【本文】")) {
			// Dispatch.call(oSelection, "MoveDown"); // 下へ移動
		} else {
			// 段落入力後に改行するかどうかを判定する
			if (m_sCurrentLongStyle.indexOf("【表】") != -1) {
				if (m_sCurrentLongStyle.matches(".*【本文】$")) {
					// 次の行が同じ「～【本文】【本文】」の場合は改行する
					// 先読み
					IntermediateText cCurrentNode = cTreeWalker.getCurrentNode();
					IntermediateText cNextSibling_TreeWalker = cTreeWalker.nextSibling();
					if (cNextSibling_TreeWalker != null) {
						// 次が完全に同一のスタイル（cTreeWalkerは動いている）→改行が必要
						Tx2xDispatch.call(oSelection, "TypeParagraph"); // 改行

						// cTreeWalkerを戻す
						cTreeWalker.previousSibling();
					} else {
						// 次が完全に同一のスタイルではない（cTreeWalkerは動いていない）

						// 2つ遡ってみる
						// cParentNodesには、cCurrentNodeから、cCurrentNodeが属するセルまでのルート（親ノードたち）を入れる
						Stack<IntermediateText> cParentNodes = new Stack<IntermediateText>();
						ControlText cParentNode = null;
						for (int i = 0; i < 2; i++) {
							cParentNode = cTreeWalker.parentNode();
							cParentNodes.push(cParentNode);
						}

						IntermediateText cParentNextSiblingNode = cTreeWalker.nextSibling();
						if (cParentNextSiblingNode != null) {
							// 次がいる
							Tx2xDispatch.call(oSelection, "TypeParagraph"); // 改行
							cTreeWalker.parentNode();
							cTreeWalker.firstChild();
							while (cTreeWalker.getCurrentNode() != cParentNode) {
								cTreeWalker.nextSibling();
							}
						}

						// m_cTreeWalkerを戻す
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
					}
				} else {
					// セルの１行目かどうかを調べる
					checkFirstLineInCell(oSelection, cTreeWalker);
					// セルの末尾か調べる
					checkLastLineInCell(oSelection, cTreeWalker);
				}
			} else {
				Tx2xDispatch.call(oSelection, "TypeParagraph"); // 改行
			}
		}
	}

	private void typeText(Dispatch oSelection, String sText) {
		String[] b = sText.split("((?<=</?b>)|(?=</?b>))", -1);
		for (int i = 0; i < b.length; i++) {
			if (b[i].equals("<b>")) {
				Tx2xDispatch.put(oSelection, "Font.Bold", true);
			} else if (b[i].equals("</b>")) {
				Tx2xDispatch.put(oSelection, "Font.Bold", false);
			} else {
				String[] a = b[i].split("((?<=[<>])|(?=[<>]))", -1);
				for (int j = 0; j < a.length; j++) {
					if (a[j].equals("<")) {
						Pattern p = Pattern.compile("a href=\"(.+)\"");
						Matcher matcher = p.matcher(a[j + 1]);
						String sHref = "";
						if (matcher.find()) {
							sHref = matcher.group(1);
							Variant oRange = Tx2xDispatch.call(oSelection, "Range");
							Tx2xDispatch.call(oSelection, "Document.Hyperlinks.Add", oRange, sHref, "", "", a[j + 3]);
							j += 3;
						} else if (a[j + 1].equals("/a")) {
							// 出力無し
							j++;
						}
					} else if (a[j].equals(">")) {
						// 出力無し
					} else {
						Dispatch.call(oSelection, "TypeText", a[j]);
						// Dispatch.put(oSelection, "Text", t[i]);
						Dispatch.call(oSelection, "EndKey", 5 /* wdLine */); // 行末へ移動
					}
				}
			}
		}
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

	// セルの先頭行か調べる
	private void checkFirstLineInCell(Dispatch oSelection, IntermediateTextTreeWalker cTreeWalker) {
		IntermediateText cCurrentNode = cTreeWalker.getCurrentNode();

		// cParentNodesには、cCurrentNodeから、cCurrentNodeが属するセルまでのルート（親ノードたち）を入れる
		Stack<IntermediateText> cParentNodes = new Stack<IntermediateText>();
		ControlText cParentNode = null;
		while (true) {
			cParentNode = cTreeWalker.parentNode();
			cParentNodes.push(cParentNode);
			if (cParentNode.getStyle().getStyleName().equals("【セル】")) {
				break; // セルまで遡ったらbreak;
			}
		}

		// ここに、先頭行だけで行う処理を記述する
		// 現在は何も無い

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
	}

	private void checkLastLineInCell(Dispatch oSelection, IntermediateTextTreeWalker cTreeWalker) {
		// セルの最終行か調べる
		IntermediateText cCurrentNode = cTreeWalker.getCurrentNode();
		IntermediateText cNextSibling_TreeWalker = cTreeWalker.nextSibling();

		Stack<IntermediateText> cParentNodes = new Stack<IntermediateText>();
		ControlText cParentNode = null;
		while (cNextSibling_TreeWalker == null) {
			cParentNode = cTreeWalker.parentNode();
			cParentNodes.push(cParentNode);
			if (cParentNode.getStyle().getStyleName().equals("【セル】")) {
				break; // セルまで遡ったらbreak;
			} else {
				cNextSibling_TreeWalker = cTreeWalker.nextSibling();
			}
		}

		// 次の行がまだあるから改行する
		if (cNextSibling_TreeWalker != null) {
			Tx2xDispatch.call(oSelection, "TypeParagraph"); // 改行
			cParentNode = cTreeWalker.parentNode();
			cParentNodes.push(cParentNode);
		}

		// m_cTreeWalkerを戻す
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
	}
}
