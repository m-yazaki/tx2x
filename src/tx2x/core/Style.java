package tx2x.core;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Style {
	private String m_sStyleName;
	private Pattern m_cPatternFirst;
	private Pattern m_cPatternLast;

	public Style(String styleName, String regexpFirst, String regexpLast) {
		m_sStyleName = styleName;
		m_cPatternFirst = Pattern.compile(regexpFirst);
		m_cPatternLast = Pattern.compile(regexpLast);
	}

	public String getStyleName() {
		return m_sStyleName;
	}

	public boolean isMatch_Start(String line) {
		Matcher matcher = m_cPatternFirst.matcher(line);
		return matcher.matches();
	}

	public boolean isMatch_Last(String line) {
		if (bBulletLikeStyle() && line.compareTo("") == 0) { // テキストがない場合もブロックの終了と判断する・・・？
			return true;
		}
		Matcher matcher = m_cPatternLast.matcher(line);
		return matcher.matches();
	}

	boolean isNull() {
		return true;
	}

	/*
	 * ブロックの開始／終了を自動的に判断するかどうか。
	 *
	 * ▼～▲で囲まれるタイプはfalse; 箇条書きのようなタイプはtrue; を返してください。
	 */
	public boolean bBulletLikeStyle() {
		return false;
	}

	public boolean bNoteLikeStyle() {
		return false;
	}

	public boolean bTableLikeStyle() {
		return false;
	}

	/*
	 * 1行目の処理
	 */
	public int compileLine(ControlText controlText,
			ArrayList<String> smallPartText, int startPos) {
		/* controlTextのスタイルを確保 */
		Style styleControlText = controlText.getStyle();

		/* controlTextの子供を確保 */
		ArrayList<IntermediateText> childArrayControlText = controlText
				.getChildList();

		/* 1行目のIntermediateTextを作成して、次の行に進む */
		IntermediateText textFirstLine = new IntermediateText(styleControlText,
				smallPartText.get(startPos)); // 1行分
		childArrayControlText.add(textFirstLine);
		return startPos + 1;
	}
}
