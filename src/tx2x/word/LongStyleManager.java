/**
 * LongStyle（【お知らせ】【箇条書き】【箇条書き】）を元に、InDesignタグ付きテキストを生成する
 * ついでに特殊文字の置換も行っています。
 */
package tx2x.word;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import tx2x_core.IntermediateText;
import tx2x_core.Style;

import com.jacob.com.Dispatch;

public class LongStyleManager {
	LinkedList<Style> m_StyleLinkedList; // スタイル情報をpush/popする
	String m_sPrevLongStyle; // 直前の長いスタイル名

	// 未定義のスタイル（dummy000）を管理するための変数
	private Hashtable<String, String> m_cDummyStyleHashTable;

	// type = 0x1: 画面あり
	// type = 0x2: 手順数字直後に表あり
	int m_nStepTableType = 0;

	// type = 0x1のときは、画面キャプションを保持
	// type = 0x2のときは、ステップ番号を保持
	String m_sStepCaption = "";

	// ステップ番号を保持
	String m_sStepNumber = "";

	int m_nPrevStepTableWidth = 0;

	// type = 0x1: 画面あり
	// type = 0x2: 手順数字直後に表あり
	int m_nPrevStepTableType = 0;

	String m_sPrevStepCaption = "";

	private ArrayList<String> m_cLongStyleArrayList;

	LongStyleManager(Dispatch oSelection) {
		m_StyleLinkedList = new LinkedList<Style>();
		m_sPrevLongStyle = "";
		m_cDummyStyleHashTable = new Hashtable<String, String>();
		m_cLongStyleArrayList = new ArrayList<String>();
	}

	public String getTargetStyle(IntermediateText iText, int nLsIndex)
			throws IOException {
		String longStyle = getLongStyle();
		if (iText.getStyle() == null) {
			longStyle += "【本文】";
		}

		// iTextの本文を処理。【初期操作】以外の全段落共通
		{
			String text = iText.getEscapeText();
			iText.setText(text);
		}

		// 標準的なチェック（それぞれ独立しているので順不同）

		if (longStyle.equals("【章】【章】")) {
			m_sPrevLongStyle = longStyle;
			return "見出し 1";
		}

		if (longStyle.equals("【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "本文";
		}

		if (longStyle.equals("【箇条書き・】【箇条書き・】")) {
			m_sPrevLongStyle = longStyle;
			iText.setText(iText.getText().replaceFirst("・\t", ""));
			return "箇条書き";
		}

		if (longStyle.equals("【表】")) {
			m_sPrevLongStyle = longStyle;
			return "本文";
		}

		if (longStyle.equals("【表】【行】【セル】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "本文";
		}

		// 以降、ダミースタイルの処理
		m_sPrevLongStyle = longStyle;
		String style = m_cDummyStyleHashTable.get(m_sPrevLongStyle);
		if (style != null) {
			return style;
		}
		DecimalFormat df = new DecimalFormat();
		df.applyLocalizedPattern("0000");
		style = "標準";
		iText.setText("■未定義スタイル:" + m_sPrevLongStyle + iText.getText());
		System.out.println(m_sPrevLongStyle + "は、" + style + "スタイルで出力されました。");
		m_cDummyStyleHashTable.put(m_sPrevLongStyle, style);
		return style;
	}

	public void addStyle(Style style) {
		m_StyleLinkedList.add(style);
	}

	public void removeLastStyle() {
		m_StyleLinkedList.removeLast();
	}

	public String getLongStyle() {
		String longStyle = "";
		Iterator<Style> it2 = m_StyleLinkedList.iterator();

		// longStyleの取得
		while (it2.hasNext()) {
			Style r2 = it2.next();
			if (r2 == null) {
				longStyle += "【本文】";
			} else {
				longStyle += r2.getStyleName();
			}
		}
		return longStyle;
	}

	public void setPrevLongStyle(String prevLongStyle) {
		m_sPrevLongStyle = prevLongStyle;
	}

	public void addLongStyleToArrayList() {
		m_cLongStyleArrayList.add(getLongStyle());
	}

	public String getLongStyleFromArrayList(int nLsIndex) {
		if (nLsIndex == m_cLongStyleArrayList.size()) {
			return ""; // スタイルなし
		}
		return m_cLongStyleArrayList.get(nLsIndex);
	}

	public String getPrevLongStyle() {
		return m_sPrevLongStyle;
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
	public void writeTargetData(Dispatch oSelection, String style,
			IntermediateText iText, int i) {
		Dispatch.put(oSelection, "Text", iText.getText());
		Dispatch.put(oSelection, "Style", style);

		Dispatch.call(oSelection, "MoveRight"); // 右へ移動
		Dispatch.call(oSelection, "TypeParagraph"); // 改行
	}
}
