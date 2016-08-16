package tx2x.xhtml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import tx2x.StyleManager;
/**
 * iTextツリーを構築する
 */
import tx2x.StyleManagerFactory;
import tx2x.core.ControlText;
import tx2x.core.IntermediateText;
import tx2x.core.Style;

public class IntermediateTextTreeBuilderXHTML extends tx2x.xhtml.IntermediateTextTreeBuilderXHTML {
	public IntermediateTextTreeBuilderXHTML(boolean bDebugMode) {
		super(bDebugMode);

		// StyleManagerの作成と登録
		StyleManagerFactory cFactory = StyleManagerFactory.getInstance();
		m_cStyleManager = new GioPriNasStyleManager();
		cFactory.regist(m_cStyleManager);
	}

	protected ControlText preFormatControlText(ControlText resultRootText) {
		ArrayList<IntermediateText> cDeleteBlock = new ArrayList<IntermediateText>();
		ArrayList<IntermediateText> cRootChildList = resultRootText.getChildList();

		/*
		 * 【項】【項下】【■】【対応パッケージ】の直後の空行（スタイル：標準）を削除
		 */
		ListIterator<IntermediateText> it = cRootChildList.listIterator();
		while (it.hasNext()) {
			ControlText cText = (ControlText) it.next();
			Style style = cText.getStyle();
			if (style != null && (style.getStyleName().equals("【項】") || style.getStyleName().equals("【項下】")
					|| style.getStyleName().equals("【：】") || style.getStyleName().equals("【■】")
					|| style.getStyleName().equals("【対応パッケージ】"))) {

				// 次の行があるかチェック
				if (cRootChildList.size() <= it.nextIndex()) {
					break; // チェック終了
				}
				// 次の行が【本文】の場合は、削除できるかもしれない
				ControlText cNextText = (ControlText) cRootChildList.get(it.nextIndex());
				if (cNextText.getStyle() == StyleManager.getBodyStyle()) {
					ArrayList<IntermediateText> cChildList = cNextText.getChildList();
					for (int i = 0; i < cChildList.size();) {
						IntermediateText iText1 = cChildList.get(i);
						IntermediateText iText2 = ((ControlText) iText1).getChildList().get(0);
						if (iText2.getStyle() == StyleManager.getBodyStyle() && iText2.getText().equals("")) {
							cChildList.remove(i);
						} else {
							break;
						}
					}
					if (cText.getChildList().size() == 0) {
						// 何もなくなった（後で削除する）
						// + "の直後の空行（スタイル：標準）を削除");
						cDeleteBlock.add(cText);
						// break;
					}
				}
			}
		}
		// 削除することになったブロックを削除
		Iterator<IntermediateText> itDelete = cDeleteBlock.iterator();
		while (itDelete.hasNext()) {
			IntermediateText i = itDelete.next();
			cRootChildList.remove(i);
		}
		cDeleteBlock.clear();

		/*
		 * 【項下】などの直前の空行（スタイル：標準）を削除
		 */
		it = cRootChildList.listIterator();
		ControlText cPrevBlock = null;
		while (it.hasNext()) {
			ControlText cText = (ControlText) it.next();
			Style style = cText.getStyle();

			if (style != null && (style.getStyleName().equals("【項下】") || style.getStyleName().equals("【：】")
					|| style.getStyleName().equals("【■】") || style.getStyleName().equals("【項下下】")
					|| style.getStyleName().equals("【手順】") || style.getStyleName().equals("【重要】")
					|| style.getStyleName().equals("【注意】") || style.getStyleName().equals("【補足】")
					|| style.getStyleName().equals("【表】"))) {
				while (true) {
					if (cPrevBlock == null)
						break;
					ArrayList<IntermediateText> cChildList = cPrevBlock.getChildList();
					int lastIndex = cChildList.size() - 1;
					if (lastIndex == -1) {
						// 何もなくなった（後で削除する）
						// System.out.println(style.getStyleName()
						// + "の直前の空行（スタイル：標準）を削除");
						cDeleteBlock.add(cPrevBlock);
						break;
					}
					IntermediateText cLast = cChildList.get(lastIndex);
					if (cLast.getStyle() == StyleManager.getBodyStyle() && cLast.getChildText().equals("")) {
						cChildList.remove(lastIndex);
					} else {
						break;
					}
				}
			}
			cPrevBlock = cText;
		}
		// 削除することになったブロックを削除
		itDelete = cDeleteBlock.iterator();
		while (itDelete.hasNext()) {
			IntermediateText i = itDelete.next();
			cRootChildList.remove(i);
		}
		cDeleteBlock.clear();

		/*
		 * 【重要】、【注意】、【補足】は、1項目の場合は箇条書きをトル
		 */
		it = cRootChildList.listIterator();
		while (it.hasNext()) {
			ControlText cText = (ControlText) it.next();
			Style style = cText.getStyle();

			//
			if (style.getStyleName().equals("【手順】")) {
				Iterator<IntermediateText> it2 = cText.getChildList().iterator();
				while (it2.hasNext()) {
					ControlText cText2 = (ControlText) it2.next();
					Iterator<IntermediateText> it3 = cText2.getChildList().iterator();
					while (it3.hasNext()) {
						IntermediateText cText3 = it3.next();
						if (cText3 instanceof ControlText)
							convertOneBulletToBody((ControlText) cText3);
					}
				}
			}

			convertOneBulletToBody(cText);
			// ここまでループ
		}
		// 削除することになったブロックを削除
		itDelete = cDeleteBlock.iterator();
		while (itDelete.hasNext()) {
			IntermediateText i = itDelete.next();
			cRootChildList.remove(i);
		}
		cDeleteBlock.clear();

		/*
		 * 最後のテキストブロックから、空行（スタイル：標準）を削除
		 */
		if (cRootChildList.size() > 0) {
			int lastTextBlockIndex = cRootChildList.size() - 1;
			ControlText cLastTextBlock = (ControlText) cRootChildList.get(lastTextBlockIndex);
			while (true) {
				ArrayList<IntermediateText> cChildList = cLastTextBlock.getChildList();
				int lastIndex = cChildList.size() - 1;
				if (lastIndex == -1) {
					// 何もなくなった（後で削除する）
					// System.out.println("最後のテキストブロックから、空行（スタイル：標準）を削除");
					cDeleteBlock.add(cLastTextBlock);
					break;
				}
				IntermediateText cLast = cChildList.get(lastIndex);
				if (cLast.getStyle() == StyleManager.getBodyStyle() && cLast.getChildText().equals("")) {
					cChildList.remove(lastIndex);
				} else {
					break;
				}
			}
			// 削除することになったブロックを削除
			itDelete = cDeleteBlock.iterator();
			while (itDelete.hasNext()) {
				IntermediateText i = itDelete.next();
				cRootChildList.remove(i);
			}
			cDeleteBlock.clear();
		}

		return resultRootText;
	}

}
