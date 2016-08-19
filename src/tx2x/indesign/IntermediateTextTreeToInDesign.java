/**
 * IntermediateTextを、InDesignのタグ付きテキストに変換する
 */
package tx2x.indesign;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import tx2x.IntermediateTextTreeWalker;
import tx2x.core.ControlText;
import tx2x.core.IntermediateText;
import tx2x.core.Style;
import tx2x.core.TableManager;

public class IntermediateTextTreeToInDesign {
	LinkedList<TableWriter> m_TableWriterList = null;
	private boolean m_bMac;
	int m_nLsIndex = 0;
	private boolean m_bDebugMode;

	public IntermediateTextTreeToInDesign(boolean bMac, boolean bDebugMode) {
		this();
		m_bMac = bMac;
		m_bDebugMode = bDebugMode;
	}

	public IntermediateTextTreeToInDesign() {
		m_TableWriterList = new LinkedList<TableWriter>();
	}

	public void output(File cInDesign, ControlText resultRootText, LongStyleManagerInDesign lsManager,
			IntermediateTextTreeWalker cTreeWalker) throws IOException {
		InDesignTT_FileWriter fwInDesign;
		try {
			fwInDesign = new InDesignTT_FileWriter(cInDesign, m_bMac);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return;
		}

		// 書き込み
		preScan(resultRootText, lsManager, cTreeWalker); // プレスキャン。lsManagerにスタイル情報（longStyle）のArrayListを準備する
		outputHeader(fwInDesign);
		outputResult(fwInDesign, resultRootText, lsManager, cTreeWalker);

		try {
			fwInDesign.close();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}

	private void preScan(ControlText resultText, LongStyleManagerInDesign lsManager,
			IntermediateTextTreeWalker cTreeWalker) {
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
						if (m_bDebugMode)
							System.out.println("【表】");
						// String sStyle = "";
						// if (sTableInfo.indexOf("style:") != -1) {
						// sStyle = sTableInfo.replaceFirst(
						// ".*style:([^),]+.*)", "$1");
						// }

						/* 注目しているcTextは表の始まりなので、Width,Heightを取得して処理を始める */
						TableManager currentTable = new TableManager(cText, cTreeWalker, m_bDebugMode);
						TableWriter tWriter = new TableWriter(currentTable);
						m_TableWriterList.add(tWriter);

					} else if (currentStyle.getStyleName().equals("【行】")) {
						// TableWriter tWriter = m_TableWriterList.getLast();
						if (m_bDebugMode)
							System.out.println("【行】");
					} else if (currentStyle.getStyleName().equals("【セル：ヘッダー】")) {
						// TableWriter tWriter = m_TableWriterList.getLast();
						if (m_bDebugMode)
							System.out.println("【セル：ヘッダー】");
					} else if (currentStyle.getStyleName().equals("【セル】")) {
						// TableWriter tWriter = m_TableWriterList.getLast();
						if (m_bDebugMode)
							System.out.println("【セル】");
					}
				}
				preScan(cText, lsManager, cTreeWalker); // さらに奥深くへ（再帰）
				// 表・行・セルの終了
				if (currentStyle != null && currentStyle.bTableLikeStyle()) {
					if (currentStyle.getStyleName().equals("【表】")) {
						m_TableWriterList.removeLast(); // 表終了
						lsManager.setPrevLongStyle("【表】▲");
					} else if (currentStyle.getStyleName().equals("【行】")) {
					} else if (currentStyle.getStyleName().equals("【セル】")) {
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
						lsManager.addLongStyleToLongStyleArrayList();
						lsManager.removeLastStyle(); // スタイルをpop
					}
				} else {
					// スタイルがないのでテキストを出力するのみ
					if (iText.getText() != null) {
						lsManager.addLongStyleToLongStyleArrayList();
					}
				}
			}
		}
		cTreeWalker.parentNode();
	}

	private void outputHeader(InDesignTT_FileWriter fwInDesign) throws IOException {
		if (m_bMac == true) {
			// fwInDesign.write("<SJIS-MAC>", true);
			fwInDesign.write("<UNICODE-MAC>", true);
		} else {
			// fwInDesign.write("<SJIS-WIN>", true);
			fwInDesign.write("<UNICODE-WIN>", true);
		}
		fwInDesign.write("<Version:7><FeatureSet:InDesign-Japanese><ColorTable:=>", true);
	}

	private void outputResult(InDesignTT_FileWriter fwInDesign, ControlText resultText,
			LongStyleManagerInDesign lsManager, IntermediateTextTreeWalker cTreeWalker) throws IOException {
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
						TableManager currentTable = new TableManager(cText, cTreeWalker, m_bDebugMode);
						TableWriter tWriter = new TableWriter(currentTable);
						m_TableWriterList.add(tWriter);

						// coStartを出力
						// lsManager.getInDesignStyle(cText)は、表を挿入する行のスタイルを返してくれる
						fwInDesign.write(lsManager.getInDesignStyle(cText, m_nLsIndex + 1)
								+ tWriter.getHeader(lsManager, m_nLsIndex), false);
					} else if (currentStyle.getStyleName().compareTo("【行】") == 0) {
						TableWriter tWriter = m_TableWriterList.getLast();
						fwInDesign.write(tWriter.getRowHeader(lsManager), false);
					} else if (currentStyle.getStyleName().compareTo("【セル：ヘッダー】") == 0) {
						TableWriter tWriter = m_TableWriterList.getLast();
						fwInDesign.write(tWriter.getCellHeader(lsManager), false);
					} else if (currentStyle.getStyleName().compareTo("【セル】") == 0) {
						TableWriter tWriter = m_TableWriterList.getLast();
						fwInDesign.write(tWriter.getCellHeader(lsManager), false);
					}
				}

				ControlTextWriter cControlTextWriter = new ControlTextWriter();
				cControlTextWriter.writeBigBlockOpenInfo(fwInDesign, lsManager, cText);
				outputResult(fwInDesign, cText, lsManager, cTreeWalker); // さらに奥深くへ（再帰）
				cControlTextWriter.writeBigBlockCloseInfo(fwInDesign, lsManager, cText);

				// 表・行・セルの終了
				if (currentStyle != null && currentStyle.bTableLikeStyle()) {
					if (currentStyle.getStyleName().compareTo("【表】") == 0) {
						fwInDesign.write("<TableEnd:>", true);
						m_TableWriterList.removeLast(); // 表終了
						lsManager.setPrevLongStyle("【表】▲");
					} else if (currentStyle.getStyleName().compareTo("【行】") == 0) {
						fwInDesign.write("<RowEnd:>", false);
					} else if (currentStyle.getStyleName().compareTo("【セル：ヘッダー】") == 0) {
						fwInDesign.write("<CellEnd:>", false);
					} else if (currentStyle.getStyleName().compareTo("【セル】") == 0) {
						fwInDesign.write("<CellEnd:>", false);
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
						outputText(fwInDesign, lsManager, iText);
						lsManager.removeLastStyle(); // スタイルをpop
					}
				} else {
					// スタイルがないのでテキストを出力するのみ
					if (iText.getText() != null) {
						outputText(fwInDesign, lsManager, iText);
					}
				}
			}
		}
		cTreeWalker.parentNode();
	}

	private void outputText(InDesignTT_FileWriter fwInDesign, LongStyleManagerInDesign lsManager,
			IntermediateText iText) {
		if (iText instanceof ControlText) {
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
			System.out.println("longStyle NG:" + realtimeStyle + "/" + bufferingStyle);
		}
		// sLongStyleを正しいスタイルに変換
		try {
			String style = lsManager.getInDesignStyle(iText, m_nLsIndex + 1);
			if (style.equals("") == false) {
				if (iText.getText() != null) {
					fwInDesign.write(style + iText.getText(), true);
				} else {
					fwInDesign.write(style, false);
				}
				if (m_bDebugMode)
					System.out.println("[" + style + "]" + iText.getText());
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		m_nLsIndex++;
	}
}
