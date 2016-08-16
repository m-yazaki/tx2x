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
import tx2x.xhtml.NavPointManager;

public class LongStyleManagerXHTML extends tx2x.xhtml.LongStyleManagerXHTML {
	// 未定義のスタイル（dummy000）を管理するための変数
	int m_nDummyCounter;
	Hashtable<String, Style_TagInfo> m_cDummyStyleHashTable;

	public LongStyleManagerXHTML(boolean bDebugMode, File cTargetFile, NavPointManager cNavPointManager) {
		super(bDebugMode, cTargetFile, cNavPointManager);
		m_nDummyCounter = 0;
		m_cDummyStyleHashTable = new Hashtable<String, Style_TagInfo>();
		m_bDebugMode = bDebugMode;
		m_cTargetFile = cTargetFile;
		m_cNavPointManager = cNavPointManager;
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

		if (longStyle.equals("【編】") || longStyle.equals("【章】") /* 2013.12.19 */
				|| longStyle.equals("【節】") /* 2013.12 .19 */
				|| longStyle.equals("【項】")
				|| longStyle.equals("【項下】") /*
											 * 2013.12 .19
											 */
				|| longStyle.equals("【：】") || longStyle.equals("【■】") || longStyle.equals("【項下下】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【編】【編】")
				|| longStyle.equals("【章】【章】") /* 2013.12.19 */
				|| longStyle.equals("【節】【節】") /* 2013.12.19 */
				|| longStyle.equals("【項】【項】")
				|| longStyle.equals("【項下】【項下】")/*
												 * 2013.12 .19
												 */
				|| longStyle.equals("【：】【：】") || longStyle.equals("【■】【■】") || longStyle.equals("【項下下】【項下下】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【編】【編】【編】")) {
			return null;
		}

		if (longStyle.equals("【章】【章】【章】")) { /* 2013.12.19 */
			String sString = iText.getText().replaceFirst("\\. ", ".");
			return new Style_TagInfo(null, null, "\n		<h1>", sString, "</h1>\n\n", null, null);
		}

		if (longStyle.equals("【節】【節】【節】")) { /* 2013.12.19 */
			String sString = iText.getText().replace('\t', ' ');
			if (sString.indexOf("（Line2）") != -1) {
				sString = sString.replace("（Line2）", "");
				return new Style_TagInfo(null, null, "\n		<h2 class=\"Line2\">", sString, "</h2>\n", null, null);
			} else {
				return new Style_TagInfo(null, null, "\n		<h2>", sString, "</h2>\n\n", null, null);
			}
		}

		if (longStyle.equals("【項】【項】【項】")) { /* 2013.12.19 */
			String sString = iText.getText().replace('\t', ' ');
			return new Style_TagInfo(null, null, "\n		<h3>", sString, "</h3>\n", null, null);
		}

		if (longStyle.equals("【：】【：】【：】")) {
			String sString = iText.getText().replaceFirst("：", "");
			return new Style_TagInfo(null, null, "\n		<h4>", sString, "</h4>\n", null, null);
		}

		if (longStyle.equals("【項下】【項下】【項下】") /* 2013.12.19 */
				|| longStyle.equals("【■】【■】【■】")) {
			String sString = iText.getText().replaceFirst("■", "");
			return new Style_TagInfo(null, null, "\n		<h5>", sString, "</h5>\n", null, null);
		}

		if (longStyle.equals("【項下下】【項下下】【項下下】")) {
			String sString = iText.getText().replaceFirst("【項下下】", "");
			return new Style_TagInfo(null, null, "\n		<h5>", sString, "</h5>\n", null, null);
		}

		if (longStyle.equals("【○】") || longStyle.equals("【○】【○】")) {
			return null;
		}

		if (longStyle.equals("【○】【○】【○】")) {
			String md5 = "id" + Utils.caluculateMD5(iText.getText());
			iText.setText(iText.getText().substring(1));
			m_cNavPointManager.add(iText.getText(), 3, md5);
			return new Style_TagInfo(null, null, "<h3 xmlns=\"http://www.w3.org/1999/xhtml\" id=\"" + md5 + "\">",
					iText.getText(), "</h3>", null, null);
		}

		if (longStyle.equals("【本文】")) {
			if (getPrevLongStyle().equals("【画面】【画面】【画面】")) { /* 2013.12.19 */
				return new Style_TagInfo("\n", null, null, null, null, null, "");
			} else {
				return new Style_TagInfo("", null, null, "", null, null,
						""); /* 2013.12.19 */
			}
		}

		if (longStyle.equals("【本文】【本文】")) {
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.equals("【本文】【本文】【本文】")) {
			if (iText instanceof ControlText) {
				return new Style_TagInfo("", null, null, "", null, null, "");
			} else {
				String sString = iText.getText();
				if (sString.equals("")) {
					sString = "<br />";
				} else if (sString.indexOf("【右揃え】") > -1) {
					sString = sString.replaceFirst("【右揃え】", "");
					return new Style_TagInfo("", null, "\t\t<p class=\"TextRight\">", sString, "</p>\n", null,
							""); /* 2013.12.19 */
				}
				return new Style_TagInfo("", null, "\t\t<p>", sString, "</p>\n", null,
						""); /* 2013.12.19 */
			}
		}

		if (longStyle.equals("【箇条書き●】") /* 2013.12.19 */) {
			return new Style_TagInfo("\t\t<ul>\n", null, null, null, null, null, "\t\t</ul>\n");
		}

		if (longStyle.equals("【手順】【手順】【箇条書き●】")) {
			return new Style_TagInfo("\t\t<ul class=\"Indent2\">\n", null, null, null, null, null, "\t\t</ul>\n");
		}

		if (longStyle.equals("【表】【行】【セル】【箇条書き●】")) {
			return new Style_TagInfo("\t\t<ul>\n", null, null, null, null, null, "\t\t</ul>\n");
		}

		if (longStyle.equals("【箇条書き●】【箇条書き●】")
				|| longStyle.equals("【手順】【手順】【箇条書き●】【箇条書き●】")/* 2013.12.19 */
				|| longStyle.equals("【表】【行】【セル】【箇条書き●】【箇条書き●】")) {
			return new Style_TagInfo("\t\t\t<li>", null, null, null, null, null, "</li>\n");
		}

		if (longStyle.equals("【箇条書き●】【箇条書き●】【箇条書き●】") /* 2013.12.19 */
				|| longStyle.equals("【手順】【手順】【箇条書き●】【箇条書き●】【箇条書き●】")
				|| longStyle.equals("【表】【行】【セル】【箇条書き●】【箇条書き●】【箇条書き●】")) {
			if (iText instanceof ControlText) {
				return new Style_TagInfo("\n\t\t\t\t<ul>\n", null, null, null, null, null, "\t\t\t\t</ul>");
			} else {
				String sString = iText.getText().replaceFirst("●\t", "");
				return new Style_TagInfo("", "", "", sString, "", "",
						""); /*
								 * 2013.12 .19
								 */
			}
		}

		if (longStyle.equals("【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】")) {
			return new Style_TagInfo("\t\t\t\t\t<li>", null, null, "", null, null, "</li>\n");
		}

		if (longStyle.equals("【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】")) {
			String sString = iText.getText().replaceFirst("●\t", "");
			return new Style_TagInfo(null, null, "", sString, "", null, null);

		}

		if (longStyle.equals("【箇条書き●】【箇条書き●】【本文】")) {
			return new Style_TagInfo("<br />\n", null, null, iText.getText(), null, null, "");
		}

		if (longStyle.equals("【箇条書き●】【箇条書き●】【本文】【本文】")) {
			String sLineCloseInfo = "";
			String sNextSiblingTextStyle = getNextSiblingTextStyle(iText);
			if (sNextSiblingTextStyle != null && sNextSiblingTextStyle.equals("【本文】"))
				sLineCloseInfo = "<br />";
			return new Style_TagInfo(null, null, "\t\t\t\t", iText.getText(), sLineCloseInfo, null, null);
		}

		if (longStyle.equals("【※】") || longStyle.equals("【※0】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【※】【※】") || longStyle.equals("【※0】【※0】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【※】【※】【※】") || longStyle.equals("【※0】【※0】【※0】")) {
			String sString = iText.getText().replaceFirst("\t", " ");
			return new Style_TagInfo(null, null, "\t\t<p class=\"Postscript\">", sString, "</p>\n", null, null);
		}

		if (longStyle.equals("【手順】【手順】【※】") || longStyle.equals("【手順】【手順】【※0】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【手順】【手順】【※】【※】") || longStyle.equals("【手順】【手順】【※0】【※0】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【手順】【手順】【※】【※】【※】") || longStyle.equals("【手順】【手順】【※0】【※0】【※0】")) {
			String sString = iText.getText().replaceFirst("\t", " ");
			return new Style_TagInfo(null, null, "\t\t<p class=\"Postscript Indent3\">", sString, "</p>\n", null, null);
		}

		if (longStyle.equals("【手順】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.equals("【手順】【手順】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.equals("【手順】【手順】【手順】")) { /* 2013.12.19 */
			Pattern pattern = Pattern.compile("^([0-9]+)[．\\.]\t");
			Matcher matcher = pattern.matcher(iText.getText());
			if (matcher.find()) {
				String sString = iText.getText().replaceFirst("[．\\.]\t", "．");
				return new Style_TagInfo("", null, "\n\t\t<h6 id=\"step" + matcher.group(1) + "\">", sString, "</h6>\n",
						null, "");
			}
		}

		if (longStyle.equals("【手順】【手順】【項下】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【手順】【手順】【項下】【項下】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【手順】【手順】【項下】【項下】【項下】")) { /* 2013.12.19 */
			String sString = iText.getText().replaceFirst("■", "");
			return new Style_TagInfo(null, null, "\n\t\t<h5 class=\"Indent15\">", sString, "</h5>\n", null, null);
		}

		if (longStyle.equals("【手順】【手順】【本文】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【手順】【手順】【本文】【本文】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【手順】【手順】【本文】【本文】【本文】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, "\t\t<p class=\"Result\">", iText.getText(), "</p>\n", null, "");
		}

		if (longStyle.equals("【手順】【手順】【例：】")) {
			return new Style_TagInfo(null, null, null, iText.getText(), null, null, null);
		}

		if (longStyle.equals("【手順】【手順】【例：】【例：】")) {
			return new Style_TagInfo("", null, null, iText.getText(), null, null, "");
		}

		/* 2013.12.19 */
		if (longStyle.equals("【手順】【手順】【例：】【例：】【例：】")) {
			return new Style_TagInfo(null, null, "\t\t<p class=\"Result\">", iText.getText(), "</p>\n", null, null);
		}

		if (longStyle.equals("【手順】【手順】【code】")) {
			return new Style_TagInfo("<pre>", null, null, iText.getText(), null, null, "</pre>");
		}

		if (longStyle.equals("【手順】【手順】【code】【本文】") || longStyle.equals("【手順】【手順】【code】【本文】【本文】")) {
			return new Style_TagInfo(null, null, "", iText.getText(), "", null, null);
		}

		if (longStyle.equals("【画面】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.equals("【補足】【画面】")) { /* 2013.12.19 */
			return new Style_TagInfo("\t\t\t\t", null, null, null, null, null, "");
		}

		if (longStyle.equals("【補足】【画面】【画面】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【補足】【箇条書き●】【箇条書き●】【画面】")) { /* 2013.12.19 */
			return new Style_TagInfo("\t\t\t\t\t", null, null, null, null, null, "");
		}

		if (longStyle.equals("【補足】【箇条書き●】【箇条書き●】【画面】【画面】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【画面】【画面】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.matches("【手順】【手順】(【補足】)?【画面】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.matches("【手順】【手順】(【補足】)?【画面】【画面】")) { /* 2013.12.19 */
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.matches("(【手順】【手順】)?【画面】【画面】【画面】")) {
			String sString = iText.getText();
			if (longStyle.matches("【画面】【画面】【画面】")) {
				// System.out.println(sString);
				sString = sString.replaceFirst("img src", "img class=\"Indent0\" src");
				// System.out.println(sString);
			}
			return new Style_TagInfo("", null, "\t\t", sString, "\n", null, "");
		}

		if (longStyle.matches("【補足】【画面】【画面】【画面】")) { /* 2013.12.19 */
			String sString = iText.getText();
			return new Style_TagInfo("", null, "", sString, "\n", null, "");
		}

		if (longStyle
				.matches("【補足】【箇条書き●】【箇条書き●】【画面】【画面】【画面】")) { /* 2013.12.19 */
			String sString = iText.getText();
			return new Style_TagInfo("", null, "", sString, "\n", null, "");
		}

		if (longStyle.matches("【手順】【手順】【補足】【画面】【画面】【画面】")) { /* 2013.12.19 */
			String sString = iText.getText();
			return new Style_TagInfo("", null, "\t\t\t\t", sString, "\n", null, "");
		}

		/* 2013.12.19 */
		if (longStyle.equals("【手順】【手順】【重要】")) {
			return new Style_TagInfo(
					"\t\t<div class=\"Importance Indent2\">\n\t\t\t<div class=\"ImportanceTitle\">重 要</div>\n", null,
					null, "", null, null, "\t\t</div>\n");
		}

		if (longStyle.equals("【手順】【手順】【注意】")) {
			return new Style_TagInfo(
					"\t\t<div class=\"Caution Indent2\">\n\t\t\t<div class=\"CautionTitle\">注 意</div>\n", null, null,
					"", null, null, "\t\t</div>\n");
		}

		/* 2013.12.19 */
		if (longStyle.equals("【手順】【手順】【補足】")) {
			return new Style_TagInfo(
					"\t\t<div class=\"supplement Indent15\">\n\t\t\t<div class=\"supplementtitle\">補 足</div>\n", null,
					null, "", null, null, "\t\t</div>\n");
		}

		/* 2013.12.19 */
		if (longStyle.equals("【コマンド】") || longStyle.equals("【手順】【手順】【コマンド】")) {
			return new Style_TagInfo("\t\t<div class=\"Command\">\n", null, null, "", null, null, "\t\t</div>\n");
		}

		/* 2013.12.19 */
		if (longStyle.equals("【注意】【コマンド】")) {
			return new Style_TagInfo("\t\t\t\t<div class=\"Command\">\n", null, null, "", null, null,
					"\t\t\t\t</div>\n");
		}

		/* 2013.12.19 */
		if (longStyle.equals("【コマンド】【本文】") || longStyle.equals("【手順】【手順】【コマンド】【本文】")
				|| longStyle.equals("【注意】【コマンド】【本文】")) {
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		/* 2013.12.19 */
		if (longStyle.equals("【コマンド】【本文】【本文】") || longStyle.equals("【手順】【手順】【コマンド】【本文】【本文】")) {
			String sString = iText.getText();
			sString = sString.replace("（", "<span>（");
			sString = sString.replace("）", "）</span>");
			if (sString.equals("")) {
				sString = "<br />";
			}
			return new Style_TagInfo(null, null, "\t\t\t<p>", sString, "</p>\n", null, null);
		}
		/* 2013.12.19 */
		if (longStyle.equals("【注意】【コマンド】【本文】【本文】")) {
			String sString = iText.getText();
			sString = sString.replace("（", "<span>（");
			sString = sString.replace("）", "）</span>");
			return new Style_TagInfo(null, null, "\t\t\t\t\t<p>", sString, "</p>\n", null, null);
		}

		/* 2013.12.19 */
		if (longStyle.equals("【注意】【箇条書き●】【箇条書き●】【コマンド】")) {
			return new Style_TagInfo("\n\t\t\t\t<div class=\"Command\">\n", null, null, "", null, null,
					"\t\t\t\t</div>\n");
		}

		/* 2013.12.19 */
		if (longStyle.equals("【注意】【箇条書き●】【箇条書き●】【コマンド】【本文】")) {
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		/* 2013.12.19 */
		if (longStyle.equals("【注意】【箇条書き●】【箇条書き●】【コマンド】【本文】【本文】")) {
			String sString = iText.getText();
			sString = sString.replace("（", "<span>（");
			sString = sString.replace("）", "）</span>");
			return new Style_TagInfo(null, null, "\t\t\t\t\t<p class=\"Indent1\">", sString, "</p>\n", null, null);
		}

		if (longStyle.equals("【重要】")) { /* 2013.12.19 */
			return new Style_TagInfo(
					"\n\t\t<div class=\"Importance\">\n\t\t\t<div class=\"ImportanceTitle\">重 要</div>\n", null, null,
					"", null, null, "\t\t</div>\n\n");
		}

		if (longStyle.equals("【注意】")) { /* 2013.12.19 */
			return new Style_TagInfo("\t\t<div class=\"Caution\">\n\t\t\t<div class=\"CautionTitle\">注 意</div>\n", null,
					null, "", null, null, "\t\t</div>\n");
		}

		if (longStyle.equals("【補足】")) {
			return new Style_TagInfo(
					"\n\t\t<div class=\"supplement\">\n\t\t\t<div class=\"supplementtitle\">補足</div>\n", null, null, "",
					null, null, "\t\t</div>\n");
		}

		if (longStyle.equals("【囲み】")) { /* 2013.12.19 */
			if (getPrevLongStyle().equals("【節】【節】【節】"))
				return new Style_TagInfo("\n\t\t<div class=\"Note\">\n", null, null, "", null, null, "\t\t</div>\n");
			else
				return new Style_TagInfo("\t\t<br />\n\t\t<div class=\"Note\">\n", null, null, "", null, null,
						"\t\t</div>\n\n");
		}

		if (longStyle.equals("【重要】【重要】") || longStyle.equals("【注意】【注意】") || longStyle.equals("【補足】【補足】")) {
			return new Style_TagInfo("", null, "", "", "", null, "");
		}

		if (longStyle.equals("【重要】【本文】") /* 2013.12.19 */
				|| longStyle.equals("【注意】【本文】") || longStyle.equals("【補足】【本文】") || longStyle.equals("【手順】【手順】【重要】【本文】")
				|| longStyle.equals("【手順】【手順】【注意】【本文】")
				|| longStyle.equals("【手順】【手順】【補足】【本文】") /* 2013.12.19 */) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【囲み】【本文】")) {
			return null;
			// return new Style_TagInfo("\t\t\t<ul>\n", null, null, null, null,
			// null, "\t\t\t</ul>\n");
		}

		if (longStyle.equals("【手順】【手順】【補足】【本文】【本文】")) {
			return null;
		}

		if (longStyle.equals("【重要】【本文】【本文】【本文】") || longStyle.equals("【注意】【本文】【本文】【本文】")
				|| longStyle.equals("【補足】【本文】【本文】【本文】") || longStyle.equals("【手順】【手順】【重要】【本文】【本文】【本文】")
				|| longStyle.equals("【手順】【手順】【注意】【本文】【本文】【本文】")
				|| longStyle.equals("【手順】【手順】【補足】【本文】【本文】【本文】") /* 2013.12.19 */) {
			String sLineOpenInfo = "\t\t\t\t<p>";
			String sLineCloseInfo = "</p>\n";
			String sNextSiblingTextStyle = getNextSiblingTextStyle(iText);
			if (sNextSiblingTextStyle != null) {
				if (sNextSiblingTextStyle.equals("【箇条書き●】")) {
					sLineOpenInfo = "";
					sLineCloseInfo = "\n";
				}
			}
			return new Style_TagInfo(null, null, sLineOpenInfo, iText.getText(), sLineCloseInfo, null, null);
		}

		if (longStyle.equals("【囲み】【本文】【本文】")) {
			String sLineOpenInfo = "\t\t\t<p>";
			String sLineCloseInfo = "</p>\n";
			String sNextSiblingTextStyle = getNextSiblingTextStyle(iText);
			if (sNextSiblingTextStyle != null) {
				if (sNextSiblingTextStyle.equals("【箇条書き●】")) {
					sLineOpenInfo = "";
					sLineCloseInfo = "\n";
				}
			}
			return new Style_TagInfo(null, null, sLineOpenInfo, iText.getText(), sLineCloseInfo, null, null);
		}

		if (longStyle.equals("【重要】【本文】【本文】") || longStyle.equals("【注意】【本文】【本文】") || longStyle.equals("【補足】【本文】【本文】")) {
			return null;
		}

		if (longStyle.equals("【重要】【本文】【本文】【本文】") || longStyle.equals("【注意】【本文】【本文】【本文】")
				|| longStyle.equals("【補足】【本文】【本文】【本文】")) {
			return new Style_TagInfo(null, null, "\t\t\t\t<p>", iText.getText(), "</p>\n", null, "");
		}

		if (longStyle.equals("【重要】【箇条書き●】") /* 2013.12.19 */
				|| longStyle.equals("【注意】【箇条書き●】") /* 2013.12.19 */
				|| longStyle.equals("【補足】【箇条書き●】") /* 2013.12.19 */
				|| longStyle.equals("【手順】【手順】【重要】【箇条書き●】") || longStyle.equals("【手順】【手順】【注意】【箇条書き●】")
				|| longStyle.equals("【手順】【手順】【補足】【箇条書き●】")) {
			return new Style_TagInfo("\t\t\t<ul>\n", null, null, null, null, null, "\t\t\t</ul>\n");
		}

		if (longStyle.equals("【重要】【箇条書き●】【箇条書き●】") /* 2013.12.19 */
				|| longStyle.equals("【注意】【箇条書き●】【箇条書き●】") /* 2013.12.19 */
				|| longStyle.equals("【補足】【箇条書き●】【箇条書き●】") /* 2013.12.19 */) {
			return new Style_TagInfo("\t\t\t\t<li>", null, null, null, null, null, "\t\t\t\t</li>\n");
		}

		if (longStyle.equals("【手順】【手順】【重要】【箇条書き●】【箇条書き●】") || longStyle.equals("【手順】【手順】【注意】【箇条書き●】【箇条書き●】")
				|| longStyle.equals("【手順】【手順】【補足】【箇条書き●】【箇条書き●】")) {
			return new Style_TagInfo("\t\t\t\t<li>", null, null, null, null, null, "\t\t\t\t</li>\n");
		}

		if (longStyle.equals("【重要】【箇条書き●】【箇条書き●】【箇条書き●】")
				|| longStyle
						.equals("【注意】【箇条書き●】【箇条書き●】【箇条書き●】") /* 2013.12.19 */
				|| longStyle.equals("【補足】【箇条書き●】【箇条書き●】【箇条書き●】")
				|| longStyle.equals("【手順】【手順】【重要】【箇条書き●】【箇条書き●】【箇条書き●】")
				|| longStyle.equals("【手順】【手順】【注意】【箇条書き●】【箇条書き●】【箇条書き●】")
				|| longStyle.equals("【手順】【手順】【補足】【箇条書き●】【箇条書き●】【箇条書き●】")) {
			// 【(重要|注意|補足)】【箇条書き●】【箇条書き●】【箇条書き●】では、1番が使用される
			if (iText instanceof ControlText) {
				return new Style_TagInfo("\n\t\t\t\t<ul class=\"Indent15\">\n", "", "", null, "", "",
						"\t\t\t\t</ul>\n");
			} else {
				String sString = iText.getChildText().replaceFirst("●\t", "");
				return new Style_TagInfo("", "", "", sString, "<br/>\n", "", "");
			}
		}

		if (longStyle.equals("【重要】【本文】【箇条書き●】") || longStyle.equals("【注意】【本文】【箇条書き●】")
				|| longStyle.equals("【補足】【本文】【箇条書き●】")) {
			return new Style_TagInfo("\t\t\t\t\t<ul>\n", null, null, null, null, null, "\t\t\t\t\t</ul>\n\t\t\t\t");
		}

		if (longStyle.equals("【重要】【本文】【箇条書き●】【箇条書き●】") || longStyle.equals("【注意】【本文】【箇条書き●】【箇条書き●】")
				|| longStyle.equals("【補足】【本文】【箇条書き●】【箇条書き●】")) {
			return new Style_TagInfo("\t\t\t\t\t\t<li>", null, null, null, null, null, "</li>\n");
		}

		if (longStyle.equals("【重要】【本文】【箇条書き●】【箇条書き●】【箇条書き●】") || longStyle.equals("【注意】【本文】【箇条書き●】【箇条書き●】【箇条書き●】")
				|| longStyle.equals("【補足】【本文】【箇条書き●】【箇条書き●】【箇条書き●】")) {
			String sString = iText.getText().replaceFirst("●\t", "");
			return new Style_TagInfo(null, null, "", sString, "", null, null);
		}

		if (longStyle.equals("【重要】【箇条書き●】【箇条書き●】【本文】") || longStyle.equals("【注意】【箇条書き●】【箇条書き●】【本文】")
				|| longStyle.equals("【補足】【箇条書き●】【箇条書き●】【本文】")) {
			if (getPrevLongStyle().equals("【重要】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】")
					|| getPrevLongStyle().equals("【注意】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】")
					|| getPrevLongStyle().equals("【補足】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】")) {
				return new Style_TagInfo("", null, null, iText.getText(), null, null, "");
			} else {
				return new Style_TagInfo("<br />\n", null, null, iText.getText(), null, null, "");
			}
		}

		if (longStyle.equals("【重要】【箇条書き●】【箇条書き●】【本文】【本文】") || longStyle.equals("【注意】【箇条書き●】【箇条書き●】【本文】【本文】")
				|| longStyle.equals("【補足】【箇条書き●】【箇条書き●】【本文】【本文】")) {
			String sLineCloseInfo = "";
			String sNextSiblingTextStyle = getNextSiblingTextStyle(iText);
			if (sNextSiblingTextStyle != null && sNextSiblingTextStyle.equals("【本文】"))
				sLineCloseInfo = "<br />";
			return new Style_TagInfo(null, null, "\t\t\t\t\t", iText.getText(), sLineCloseInfo, null, null);
		}

		if (longStyle.equals("【重要】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】")
				|| longStyle.equals(
						"【注意】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】") /*
															 * 2013.12 .19
															 */
				|| longStyle.equals("【補足】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】")) {
			return new Style_TagInfo("\t\t\t\t\t<li>", null, null, null, null, null, "</li>\n");
		}

		if (longStyle.equals("【重要】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】")
				|| longStyle.equals("【注意】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】")
				|| longStyle.equals("【補足】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】【箇条書き●】")) {
			String sString = iText.getText().replaceFirst("●\t", "");
			return new Style_TagInfo("\t\t\t\t\t<ul>\n", "", "", sString, "", "", "\t\t\t\t\t</ul>\n");
		}

		if (longStyle.equals("【表】")/* 2013.12.19 */
				|| longStyle.equals("【手順】【手順】【表】")) {
			return new Style_TagInfo("\n", null, null, "", null, null, "");
		}

		if (longStyle.equals("【表】【行】") /* 2013.12.19 */
				|| longStyle.equals("【手順】【手順】【表】【行】")) {
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.matches("(【手順】【手順】)?【表】【行】【セル：ヘッダー】")) {/* 2013.12.19 */
			return new Style_TagInfo("", null, "", "", "", null, "");
		}

		if (longStyle
				.matches("(【手順】【手順】)?【表】【行】【セル：ヘッダー】【本文】")) {/* 2013.12.19 */
			return null;
		}

		if (longStyle.matches(
				"(【手順】【手順】)?【表】【行】【セル：ヘッダー】【本文】【本文】")) {/* 2013.12.19 */
			return null;
		}

		if (longStyle.matches(
				"(【手順】【手順】)?【表】【行】【セル：ヘッダー】【本文】【本文】【本文】")) {/* 2013.12.19 */
			String sString = iText.getText().replaceFirst("【ヘッダー】", "");
			String sLineCloseInfo = "";
			String sNextSiblingTextStyle = getNextSiblingTextStyle(iText);
			if (sNextSiblingTextStyle != null && sNextSiblingTextStyle.equals("【本文】"))
				sLineCloseInfo = "<br />\n\t\t\t\t";
			return new Style_TagInfo("", null, "", sString, sLineCloseInfo, null, "");
		}

		if (longStyle.equals("【表】【行】【セル】") /* 2013.12.19 */
				|| longStyle.equals("【手順】【手順】【表】【行】【セル】")) {
			ControlText cChildText = (ControlText) ((ControlText) iText).getChildList().get(0);
			if (((ControlText) iText).getChildList().size() == 1 && cChildText.getChildList().size() == 1) {
				return null;
			} else {
				return new Style_TagInfo(null, null, null, null, null, null, "\t\t\t\t");
			}
		}

		if (longStyle.equals("【表】【行】【セル】【項下】") || longStyle.equals("【表】【行】【セル】【■】")) {
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.equals("【表】【行】【セル】【項下】【項下】") || longStyle.equals("【表】【行】【セル】【■】【■】")) {
			String sString = iText.getText().replace('\t', ' ');
			return new Style_TagInfo(null, null, "", sString, "", null, null);
		}

		if (longStyle.equals("【表】【行】【セル】【項下】【項下】【項下】") || longStyle.equals("【表】【行】【セル】【■】【■】【■】")) {
			String sString = iText.getText().replaceFirst("■", "");
			return new Style_TagInfo(null, null, "<h5>", sString, "</h5>", null, null);
		}

		if (longStyle.equals("【表】【行】【セル】【本文】")/* 2013.12.19 */
				|| longStyle.equals("【手順】【手順】【表】【行】【セル】【本文】")) {
			return null;
		}

		if (longStyle.equals("【表】【行】【セル】【本文】【本文】")/* 2013.12.19 */
				|| longStyle.equals("【手順】【手順】【表】【行】【セル】【本文】【本文】")) {
			return null;
		}

		if (longStyle.equals("【表】【行】【セル】【本文】【本文】【本文】")/* 2013.12.19 */
				|| longStyle.equals("【手順】【手順】【表】【行】【セル】【本文】【本文】【本文】")) {
			// 前の行から同じスタイルが続く場合は<br />を追加する
			String sLineOpenInfo = "";
			String sLineCloseInfo = "";
			ControlText cParentParentParent = cTreeWalker.peekParent(3);

			if (cParentParentParent.getChildList().size() > 1 || cParentParent.getChildList().size() > 1) {
				sLineOpenInfo = "\n\t\t\t\t\t<p>";
				sLineCloseInfo = "</p>";
				if (cParentParent.getIndex(cParent) == cParentParent.getChildList().size() - 1) {
					// 複数行の場合、最後の1行だけインデントを調整する
					sLineCloseInfo = sLineCloseInfo + "\n";
				}
			} else {
				// 兄弟がいない
				sLineOpenInfo = "";
				sLineCloseInfo = "";
			}

			// 【中央揃え】は削除する（すでにスタイル変更済み）
			String sString = iText.getText();
			if (sString.indexOf("【中央揃え】") != -1) {
				sString = sString.replaceFirst("【中央揃え】", "");
			}
			return new Style_TagInfo("", null, sLineOpenInfo, sString, sLineCloseInfo, null, "");
		}

		if (longStyle.equals("【手順】【手順】【表】【行】【セル】【箇条書き●】")) {
			return new Style_TagInfo("\t\t\t\t\t<ul>\n", null, null, null, null, null, "\t\t\t\t\t</ul>\n");
		}

		if (longStyle.equals("【手順】【手順】【表】【行】【セル】【箇条書き●】【箇条書き●】")) {
			// String sString = iText.getText().replaceFirst("●\t", "");
			return new Style_TagInfo("\t\t\t\t\t\t<li>", null, null, null, null, null, "</li>\n");
		}

		if (longStyle.equals("【手順】【手順】【表】【行】【セル】【箇条書き●】【箇条書き●】【箇条書き●】")) {
			String sString = iText.getText().replaceFirst("●\t", "");
			return new Style_TagInfo(null, null, "", sString, "", null, null);
		}

		if (longStyle.equals("【表】【行】【セル】【画面】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【表】【行】【セル】【画面】【画面】")) {
			return new Style_TagInfo("", null, null, null, null, null, "");
		}

		if (longStyle.equals("【表】【行】【セル】【画面】【画面】【画面】")) {
			return new Style_TagInfo(null, null, "", iText.getText(), "", null, null);
		}

		if (longStyle.equals("【リンク】")) {
			return new Style_TagInfo("\t\t<ul class=\"SectionList\">\n", null, null, "", null, null, "\t\t</ul>\n");
		}

		if (longStyle.equals("【リンク】【本文】") || longStyle.equals("【リンク】【箇条書き・】")) {
			return new Style_TagInfo("", null, null, "", null, null, "");
		}

		if (longStyle.equals("【リンク】【本文】【本文】")) {
			if (iText instanceof ControlText) {
				ControlText cText = (ControlText) iText;
				if (cText.getChildList().size() == 1) {
					return new Style_TagInfo("\t\t\t<li>", null, null, "", null, null, "</li>\n");
				} else {
					return new Style_TagInfo("\t\t\t<li>", null, null, "", null, null, "\t\t\t</li>\n");
				}
			}
		}

		if (longStyle.equals("【リンク】【本文】【本文】【本文】")) {
			if (iText instanceof ControlText) {
				return new Style_TagInfo("\n\t\t\t\t<ul>\n", null, null, "", null, null, "\t\t\t\t</ul>\n");
			} else {
				if (cParent.getChildList().size() == 1) {
					return new Style_TagInfo(null, null, "", iText.getText(), "", null, null);
				} else {
					return new Style_TagInfo(null, null, "\n\t\t\t\t", iText.getText(), "", null, null);
				}
			}
		}

		if (longStyle.equals("【リンク】【本文】【本文】【本文】【本文】")) {
			if (iText instanceof ControlText) {
				return new Style_TagInfo("", null, null, "", null, null, "");
			} else {
				String sLineOpenInfo = "";
				String sString = iText.getText();
				String sLineCloseInfo = "";

				if (cParentParent.getChildList().size() > 1) {
					sLineOpenInfo = "";
					sLineCloseInfo = "";
					if (cParentParent.getIndex(cParent) == cParentParent.getChildList().size() - 1) {
						// 複数行の場合、最後の1行だけインデントを調整する
						sLineOpenInfo = sLineOpenInfo + "\t\t\t\t";
						sLineCloseInfo = sLineCloseInfo + "\n";
					}
				} else {
					// 兄弟がいない
					sLineOpenInfo = "";
					sLineCloseInfo = "";
				}

				return new Style_TagInfo(null, null, sLineOpenInfo, sString, sLineCloseInfo, null, null);
			}
		}

		if (longStyle.equals("【リンク】【本文】【本文】【本文】【本文】【本文】")) {
			if (iText instanceof ControlText) {
				return new Style_TagInfo("\n\t\t\t\t<ul>\n", null, null, "", null, null, "\t\t\t\t</ul>\n");
			} else {
				if (cParent.getChildList().size() == 1) {
					return new Style_TagInfo(null, null, "\t\t\t\t\t<li>", iText.getText(), "</li>\n", null, null);
				} else {
					return new Style_TagInfo(null, null, "\n\t\t\t\t", iText.getText(), "", null, null);
				}
			}
		}

		if (longStyle.equals("【リンク】【箇条書き・】【箇条書き・】")) {
			ControlText cText = (ControlText) iText;
			if (cText.getChildList().size() == 1 && (cText.getChildList().get(0) instanceof ControlText == false)) {
				return new Style_TagInfo("\t\t\t<li>", null, null, "", null, null, "</li>\n");
			} else {
				return new Style_TagInfo("\t\t\t<li>\n", null, null, "", null, null, "\t\t\t</li>\n");
			}
		}

		if (longStyle.equals("【リンク】【箇条書き・】【箇条書き・】【箇条書き・】")) { /* 2013.12.19 */
			if (iText instanceof ControlText) {
				return new Style_TagInfo("\t\t\t\t<ul>\n", null, null, "", null, null, "\t\t\t\t</ul>\n");
			} else {
				String sLineOpenInfo = "";
				String sString = iText.getText().replaceFirst("・\t", "");
				String sLineCloseInfo = "";

				if (cParentParent.getChildList().size() > 1) {
					sLineOpenInfo = "";
					sLineCloseInfo = "";
					if (cParentParent.getIndex(cParent) == cParentParent.getChildList().size() - 1) {
						// 複数行の場合、最後の1行だけインデントを調整する
						sLineOpenInfo = sLineOpenInfo + "\t\t\t\t";
						sLineCloseInfo = sLineCloseInfo + "\n";
					}
				} else {
					// 兄弟がいない
					sLineOpenInfo = "";
					sLineCloseInfo = "";
				}

				return new Style_TagInfo(null, null, sLineOpenInfo, sString, sLineCloseInfo, null, null);
			}
		}

		if (longStyle
				.equals("【リンク】【箇条書き・】【箇条書き・】【箇条書き・】【箇条書き・】")) { /* 2013.12.19 */
			return new Style_TagInfo("\t\t\t\t\t<li>\n", null, null, "", null, null, "\t\t\t\t\t</li>\n");
		}

		if (longStyle.equals("【リンク】【箇条書き・】【箇条書き・】【箇条書き・】【箇条書き・】【箇条書き・】")) {
			String sString = iText.getText().replaceFirst("・\t", "");
			return new Style_TagInfo(null, null, "\t\t\t\t\t\t", sString, "\n", null, null);
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
