package tx2x.xhtml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import tx2x.IntermediateTextTreeBuilder;
import tx2x.StyleManager;
import tx2x.core.ControlText;
import tx2x.core.IntermediateText;
import tx2x.core.Style;

/**
 * iTextツリーを構築する
 *
 * @author yazaki.makoto
 *
 */
public class IntermediateTextTreeBuilderXHTML extends IntermediateTextTreeBuilder {

	public IntermediateTextTreeBuilderXHTML(boolean bDebugMode) {
		super(bDebugMode);
	}

	/*
	 * 整形
	 */
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
					|| style.getStyleName().equals("【■】") || style.getStyleName().equals("【対応パッケージ】"))) {

				ControlText cNextText = (ControlText) cRootChildList.get(it.nextIndex());
				ArrayList<IntermediateText> cChildList = cNextText.getChildList();
				for (int i = 0; i < cChildList.size();) {
					IntermediateText iText = cChildList.get(i);
					if (iText.getStyle() == StyleManager.getBodyStyle() && iText.getChildText().equals("")) {
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

			if (style != null && (style.getStyleName().equals("【項下】") || style.getStyleName().equals("【■】")
					|| style.getStyleName().equals("【項下下】") || style.getStyleName().equals("【手順】")
					|| style.getStyleName().equals("【重要】") || style.getStyleName().equals("【注意】")
					|| style.getStyleName().equals("【補足】") || style.getStyleName().equals("【表】"))) {
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

	/**
	 * ●1個は本文と同じ扱いにする
	 *
	 * @param cText
	 */
	public void convertOneBulletToBody(ControlText cText) {
		Style style = cText.getStyle();
		if (style != null && (style.getStyleName().equals("【重要】") || style.getStyleName().equals("【注意】")
				|| style.getStyleName().equals("【補足】"))) {
			ArrayList<IntermediateText> cChildList = cText.getChildList();
			if (cChildList.size() > 1)
				return;
			Iterator<IntermediateText> itChild = cChildList.iterator();
			while (itChild.hasNext()) {
				ControlText cChildText = (ControlText) itChild.next();
				if (cChildText.getStyle().getStyleName().equals("【箇条書き●】")) {
					ArrayList<IntermediateText> cBulletList = cChildText.getChildList();
					if (cBulletList.size() == 1) {
						// 箇条書きが1個しかない
						Style bodyStyle = StyleManager.getBodyStyle();
						cChildText.setStyle(bodyStyle);
						ControlText cOneBulletText = (ControlText) cBulletList.get(0);
						// ControlTextの書き替え
						cOneBulletText.setStyle(bodyStyle);
						// cOneBulletText.setText("【本文】");
						// cChildList.set(0, cOneBulletText);

						// itBulletChildは、
						// iText（ごにょごにょ）
						// cText（【本文】）
						// を巡るイテレータ
						Iterator<IntermediateText> itBulletChild = cOneBulletText.getChildList().iterator();

						// ●以外に何かがある
						// 1つ目を【本文】にする
						IntermediateText iOneBulletText = itBulletChild.next();
						iOneBulletText.setText(iOneBulletText.getText().replaceFirst("●\t", ""));
						iOneBulletText.setStyle(bodyStyle);

						if (cOneBulletText.getChildList().size() > 1) {
							IntermediateText iOneBulletTextNext = (ControlText) cOneBulletText.getChildList().get(1);

							// 2つ目が【本文】なら1つ目を2つ目の子供に移動する
							if (iOneBulletTextNext.getStyle().getStyleName().equals("【本文】")) {
								ArrayList<IntermediateText> childList = ((ControlText) iOneBulletTextNext)
										.getChildList();
								cChildText.getChildList().addAll(childList);
								cOneBulletText.getChildList().remove(1);
								break;
							} else {
								// 2つ目が【本文】以外なら2つ目以降を1レベル上げる
								ArrayList<IntermediateText> childList = ((ControlText) cText.getChildList().get(0))
										.getChildList();

								// 移動対象のcText
								int count = childList.size() - 1;
								for (int i = 0; i < count; i++) {
									ControlText moveControlText = (ControlText) childList.get(1);
									cText.getChildList().add(i + 1, moveControlText);
									childList.remove(moveControlText);
								}
								break;
							}
						}
					}
				}
			}
		}
	}
}
