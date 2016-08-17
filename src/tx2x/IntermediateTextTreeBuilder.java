package tx2x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tx2x.core.ControlText;
import tx2x.core.IntermediateText;
import tx2x.core.Style;
import tx2x.core.Style_TableCell;

/**
 * Tx2xテキストからIntermediateTextツリーを作成する<br />
 * ※variable処理やtarget処理など、Tx2xテキストの整形も担当する
 */
public class IntermediateTextTreeBuilder {
	private static final char TAB_CHAR = '\t';
	private boolean m_bDebugMode;
	private int m_nTargetLevel;
	private HashMap<String, String> m_cVariables = new HashMap<String, String>();
	private ArrayList<String> m_cTarget;

	// IntermediateTextTreeBuilderのサブクラスで、m_cStyleManagerを上書きできる仕組みを提供
	protected StyleManager m_cStyleManager;

	public IntermediateTextTreeBuilder(boolean bDebugMode) {
		m_bDebugMode = bDebugMode;

		// tx2x標準のStyleManager()を登録する
		StyleManagerFactory cFactory = StyleManagerFactory.getInstance();
		m_cStyleManager = new StyleManager();
		cFactory.regist(m_cStyleManager);
	}

	public ControlText parse_file(File cInputTx2xFile, LongStyleManager lsManager)
			throws IOException, IntermediateTextTreeBuildException {
		/*
		 * 作業用ArrayListを準備
		 */
		ArrayList<String> allText = new ArrayList<String>();

		/*
		 * Tx2x形式のテキストファイルをバッファ（ArrayList<String> allText）に読み込む
		 */
		BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(cInputTx2xFile), "UTF-8"));
		// BOMの先読みに対応する
		bf.mark(10);
		char[] buffer = new char[1];
		bf.read(buffer, 0, 1);
		if (buffer[0] != (char) 0xfeff) {
			bf.reset(); // BOMの場合は巻き戻し
		}

		m_nTargetLevel = 0;
		String line;
		// addLineToAllText(allText, line);
		while ((line = bf.readLine()) != null) {
			addLineToAllText(allText, line);
		}

		bf.close();

		return parseTextArrayList(allText, lsManager);
	}

	private void addLineToAllText(ArrayList<String> allText, String line) {
		if (m_cTarget != null) {
			// <var>変数処理
			line = changeVariables(line);
			// <target>ターゲット処理
			line = checkTarget(line);
		}
		if (line != null) {
			allText.add(line);
		}
	}

	private String checkTarget(String currentText) {
		Pattern pTarget = Pattern.compile("<target (?<mode>if|unless)=\"(?<target>[^\"]+)\">");
		Matcher matcher = pTarget.matcher(currentText);
		if (matcher.find()) {
			String sMode = matcher.group("mode");
			String sTargetCondition = matcher.group("target");
			if (sMode.equals("if")) {
				if (m_cTarget.indexOf(sTargetCondition) == -1) {
					m_nTargetLevel++; // 出力しないフラグ
				} else {
					if (m_nTargetLevel > 0)
						m_nTargetLevel--;
				}
			} else if (sMode.equals("unless")) {
				if (m_cTarget.indexOf(sTargetCondition) == -1) {
					if (m_nTargetLevel > 0)
						m_nTargetLevel--;
				} else {
					m_nTargetLevel++; // 出力しないフラグ
				}
			}
			return null; // <target>行は出力しない
		}

		if (currentText.indexOf("</target>") != -1) { // </target>行は出力しない
			if (m_nTargetLevel > 0)
				m_nTargetLevel--;
			return null;
		}
		if (m_nTargetLevel > 0)
			return null;
		return currentText;
	}

	private String changeVariables(String currentText) {
		int already_input = 0;
		Pattern pVar = Pattern.compile("<var name=\"(?<varname>[^\"]+)\" />");
		Matcher matcher = pVar.matcher(currentText);
		String sRet = "";
		while (matcher.find()) {
			if (already_input < matcher.start()) {
				sRet = sRet + currentText.substring(already_input, matcher.start());
			}
			already_input = matcher.end();
			String sVarname = matcher.group("varname");

			sRet = sRet + m_cVariables.get(sVarname);
		}
		if (currentText.substring(already_input).equals("") == false)
			sRet = sRet + currentText.substring(already_input);
		return sRet;
	}

	/**
	 * テキストを解釈してIntermediateTextツリーを生成します。
	 *
	 * @param allText
	 *            テキスト
	 * @param lsManager
	 *            LongStyleManager
	 * @return ツリーの根（ルートオブジェクト）
	 * @throws IntermediateTextTreeBuildException
	 */
	public ControlText parseTextArrayList(ArrayList<String> allText, LongStyleManager lsManager)
			throws IntermediateTextTreeBuildException {
		ControlText resultRootText = new ControlText(StyleManager.getRootStyle());
		compileBlock(allText, resultRootText); // 第一次コンパイル（共通処理）
		preFormatControlText(resultRootText); // 整形
		lastScan(resultRootText, lsManager); // 最終スキャン
		return resultRootText;
	}

	/*
	 * allText（Tx2x形式のテキストファイル）を変換して、cGrandParentTextにぶら下げる
	 *
	 * allTextすべてを変換する責任を持つ （表を除く）
	 */
	private void compileBlock(ArrayList<String> allText, ControlText cGrandParentText)
			throws IntermediateTextTreeBuildException {
		for (int i = 0; i < allText.size();) {
			/*
			 * 1行目を読み取って、styleParentTextの決定
			 */
			String currentLine = allText.get(i);
			Style styleParentText = m_cStyleManager.getMatchStyle_Start(currentLine);

			/*
			 * controlText（cParentText：親）の生成と、cGrandParentTextへの登録
			 */
			ControlText cParentText = new ControlText(styleParentText);
			cGrandParentText.getChildList().add(cParentText);

			/* 変換！ */
			try {
				int prev_i = i;
				i = compileChildBlockFromStartPos(allText, i, styleParentText, cParentText);
				if (i == prev_i) {
					throw new IntermediateTextTreeBuildException("i == prev_i");
				}
			} catch (IntermediateTextTreeBuildException e) {
				// e.printStackTrace();
				String error = "@compileText ===== 以下の文章から始まるブロックでエラー発生 =====" + Tx2x.getMessageCRLF();
				for (int j = 0; j < 3 && j < allText.size(); j++) {
					error += "|" + allText.get(j) + Tx2x.getMessageCRLF();
				}
				error += "エラー発生行(" + (i + 1) + "行目): " + allText.get(i) + Tx2x.getMessageCRLF();
				Tx2x.appendWarn("エラー発生行(" + (i + 1) + "行目)");

				throw new IntermediateTextTreeBuildException(
						error + "　" + e.getMessage().replaceAll(Tx2x.getMessageCRLF(), Tx2x.getMessageCRLF() + "　"));
			}
		}
	}

	/***
	 * smallPartTextのstartPosから始まるブロックから、controlTextの子供にあたる部分を変換する
	 * 子供にあたる部分の変換が終わったら、次の読み取り行番号を返す。
	 *
	 * 1行目から始まるブロックが終わるまでを変換する責任を持つ
	 *
	 * 引数：
	 * <ul>
	 * <li>smallPartText, startPos：始まりに関する情報。1行目はstartPos = 0
	 * <li>cLastStyle：終わりに関する情報
	 * <li>cGrandParentText：追加先
	 * </ul>
	 *
	 * @throws IntermediateTextTreeBuildException
	 */
	private int compileChildBlockFromStartPos(ArrayList<String> smallPartText, int startPos, Style cLastStyle,
			ControlText cGrandParentText) throws IntermediateTextTreeBuildException {

		/* ControlTextのスタイルを取り出しておく */
		Style styleGrandParentText = cGrandParentText.getStyle();

		/* ブロックを統括するControlTextに、IntermediateTextを登録する先を準備 */
		ControlText cTextToAddChild = cGrandParentText;

		/*
		 * これ以降、変換結果をchildArrayControlTextに登録する
		 */
		/***** 開始行以降を確認 *****/
		for (int currentPos = startPos; currentPos < smallPartText.size();) {
			String currentLine = smallPartText.get(currentPos);

			/* 入れ子のブロックを確認する */
			Style styleCurrentLine = m_cStyleManager.getMatchStyle_Start(currentLine);

			// ほんとうの表関連か見極める（完全ではない）
			if (styleCurrentLine.bTableLikeStyle()) {
				// System.out.println(currentLine + "は、表関連の疑い");
				if (cGrandParentText.getStyle().bTableLikeStyle() == false
						&& styleCurrentLine.getStyleName().equals("【表】") == false) {
					// System.out.println("ほんとは表関連じゃない");
					styleCurrentLine = StyleManager.getBodyStyle();
				} else {
					// System.out.println("ほんとの表関連");
				}
			}

			if (styleCurrentLine != StyleManager.getBodyStyle()) {
				if (styleGrandParentText == StyleManager.getBodyStyle()) {
					// 現象：本文ブロックをコンパイル中にスタイル行が来た
					// 対応：本文ブロックの終了
					return currentPos; // 次のスタイル（ブロック）は、現在位置からであることを呼び出し元に伝える
				} else if (styleGrandParentText.bBulletLikeStyle()) {
					// 現象：箇条書きタイプをコンパイル中にスタイル行が来た
					if (styleCurrentLine == styleGrandParentText) {
						// 現象：同じスタイルが続いた
						// 対応：controlTextに登録すべき子供であると解釈して、childArrayControlTextに追加。

						// <ul><li><p>の場合
						// GrandParentText <ul>相当
						// + SmallBlock <li>相当
						// + 現在行 <p>相当

						// <h2>の場合
						// GrandParentText なし
						// + SmallBlock <h2>相当
						// + 現在行 なし（<p>相当）

						// BulletLikeStyleの場合は、周囲にSmallBlockを作る
						ControlText controlSmallBlockText = new ControlText(styleCurrentLine);
						cGrandParentText.getChildList().add(controlSmallBlockText);

						// SmallBlockに現在行を登録
						IntermediateText textCurrentLine = new IntermediateText(styleCurrentLine, currentLine); // 1行分
						controlSmallBlockText.getChildList().add(textCurrentLine);

						cTextToAddChild = controlSmallBlockText;
						currentPos++; // 次のループでは、次行を読む
						continue; // 続きもcontrolText内の本文である可能性があるため、continue。
					} else {
						// 現象：別のスタイルが続いた
						// 対応：controlTextが終了したと判断し、このメソッドの仕事は終了。
						return currentPos; // 次のブロックは、（別のスタイルが始まる）現在位置からである
					}
					// （到達不能）
				} else if (styleGrandParentText.bNoteLikeStyle()) {
					// 現象：ノートタイプをコンパイル中にスタイル行が来た（ノートタイプの1行目である）
					// 対応：次の行（currentPos+1）からは、新しいブロックの始まり。

					// 終端まですべてnotePartTextに入れる
					ArrayList<String> notePartText = new ArrayList<String>();
					currentPos = copyNotePartBlockTo(smallPartText, currentPos, styleGrandParentText, notePartText);
					compileBlock(notePartText, cTextToAddChild);
					cTextToAddChild = cGrandParentText;
					continue;
				} else if (styleCurrentLine.bTableLikeStyle()) {
					// 現象：表関連（【表】【行】【セル】）の行を処理中
					if (styleGrandParentText.bTableLikeStyle() == false) {
						// 現象：表以外のブロックを処理中に表が来た（「お知らせ」直下の「表」などが考えられる？）
						// 対応：入れ子の表と解釈し、新しい表を作る
						/*
						 * nextControlTextの生成
						 */
						if (styleCurrentLine.getStyleName().equals("【行】")
								|| styleCurrentLine.getStyleName().equals("【セル】")) {
							throw new IntermediateTextTreeBuildException("「▼表」が無いのに、" + currentLine + "が来ました。");
						}
						ControlText nextControlText = new ControlText(styleCurrentLine);

						/* controlTextの子供に登録 */
						cGrandParentText.getChildList().add(nextControlText);

						// 【表】の解釈を始める
						currentPos = compileChildBlockFromStartPos(smallPartText, currentPos, cLastStyle,
								nextControlText);

						continue; // currentPosは「▲」の次の行を指しているはず
					}
					/*
					 * 確認中の行が表関連のスタイル
					 *
					 * 表スタイルの特性上、以下の6パターンしか存在し得ない
					 *
					 * (0)controlText：表 currentLine：表 …表の始まり
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
					if (styleCurrentLine == styleGrandParentText) {
						/*
						 * 現象：controlTextと同じスタイルが続いている（1行目）
						 */
						if (styleGrandParentText.getStyleName().equals("【表】")) {
							/*
							 * (現象0)controlText：表 currentLine：表（表の始まり）
							 */
							// TableInfo...「▼表(2)」を格納
							IntermediateText sTableInfo = new IntermediateText(styleCurrentLine, currentLine);
							cGrandParentText.getChildList().add(sTableInfo);
							currentPos++;
							continue; // currentPosは「==========」を指している
						} else if (styleGrandParentText.getStyleName().equals("【行】")) {
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
								Style styleCell = m_cStyleManager.getStyle("【セル】");
								//
								// ControlText nextControlText = new
								// ControlText(
								// styleCell, styleCell.getStyleName());
								//
								// /* controlTextの子供に登録 */
								// controlText_BigBlock.getChildList().add(
								// nextControlText);

								// cellPartText
								ArrayList<String> cellPartText = new ArrayList<String>();
								cellPartText.add("-----");
								// currentPosは==========や-----を指しているので、次の行から変換開始
								currentPos = compileCellPartBlock(currentPos, smallPartText, cellPartText, styleCell);
								cellPartText.add("▲"); // compileBlockが終了を検知できるように。
								compileBlock(cellPartText, cTextToAddChild);
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
						} else if (styleGrandParentText.getStyleName().equals("【セル】")) {
							/*
							 * (現象6)controlText：セル currentLine：セル
							 *
							 * 対応：セル内部をコンパイル
							 */
							ArrayList<String> cellPartText = new ArrayList<String>();
							currentPos = compileCellPartBlock(currentPos, smallPartText, cellPartText,
									styleCurrentLine);
							compileBlock(cellPartText, cTextToAddChild);
							return currentPos + 1; // currentPosは「-----」を指している
						}
					} else {
						/* 現象：同じスタイルではない */
						if (styleGrandParentText.getStyleName().equals("【表】")
								&& styleCurrentLine.getStyleName().equals("【行】")) {
							/*
							 * (現象1)controlText：表 currentLine：行
							 *
							 * 対応：別のブロックが入れ子で始まったことにする
							 */
							/*
							 * nextControlText（行）の生成＆追加
							 */
							// ControlText nextControlText = new ControlText(
							// styleCurrentLine,
							// styleCurrentLine.getStyleName());
							//
							// /* controlTextの子供に登録 */
							// controlText_BigBlock.getChildList().add(
							// nextControlText);

							// 本文
							// ▼表
							// ========== …★currentLine（1回目）★
							// セル1
							// -----
							// セル2
							// ▲

							ArrayList<String> rowPartText = new ArrayList<String>();
							rowPartText.add(currentLine);
							// currentPosは==========や-----の次を指しているので、そこから変換開始
							currentPos = compileCellPartBlock(currentPos, smallPartText, rowPartText, styleCurrentLine);
							rowPartText.add("▲"); // compileBlockが終了を検知できるように。

							compileBlock(rowPartText, cGrandParentText);
							continue;
						} else if (styleGrandParentText.getStyleName().equals("【行】")
								&& styleCurrentLine.getStyleName().equals("【セル】")) {
							/*
							 * (現象3)controlText：行 currentLine：セル
							 */
							/*
							 * （セル）の生成
							 */
							Style styleCell = new Style_TableCell();
							// ControlText cellControlText = new ControlText(
							// styleCell, styleCell.getStyleName());
							// controlText_BigBlock.getChildList().add(
							// cellControlText);

							// cellPartText
							ArrayList<String> cellPartText = new ArrayList<String>();
							cellPartText.add("-----");
							// currentPosは==========や-----を指しているので、次の行から変換開始
							currentPos = compileCellPartBlock(currentPos, smallPartText, cellPartText, styleCell);
							cellPartText.add("▲"); // compileBlockが終了を検知できるように。
							compileBlock(cellPartText, cTextToAddChild);
							continue;
						} else if ((styleGrandParentText.getStyleName().compareTo("【セル】") == 0
								&& styleCurrentLine.getStyleName().compareTo("【表】") == 0)) {
							/*
							 * (現象4)controlText：セル currentLine：表
							 *
							 * 対応：入れ子の表と解釈し、新しい表を作る
							 */
							/*
							 * nextControlTextの生成
							 */
							ControlText nextControlText = new ControlText(styleCurrentLine);

							/* controlTextの子供に登録 */
							cGrandParentText.getChildList().add(nextControlText);

							// 【表】の解釈を始める
							currentPos = compileChildBlockFromStartPos(smallPartText, currentPos, cLastStyle,
									nextControlText);

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
					ControlText nextControlText = new ControlText(styleCurrentLine);

					/* controlTextの子供に登録 */
					cTextToAddChild.getChildList().add(nextControlText);

					cTextToAddChild = nextControlText;
					currentPos = compileChildBlockFromStartPos(smallPartText, currentPos + 1, cLastStyle,
							cTextToAddChild);

					// compileBlockで、smallPartTextを全部読んでない？
					if (currentPos < smallPartText.size()) {
						continue; // 全部読んでないよ！
					} else {
						break; // 最後まで読んじゃった・・・？
					}
				}
				throw new IntermediateTextTreeBuildException("スタイルが定義された行にもかかわらず処理されていません");
			} else {
				// 本文ブロックの処理

				/* タブブロックの確認 */
				if (currentLine.length() > 0 && currentLine.charAt(0) == TAB_CHAR) {
					// タブ文字で始まる行はすべてtabPartTextに入れる
					// ※先頭のタブ文字を除いてtabPartTextに入れる
					ArrayList<String> tabPartText = new ArrayList<String>();
					for (; currentPos < smallPartText.size(); currentPos++) {
						currentLine = smallPartText.get(currentPos);
						if (currentLine.length() > 0 && currentLine.charAt(0) == TAB_CHAR)
							tabPartText.add(currentLine.substring(1));
						else
							break;
					}
					int lastIndex = cTextToAddChild.getChildList().size();
					IntermediateText cTemp = cTextToAddChild.getChildList().get(lastIndex - 1);
					if (cTemp instanceof ControlText) {
						cTextToAddChild = (ControlText) cTemp;
					}
					compileBlock(tabPartText, cTextToAddChild);
					cTextToAddChild = cGrandParentText;
					continue;
				}

				/* ブロックが終了していないか確認。ただし、本文の場合は終了を検知しない */
				if (cLastStyle != StyleManager.getBodyStyle() && cLastStyle.isMatch_Last(currentLine)) {
					// このブロックが終了した！
					if (cLastStyle.bNoteLikeStyle()) {
						// ▼～▲のタイプ
						return currentPos + 1; // currentPosは「▲」を指しているので次の行を案内
					} else if (cLastStyle.bTableLikeStyle()) {
						// 表のタイプ
						if (cLastStyle.getStyleName().compareTo("【表】") == 0) {
							// 最後の1行（▲など）を登録する
							IntermediateText lastLineText = new IntermediateText(cLastStyle, currentLine);
							cTextToAddChild.getChildList().add(lastLineText);
							return currentPos + 1; // currentPosは「▲」を指しているので次の行を案内
						} else if (cLastStyle.getStyleName().equals("【セル】")) {
							return currentPos + 1; // currentPosは「▲」を指しているので次の行を案内
						} else {
							return currentPos + 1; // currentPosは「▲」を指しているので次の行を案内
						}
					}
					// 箇条書きのタイプ
					return currentPos; // currentPosは、終了が明らかになった行を指している（通常は次のスタイルの開始行を指している）
				}

				/*
				 * 現在調べているところが、ただの本文の場合にたどり着く場所（始まりでも終わりでもないところ）
				 */
				// 周囲にSmallBlockを作る
				ControlText controlSmallBlockText = new ControlText(styleCurrentLine);
				cGrandParentText.getChildList().add(controlSmallBlockText);

				IntermediateText textBody = new IntermediateText(StyleManager.getBodyStyle(), currentLine);
				controlSmallBlockText.getChildList().add(textBody);
				currentPos++; // 次の行へ・・・
			}
		}

		// 閉じる前にitがなくなっちゃった。。。
		if (styleGrandParentText != null) {
			if (styleGrandParentText.bNoteLikeStyle() || styleGrandParentText.bTableLikeStyle()) {
				Tx2x.appendWarn("ブロックが終了する前にテキストがなくなりました。");
				throw new IntermediateTextTreeBuildException(
						"ブロックが終了する前にテキストがなくなりました。" + Tx2x.getMessageCRLF() + smallPartText);
			}
		}
		return smallPartText.size();
	}

	private int compileCellPartBlock(int currentPos, ArrayList<String> smallPartText, ArrayList<String> notePartText,
			Style lastStyle) throws IntermediateTextTreeBuildException {
		String currentLine = "";
		currentPos++;
		while (currentPos < smallPartText.size()) {
			currentLine = smallPartText.get(currentPos);
			/* 終了かチェック */
			if (lastStyle != null && lastStyle.isMatch_Last(currentLine)) {
				break;
			}
			/* 入れ子のブロックがないか確認 */
			Style styleCurrentLine_local = m_cStyleManager.getMatchStyle_Start(currentLine);
			if (styleCurrentLine_local.bNoteLikeStyle()) {
				notePartText.add(currentLine);
				currentPos = copyNotePartBlockTo(smallPartText, currentPos, lastStyle, notePartText);
				notePartText.add(smallPartText.get(currentPos));
			} else {
				notePartText.add(currentLine);
			}
			currentPos++;
		}
		return currentPos;
	}

	/***
	 * ノートタイプのテキストをコンパイルして、notePartTextに
	 *
	 * @param currentPos
	 * @param smallPartText
	 * @param lastStyle
	 * @param controlText_BigBlock
	 * @param copyTo
	 *            値が更新されます
	 * @return
	 * @throws IntermediateTextTreeBuildException
	 */
	private int copyNotePartBlockTo(ArrayList<String> smallPartText, int startPos, Style lastStyle,
			ArrayList<String> copyTo) throws IntermediateTextTreeBuildException {
		String currentLine = "";
		int currentPos = startPos;
		try {
			while (currentPos < smallPartText.size()) {
				currentPos++;
				currentLine = smallPartText.get(currentPos);
				/* 入れ子のブロックがないか確認 */
				Style styleCurrentLine_local = m_cStyleManager.getMatchStyle_Start(currentLine);
				if (lastStyle.isMatch_Last(currentLine)) {
					break;
				}
				if (styleCurrentLine_local.bNoteLikeStyle()) {
					copyTo.add(currentLine);
					currentPos = copyNotePartBlockTo(smallPartText, currentPos, lastStyle, copyTo);
					copyTo.add(smallPartText.get(currentPos));
				} else {
					copyTo.add(currentLine);
				}
			}
		} catch (IndexOutOfBoundsException e) {
			throw new IntermediateTextTreeBuildException(e.getLocalizedMessage());
		}
		return currentPos;
	}

	/**
	 * ControlTextの事前整形用（オーバーライドポイント）<br/>
	 * 例えば、タイトル前後の空行を削除する
	 */
	protected ControlText preFormatControlText(ControlText resultRootText) {
		return resultRootText;
	}

	/**
	 * 最終スキャンを実行する
	 *
	 * @param resultText
	 *            ここまでの変換結果
	 * @param lsManager
	 *            LongStyleManager
	 */
	protected void lastScan(ControlText resultText, LongStyleManager lsManager) {
		Iterator<IntermediateText> it = resultText.getChildList().iterator();
		while (it.hasNext()) {
			IntermediateText iText = it.next();
			if (m_bDebugMode)
				System.out.println(iText.getDebugText());

			if (iText instanceof ControlText) {
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
					} else if (currentStyle.getStyleName().compareTo("【行】") == 0) {
						if (m_bDebugMode)
							System.out.println("【行】");
					} else if (currentStyle.getStyleName().compareTo("【セル】") == 0) {
						if (m_bDebugMode)
							System.out.println("【セル】");
						if (cText.getChildList().size() == 0) {
							System.out.println(resultText.getDebugText());
						}
						ControlText cText2 = (ControlText) cText.getChildList().get(0);
						if (cText2.getChildList().get(0).getChildText().matches(".*【ヘッダー】.*")) {
							// 【セル】→【セル：ヘッダー】に置換
							Style newStyle = m_cStyleManager.getStyle("【セル：ヘッダー】");
							lsManager.removeLastStyle();
							lsManager.addStyle(newStyle);
							cText.setStyle(newStyle); // cTextもStyleを変更する
							cText.setText("【セル：ヘッダー】");
							// System.out.println("【セル】→【セル：ヘッダー】に置換");
						} else if (cText2.getChildList().get(0).getChildText().matches(".*【行ヘッダー】.*")) {
							// 【セル】→【セル：行ヘッダー】に置換
							Style newStyle = m_cStyleManager.getStyle("【セル：行ヘッダー】");
							lsManager.removeLastStyle();
							lsManager.addStyle(newStyle);
							cText.setStyle(newStyle); // cTextもStyleを変更する
							cText.setText("【セル：行ヘッダー】");
							// System.out.println("【セル】→【セル：ヘッダー】に置換");
						}
					}
				}
				lastScan(cText, lsManager); // さらに奥深くへ（再帰）
				// 表・行・セルの終了
				if (currentStyle != null && currentStyle.bTableLikeStyle()) {
					if (currentStyle.getStyleName().equals("【表】")) {
						lsManager.setPrevLongStyle(lsManager.getCurrentLongStyle().replaceFirst("【表】.*?$", "【表】▲"));
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
					if (currentStyle.getStyleName().equals("【表】")) {
						// 表の場合は何もしない…？
					} else if (iText.getText() != null) {
						// 表以外の場合は…

						// LongStyleArrayListを更新（スタイルのバッファリング）
						lsManager.addStyle(currentStyle); // スタイルをpush
						lsManager.addLongStyleToLongStyleArrayList(); // LongStyleをLongStyleArrayListに登録する
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
	}

	/**
	 * 変換対象を指定する<br/>
	 * 1つのTx2xテキストで、複数のドキュメントを出力するための機能（かき分け）
	 *
	 * @param sTarget
	 *            変換対象を表す文字列
	 * @note Converterなど別クラスから呼び出される
	 */
	public void setTarget(String sTarget) {
		if (m_cTarget == null) {
			m_cTarget = new ArrayList<String>();
		}
		m_cTarget.add(sTarget);
	}

	/**
	 * 変数の値を指定する<br/>
	 * 1つのTx2xテキストで、複数のドキュメントを出力するための機能（変数）
	 *
	 * @param key
	 *            変数名
	 * @param value
	 *            値
	 * @note Converterなど別クラスから呼び出される
	 */
	public void putVariable(String key, String value) {
		System.out.println("Variable: " + key + "=" + value);
		m_cVariables.put(key, value);
	}
}
