/**
 * IntermediateTextを、InDesignのタグ付きテキストに変換する
 */
package tx2x;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import tx2x_core.ControlText;
import tx2x_core.IntermediateText;
import tx2x_core.Style;
import tx2x_core.TableManager;

public class IntermediateTextTreeToInDesign {
	String m_sTagFilename = null;
	LinkedList<TableWriter> m_TableWriterList = null;
	private boolean m_bMac;
	int m_nLsIndex = 0;
	private String m_sMaker;
	private boolean m_bDebugMode;

	public IntermediateTextTreeToInDesign(String tagFilename, String sMaker,
			boolean bMac, boolean bDebugMode) {
		super();
		m_sTagFilename = tagFilename;
		m_TableWriterList = new LinkedList<TableWriter>();
		m_bMac = bMac;
		m_sMaker = sMaker;
		m_bDebugMode = bDebugMode;
	}

	void output(ControlText resultRootText) throws IOException {
		IDTTG_FileWriter fwInDesign;
		File aInDesign = new File(m_sTagFilename);
		try {
			fwInDesign = new IDTTG_FileWriter(aInDesign);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return;
		}

		// 書き込み
		LongStyleManager lsManager = new LongStyleManager(m_sMaker, m_bMac);
		preScan(resultRootText, lsManager); // プレスキャン。lsManagerにスタイル情報（longStyle）のArrayListを準備する
		outputHeader(fwInDesign);
		outputResult(fwInDesign, resultRootText, lsManager);

		try {
			fwInDesign.close(m_bMac);
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
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
						// widthを取得
						String sTableInfo = cText.getChildList().get(0)
								.getText();
						String sWidth = sTableInfo.replaceFirst(
								"▼表[\\(（]([0-9]+).*", "$1");
						// String sStyle = "";
						// if (sTableInfo.indexOf("style:") != -1) {
						// sStyle = sTableInfo.replaceFirst(
						// ".*style:([^),]+.*)", "$1");
						// }
						if (Integer.parseInt(sWidth) == 0) {
							Tx2x.appendWarn("sWidth==0");
						}

						// // heightを取得
						// ArrayList<IntermediateText> child = cText
						// .getChildList();
						// int nHeight;
						// if (child == null) {
						// nHeight = 0;
						// } else {
						// nHeight = child.size() - 2;
						// }

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

	private void outputHeader(IDTTG_FileWriter fwInDesign) throws IOException {
		if (m_bMac == true) {
			// fwInDesign.write("<SJIS-MAC>", true, m_bMac);
			fwInDesign.write("<UNICODE-MAC>", true, m_bMac);
		} else {
			// fwInDesign.write("<SJIS-WIN>", true, m_bMac);
			fwInDesign.write("<UNICODE-WIN>", true, m_bMac);
		}
		fwInDesign.write(
				"<Version:7><FeatureSet:InDesign-Japanese><ColorTable:=>",
				true, m_bMac);
	}

	private void outputResult(IDTTG_FileWriter fwInDesign,
			ControlText resultText, LongStyleManager lsManager)
			throws IOException {
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
						// widthを取得
						String sTableInfo = cText.getChildList().get(0)
								.getText();
						String sWidth = sTableInfo.replaceFirst(
								"▼表[\\(（]([0-9]+).*", "$1");
						// String sStyle = "";
						// if (sTableInfo.indexOf("style:") != -1) {
						// sStyle = sTableInfo.replaceFirst(
						// ".*style:([^),]+).*", "$1");
						// }
						if (Integer.parseInt(sWidth) == 0) {
							Tx2x.appendWarn("sWidth==0");
						}

						// // heightを取得
						// ArrayList<IntermediateText> child = cText
						// .getChildList();
						// int nHeight;
						// if (child == null) {
						// nHeight = 0;
						// } else {
						// nHeight = child.size() - 2;
						// }

						/* 注目しているcTextは表の始まりなので、Width,Heightを取得して処理を始める */
						TableManager currentTable = new TableManager(cText,
								m_bDebugMode);
						TableWriter tWriter = new TableWriter(currentTable);
						m_TableWriterList.add(tWriter);

						// coStartを出力
						// lsManager.getInDesignStyle(cText)は、表を挿入する行のスタイルを返してくれる
						fwInDesign.write(
								lsManager.getInDesignStyle(cText,
										m_nLsIndex + 1)
										+ tWriter.getHeader(lsManager,
												m_nLsIndex), false, m_bMac);
					} else if (currentStyle.getStyleName().compareTo("【行】") == 0) {
						TableWriter tWriter = m_TableWriterList.getLast();
						fwInDesign.write(tWriter.getRowHeader(lsManager),
								false, m_bMac);
					} else if (currentStyle.getStyleName().compareTo("【セル】") == 0) {
						TableWriter tWriter = m_TableWriterList.getLast();
						fwInDesign.write(tWriter.getCellHeader(lsManager),
								false, m_bMac);

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
				outputResult(fwInDesign, cText, lsManager); // さらに奥深くへ（再帰）
				// 表・行・セルの終了
				if (currentStyle != null && currentStyle.bTableLikeStyle()) {
					if (currentStyle.getStyleName().compareTo("【表】") == 0) {
						fwInDesign.write("<TableEnd:>", true, m_bMac);
						m_TableWriterList.removeLast(); // 表終了
						lsManager.setPrevLongStyle("【表】▲");
					} else if (currentStyle.getStyleName().compareTo("【行】") == 0) {
						fwInDesign.write("<RowEnd:>", false, m_bMac);
					} else if (currentStyle.getStyleName().compareTo("【セル】") == 0) {
						fwInDesign.write("<CellEnd:>", false, m_bMac);
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
	}

	private void outputText(IDTTG_FileWriter fwInDesign,
			LongStyleManager lsManager, IntermediateText iText) {
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
		// sLongStyleを正しいスタイルに変換
		try {
			String style = lsManager.getInDesignStyle(iText, m_nLsIndex + 1);
			if (style.equals("") == false) {
				if (iText.getText() != null) {
					fwInDesign.write(style + iText.getText(), true, m_bMac);
				} else {
					fwInDesign.write(style, false, m_bMac);
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
