/**
 * LongStyle（【お知らせ】【箇条書き】【箇条書き】【本文】【本文】等）の構築を管理する
 */
package tx2x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import tx2x.core.Style;

public class LongStyleManager {
	private LinkedList<Style> m_cStyleLinkedList; // スタイル情報をpush/popする
	private String m_sPrevLongStyle; // 直前の長いスタイル名
	protected String m_sCurrentLongStyle; // 現在の長いスタイル名

	private ArrayList<String> m_cLongStyleArrayList;
	public String m_sStepCaption = "";

	protected LongStyleManager() {
		m_cStyleLinkedList = new LinkedList<Style>();
		m_sPrevLongStyle = "";
		m_cLongStyleArrayList = new ArrayList<String>();
	}

	public void addStyle(Style style) {
		m_cStyleLinkedList.add(style);
	}

	public void removeLastStyle() {
		m_cStyleLinkedList.removeLast();
	}

	public String getLongStyle() {
		String longStyle = "";
		Iterator<Style> it2 = m_cStyleLinkedList.iterator();

		// longStyleの取得
		while (it2.hasNext()) {
			Style r2 = it2.next();
			if (r2 == null) {
				System.out.println("どのような条件でr2 == nullになるのか");
				longStyle += "【本文】";
			} else {
				longStyle += r2.getStyleName();
			}
		}
		setCurrentLongStyle(longStyle);
		return longStyle;
	}

	public void setCurrentLongStyle(String currentLongStyle) {
		m_sCurrentLongStyle = currentLongStyle;
	}

	public String getCurrentLongStyle() {
		return m_sCurrentLongStyle;
	}

	public void setPrevLongStyle() {
		m_sPrevLongStyle = m_sCurrentLongStyle;
	}

	public void setPrevLongStyle(String prevLongStyle) {
		m_sPrevLongStyle = prevLongStyle;
	}

	public String getPrevLongStyle() {
		return m_sPrevLongStyle;
	}

	public void addLongStyleToLongStyleArrayList() {
		m_cLongStyleArrayList.add(getLongStyle());
	}

	public String getLongStyleFromArrayList(int nLsIndex) {
		if (nLsIndex < 0) {
			return ""; // スタイルなし
		}
		if (nLsIndex == m_cLongStyleArrayList.size()) {
			return ""; // スタイルなし
		}
		return m_cLongStyleArrayList.get(nLsIndex);
	}
}
