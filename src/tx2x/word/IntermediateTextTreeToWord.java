/**
 * IntermediateTextを、Wordデータに変換する
 */
package tx2x.word;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import tx2x.IntermediateTextTreeWalker;
import tx2x.Tx2xOptions;
import tx2x.core.ControlText;
import tx2x.core.IntermediateText;
import tx2x.core.Style;
import tx2x.core.TableManager;

public class IntermediateTextTreeToWord {
	LinkedList<TableWriter> m_TableWriterList = null;
	int m_nLsIndex = 0;
	private boolean m_bDebugMode;
	String m_sTemplateDoc = "Tx2xWordTemplate.dotx";

	public IntermediateTextTreeToWord(boolean bDebugMode) {
		super();
		m_TableWriterList = new LinkedList<TableWriter>();
		m_bDebugMode = bDebugMode;
	}

	public void output(File cWordFile, ControlText resultRootText, LongStyleManagerWord lsManager,
			IntermediateTextTreeWalker cTreeWalker) {

		boolean tVisible = Tx2xOptions.getInstance().getBoolean("Visible");
		ActiveXComponent oWord = new ActiveXComponent("Word.Application");
		oWord.setProperty("Visible", new Variant(tVisible));
		Dispatch oDocuments = oWord.getProperty("Documents").toDispatch();
		try {
			Dispatch.call(oDocuments, "Add", cWordFile.getParent() + "\\" + m_sTemplateDoc).toDispatch();
			Dispatch oSelection = oWord.getProperty("Selection").toDispatch();

			// 書き込み
			outputResult(oSelection, resultRootText, lsManager, cTreeWalker);

			// 保存
			Dispatch oDocument = Dispatch.call(oSelection, "Document").toDispatch();
			Dispatch.call(oDocument, "SaveAs2", cWordFile.getAbsolutePath(), 12 /* wdFormatXMLDocument */);

			oWord.setProperty("Visible", new Variant(true));
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ComFailException e) {
			System.out.println("---------- error ----------\n" + e.getLocalizedMessage());
			if (e.getLocalizedMessage().indexOf("Description: ファイルが見つかりません。") != -1) {
				System.out.println(cWordFile.getParent() + "\\" + m_sTemplateDoc);
			}
			System.out.println("---------------------------");
		}
	}

	private void outputResult(Dispatch oSelection, ControlText resultText, LongStyleManagerWord lsManager,
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
						TableManager currentTable = new TableManager(cText, cTreeWalker, m_bDebugMode);
						TableWriter tWriter = new TableWriter(currentTable);
						m_TableWriterList.add(tWriter);
						tWriter.write(oSelection);
						tWriter.setIndent(lsManager, m_nLsIndex);
					} else if (currentStyle.getStyleName().compareTo("【行】") == 0) {
						TableWriter tWriter = m_TableWriterList.getLast();
						tWriter.selectNextRow();
					} else if (currentStyle.getStyleName().equals("【セル：ヘッダー】")) {
						TableWriter tWriter = m_TableWriterList.getLast();
						tWriter.selectNextCell();
					} else if (currentStyle.getStyleName().compareTo("【セル】") == 0) {
						TableWriter tWriter = m_TableWriterList.getLast();
						tWriter.selectNextCell();
					}
				}

				ControlTextWriter cControlTextWriter = new ControlTextWriter();
				cControlTextWriter.writeBigBlockOpenInfo(oSelection, lsManager, cText);
				outputResult(oSelection, cText, lsManager, cTreeWalker); // さらに奥深くへ（再帰）
				cControlTextWriter.writeBigBlockCloseInfo(oSelection, lsManager, cText);

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
						outputText(oSelection, lsManager, iText, cTreeWalker);
						lsManager.removeLastStyle(); // スタイルをpop
					}
				} else {
					// スタイルがないのでテキストを出力するのみ
					if (iText.getText() != null) {
						outputText(oSelection, lsManager, iText, cTreeWalker);
					}
				}
			}
		}
		cTreeWalker.parentNode();
	}

	private void outputText(Dispatch oSelection, LongStyleManagerWord lsManager, IntermediateText iText,
			IntermediateTextTreeWalker cTreeWalker) {
		if (iText instanceof ControlText) {
			// System.out.println("outputText:" + iText.getText());
			return; // ControlTextはカエレ！
		}
		// System.out.println("outputText");
		String realtimeStyle = lsManager.getLongStyle();
		String bufferingStyle = lsManager.getLongStyleFromArrayList(m_nLsIndex);

		// getLongStyleFromArrayList()のデバッグ用コード
		if (realtimeStyle.compareTo(bufferingStyle) == 0) {
			// ok!
			// System.out.println("longStyle OK");
		} else {
			// NG!
			System.out.println("longStyle NG:" + realtimeStyle + "/" + bufferingStyle);
		}

		// iTextとsLongStyleから必要な処理を行う
		try {
			String style = lsManager.getTargetStyle(iText, m_nLsIndex + 1);
			lsManager.writeTargetIntermediateText(oSelection, style, iText, m_nLsIndex + 1, cTreeWalker);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		lsManager.setPrevLongStyle();
		m_nLsIndex++;
	}
}
