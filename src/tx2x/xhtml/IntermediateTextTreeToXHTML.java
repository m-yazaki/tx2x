/**
 * IntermediateTextを、XHTMLファイルに変換する
 */
package tx2x.xhtml;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import tx2x.IntermediateTextTreeWalker;
import tx2x.Style_TagInfo;
import tx2x.core.ControlText;
import tx2x.core.IntermediateText;
import tx2x.core.Style;

public class IntermediateTextTreeToXHTML {
	protected boolean m_bMac;
	int m_nLsIndex = 0;
	private boolean m_bDebugMode;
	private NavPointManager m_cNavPointManager;
	private LinkedList<TableWriter> m_TableWriterList;

	public IntermediateTextTreeToXHTML(boolean bMac, boolean bDebugMode, NavPointManager cNavPointManager) {
		super();
		m_bMac = bMac;
		m_bDebugMode = bDebugMode;
		m_cNavPointManager = cNavPointManager;
		m_TableWriterList = new LinkedList<TableWriter>();
	}

	public void output(File cXHTML, ControlText resultRootText, LongStyleManagerXHTML lsManager,
			IntermediateTextTreeWalker cTreeWalker) throws IOException {
		try {
			XHTML_FileWriter fwXHTML = new XHTML_FileWriter(cXHTML);
			// テキストファイルからXHTMLファイルの作成
			m_cNavPointManager.setHref("base-test-href");

			String sTitle = searchTitle(resultRootText, "【編】");
			if (sTitle == null)
				sTitle = searchTitle(resultRootText, "【章】");
			if (sTitle == null)
				sTitle = searchTitle(resultRootText, "【節】");
			if (sTitle == null)
				sTitle = searchTitle(resultRootText, "【項】");
			if (sTitle == null) {
				if (resultRootText.getChildList().size() > 0) {
					System.out.println("【章】【節】【項】に該当するスタイルが見つかりません。BOM付きUTF-8になっていませんか。");
					sTitle = resultRootText.getChildList().get(0).getChildText();
				} else
					sTitle = "";
			} else
				sTitle = sTitle.replaceFirst("\t", " ");
			outputHeader(fwXHTML, sTitle);
			outputResult(fwXHTML, resultRootText, lsManager, cTreeWalker);
			outputFooter(fwXHTML);

			fwXHTML.close(m_bMac);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return;
		}
	}

	// styleで指定された行の内容を返す
	private String searchTitle(IntermediateText resultRootText, String sSearchStyle) {
		IntermediateText cCurrentText = resultRootText;
		if (resultRootText instanceof ControlText) {
			ControlText cText = (ControlText) cCurrentText;
			Iterator<IntermediateText> it = cText.getChildList().iterator();
			while (it.hasNext()) {
				String ret = searchTitle(it.next(), sSearchStyle);
				if (ret != null) {
					return ret;
				}
			}
		} else {
			// テキストに子供がいない場合
			Style sCurrentStyle = cCurrentText.getStyle();
			if (sCurrentStyle != null && sCurrentStyle.getStyleName().equals(sSearchStyle)) {
				return cCurrentText.getText();
			}
		}
		return null;
	}

	private void outputHeader(XHTML_FileWriter fwXHTML, String sTitle) throws IOException {
		fwXHTML.write(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">",
				true, m_bMac);
		fwXHTML.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"ja\" lang=\"ja\">", true, m_bMac);
		fwXHTML.write("<head>", true, m_bMac);
		fwXHTML.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />", true, m_bMac);
		fwXHTML.write("<title>" + sTitle + "</title>", true, m_bMac);
		fwXHTML.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />", true, m_bMac);
		fwXHTML.write("</head>", true, m_bMac);
		fwXHTML.write("", true, m_bMac);
		fwXHTML.write("<body>", true, m_bMac);

		// header
		fwXHTML.write("<div id=\"Wrapper\">", true, m_bMac);
		fwXHTML.write("", true, m_bMac);
		fwXHTML.write("	<!-- ここからメインエリア -->", true, m_bMac);
		fwXHTML.write("	<div id=\"Main\">", true, m_bMac);
	}

	private void outputResult(XHTML_FileWriter fwXHTML, ControlText resultText, LongStyleManagerXHTML lsManager,
			IntermediateTextTreeWalker cTreeWalker) throws IOException {

		Iterator<IntermediateText> it = resultText.getChildList().iterator();
		IntermediateText iText_TreeWalker_Temp = null;
		while (it.hasNext()) {
			IntermediateText iText = it.next();

			// TreeWalkerに引っ越す用
			IntermediateText iText_dup = iText;
			IntermediateText iText_TreeWalker = null;
			if (iText_TreeWalker_Temp == null) {
				iText_TreeWalker = cTreeWalker.firstChild();
				iText_TreeWalker_Temp = iText_TreeWalker;
			} else {
				iText_TreeWalker = cTreeWalker.nextSibling();
				iText_TreeWalker_Temp = iText_TreeWalker;
			}
			if (iText_dup != iText_TreeWalker) {
				System.out.println("ERROR!");
				System.out.println("iText:");
				System.out.println(iText_dup.getDebugText());
				System.out.println("iText_TreeWalker:");
				System.out.println(iText_TreeWalker.getDebugText());
				System.out.println("Ooooooops;");
			}

			if (iText instanceof ControlText) {
				// 子供がいる＝ControlTextである
				ControlText cText = (ControlText) iText;
				Style currentStyle = cText.getStyle();

				lsManager.addStyle(currentStyle);

				/*
				 * ControlTextでも、手順・表の場合は少し特別な出力方法をとる
				 */
				// 表・行・セルの開始
				if (currentStyle != null && currentStyle.bTableLikeStyle()) {
					if (currentStyle.getStyleName().compareTo("【表】") == 0) {
						/* 注目しているcTextは表の始まりなので、Width,Heightを取得して処理を始める */
						TableWriter currentTable = new TableWriter(cText, cTreeWalker, m_bDebugMode);
						m_TableWriterList.add(currentTable);

						// lsManager.getStyle(cText)は、表を挿入する行のスタイルを返してくれる
						fwXHTML.write(currentTable.getTableHeader(lsManager, m_nLsIndex), false, m_bMac);
					} else if (currentStyle.getStyleName().compareTo("【行】") == 0) {
						TableWriter currentTable = m_TableWriterList.getLast();
						fwXHTML.write(currentTable.getRowHeader(lsManager), false, m_bMac);
					} else if (currentStyle.getStyleName().compareTo("【セル：ヘッダー】") == 0) {
						TableWriter currentTable = m_TableWriterList.getLast();
						fwXHTML.write(currentTable.getHeaderCellHeader(lsManager, cText), false, m_bMac);
					} else if (currentStyle.getStyleName().compareTo("【セル】") == 0) {
						TableWriter currentTable = m_TableWriterList.getLast();
						fwXHTML.write(currentTable.getCellHeader(lsManager, cText), false, m_bMac);
					}
				}

				Style_TagInfo style = lsManager.getStyle_TagInfo(cText, cTreeWalker, m_nLsIndex + 1);
				if (style != null && style.getBigBlockOpenInfo() != null) {
					fwXHTML.write(style.getBigBlockOpenInfo(), false, m_bMac);
				}
				outputResult(fwXHTML, cText, lsManager, cTreeWalker); // さらに奥深くへ（再帰）
				if (style != null && style.getBigBlockCloseInfo() != null) {
					fwXHTML.write(style.getBigBlockCloseInfo(), false, m_bMac);
				}

				// 表・行・セルの終了
				if (currentStyle != null && currentStyle.bTableLikeStyle()) {
					if (currentStyle.getStyleName().compareTo("【表】") == 0) {
						TableWriter currentTable = m_TableWriterList.getLast();
						fwXHTML.write(currentTable.getTableFooter(lsManager, m_nLsIndex), false, m_bMac);

						m_TableWriterList.removeLast(); // 表を1つ捨てる
						lsManager.setPrevLongStyle("【表】▲");
					} else if (currentStyle.getStyleName().compareTo("【行】") == 0) {
						TableWriter currentTable = m_TableWriterList.getLast();
						fwXHTML.write(currentTable.getRowFooter(lsManager), false, m_bMac);
					} else if (currentStyle.getStyleName().compareTo("【セル：ヘッダー】") == 0) {
						TableWriter currentTable = m_TableWriterList.getLast();
						fwXHTML.write(currentTable.getHeaderCellFooter(lsManager, cText), false, m_bMac);
					} else if (currentStyle.getStyleName().compareTo("【セル】") == 0) {
						TableWriter currentTable = m_TableWriterList.getLast();
						fwXHTML.write(currentTable.getCellFooter(lsManager, cText), false, m_bMac);
					}
				}
				lsManager.removeLastStyle();
			} else {
				// 子供がいない
				Style currentStyle = iText.getStyle();
				if (currentStyle != null) {
					// スタイルがある
					if (currentStyle.getStyleName().compareTo("【表】") == 0) {
						// 表の場合は、表を開始する「▼表(xx)」または、表を閉じる「▲」。
						// 今のところ何もしない
					} else if (iText.getText() != null) {
						// 表以外の場合は…

						// （共通）テキストを出力
						lsManager.addStyle(currentStyle); // スタイルをpush
						outputText(fwXHTML, lsManager, iText, cTreeWalker);
						lsManager.removeLastStyle(); // スタイルをpop
					}
				} else {
					// スタイルがないのでテキストを出力するのみ
					if (iText.getText() != null) {
						outputText(fwXHTML, lsManager, iText, cTreeWalker);
					}
				}
			}
		}
		cTreeWalker.parentNode();
	}

	private void outputText(XHTML_FileWriter fwXHTML, LongStyleManagerXHTML lsManager, IntermediateText iText,
			IntermediateTextTreeWalker cTreeWalker) {
		// エラーチェック
		if (iText instanceof ControlText) {
			System.out.println("outputText.iText.hasChild() error!");
			return; // ControlTextがここに来るはずがない
		}
		// 同期具合をチェックしている
		String realtimeStyle = lsManager.getLongStyle();
		String bufferingStyle = lsManager.getLongStyleFromArrayList(m_nLsIndex);
		if (realtimeStyle.compareTo(bufferingStyle) == 0) {
			// ok!
		} else {
			// NG!
			System.out.print(m_nLsIndex + ":");
			System.out.println("longStyle NG:" + realtimeStyle + "/" + bufferingStyle);
			System.out.println(iText.getText());
		}
		// ここまでエラーチェック
		// 結合指示セルは出力しない
		if (iText.getText().matches("【[上下左右]と結合】")) {
			m_nLsIndex++;
			return;
		}
		// sLongStyleを正しいスタイルに変換
		try {
			Style_TagInfo style = lsManager.getStyle_TagInfo(iText, cTreeWalker, m_nLsIndex + 1);
			if (style != null) {
				fwXHTML.write(style.getLineOpenInfo() + style.getLine() + style.getLineCloseInfo(), false, m_bMac);
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		m_nLsIndex++;
	}

	private void outputFooter(XHTML_FileWriter fwXHTML) throws IOException {
		fwXHTML.write("	</div>", true, m_bMac);
		fwXHTML.write("	<!-- ここまでメインエリア -->", true, m_bMac);
		// fwXHTML.write("", true, m_bMac);
		fwXHTML.write("</div>", true, m_bMac);
		fwXHTML.write("", true, m_bMac);
		fwXHTML.write("</body>", true, m_bMac);
		fwXHTML.write("</html>", true, m_bMac);
	}

}
