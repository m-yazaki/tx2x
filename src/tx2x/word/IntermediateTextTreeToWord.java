/**
 * IntermediateTextを、InDesignのタグ付きテキストに変換する
 */
package tx2x.word;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import tx2x.StyleManager;
import tx2x.Tx2xOptions;
import tx2x.core.ControlText;
import tx2x.core.IntermediateText;
import tx2x.core.Style;
import tx2x.core.TableManager;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class IntermediateTextTreeToWord {
	LinkedList<TableWriter> m_TableWriterList = null;
	int m_nLsIndex = 0;
	private boolean m_bDebugMode;
	String m_sTemplateDoc = "Tx2xWordTemplate.dotx";
	private TableManager m_currentTable;

	public IntermediateTextTreeToWord(boolean bDebugMode) {
		super();
		m_TableWriterList = new LinkedList<TableWriter>();
		m_bDebugMode = bDebugMode;
	}

	public void output(File cWordFile, ControlText resultRootText)
			throws IOException {

		boolean tVisible = Tx2xOptions.getInstance().getBoolean("Visible");
		ActiveXComponent oWord = new ActiveXComponent("Word.Application");
		oWord.setProperty("Visible", new Variant(tVisible));
		Dispatch oDocuments = oWord.getProperty("Documents").toDispatch();
		Dispatch.call(oDocuments, "Add",
				cWordFile.getParent() + "\\" + m_sTemplateDoc).toDispatch();
		Dispatch oSelection = oWord.getProperty("Selection").toDispatch();

		// 書き込み
		LongStyleManager lsManager = new LongStyleManager(oSelection);
		preScan(resultRootText, lsManager); // プレスキャン。lsManagerにスタイル情報（longStyle）のArrayListを準備する
		outputResult(oSelection, resultRootText, lsManager);

		// 保存
		try {
			Dispatch oDocument = Dispatch.call(oSelection, "Document")
					.toDispatch();
			Dispatch.call(oDocument, "SaveAs2", cWordFile.getAbsolutePath(), 12 /* wdFormatXMLDocument */);
		} catch (ComFailException e) {
			System.out.println("---------- error ----------\n"
					+ e.getLocalizedMessage() + "---------------------------");
		}
		oWord.setProperty("Visible", new Variant(true));
	}

	private void preScan(ControlText resultText, LongStyleManager lsManager) {
		// TODO 自動生成されたメソッド・スタブ
		Iterator<IntermediateText> it = resultText.getChildList().iterator();
		while (it.hasNext()) {
			IntermediateText iText = it.next();
			if (iText.hasChild()) {
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
						if (m_bDebugMode)
							System.out.println("【表】");
						/* 注目しているcTextは表の始まりなので、Width,Heightを取得して処理を始める */
						TableManager currentTable = new TableManager(cText,
								m_bDebugMode);
						TableWriter tWriter = new TableWriter(currentTable);
						m_TableWriterList.add(tWriter);
					} else if (currentStyle.getStyleName().compareTo("【行】") == 0) {
						// TableWriter tWriter = m_TableWriterList.getLast();
						if (m_bDebugMode)
							System.out.println("【行】");
					} else if (currentStyle.getStyleName().compareTo("【セル】") == 0) {
						// TableWriter tWriter = m_TableWriterList.getLast();
						if (m_bDebugMode)
							System.out.println("【セル】");
						if (cText.getChildList().get(0).getText()
								.matches(".*【ヘッダー】.*")) {
							StyleManager styleManager = StyleManager
									.getInstance();
							Style newStyle = styleManager.getStyle("【セル：ヘッダー】");
							lsManager.removeLastStyle();
							lsManager.addStyle(newStyle);
						}
					}
				}
				preScan(cText, lsManager); // さらに奥深くへ（再帰）
				// 表・行・セルの終了
				if (currentStyle != null && currentStyle.bTableLikeStyle()) {
					if (currentStyle.getStyleName().compareTo("【表】") == 0) {
						m_TableWriterList.removeLast(); // 表終了
						lsManager.setPrevLongStyle("【表】▲");
					} else if (currentStyle.getStyleName().compareTo("【行】") == 0) {
					} else if (currentStyle.getStyleName().compareTo("【セル】") == 0) {
					}
				}
				lsManager.removeLastStyle();
			} else {
				// 子供がいない
				Style currentStyle = iText.getStyle();
				if (currentStyle != null) {
					// スタイルがある
					if (currentStyle.getStyleName().compareTo("【表】") == 0) {
						// 表の場合は何もしない…？
					} else if (iText.getText() != null) {
						// 表以外の場合は…

						// （共通）テキストを出力
						lsManager.addStyle(currentStyle); // スタイルをpush
						lsManager.addLongStyleToArrayList();
						lsManager.removeLastStyle(); // スタイルをpop
					}
				} else {
					// スタイルがないのでテキストを出力するのみ
					if (iText.getText() != null) {
						lsManager.addLongStyleToArrayList();
					}
				}
			}
		}
	}

	private void outputResult(Dispatch oSelection, ControlText resultText,
			LongStyleManager lsManager) throws IOException {
		Iterator<IntermediateText> it = resultText.getChildList().iterator();
		while (it.hasNext()) {
			IntermediateText iText = it.next();
			if (iText.hasChild()) {
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
						m_currentTable = new TableManager(cText, m_bDebugMode);
						TableWriter tWriter = new TableWriter(m_currentTable);
						m_TableWriterList.add(tWriter);
						tWriter.write(oSelection);
					} else if (currentStyle.getStyleName().compareTo("【行】") == 0) {
						TableWriter tWriter = m_TableWriterList.getLast();
						tWriter.selectNextRow();
					} else if (currentStyle.getStyleName().compareTo("【セル】") == 0) {
						TableWriter tWriter = m_TableWriterList.getLast();
						tWriter.selectNextCell();

						if (cText.getChildList().get(0).getText()
								.matches(".*【ヘッダー】.*")) {
							StyleManager styleManager = StyleManager
									.getInstance();
							Style newStyle = styleManager.getStyle("【セル：ヘッダー】");
							lsManager.removeLastStyle();
							lsManager.addStyle(newStyle);
						}
					}
				}
				outputResult(oSelection, cText, lsManager); // さらに奥深くへ（再帰）
				// 表・行・セルの終了
				if (currentStyle != null && currentStyle.bTableLikeStyle()) {
					if (currentStyle.getStyleName().compareTo("【表】") == 0) {
						Dispatch.call(oSelection, "MoveDown"); // 下へ移動
						m_TableWriterList.removeLast(); // 表終了
						lsManager.setPrevLongStyle("【表】▲");

					} else if (currentStyle.getStyleName().compareTo("【行】") == 0) {
					} else if (currentStyle.getStyleName().compareTo("【セル】") == 0) {
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
						outputText(oSelection, lsManager, iText);
						lsManager.removeLastStyle(); // スタイルをpop
					}
				} else {
					// スタイルがないのでテキストを出力するのみ
					if (iText.getText() != null) {
						outputText(oSelection, lsManager, iText);
					}
				}
			}
		}
	}

	private void outputText(Dispatch oSelection, LongStyleManager lsManager,
			IntermediateText iText) {
		if (iText.hasChild()) {
			// System.out.println("outputText:" + iText.getText());
			return; // ControlTextはカエレ！
		}
		// System.out.println("outputText");
		String realtimeStyle = lsManager.getLongStyle();
		String bufferingStyle = lsManager.getLongStyleFromArrayList(m_nLsIndex);
		if (realtimeStyle.compareTo(bufferingStyle) == 0) {
			// ok!
			// System.out.println("longStyle OK");
		} else {
			// NG!
			System.out.println("longStyle NG:" + realtimeStyle + "/"
					+ bufferingStyle);
		}
		// iTextとsLongStyleから必要な処理を行う
		try {
			String style = lsManager.getTargetStyle(iText, m_nLsIndex + 1);
			lsManager.writeTargetData(oSelection, style, iText, m_nLsIndex + 1);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		lsManager.setPrevLongStyle();
		m_nLsIndex++;
	}
}
