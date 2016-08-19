/**
 * LongStyle（【お知らせ】【箇条書き】【箇条書き】【本文】【本文】）を元に、XHTMLタグ付きテキストを生成する
 * ついでに特殊文字の置換も行っています。
 */
package tx2x.xhtml;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import tx2x.IntermediateTextTreeWalker;
import tx2x.Utils;
import tx2x.core.ControlText;
import tx2x.core.IntermediateText;
import tx2x.core.Style;

public class LongStyleManagerXHTML extends tx2x.LongStyleManager {
	private static final String STRING_DEFAULT_HTMLTAG_INDENT = "\t\t";

	// 未定義のスタイル（dummy000）を管理するための変数
	int m_nDummyCounter;
	Hashtable<String, Style_TagInfo> m_cDummyStyleHashTable;

	protected boolean m_bDebugMode;
	protected NavPointManager m_cNavPointManager;
	protected File m_cTargetFile;
	protected Stack<ControlText> m_cParentText;
	private String m_sHTMLTagIndent;

	public LongStyleManagerXHTML(boolean bDebugMode, File cTargetFile, NavPointManager cNavPointManager) {
		super();
		m_nDummyCounter = 0;
		m_cDummyStyleHashTable = new Hashtable<String, Style_TagInfo>();
		m_bDebugMode = bDebugMode;
		m_cTargetFile = cTargetFile;
		m_cNavPointManager = cNavPointManager;
		m_cParentText = new Stack<ControlText>();
		m_sHTMLTagIndent = STRING_DEFAULT_HTMLTAG_INDENT;
	}

	public Style_TagInfo getStyle_TagInfo(IntermediateText iText_dup, IntermediateTextTreeWalker cTreeWalker,
			int nLsIndex) throws IOException {
		IntermediateText iText = cTreeWalker.getCurrentNode();
		if (iText != iText_dup) {
			System.out.println("cTreeWalkerの利用に失敗している");
		}
		setPrevLongStyle();
		String longStyle = getLongStyle();
		// System.out.println(longStyle);

		// ★が見つかった場合はコンソールに出力する
		if (iText instanceof ControlText == false && iText.getText().indexOf("★") >= 0) {
			System.out.println(iText.getText());
		}

		// デバッグモードのときはすべてダミー扱い
		if (m_bDebugMode) {
			return dummyStyle(longStyle, iText);// + longStyle;
		}

		// iText.getText()の共通処理
		{
			if (iText instanceof ControlText) {
				m_cParentText.push((ControlText) iText);
			} else {
				String sText = iText.getText().replaceAll("&", "&amp;");
				sText = sText.replaceAll("&amp;amp;", "&amp;");
				sText = sText.replaceAll("&amp;lt;", "&lt;");
				sText = sText.replaceAll("&amp;gt;", "&gt;");
				iText.setText(sText);

				// widthプロパティの設定
				String sString = setWidthProperty(iText);
				iText.setText(sString);
			}
		}

		/**
		 * 標準的なチェック（それぞれ独立しているので順不同）
		 */
		if (longStyle.equals("【章】") || longStyle.equals("【章】【章】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【章】【章】【章】")) {
			String sString = iText.getText().replaceFirst("【章】", "");
			return new Style_TagInfo("", "\n<h1>", sString, "</h1>\n");
		}

		if (longStyle.equals("【章サブ】") || longStyle.equals("【章サブ】【章サブ】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【章サブ】【章サブ】【章サブ】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", "<div class=\"h1_sub\">", sString, "</div>");
		}

		if (longStyle.equals("【節】") || longStyle.equals("【節】【節】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【節】【節】【節】")) {
			String sString = iText.getText().replaceFirst("【節】", "");
			return new Style_TagInfo("", "\n<h2>", sString, "</h2>\n");
		}

		if (longStyle.equals("【項】") || longStyle.equals("【項】【項】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【項】【項】【項】")) {
			String sString = iText.getText().replaceFirst("【項】", "");
			return new Style_TagInfo("", "\n<h3>", sString, "</h3>\n");
		}

		if (longStyle.matches("【ヒント】")) {
			return new Style_TagInfo("\t", "<div class=\"hint\">\n\t<div class=\"title\">ヒント</div>\n", "", "</div>");
		}

		if (longStyle.matches("【1.】【1.】【ヒント】")) {
			return new Style_TagInfo("\t\t", "<div class=\"hint\">\n\t<div class=\"title\">ヒント</div>\n", "", "</div>");
		}

		if (longStyle.matches("(【1.】【1.】)?【ヒント】【本文】") || longStyle.matches("(【1.】【1.】)?【ヒント】【本文】【本文】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.matches("【ヒント】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", "<p>", sString, "</p>");
		}

		if (longStyle.matches("【1.】【1.】【ヒント】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", "<p>", sString, "</p>");
		}

		if (longStyle.equals("【注意】")) {
			return new Style_TagInfo("\t", "<div class=\"note\">\n\t<div class=\"title\">注意</div>\n", "", "</div>");
		}

		if (longStyle.equals("【注意】【本文】") || longStyle.equals("【注意】【本文】【本文】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【注意】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", "<p>", sString, "</p>");
		}

		if (longStyle.equals("【HACK】")) {
			return new Style_TagInfo("\t", "<div class=\"hack\">\n", null, "</div>");
		}

		if (longStyle.equals("【HACK】【HACK】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【HACK】【HACK】【HACK】")) {
			String sString = iText.getText();
			Pattern p = Pattern.compile("【HACK (.*)】(.*)");
			Matcher matcher = p.matcher(sString);
			matcher.find();
			return new Style_TagInfo("", "<div class=\"hack_no\"><div class=\"title\">HACK</div><div class=\"no\">"
					+ matcher.group(1) + "</div></div>\n<p>", matcher.group(2), "</p>");
		}

		if (longStyle.equals("【HACK】【HACK】【本文】") || longStyle.equals("【HACK】【HACK】【本文】【本文】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【HACK】【HACK】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", "<p>", sString, "</p>");
		}

		if (longStyle.matches("(【ヒント】|【1.】【1.】)?【箇条書き・】")) {
			return new Style_TagInfo("\t", "<ul>\n", null, "</ul>");
		}

		if (longStyle.matches("(【ヒント】|【1.】【1.】)?【箇条書き・】【箇条書き・】")) {
			return new Style_TagInfo("\t", "<li>\n", null, "</li>");
		}

		if (longStyle.matches("(【ヒント】|【1.】【1.】)?【箇条書き・】【箇条書き・】【箇条書き・】")) {
			String sString = iText.getText().replaceFirst("・\t", "");
			return new Style_TagInfo("", "", sString, "");
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】")) {
			return new Style_TagInfo("\t", "\n<div class=\"code\">\n", "", "</div>\n");
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】")
				|| longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】【本文】")) {
			if (iText instanceof ControlText) {
				return Style_TagInfo.NULL_STYLE_TAGINFO;
			} else {
				String sString = iText.getText();
				return new Style_TagInfo("", "<p>", sString, "</p>");
			}
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】【本文】【本文】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】【本文】【本文】【本文】")) {
			if (iText instanceof ControlText) {
				return Style_TagInfo.NULL_STYLE_TAGINFO;
			} else {
				String sString = iText.getText();
				return new Style_TagInfo("", "<p>&nbsp;&nbsp;&nbsp;&nbsp;", sString, "</p>");
			}
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】【本文】【本文】【本文】【本文】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.matches("(【ヒント】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】【本文】【本文】【本文】【本文】【本文】")) {
			if (iText instanceof ControlText) {
				return Style_TagInfo.NULL_STYLE_TAGINFO;
			} else {
				String sString = iText.getText();
				return new Style_TagInfo("", "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", sString, "</p>");
			}
		}

		if (longStyle.equals("【1.】")) {
			return new Style_TagInfo("\t", "<div class=\"steps\">\n", "", "</div>");
		}

		if (longStyle.equals("【1.】【1.】")) {
			return new Style_TagInfo("\t", "<div class=\"step_block\">\n", "", "</div>");
		}

		if (longStyle.equals("【1.】【1.】【1.】")) {
			Pattern pattern = Pattern.compile("^([0-9]+)[．\\.]\t");
			Matcher matcher = pattern.matcher(iText.getText());
			if (matcher.find()) {
				String sString = iText.getText().replaceFirst("[0-9]+[．\\.]\t", "");
				return new Style_TagInfo("\t",
						"<div class=\"step\"><span class=\"no\">" + matcher.group(1) + "．</span>", sString, "</div>");
			}
		}

		if (longStyle.equals("【1.】【1.】【本文】") || longStyle.equals("【1.】【1.】【本文】【本文】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【1.】【1.】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", "<p>", sString, "</p>");
		}

		if (longStyle.equals("【箇条書き（用語）】")) {
			return new Style_TagInfo("\t", "<div class=\"glossary\">\n", null, "</div>");
		}

		if (longStyle.matches("(【箇条書き（用語）】){2}")) {
			return new Style_TagInfo("\t", "<div class=\"item\">\n", null, "</div>");
		}

		if (longStyle.matches("(【箇条書き（用語）】){3}")) {
			if (iText instanceof ControlText) {
				return new Style_TagInfo("\t", "<div class=\"glossary\">\n", null, "</div>");
			} else {
				String sString = iText.getText().replaceFirst("^::", "");
				return new Style_TagInfo("", "<div class=\"title\">", sString, "</div>");
			}
		}

		if (longStyle.matches("(【箇条書き（用語）】){2}(【本文】){1,2}")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.matches("(【箇条書き（用語）】){2}(【本文】){3}")) {
			String sString = iText.getText();
			return new Style_TagInfo("", "<p>", sString, "</p>");
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】")) {
			return new Style_TagInfo("\t", "<div class=\"item\">\n", null, "</div>");
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】")) {
			if (iText instanceof ControlText) {
				return new Style_TagInfo("\t", "<div class=\"glossary\">\n", null, "</div>");
			} else {
				String sString = iText.getText().replaceFirst("^::", "");
				return new Style_TagInfo("\t", "<div class=\"title\">", sString, "</div>");
			}
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【本文】")
				|| longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【本文】【本文】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", "<p>", sString, "</p>");
		}

		if (longStyle.equals("【――】") || longStyle.equals("【――】【――】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【――】【――】【――】")) {
			String sString = iText.getText().replaceFirst("^-+", "－－");
			return new Style_TagInfo("", "<div class=\"hack_author\">", sString, "</div>");
		}
		if (longStyle.equals("【画面】") || longStyle.equals("【画面】【画面】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【画面】【画面】【画面】")) {
			String sString = iText.getText();
			sString = sString.replaceFirst("img src", "img class=\"Indent0\" src");
			return new Style_TagInfo("", "", sString, "");
		}

		if (longStyle.equals("【本文】") || longStyle.equals("【本文】【本文】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【本文】【本文】【本文】")) {
			String sString = iText.getText();
			if (sString.equals("")) {
				sString = "<br />";
			}
			return new Style_TagInfo("", "<p>", sString, "</p>");
		}

		if (longStyle.equals("【表】")/* 2013.12.19 */
				|| longStyle.equals("【手順】【手順】【表】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.equals("【表】【行】") /* 2013.12.19 */
				|| longStyle.equals("【手順】【手順】【表】【行】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.matches("【表】【行】【セル(：ヘッダー)?】")) {/* 2013.12.19 */
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.matches("【表】【行】【セル(：ヘッダー)?】【本文】(【本文】)?")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		if (longStyle.matches("【表】【行】【セル(：ヘッダー)?】【本文】【本文】【本文】")) {
			String sString = iText.getText().replaceFirst("【ヘッダー】", "");
			String sCloseInfo = "";
			String sNextSiblingTextStyle = getNextSiblingTextStyle(iText);
			if (sNextSiblingTextStyle != null && sNextSiblingTextStyle.equals("【本文】")) {
				sCloseInfo = "<br />\n\t\t\t\t";
			} else {
				sCloseInfo = "";
			}
			return new Style_TagInfo("", "", sString, sCloseInfo);
		}

		if (longStyle.equals("【表】【行】【セル】")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		// コメントスタイルの処理
		if (longStyle.matches(".*【＃】$")) {
			return Style_TagInfo.NULL_STYLE_TAGINFO;
		}

		// 以降、ダミースタイルの処理
		return

		dummyStyle(longStyle, iText);// + longStyle;
	}

	protected String getNextSiblingTextStyle(IntermediateText iText) {
		String sNextTextStyle = null;
		Iterator<IntermediateText> it = m_cParentText.get(0).getChildList().iterator(); // get(0)は仮
		while (it.hasNext()) {
			IntermediateText iTemp = it.next();
			if (iTemp == iText) {
				// 次のスタイルを調べる
				if (it.hasNext()) {
					IntermediateText iTempNext = it.next();
					Style iTempNextStyle = iTempNext.getStyle();
					sNextTextStyle = iTempNextStyle.getStyleName();
					break;
				}
			}
		}
		return sNextTextStyle;
	}

	private String setWidthProperty(IntermediateText iText) throws IOException {
		String sString = iText.getText();
		String filename = iText.getText();
		Pattern p = Pattern.compile("src=\\\"([^\\\"]+)\\\"");
		Matcher m = p.matcher(filename);
		if (m.find()) {
			filename = m.group(1);
			File cFile = new File(m_cTargetFile.getParentFile().getAbsolutePath() + "\\" + filename);
			String sProperties = "";
			if (cFile.exists()) {
				// ファイルサイズを取得する
				Iterator<ImageReader> it = ImageIO.getImageReadersBySuffix(Utils.getFileExtension(filename));
				while (it.hasNext()) {
					ImageReader cImageReader = it.next();
					ImageInputStream stream = ImageIO.createImageInputStream(cFile);
					cImageReader.setInput(stream);
					int width = cImageReader.getWidth(0);
					@SuppressWarnings("unused")
					int height = cImageReader.getHeight(0);
					if (800 <= width) {
						if (getLongStyle().indexOf("【補足】") > -1) {
							sProperties = " style=\"width:800px;max-width:90%;\"";
						} else {
							sProperties = " width=\"800\"";
						}
					} else if (585 <= width && width < 800) {
						if (getLongStyle().indexOf("【補足】") > -1) {
							sProperties = " style=\"max-width:90%;\"";
						} else {
							sProperties = "";
						}
					} else {
						sProperties = " style=\"width:auto;\"";
					}
				}
			} else {
				System.out.println("ファイルが見つかりません：" + cFile.getAbsolutePath());
			}

			sString = iText.getText().replaceFirst("(src=\\\"[^\\\"]+\\\")(.*)", "$1" + sProperties + "$2");
		}
		return sString;
	}

	private Style_TagInfo dummyStyle(String longStyle, IntermediateText iText) {
		Style_TagInfo style = m_cDummyStyleHashTable.get(longStyle);
		if (style != null) {
			// テキスト部分だけ書き換える
			style.setLine(iText.getChildText());
			return style;
		}
		DecimalFormat df = new DecimalFormat();
		df.applyLocalizedPattern("0000");
		String sStyleName = df.format(m_nDummyCounter) + "..." + longStyle;
		style = new Style_TagInfo("", "<!-- " + sStyleName + " -->", iText.getChildText(),
				"<!-- /" + sStyleName + " -->");
		System.out.println(longStyle + "は、" + style.getOpenInfo() + "として出力されました。");
		m_cDummyStyleHashTable.put(longStyle, style);
		m_nDummyCounter++;
		return style;
	}

	public String getHTMLTagIndent() {
		return m_sHTMLTagIndent;
	}

	public void addHTMLTagIndent(String sAdditionalIndent) {
		m_sHTMLTagIndent = sAdditionalIndent + m_sHTMLTagIndent;
	}

	public void removeHTMLTagIndent(String sRemoveIndent) {
		if (sRemoveIndent.equals(""))
			return;
		m_sHTMLTagIndent = m_sHTMLTagIndent.replaceFirst("^" + sRemoveIndent, "");
	}
}
