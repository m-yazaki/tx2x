package tx2x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import tx2x_core.ControlText;
import tx2x_core.IntermediateText;
import tx2x_core.Style;

/*
 * 第二次フォーマット（Tx2xテキストを整形する）
 */
public class IntermediateTextTreeBuilder {
	boolean m_bMac = true;
	private boolean m_bDebugMode;

	public IntermediateTextTreeBuilder(boolean bMac, boolean bDebugMode) {
		m_bMac = bMac;
		m_bDebugMode = bDebugMode;
	}

	public void parse_file(String sTextFilename, String sMaker)
			throws IOException {
		/*
		 * 作業用ArrayListを準備
		 */
		ArrayList<String> allText = new ArrayList<String>();

		/*
		 * Tx2x形式のテキストファイルをバッファ（ArrayList<String> allText）に読み込む
		 */
		try {
			// 入力ファイル
			File inputFile = new File(sTextFilename);
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					new FileInputStream(inputFile), "UTF-8"));

			String line;
			while ((line = bf.readLine()) != null) {
				// ループ
				allText.add(line);
			}

			bf.close();
		} catch (FileNotFoundException e1) {
			Tx2x.appendWarn("ファイルが見つかりません@IntermediateTextTreeBuilder："
					+ sTextFilename);
			return;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		/*
		 * allTextを解釈してIntermediateTextツリーを生成
		 * resultRootTextが、ツリーの根（ルートオブジェクト）です。
		 */
		ControlText resultRootText = new ControlText(null, null);

		/*
		 * 変換開始。
		 *
		 * allTextを解釈して、resultRootTextにIntermediateTextツリーを登録します。
		 */
		try {
			compileText(allText, resultRootText);
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			throw new IOException(e1.getMessage());
		}

		/*
		 * 結果出力（その1）
		 *
		 * コンソールへ出力します。簡易デバッグ用。
		 */
		/*
		 * LinkedList<Style> ruleLinkedList = new LinkedList<Style>(); try {
		 * outputResult(resultRootText, ruleLinkedList); //
		 * outputDump(resultRootText, ruleLinkedList, 0); } catch (IOException
		 * e) { // TODO 自動生成された catch ブロック e.printStackTrace(); }
		 */
		/*
		 * 結果出力（その2）
		 *
		 * InDesign用のタグ付きデータをファイルに出力します。
		 */
		String sOutputFilename;
		if (m_bMac) {
			sOutputFilename = sTextFilename.replaceFirst(".[Tt][Xx][Tt]$",
					".indesign.txt");
		} else {
			sOutputFilename = sTextFilename.replaceFirst(".[Tt][Xx][Tt]$",
					".win.indesign.txt");
		}
		if (sTextFilename.equals(sOutputFilename)) {
			System.out.println("上書きされるため中止しました。ファイル名を確認してください。");
		} else {
			IntermediateTextTreeToInDesign converter = new IntermediateTextTreeToInDesign(
					sOutputFilename, sMaker, m_bMac, m_bDebugMode);
			try {
				converter.output(resultRootText);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	/*
	 * allText（Tx2x形式のテキストファイル）を変換して、rootTextにぶら下げる
	 */
	private void compileText(ArrayList<String> allText, ControlText rootText)
			throws IOException {
		StyleManager cStyleManager = StyleManager.getInstance();
		for (int i = 0; i < allText.size();) {
			/*
			 * 1行読み取って、rbControlTextの決定
			 */
			if (m_bDebugMode) {
				System.out.println(allText.get(i));
			}
			Style styleControlText = cStyleManager.getMatchStyle_Start(allText
					.get(i));

			/*
			 * controlTextの生成
			 */
			ControlText controlText;
			if (styleControlText == null) {
				controlText = new ControlText(null, null);
			} else if (styleControlText.getStyleName().equals("【行】")) {
				throw new IOException("controlTextが【行】になりました。");
			} else {
				controlText = new ControlText(styleControlText,
						styleControlText.getStyleName());
			}

			/* controlTextをrootTextの子供に登録 */
			rootText.getChildList().add(controlText);

			/* 変換！ */
			try {
				int prev_i = i;
				i = compileBlock(controlText, allText, i);
				if (i == prev_i) {
					throw new IOException("i == prev_i");
				}
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				// e.printStackTrace();
				String error = "@compileText ===== 以下の文章から始まるブロックでエラー発生 =====\n";
				for (int j = 0; j < 3 && j < allText.size(); j++) {
					error += "|" + allText.get(j) + "\n";
				}
				error += "エラー発生行(" + (i + 1) + "行目): " + allText.get(i) + "\n";
				Tx2x.appendWarn("エラー発生行(" + (i + 1) + "行目)");

				throw new IOException(error + "　"
						+ e.getMessage().replaceAll("\n", "\n　"));
			}
		}
	}

	/*
	 * smallPartTextのstartPosから始まるブロックから、controlTextの子供にあたる部分を変換する
	 * 子供にあたる部分の変換が終わったら、次の読み取り行番号を返す。
	 */
	private int compileBlock(ControlText controlText,
			ArrayList<String> smallPartText, int startPos) throws IOException {

		/* controlTextのスタイルを取り出しておく */
		Style styleControlText = controlText.getStyle();

		/* ブロックを統括するIntermediateTextに、IntermediateTextを登録する先を準備 */
		ArrayList<IntermediateText> childArrayControlText = controlText
				.getChildList();

		/*
		 * これ以降、変換結果をchildArrayControlTextに登録する
		 */
		/* 初めの1行を処理する */
		if (styleControlText != null)
			startPos = styleControlText.compileLine(controlText, smallPartText,
					startPos);

		StyleManager cStyleManager = StyleManager.getInstance();

		/* **** 開始行以降を確認 **** */
		for (int currentPos = startPos; currentPos < smallPartText.size();) {

			if (styleControlText != null) {
				// System.out.println(styleControlText.getStyleName() + ":"
				// + smallPartText.get(currentPos));
			} else {
				// System.out.println("【本文】:" + smallPartText.get(currentPos));
			}

			String currentLine = smallPartText.get(currentPos);

			/* 入れ子のブロックがないか確認 */
			Style styleCurrentLine = cStyleManager
					.getMatchStyle_Start(currentLine);
			if (styleCurrentLine != null) {
				if (styleControlText == null) {
					/*
					 * 現象：本文ブロックで、スタイル行がきた 対応：本文ブロックの終了
					 */
					return currentPos; // 次のスタイル（ブロック）は、現在位置からであることを呼び出し元に伝える
				} else if (styleControlText.bBulletLikeStyle()) {
					if (styleCurrentLine == styleControlText) {
						// 現象：箇条書きタイプで、同じスタイルが続いた
						// 対応：controlTextに登録すべき子供であると解釈して、childArrayControlTextに追加。
						IntermediateText textCurrentLine = new IntermediateText(
								styleCurrentLine, currentLine); // 1行分
						childArrayControlText.add(textCurrentLine);
						currentPos++; // 次のループでは、次行を読む
						continue; // 続きもcontrolText内の本文である可能性があるため、continue。
					} else {
						/*
						 * 現象：箇条書きタイプで、別のスタイルが続いた
						 * 対応：controlTextが終了したと判断し、このメソッドの仕事は終了。
						 */
						return currentPos; // 次のスタイル（ブロック）は、現在位置からであることを呼び出し元に伝える
					}
				} else if (styleCurrentLine.bTableLikeStyle()) {
					if (styleControlText.bTableLikeStyle() == false) {
						/*
						 * 「お知らせ」直下の「表」など 対応：入れ子の表と解釈し、新しい表を作る
						 */
						/*
						 * nextControlTextの生成
						 */
						if (styleCurrentLine.getStyleName().equals("【行】")
								|| styleCurrentLine.getStyleName().equals(
										"【セル】")) {
							throw new IOException("▼表(n) が無いのに、" + currentLine
									+ "が来ました。");
						}
						ControlText nextControlText = new ControlText(
								styleCurrentLine,
								styleCurrentLine.getStyleName());

						/* controlTextの子供に登録 */
						controlText.getChildList().add(nextControlText);

						// 【表】の解釈を始める
						currentPos = compileBlock(nextControlText,
								smallPartText, currentPos);

						continue; // currentPosは「▲」の次の行を指しているはず
					}
					/*
					 * 確認中の行が表関連のスタイル
					 *
					 * 表スタイルの特性上、以下の6パターンしか存在し得ない
					 *
					 * (1)controlText：表 currentLine：行 …初めての行（1行目）の始まり
					 *
					 * (2)controlText：行 currentLine：行 …新しい行（2行目以降）の始まり
					 *
					 * (3)controlText：行 currentLine：セル …初めてのセル（1列目）の始まり
					 *
					 * (4)controlText：セル currentLine：表 …入れ子の表の始まり
					 *
					 * (5)controlText：セル currentLine：行 …最後のセルが終わり、2行目以降の始まり
					 *
					 * (6)controlText：セル currentLine：セル …新しいセル（1列目）の始まり
					 */
					if (styleCurrentLine == styleControlText) {
						/*
						 * 現象：controlTextと同じスタイルが続いている
						 */
						if (styleControlText.getStyleName().compareTo("【行】") == 0) {
							if (startPos == currentPos) {
								/*
								 * (現象2)controlText：行 currentLine：行 （表の1行目）
								 *
								 * (1)確認中の行が表関連のスタイル
								 * (2)controlTextと同じスタイル（【行】）である
								 * (3)現在のブロックの初めの一行のとき＝最初の【行】
								 *
								 * 対応：次のcurrentLineはセルのつもりで登録
								 */

								/* nextControlText（セル）の生成 */
								Style styleNextControlText = cStyleManager
										.getStyle("【セル】");

								ControlText nextControlText = new ControlText(
										styleNextControlText,
										styleNextControlText.getStyleName());

								/* controlTextの子供に登録 */
								controlText.getChildList().add(nextControlText);

								// currentPosは==========を指しているので、次の行から変換開始
								currentPos = compileBlock(nextControlText,
										smallPartText, currentPos + 1);

								// セルが終わったときは、
								continue; // currentPosは「-----」か「=========」か「▲」の次の行を指している
							} else {
								/*
								 * (現象2)controlText：行 currentLine：行 （表の2行目以降）
								 *
								 * 対応：controlTextが終了したと判断し、このメソッドの仕事は終了。
								 */
								return currentPos; // currentPosは「==========」を指している
							}
						} else if (styleControlText.getStyleName().compareTo(
								"【セル】") == 0) {
							/*
							 * (現象6)controlText：セル currentLine：セル
							 *
							 * 対応：controlTextが終了したと判断し、このメソッドの仕事は終了。
							 */
							return currentPos; // currentPosは「-----」を指している
						}
					} else {
						/* 現象：同じスタイルではない */
						if ((styleControlText.getStyleName().compareTo("【表】") == 0 && styleCurrentLine
								.getStyleName().compareTo("【行】") == 0)
								|| (styleControlText.getStyleName().compareTo(
										"【行】") == 0 && styleCurrentLine
										.getStyleName().compareTo("【セル】") == 0)) {
							/*
							 * (現象1)controlText：表 currentLine：行
							 *
							 * (現象3)controlText：行 currentLine：セル
							 *
							 * 対応：別のブロックが入れ子で始まったことにする
							 */
							/*
							 * nextControlTextの生成
							 */
							ControlText nextControlText = new ControlText(
									styleCurrentLine,
									styleCurrentLine.getStyleName());

							/* controlTextの子供に登録 */
							controlText.getChildList().add(nextControlText);

							// currentPosは==========や-----を指しているので、次の行から変換開始
							currentPos = compileBlock(nextControlText,
									smallPartText, currentPos + 1);

							continue; // currentPosは「-----」か「=========」か「▲」の次の行を指しているはず
						} else if ((styleControlText.getStyleName().compareTo(
								"【セル】") == 0 && styleCurrentLine.getStyleName()
								.compareTo("【表】") == 0)) {
							/*
							 * (現象4)controlText：セル currentLine：表
							 *
							 * 対応：入れ子の表と解釈し、新しい表を作る
							 */
							/*
							 * nextControlTextの生成
							 */
							ControlText nextControlText = new ControlText(
									styleCurrentLine,
									styleCurrentLine.getStyleName());

							/* controlTextの子供に登録 */
							controlText.getChildList().add(nextControlText);

							// 【表】の解釈を始める
							currentPos = compileBlock(nextControlText,
									smallPartText, currentPos);

							continue; // currentPosは「▲」の次の行を指しているはず
						} else {
							/*
							 * (現象5)controlText：セル currentLine：行
							 *
							 * 対応：controlTextが終了したと判断し、このメソッドの仕事は終了。
							 */
							return currentPos; // currentPosは「==========」を指している
						}
					}
				} else {
					// 現象：上記以外
					// 対応：別のブロックが入れ子で始まったことにする
					/*
					 * controlTextの生成
					 */
					ControlText nextControlText = new ControlText(
							styleCurrentLine, styleCurrentLine.getStyleName());

					/* controlTextの子供に登録 */
					controlText.getChildList().add(nextControlText);

					currentPos = compileBlock(nextControlText, smallPartText,
							currentPos);

					// compileBlockで、smallPartTextを全部読んでない？
					if (currentPos < smallPartText.size()) {
						continue; // 全部読んでないよ！
					} else {
						break; // 最後まで読んじゃった・・・？
					}
				}
				throw new IOException("スタイルが定義された行にもかかわらず処理されていません");
			}

			/* タブブロックの確認 */
			if (currentLine.length() > 0 && currentLine.charAt(0) == '\t') {
				// タブ文字で始まる行はすべてtabPartTextに入れる
				ArrayList<String> tabPartText = new ArrayList<String>();
				for (; currentPos < smallPartText.size(); currentPos++) {
					currentLine = smallPartText.get(currentPos);
					if (currentLine.length() > 0
							&& currentLine.charAt(0) == '\t')
						tabPartText.add(currentLine.substring(1));
					else
						break;
				}

				// タブブロックのcontrolText
				ControlText tabBlockControlText = new ControlText(
						styleControlText, null);

				compileText(tabPartText, tabBlockControlText);
				childArrayControlText.add(tabBlockControlText);
				continue;
			}

			/* ブロックが終了していないか確認 */
			if (styleControlText != null
					&& styleControlText.isMatch_Last(currentLine)) {
				// このブロックが終了した！
				if (styleControlText.bNoteLikeStyle()) {
					// ▼～▲タイプ
					// 最後の1行（▲など）を念のため登録する
					IntermediateText lastLineText = new IntermediateText(
							styleControlText, currentLine);
					childArrayControlText.add(lastLineText);
					return currentPos + 1; // currentPosは「▲」を指している
				} else if (styleControlText.bTableLikeStyle()) {
					// 表のタイプ
					if (styleControlText.getStyleName().compareTo("【表】") == 0) {
						// 最後の1行（▲など）を念のため登録する
						IntermediateText lastLineText = new IntermediateText(
								styleControlText, currentLine);
						childArrayControlText.add(lastLineText);
						return currentPos + 1; // currentPosは「▲」を指している
					} else if (styleControlText.getStyleName().equals("【セル】")) {
						return currentPos;
					} else {
						return currentPos;
					}
				}
				return currentPos; // currentPosは、終了が明らかになった行を指している。通常は次のスタイルの開始行である
			}

			// 始まりでも終わりでもないところ（つまるところただの本文）
			IntermediateText textBody = new IntermediateText(null, currentLine);
			childArrayControlText.add(textBody);
			currentPos++; // 次の行へ・・・
		}
		// 閉じる前にitがなくなっちゃった。。。
		if (styleControlText != null) {
			if (styleControlText.bNoteLikeStyle()
					|| styleControlText.bTableLikeStyle()) {
				Tx2x.appendWarn("ブロックが終了する前にテキストがなくなりました。");
				throw new IOException("ブロックが終了する前にテキストがなくなりました。\n"
						+ smallPartText);
			}
		}
		return smallPartText.size();
	}

	/* 出力テスト */
	// ブロックを統括するIntermediateTextを渡すこと
	@SuppressWarnings("unused")
	private void outputResult(ControlText controlText,
			LinkedList<Style> ruleLinkedList) throws IOException {
		ArrayList<IntermediateText> childArrayList = controlText.getChildList();
		if (childArrayList == null) {
			throw new IOException(
					"ブロックを統括するIntermediateTextに、childArrayListが設定されていない");
		}
		Iterator<IntermediateText> it = childArrayList.iterator();
		while (it.hasNext()) {
			IntermediateText iText = it.next();
			if (iText.hasChild()) {
				// 子供がいる＝ControlTextである
				ControlText cText = (ControlText) iText;
				Style rb = cText.getStyle();
				ruleLinkedList.addLast(cText.getStyle());
				if (rb != null
						&& (rb.bBulletLikeStyle() || rb.bTableLikeStyle())
						&& cText.getText() != null) {
					// 箇条書きスタイルの場合は、1行目をブロックを統括するIntermediateTextが保持している
					// テキストの出力
					outputText(ruleLinkedList, cText);
				}
				outputResult(cText, ruleLinkedList);
				ruleLinkedList.removeLast();
			} else {
				// 子供がいない＝IntermediateTextである
				Style rb = (Style) iText.getStyle();
				if (rb != null) {
					// スタイルがある
					// スタイルをプッシュしてテキストを出力
					if (iText.getText() != null) {
						ruleLinkedList.addLast(rb);
						outputText(ruleLinkedList, iText);
						ruleLinkedList.removeLast();
					}
				} else {
					// スタイルがない
					// テキストを出力するのみ
					if (iText.getText() != null) {
						outputText(ruleLinkedList, iText);
					}
				}
			}
		}
	}

	private void outputText(LinkedList<Style> ruleLinkedList,
			IntermediateText iText) {
		// String outputText = "";
		Iterator<Style> it = ruleLinkedList.iterator();
		while (it.hasNext()) {
			Style style = it.next();
			if (style == null) {
				// outputText += "【本文】";
			} else {
				// outputText += style.getStyleName();
			}
		}
		if (iText.getStyle() == null) {
			// System.out.println(outputText + "【本文】:" + iText.getText());
		} else {
			// System.out.println(outputText + ":" + iText.getText());
		}
	}
}
