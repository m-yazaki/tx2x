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
import tx2x.Style_TagInfo;
import tx2x.Utils;
import tx2x.core.ControlText;
import tx2x.core.IntermediateText;
import tx2x.core.Style;

public class LongStyleManagerXHTML extends tx2x.LongStyleManager {
	// 未定義のスタイル（dummy000）を管理するための変数
	int m_nDummyCounter;
	Hashtable<String, Style_TagInfo> m_cDummyStyleHashTable;

	protected boolean m_bDebugMode;
	protected NavPointManager m_cNavPointManager;
	protected File m_cTargetFile;
	protected Stack<ControlText> m_cParentText;

	public LongStyleManagerXHTML(boolean bDebugMode, File cTargetFile, NavPointManager cNavPointManager) {
		super();
		m_nDummyCounter = 0;
		m_cDummyStyleHashTable = new Hashtable<String, Style_TagInfo>();
		m_bDebugMode = bDebugMode;
		m_cTargetFile = cTargetFile;
		m_cNavPointManager = cNavPointManager;
		m_cParentText = new Stack<ControlText>();
	}

	public Style_TagInfo getStyle_TagInfo(IntermediateText iText_dup, IntermediateTextTreeWalker cTreeWalker,
			int nLsIndex) throws IOException {
		IntermediateText iText = cTreeWalker.getCurrentNode();
		if (iText != iText_dup) {
			System.out.println("cTreeWalkerの利用に失敗している");
		}
		ControlText cParent = cTreeWalker.peekParent(1);
		ControlText cParentParent = cTreeWalker.peekParent(2);
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
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【章】【章】【章】")) {
			String sString = iText.getText().replaceFirst("【章】", "");
			return new Style_TagInfo(null, null, "\n		<h1>", sString, "</h1>\n\n", null, null);
		}

		if (longStyle.equals("【章サブ】") || longStyle.equals("【章サブ】【章サブ】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【章サブ】【章サブ】【章サブ】")) {
			String sString = iText.getText();
			return new Style_TagInfo(null, null, "\t\t<div class=\"h1_sub\">", sString, "</div>\n", null, null);
		}

		if (longStyle.equals("【節】") || longStyle.equals("【節】【節】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【節】【節】【節】")) {
			String sString = iText.getText().replaceFirst("【節】", "");
			return new Style_TagInfo(null, null, "\n		<h2>", sString, "</h2>\n\n", null, null);
		}

		if (longStyle.equals("【項】") || longStyle.equals("【項】【項】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【項】【項】【項】")) {
			String sString = iText.getText().replaceFirst("【項】", "");
			return new Style_TagInfo(null, null, "\n		<h3>", sString, "</h3>\n\n", null, null);
		}

		if (longStyle.matches("【ヒント】")) {
			return new Style_TagInfo("\t\t<div class=\"hint\">\n\t\t\t<div class=\"title\">ヒント</div>\n", null, null, "",
					null, null, "\t\t</div>\n");
		}

		if (longStyle.matches("【1.】【1.】【ヒント】")) {
			return new Style_TagInfo("\t\t\t\t<div class=\"hint\">\n\t\t\t\t\t<div class=\"title\">ヒント</div>\n", null,
					null, "", null, null, "\t\t\t\t</div>\n");
		}

		if (longStyle.matches("(【1.】【1.】)?【ヒント】【本文】") || longStyle.matches("(【1.】【1.】)?【ヒント】【本文】【本文】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.matches("【ヒント】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", null, "\t\t\t<p>", sString, "</p>\n", null, "");
		}

		if (longStyle.matches("【1.】【1.】【ヒント】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", null, "\t\t\t\t\t<p>", sString, "</p>\n", null, "");
		}

		if (longStyle.equals("【注意】")) {
			return new Style_TagInfo("\t\t<div class=\"note\">\n\t\t\t<div class=\"title\">注意</div>\n", null, null, "",
					null, null, "\t\t</div>\n");
		}

		if (longStyle.equals("【注意】【本文】") || longStyle.equals("【注意】【本文】【本文】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【注意】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", null, "\t\t\t<p>", sString, "</p>\n", null, "");
		}

		if (longStyle.equals("【HACK】")) {
			return new Style_TagInfo("\t\t<div class=\"hack\">", null, null, null, null, null, "\t\t</div>\n");
		}

		if (longStyle.equals("【HACK】【HACK】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【HACK】【HACK】【HACK】")) {
			String sString = iText.getText();
			Pattern p = Pattern.compile("【HACK (.*)】(.*)");
			Matcher matcher = p.matcher(sString);
			matcher.find();
			return new Style_TagInfo("", null,
					"<div class=\"hack_no\"><div class=\"title\">HACK</div><div class=\"no\">" + matcher.group(1)
							+ "</div></div>\n\t\t\t<p>",
					matcher.group(2) + "</p>\n", "", null, "");
		}

		if (longStyle.equals("【HACK】【HACK】【本文】") || longStyle.equals("【HACK】【HACK】【本文】【本文】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【HACK】【HACK】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", null, "\t\t\t<p>", sString, "</p>\n", null, "");
		}

		if (longStyle.matches("(【ヒント】|【1.】【1.】)?【箇条書き・】")) {
			if (longStyle.matches("^【ヒント】.*")) {
				return new Style_TagInfo("\t\t\t<ul>\n", null, null, null, null, null, "\t\t\t</ul>\n");
			} else if (longStyle.equals("【1.】【1.】【箇条書き・】")) {
				return new Style_TagInfo("\t\t\t\t<ul>\n", null, null, null, null, null, "\t\t\t\t</ul>\n");
			} else {
				return new Style_TagInfo("\t\t<ul>\n", null, null, null, null, null, "\t\t</ul>\n");
			}
		}

		if (longStyle.matches("(【ヒント】|【1.】【1.】)?【箇条書き・】【箇条書き・】")) {
			if (longStyle.matches("^【ヒント】.*")) {
				return new Style_TagInfo("\t\t\t\t<li>\n", null, null, null, null, null, "\t\t\t\t</li>\n");
			} else if (longStyle.equals("【1.】【1.】【箇条書き・】【箇条書き・】")) {
				return new Style_TagInfo("\t\t\t\t\t<li>\n", null, null, null, null, null, "\t\t\t\t\t</li>\n");
			} else {
				return new Style_TagInfo("\t\t\t<li>\n", null, null, null, null, null, "\t\t\t</li>\n");
			}
		}

		if (longStyle.matches("(【ヒント】|【1.】【1.】)?【箇条書き・】【箇条書き・】【箇条書き・】")) {
			String sString = iText.getText().replaceFirst("・\t", "");
			if (longStyle.matches("^【ヒント】.*")) {
				return new Style_TagInfo("", null, "", "\t\t\t\t\t" + sString + "\n", "", null, "");
			} else if (longStyle.equals("【1.】【1.】【箇条書き・】【箇条書き・】【箇条書き・】")) {
				return new Style_TagInfo("", null, "", "\t\t\t\t\t\t" + sString + "\n", "", null, "");
			} else {
				return new Style_TagInfo("", null, "", "\t\t\t\t" + sString + "\n", "", null, "");
			}
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】")) {
			if (longStyle.matches("^【コード】")) {
				return new Style_TagInfo("\n\t\t<div class=\"code\">\n", null, null, "", null, null, "\t\t</div>\n\n");
			} else if (longStyle.matches("(【1.】【1.】|【箇条書き・】【箇条書き・】)【コード】")) {
				return new Style_TagInfo("\n\t\t\t\t<div class=\"code\">\n", null, null, "", null, null,
						"\t\t\t\t</div>\n\n");
			} else {
				return new Style_TagInfo("\n\t\t\t<div class=\"code\">\n", null, null, "", null, null,
						"\t\t\t</div>\n\n");
			}

		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】")
				|| longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】"))

		{
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】【本文】")) {
			if (iText instanceof ControlText) {
				return new Style_TagInfo("", null, null, null, null, null, "");
			} else {
				String sString = iText.getText();
				if (longStyle.matches("^【コード】.*")) {
					return new Style_TagInfo("", null, "\t\t\t<p>", sString, "</p>\n", null, "");
				} else {
					return new Style_TagInfo("", null, "\t\t\t\t\t<p>", sString, "</p>\n", null, "");
				}
			}
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】【本文】【本文】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】【本文】【本文】【本文】")) {
			if (iText instanceof ControlText) {
				return new Style_TagInfo("", null, null, null, null, null, "");
			} else {
				String sString = iText.getText();
				if (longStyle.matches("^【コード】.*")) {
					return new Style_TagInfo("", null, "\t\t\t<p>&nbsp;&nbsp;&nbsp;&nbsp;", sString, "</p>\n", null,
							"");
				} else {
					return new Style_TagInfo("", null, "\t\t\t\t\t<p>&nbsp;&nbsp;&nbsp;&nbsp;", sString, "</p>\n", null,
							"");
				}
			}
		}

		if (longStyle.matches("(【ヒント】|【注意】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】【本文】【本文】【本文】【本文】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.matches("(【ヒント】|【1.】【1.】|【箇条書き・】【箇条書き・】)?【コード】【本文】【本文】【本文】【本文】【本文】【本文】【本文】")) {
			if (iText instanceof ControlText) {
				return new Style_TagInfo("", null, null, null, null, null, "");
			} else {
				String sString = iText.getText();
				return new Style_TagInfo("", null, "\t\t\t<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", sString,
						"</p>\n", null, "");
			}
		}

		if (longStyle.equals("【1.】")) {
			return new Style_TagInfo("\t\t<div class=\"steps\">\n", null, null, "", null, null, "\t\t</div>\n");
		}

		if (longStyle.equals("【1.】【1.】")) {
			return new Style_TagInfo("\t\t\t<div class=\"step_block\">\n", null, null, "", null, null,
					"\t\t\t</div>\n");
		}

		if (longStyle.equals("【1.】【1.】【1.】")) {
			Pattern pattern = Pattern.compile("^([0-9]+)[．\\.]\t");
			Matcher matcher = pattern.matcher(iText.getText());
			if (matcher.find()) {
				String sString = iText.getText().replaceFirst("[0-9]+[．\\.]\t", "");
				return new Style_TagInfo("", null,
						"\t\t\t\t<div class=\"step\"><span class=\"no\">" + matcher.group(1) + "．</span>", sString,
						"</div>\n", null, "");
			}
		}

		if (longStyle.equals("【1.】【1.】【本文】") || longStyle.equals("【1.】【1.】【本文】【本文】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【1.】【1.】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", null, "\t\t\t\t<p>", sString, "</p>\n", null, "");
		}

		if (longStyle.equals("【箇条書き（用語）】")) {
			return new Style_TagInfo("\t\t<div class=\"glossary\">\n", null, null, null, null, null, "\t\t</div>\n");
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】")) {
			return new Style_TagInfo("\t\t\t<div class=\"item\">\n", null, null, null, null, null, "\t\t\t</div>\n");
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】")) {
			if (iText instanceof ControlText) {
				return new Style_TagInfo("\t\t\t\t<div class=\"glossary\">\n", null, null, null, null, null,
						"\t\t\t\t</div>\n");
			} else {
				String sString = iText.getText().replaceFirst("^::", "");
				return new Style_TagInfo("", null, "\t\t\t\t<div class=\"title\">", sString, "</div>\n", null, "");
			}
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【本文】") || longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【本文】【本文】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", null, "\t\t\t\t<p>", sString, "</p>\n", null, "");
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】")) {
			return new Style_TagInfo("\t\t\t\t\t<div class=\"item\">\n", null, null, null, null, null,
					"\t\t\t\t\t</div>\n");
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】")) {
			if (iText instanceof ControlText) {
				return new Style_TagInfo("\t\t\t\t\t\t\t<div class=\"glossary\">\n", null, null, null, null, null,
						"\t\t\t\t\t\t\t</div>\n");
			} else {
				String sString = iText.getText().replaceFirst("^::", "");
				return new Style_TagInfo("", null, "\t\t\t\t\t\t<div class=\"title\">", sString, "</div>\n", null, "");
			}
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【本文】")
				|| longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【本文】【本文】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【箇条書き（用語）】【本文】【本文】【本文】")) {
			String sString = iText.getText();
			return new Style_TagInfo("", null, "\t\t\t\t\t\t<p>", sString, "</p>\n", null, "");
		}

		if (longStyle.equals("【――】") || longStyle.equals("【――】【――】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【――】【――】【――】")) {
			String sString = iText.getText().replaceFirst("^-+", "－－");
			return new Style_TagInfo(null, null, "\t\t<div class=\"hack_author\">", sString, "</div>\n", null, null);
		}
		if (longStyle.equals("【画面】") || longStyle.equals("【画面】【画面】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【画面】【画面】【画面】")) {
			String sString = iText.getText();
			sString = sString.replaceFirst("img src", "img class=\"Indent0\" src");
			return new Style_TagInfo("", null, "\t\t", sString, "\n", null, "");
		}

		if (longStyle.equals("【本文】") || longStyle.equals("【本文】【本文】")) {
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.equals("【本文】【本文】【本文】")) {
			String sString = iText.getText();
			if (sString.equals("")) {
				sString = "<br />";
			}
			return new Style_TagInfo("", null, "\t\t<p>", sString, "</p>\n", null, "");
		}

		if (longStyle.equals("【表】")/* 2013.12.19 */
				|| longStyle.equals("【手順】【手順】【表】")) {
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.equals("【表】【行】") /* 2013.12.19 */
				|| longStyle.equals("【手順】【手順】【表】【行】")) {
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.matches("【表】【行】【セル(：ヘッダー)?】")) {/* 2013.12.19 */
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.matches("【表】【行】【セル(：ヘッダー)?】【本文】(【本文】)?")) {
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.matches("【表】【行】【セル(：ヘッダー)?】【本文】【本文】【本文】")) {
			String sString = iText.getText().replaceFirst("【ヘッダー】", "");
			String sLineCloseInfo = "";
			String sNextSiblingTextStyle = getNextSiblingTextStyle(iText);
			if (sNextSiblingTextStyle != null && sNextSiblingTextStyle.equals("【本文】"))
				sLineCloseInfo = "<br />\n\t\t\t\t";
			return new Style_TagInfo("", null, "", sString, sLineCloseInfo, null, "");
		}

		if (longStyle.equals("【表】【行】【セル】")) {
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		// コメントスタイルの処理
		if (longStyle.matches(".*【＃】$"))

		{
			return null;
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
				Iterator<ImageReader> it = ImageIO.getImageReadersBySuffix(getExtension(filename));
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

	private String getExtension(String filename) {
		int i = filename.lastIndexOf('.');
		if (0 < i && i < filename.length() - 1) { // .がない場合や、末尾に.があった場合は拡張子なし
			return filename.substring(i + 1).toLowerCase();
		}
		return "";
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
		style = new Style_TagInfo("<!-- " + sStyleName + "_1 -->", "<!-- " + sStyleName + "_2 -->",
				"<!-- " + sStyleName + "_3 -->", iText.getChildText(), "<!-- " + sStyleName + "_/3 -->",
				"<!-- " + sStyleName + "_/2 -->", "<!-- " + sStyleName + "_/1 -->");
		System.out.println(longStyle + "は、" + style.getLineOpenInfo() + "として出力されました。");
		m_cDummyStyleHashTable.put(longStyle, style);
		m_nDummyCounter++;
		return style;
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

}
