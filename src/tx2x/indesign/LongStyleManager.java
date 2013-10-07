/**
 * LongStyle（【お知らせ】【箇条書き】【箇条書き】【本文】【本文】）を元に、InDesignタグ付きテキストを生成する
 * ついでに特殊文字の置換も行っています。
 */
package tx2x.indesign;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tx2x.Tx2x;
import tx2x.Tx2xOptions;
import tx2x_core.IntermediateText;
import tx2x_core.Style;

public class LongStyleManager {
	private static final String KOKOMADE_INDENT_CHAR = String.valueOf((char) 7); // ここまでインデント文字
	LinkedList<Style> m_StyleLinkedList; // スタイル情報をpush/popする
	String m_sPrevLongStyle; // 直前の長いスタイル名
	private boolean m_bMac;

	// 別紙番号がかわったときにページを切り替えるための変数
	private int m_nPrevPageNum;
	private Hashtable<String, Integer> m_cPrefixTable;

	// 未定義のスタイル（dummy000）を管理するための変数
	private int m_nDummyCounter;
	private Hashtable<String, String> m_cDummyStyleHashTable;

	/**
	 * 手順表組みを制御するための変数
	 */
	private int m_nStepTableWidth = 0;

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
	private String m_sMaker;

	LongStyleManager(String sMaker, boolean bMac) {
		m_StyleLinkedList = new LinkedList<Style>();
		m_sPrevLongStyle = "";
		m_nPrevPageNum = -1;
		m_cPrefixTable = new Hashtable<String, Integer>();
		m_nDummyCounter = 0;
		m_cDummyStyleHashTable = new Hashtable<String, String>();
		m_bMac = bMac;
		m_cLongStyleArrayList = new ArrayList<String>();
		m_sMaker = sMaker;
	}

	public String getInDesignStyle(IntermediateText iText, int nLsIndex)
			throws IOException {
		String longStyle = getLongStyle();
		if (iText.getStyle() == null) {
			longStyle += "【本文】";
		}

		// iTextの本文を処理。【初期操作】以外の全段落共通
		{
			String text = getEscapeText(iText.getText());
			text = text.replaceAll("\\\\<CharStyle:([^>]*)\\\\>",
					"<CharStyle:$1>");
			// text = text.replaceAll("\\\\<CharStyle:\\\\>", "<CharStyle:>");
			if (m_bMac) {
				String sKeyFontName = "";
				if (m_sMaker.equals("東芝")) {
					sKeyFontName = "KeyFont";

					// 東芝スマートフォン
					text = text.replace("【ホームタッチキー】", "<CharStyle:"
							+ sKeyFontName + ">A<CharStyle:>");
					text = text.replace("【電源ボタン】", "<CharStyle:" + sKeyFontName
							+ ">C<CharStyle:>");
					text = text.replace("【電源キー】", "<CharStyle:" + sKeyFontName
							+ ">C<CharStyle:>");
					text = text.replace("【カメラボタン】", "<CharStyle:"
							+ sKeyFontName + ">D<CharStyle:>");
					text = text.replace("【カメラキー】", "<CharStyle:" + sKeyFontName
							+ ">D<CharStyle:>");
					text = text.replace("【音量ボタン上】", "<CharStyle:"
							+ sKeyFontName + ">E<CharStyle:>");
					text = text.replace("【音量ボタン下】", "<CharStyle:"
							+ sKeyFontName + ">F<CharStyle:>");
					text = text.replace("【サイド上キー】", "<CharStyle:"
							+ sKeyFontName + ">E<CharStyle:>");
					text = text.replace("【サイド下キー】", "<CharStyle:"
							+ sKeyFontName + ">F<CharStyle:>");
					text = text.replace("【発信キー】", "<CharStyle:" + sKeyFontName
							+ ">G<CharStyle:>");
					text = text.replace("【電話キー】", "<CharStyle:" + sKeyFontName
							+ ">G<CharStyle:>");
					text = text.replace("【スタートキー】", "<CharStyle:"
							+ sKeyFontName + ">H<CharStyle:>");
					text = text.replace("【ホームキー】", "<CharStyle:" + sKeyFontName
							+ ">A<CharStyle:>");
					text = text.replace("【メニューキー】", "<CharStyle:"
							+ sKeyFontName + ">M<CharStyle:>");
					text = text.replace("【バックキー】", "<CharStyle:" + sKeyFontName
							+ ">R<CharStyle:>");
					// if (text.matches(".*【ホームキー】.*")) {
					// IDTaggedTextGenerator4KDDI
					// .appendWarn("【ホームキー】は曖昧です。【ホームタッチキー】または【ホーム／メールキー】を使用してください。："
					// + text);
					// }
					// E31T
					text = text.replace("【ロックキー】", "<CharStyle:" + sKeyFontName
							+ ">I<CharStyle:>");
					text = text.replace("【クリア／メモキー】", "<CharStyle:"
							+ sKeyFontName + ">B<CharStyle:>");
					text = text.replace("【メールキー】", "<CharStyle:" + sKeyFontName
							+ ">L<CharStyle:>");
					text = text.replace("【文字キー】", "<CharStyle:" + sKeyFontName
							+ ">R<CharStyle:>");
					text = text.replace("【上下左右キー】", "<CharStyle:"
							+ sKeyFontName + ">a<CharStyle:>");
					text = text.replace("【上下キー】", "<CharStyle:" + sKeyFontName
							+ ">j<CharStyle:>");
					text = text.replace("【左右キー】", "<CharStyle:" + sKeyFontName
							+ ">s<CharStyle:>");
					text = text.replace("【上キー】", "<CharStyle:" + sKeyFontName
							+ ">u<CharStyle:>");
					text = text.replace("【下キー】", "<CharStyle:" + sKeyFontName
							+ ">d<CharStyle:>");
					text = text.replace("【左キー】", "<CharStyle:" + sKeyFontName
							+ ">l<CharStyle:>");
					text = text.replace("【右キー】", "<CharStyle:" + sKeyFontName
							+ ">r<CharStyle:>");
					text = text.replace("【センターキー】", "<CharStyle:"
							+ sKeyFontName + ">c<CharStyle:>");
					text = text.replace("【連絡先キー】", "<CharStyle:" + sKeyFontName
							+ ">&<CharStyle:>");
					text = text.replace("【アドレス帳キー】", "<CharStyle:"
							+ sKeyFontName + ">&<CharStyle:>");
					{ // 【0キー】～【9キー】
						Pattern pattern = Pattern.compile("【([0-9])キー】");
						Matcher matcher = pattern.matcher(text);
						while (matcher.find()) {
							text = text.replaceFirst("【[0-9]キー】", "<CharStyle:"
									+ sKeyFontName + ">" + matcher.group(1)
									+ "<CharStyle:>");
							matcher = pattern.matcher(text);
						}
					}
					text = text.replace("【＃キー】", "<CharStyle:" + sKeyFontName
							+ ">#<CharStyle:>");
					text = text.replace("【＊キー】", "<CharStyle:" + sKeyFontName
							+ ">*<CharStyle:>");

				} else if (m_sMaker.equals("京セラ")) {
					sKeyFontName = "KeyFont\\_BaseKey001";

					text = text.replace("【＃キー】", "<CharStyle:" + sKeyFontName
							+ ">#<CharStyle:>");
					text = text.replace("【アプリキー】", "<CharStyle:" + sKeyFontName
							+ ">%<CharStyle:>");
					text = text.replace("【アドレス帳キー】", "<CharStyle:"
							+ sKeyFontName + ">&<CharStyle:>");
					text = text.replace("【マナーキー】", "<CharStyle:" + sKeyFontName
							+ ">(<CharStyle:>");
					text = text.replace("【カメラキー】", "<CharStyle:" + sKeyFontName
							+ ">)<CharStyle:>");
					text = text.replace("【＊キー】", "<CharStyle:" + sKeyFontName
							+ ">*<CharStyle:>");
					{ // 【0キー】～【9キー】
						Pattern pattern = Pattern.compile("【([0-9])キー】");
						Matcher matcher = pattern.matcher(text);
						while (matcher.find()) {
							text = text.replaceFirst("【[0-9]キー】", "<CharStyle:"
									+ sKeyFontName + ">" + matcher.group(1)
									+ "<CharStyle:>");
							matcher = pattern.matcher(text);
						}
					}
					text = text.replace("【クリア／メモキー】", "<CharStyle:"
							+ sKeyFontName + ">C<CharStyle:>");
					text = text.replace("【終話キー】", "<CharStyle:" + sKeyFontName
							+ ">F<CharStyle:>");
					text = text.replace("【メールキー】", "<CharStyle:" + sKeyFontName
							+ ">L<CharStyle:>");
					text = text.replace("【発信キー】", "<CharStyle:" + sKeyFontName
							+ ">N<CharStyle:>");
					text = text.replace("【EZキー】", "<CharStyle:" + sKeyFontName
							+ ">R<CharStyle:>");
					text = text.replace("【上下左右キー】", "<CharStyle:"
							+ sKeyFontName + ">a<CharStyle:>");
					text = text.replace("【サイド決定キー】", "<CharStyle:"
							+ sKeyFontName + ">b<CharStyle:>");
					text = text.replace("【センターキー】", "<CharStyle:"
							+ sKeyFontName + ">c<CharStyle:>");
					text = text.replace("【下キー】", "<CharStyle:" + sKeyFontName
							+ ">d<CharStyle:>");
					text = text.replace("【TVキー】", "<CharStyle:" + sKeyFontName
							+ ">e<CharStyle:>");
					text = text.replace("【サイド右キー】", "<CharStyle:"
							+ sKeyFontName + ">f<CharStyle:>");
					text = text.replace("【サイド左キー】", "<CharStyle:"
							+ sKeyFontName + ">g<CharStyle:>");
					text = text.replace("【サイド左右キー】", "<CharStyle:"
							+ sKeyFontName + ">gf<CharStyle:>");
					text = text.replace("【上下キー】", "<CharStyle:" + sKeyFontName
							+ ">j<CharStyle:>");
					text = text.replace("【左キー】", "<CharStyle:" + sKeyFontName
							+ ">l<CharStyle:>");
					text = text.replace("【右キー】", "<CharStyle:" + sKeyFontName
							+ ">r<CharStyle:>");
					text = text.replace("【左右キー】", "<CharStyle:" + sKeyFontName
							+ ">s<CharStyle:>");
					text = text.replace("【上キー】", "<CharStyle:" + sKeyFontName
							+ ">u<CharStyle:>");
					// 【マルチキー】と【クイックキー】は同じキーフォント（w）
					text = text.replace("【マルチキー】", "<CharStyle:" + sKeyFontName
							+ ">w<CharStyle:>");
					text = text.replace("【クイックキー】", "<CharStyle:"
							+ sKeyFontName + ">w<CharStyle:>");
					// KD47以降
					text = text.replace("【BOOKキー】", "<CharStyle:"
							+ sKeyFontName + ">x<CharStyle:>");
					// KD48以降
					text = text.replace("【縦横優先切替キー】", "<CharStyle:"
							+ sKeyFontName + ">m<CharStyle:>");
					text = text.replace("【クリアキー】", "<CharStyle:" + sKeyFontName
							+ ">n<CharStyle:>");
					text = text.replace("【サイド電源キー】", "<CharStyle:"
							+ sKeyFontName + ">o<CharStyle:>");
					text = text.replace("【ロックキー】", "<CharStyle:" + sKeyFontName
							+ ">p<CharStyle:>");
					if (text.matches(".*【電源キー】.*")) {
						Tx2x.appendWarn("【電源キー】は曖昧です。【終話キー】または【サイド電源キー】を使用してください。："
								+ text);
					}
				}
				// text = text.replaceAll("-", "<2013>"); // 大丈夫じゃなかった
				// text = text.replaceAll("(<[^>]+)<2013>([^>]+>)", "$1-$2"); //
				// タグの中の-は、<2013>ではダメ

				if (text.matches(".*キー】.*")) {
					Tx2x.appendWarn("不明なキーが見つかりました。：" + text);
				}

				if (!Tx2xOptions.getInstance().getBoolean("InDesignCS5")) {
					// ①～⑳
					text = text.replace("①", "★○1★");
					text = text.replace("②", "★○2★");
					text = text.replace("③", "★○3★");
					text = text.replace("④", "★○4★");
					text = text.replace("⑤", "★○5★");
					text = text.replace("⑥", "★○6★");
					text = text.replace("⑦", "★○7★");
					text = text.replace("⑧", "★○8★");
					text = text.replace("⑨", "★○9★");
					text = text.replace("⑩", "★○10★");
					text = text.replace("⑪", "★○11★");
					text = text.replace("⑫", "★○12★");
					text = text.replace("⑬", "★○13★");
					text = text.replace("⑭", "★○14★");
					text = text.replace("⑮", "★○15★");
					text = text.replace("⑯", "★○16★");
					text = text.replace("⑰", "★○17★");
					text = text.replace("⑱", "★○18★");
					text = text.replace("⑲", "★○19★");
					text = text.replace("⑳", "★○20★");
				}
			}

			{ // ※0～※999（桁数制限無し）
				Pattern pattern = Pattern.compile("★(※[0-9]*)★");
				Matcher matcher = pattern.matcher(text);
				while (matcher.find()) {
					text = text.replaceFirst("★※[0-9]*★",
							"<CharStyle:small-up>" + matcher.group(1)
									+ "<CharStyle:>");
					matcher = pattern.matcher(text);
				}
			}

			// ★（R）★
			text = text.replace("★（R）★",
					"<CharStyle:small-up><00AE><CharStyle:>");
			// ★（C）★
			text = text.replace("★（C）★", "<cOTFContAlt:0><00A9><cOTFContAlt:>");
			// ★TM★
			text = text.replace("★TM★", "<cOTFContAlt:0><2122><cOTFContAlt:>");

			// ～
			// text = text.replace("～", "<FF5E>");
			// text = text.replace("－", "<2212>");

			text = text.replace("▼P.", "<25B6>P.");
			text = text.replace(" ▼ ", " <25B6> ");

			{ // 操作n
				Pattern pattern = Pattern.compile("操作([０-９]+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find()) {
					text = matcher
							.replaceFirst("操作<2009><CharStyle:step\\\\_number02><cOTFContAlt:0><cOTFeatureList:nalt\\\\,7>"
									+ zenkakuNumberToHankakuNumber(matcher
											.group(1))
									+ "<cOTFContAlt:><cOTFeatureList:><cOTFContAlt:0><2009><cOTFContAlt:><CharStyle:>");
				}
			}

			text = text.replace("★ここまでインデント★", KOKOMADE_INDENT_CHAR);

			iText.setText(text);
		}

		// 標準的なチェック（それぞれ独立しているので順不同）

		if (longStyle.equals("【章】【章】") || longStyle.equals("【付録】【章】【章】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:大見出し>";
		}

		if (longStyle.equals("【節】【節】")) {
			iText.setText(iText.getText().substring(3)); // 【節】を削除するつもり
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:小見出し>";
		}

		if (longStyle.equals("【項】【項】") || longStyle.equals("【安全上のご注意】【項】【項】")
				|| longStyle.equals("【付録】【項】【項】")) {
			String ret = "";
			iText.setText(iText.getText().substring(3)); // 【節】を削除するつもり
			// if (iText.getText().equals("フォトサイズを設定する"))
			// System.out.println("!");
			if (m_sPrevLongStyle.equals("【memo】【memo】▲")) {
				ret += "<ParaStyle:head04\\_01>";
			} else {
				ret += "<ParaStyle:head04>";
			}
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.equals("【項2】【項2】")) {
			String ret = "";
			iText.setText(iText.getText().substring(4)); // 【節2】を削除するつもり
			ret += "<ParaStyle:head04\\_2line>";
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.equals("【項下】【項下】")
				|| longStyle.equals("【安全上のご注意】【項下】【項下】")
				|| longStyle.equals("【付録】【項下】【項下】")
				|| longStyle.equals("【付録】【利用許諾】【項下】【項下】")) {
			String ret = "";
			iText.setText(iText.getText().substring(4)); // 【項下】を削除するつもり
			if (m_sPrevLongStyle.equals("【手順】【手順】【本文】【本文】"))
				ret = "<ParaStyle:head05\\_01>■	";
			else
				ret = "<ParaStyle:head05>■	";
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.equals("【本文】【本文】")) {
			String text = iText.getText();
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:本文>";
		}

		if (longStyle.equals("【本文】【本文】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01>";
		}

		if (longStyle.equals("【利用許諾】【本文】")
				|| longStyle.equals("【付録】【利用許諾】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03>";
		}

		if (longStyle.equals("【利用許諾】【手順分岐】【手順分岐】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03-Bold01>";
		}

		if (longStyle.compareTo("【安全上のご注意】【手順分岐】【手順分岐】") == 0) {
			iText.setText("■\t" + iText.getText().substring(1));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02-Bold01>";
		}

		if (longStyle.equals("【利用許諾】【利用許諾契約（1）】【利用許諾契約（1）】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03\\_b>";
		}

		if (longStyle.equals("【利用許諾】【以上】【以上】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03-center01>";
		}

		if (longStyle.equals("【付録】【利用許諾】【箇条書き・】【箇条書き・】")) {
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03\\_a>";
		}

		if (longStyle.equals("【付録】【利用許諾】【箇条書き・】【箇条書き・】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03\\_a>\t";
		}

		if (longStyle.equals("【付録】【利用許諾】【箇条書き・】【箇条書き・】【※】【※】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle.equals("【付録】【Eng】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body\\_Eng00>";
		}

		if (longStyle.equals("【付録】【Eng】【箇条書き・】【箇条書き・】")) {
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body\\_Eng01\\_01>";
		}

		if (longStyle.equals("【付録】【Eng】【箇条書き・】【箇条書き・】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body\\_Eng01\\_01>\t";
		}

		if (longStyle.equals("【付録】【Eng】【表】【行】【セル】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body\\_Eng00>";
		}

		if (longStyle.equals("【目次】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:contents-body01>";
		}

		if (longStyle.equals("【目次】【目次】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:contents-body02>";
		}

		if (longStyle.equals("【目次】【目次】【本文】【本文】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:contents-body03>";
		}

		if (longStyle.equals("【目次】【目次】【本文】【本文】【本文】【本文】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:contents-body04>";
		}

		if (longStyle.equals("【編目次】【本文】") || longStyle.equals("【付録】【編目次】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:HeadContents01>";
		}

		if (longStyle.equals("【編目次】【編目次】【本文】【本文】")
				|| longStyle.equals("【安全上のご注意】【編目次】【編目次】【本文】【本文】")
				|| longStyle.equals("【付録】【編目次】【編目次】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:HeadContents02>";
		}

		if (longStyle.equals("【付録】【索引】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:index-body01>";
		}

		if (longStyle.equals("【付録】【索引】【索引】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:index-body02>";
		}

		if (longStyle.equals("【画面】【画面】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body00>";
		}

		if (longStyle.equals("【箇条書き・】【箇条書き・】")) {
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:バレット>";
		}

		if (longStyle.equals("【箇条書き・】【箇条書き・】【表】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01>";
		}

		if (longStyle.equals("【箇条書き・】【箇条書き・】【表】【行】【セル】【本文】")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*【中央揃え】.*")) {
				iText.setText(text.replaceFirst("【中央揃え】", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body02>";
			}
		}

		if (longStyle.equals("【箇条書き・】【箇条書き・】【表】【行】【セル：ヘッダー】【本文】")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*【中央揃え】.*")) {
				iText.setText(text.replaceFirst("【中央揃え】", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body02>";
			}
		}

		if (longStyle.equals("【箇条書き・】【箇条書き・】【本文】【本文】")
				|| longStyle.equals("【付録】【箇条書き・】【箇条書き・】【本文】【本文】")) {
			String text = iText.getText();
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01>\t";
		}

		if (longStyle.equals("【箇条書き・】【箇条書き・】【箇条書き・】【箇条書き・】")
				|| longStyle.equals("【箇条書き・】【箇条書き・】【箇条書き－】【箇条書き－】")
				|| longStyle.equals("【付録】【箇条書き・】【箇条書き・】【箇条書き－】【箇条書き－】")) {
			String text = iText.getText();
			text = text.replaceFirst("－", "-");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01\\_a>";
		}

		if (longStyle.equals("【箇条書き・】【箇条書き・】【※】【※】")
				|| longStyle.equals("【付録】【箇条書き・】【箇条書き・】【※】【※】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap03>";
		}

		if (longStyle.equals("【箇条書き・】【箇条書き・】【※】【※】【本文】【本文】")
				|| longStyle.equals("【付録】【箇条書き・】【箇条書き・】【※】【※】【本文】【本文】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap03>\t";
		}

		if (longStyle.equals("【*】【*】") || longStyle.equals("【付録】【*】【*】")
				|| longStyle.equals("【付録】【Eng】【*】【*】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01\\_a>";
		}

		if (longStyle.equals("【※】【※】") || longStyle.equals("【付録】【※】【※】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.equals("【付録】【※・】【※・】")) {
			iText.setText(iText.getText().substring(1)); // ※を削除するつもり
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.equals("【※】【※】【本文】【本文】")
				|| longStyle.equals("【付録】【※】【※】【本文】【本文】")
				|| longStyle.equals("【付録】【※・】【※・】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>\t";
		}

		if (longStyle.equals("【※】【※】【箇条書き・】【箇条書き・】")
				|| longStyle.equals("【付録】【※】【※】【箇条書き・】【箇条書き・】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle.equals("【※0】【※0】")
				|| longStyle.equals("【安全上のご注意】【※0】【※0】")
				|| longStyle.equals("【付録】【※0】【※0】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01\\_a>";
		}

		if (longStyle.equals("【※0】【※0】【本文】【本文】")
				|| longStyle.equals("【付録】【※0】【※0】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01\\_a>\t";
		}

		if (longStyle.equals("【表】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:表\\:表>";
		}

		if (longStyle.equals("【表】【行】【セル：ヘッダー】【本文】")) {
			m_sPrevLongStyle = longStyle;
			iText.setText(iText.getText().replace("【ヘッダー】", ""));
			return "<ParaStyle:表\\:表ヘッダー>";
		}

		if (longStyle.equals("【付録】【表】【行】【セル：ヘッダー】【本文】")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*【中央揃え】.*")) {
				iText.setText(text.replaceFirst("【中央揃え】", ""));
				return "<ParaStyle:table-title02>";
			} else {
				return "<ParaStyle:table-body03>";
			}
		}

		if (longStyle.equals("【表】【行】【セル】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:表\\:表本文>";
		}

		if (longStyle.equals("【安全上のご注意】【表】【行】【セル】【本文】")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*【中央揃え】.*")) {
				iText.setText(text.replaceFirst("【中央揃え】", ""));
				return "<ParaStyle:table-body03-center01>";
			} else {
				return "<ParaStyle:table-body04>";
			}
		}

		if (longStyle.equals("【安全上のご注意】【表】【行】【セル：ヘッダー】【本文】")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*【中央揃え】.*")) {
				iText.setText(text.replaceFirst("【中央揃え】", ""));
				return "<ParaStyle:table-title02>";
			} else {
				return "<ParaStyle:table-body03>";
			}
		}

		if (longStyle.equals("【安全上のご注意】【表】【行】【セル】【箇条書き・】【箇条書き・】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>";
		}

		if (longStyle.equals("【安全上のご注意】【表】【行】【セル】【1.】【1.】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>";
		}

		if (longStyle.equals("【安全上のご注意】【表】【行】【セル】【1.】【1.】【箇条書き・】【箇条書き・】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a-tab01>";
		}

		if (longStyle.equals("【安全上のご注意】【表】【行】【セル】【箇条書き●】【箇条書き●】")) {
			iText.setText(iText.getText().replaceFirst("●",
					"<CharStyle:body_color01>●<CharStyle:>")
					+ ""); // ●に色を付ける
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>";
		}

		if (longStyle.equals("【安全上のご注意】【表】【行】【セル】【箇条書き●】【箇条書き●】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>\t";
		}

		if (longStyle.equals("【安全上のご注意】【表】【行】【セル】【箇条書き●】【箇条書き●】【箇条書き・】【箇条書き・】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a-tab01>";
		}

		if (longStyle.equals("【安全上のご注意】【表】【行】【セル】【箇条書き●】【箇条書き●】【箇条書き－】【箇条書き－】")) {
			m_sPrevLongStyle = longStyle;
			iText.setText(iText.getText().replaceFirst("－", "<2212>") + "");
			return "<ParaStyle:table-body04\\_a-tab01>";
		}

		if (longStyle.equals("【安全上のご注意】【表】【行】【セル】【箇条書き●】【箇条書き●】【※】【※】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle
				.equals("【安全上のご注意】【表】【行】【セル】【箇条書き●】【箇条書き●】【箇条書き・】【箇条書き・】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a-tab01>\t";
		}

		if (longStyle
				.equals("【安全上のご注意】【表】【行】【セル】【箇条書き●】【箇条書き●】【箇条書き・】【箇条書き・】【箇条書き－】【箇条書き－】")) {
			m_sPrevLongStyle = longStyle;
			iText.setText(iText.getText().replaceFirst("\t",
					KOKOMADE_INDENT_CHAR));
			return "<ParaStyle:table-body04\\_a-tab01>\t";
		}

		if (longStyle.equals("【安全上のご注意】【表】【行】【セル】【箇条書き●】【箇条書き●】【1.】【1.】")) {
			iText.setText(iText.getText().replaceFirst("\t", ""));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a-tab01>";
		}

		if (longStyle.equals("【安全上のご注意】【表】【行】【セル】【※】【※】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.equals("【付録】【表】【行】【セル】【本文】")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*【中央揃え】.*")) {
				iText.setText(text.replaceFirst("【中央揃え】", ""));
				return "<ParaStyle:table-body03-center01>";
			} else {
				return "<ParaStyle:table-body03>";
			}
		}

		if (longStyle.compareTo("【付録】【表】【行】【セル】【箇条書き・】【箇条書き・】") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body03\\_a>";
		}

		if (longStyle.compareTo("【付録】【表】【行】【セル】【箇条書き・】【箇条書き・】【本文】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body03\\_a>\t";
		}

		if (longStyle.compareTo("【付録】【表】【行】【セル】【※】【※】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>\t";
		}

		if (longStyle.compareTo("【表】【行】【セル】【参照】【参照】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>";
		}

		if (longStyle.equals("【表】【行】【セル】【箇条書き・】【箇条書き・】")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			iText.setText(text);
			return "<ParaStyle:表\\:表バレット>";
		}

		if (longStyle.equals("【表】【行】【セル】【箇条書き・】【箇条書き・】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>\t";
		}

		if (longStyle.compareTo("【表】【行】【セル】【※】【※】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.compareTo("【表】【行】【セル】【手順分岐】【手順分岐】") == 0) {
			iText.setText("■\t" + iText.getText().substring(1));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body01-Bold01>";
		}

		if (longStyle.compareTo("【画面囲み】【画面】【画面】") == 0) {
			m_sPrevLongStyle = longStyle;
			// iText.setText(iText.getText()
			// + IDTaggedTextGenerator4KDDI.getCRLF(m_bMac)
			// + "<ParaStyle:space01>");
			return "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac)
					+ "<ParaStyle:body00>";
		}

		if (longStyle.compareTo("【画面囲み】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-cap01>";
		}
		if (longStyle.equals("【メモ】【メモ】")) {
			if (iText.getText().equals("▲")) {
				iText.setText("");
				m_sPrevLongStyle = longStyle + "▲";
				return "<CellEnd:><RowEnd:><TableEnd:>";
			} else {
				iText.setText(null);
				m_sPrevLongStyle = longStyle + "▼";

				// FIXME: 改行しないように伝えたい
				return "<ParaStyle:本文><TableStyle:メモ><TableStart:1,1:0:0<tCellDefaultCellType:Text>><ColStart:<tColAttrWidth:238.52362204733453>><RowStart:<tRowAttrHeight:167.89844595841532><tRowAttrMinRowSize:167.89844595841532>><CellStyle:メモ><StylePriority:1><CellStart:1,1>";
			}
		}

		if (longStyle.equals("【メモ】【箇条書き・】【箇条書き・】")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			iText.setText(text);
			return "<ParaStyle:バレット>";
		}

		if (longStyle.equals("【メモ】【箇条書き・】【箇条書き・】【手順】【手順】")) {
			m_sPrevLongStyle = longStyle;
			// 手順数字の置き換え
			String text = iText.getText();
			text = text.replaceFirst("^０", ""); // "０\t"は削除する
			Pattern pattern = Pattern.compile("^([０-９]+)");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				text = matcher
						.replaceFirst(zenkakuNumberToHankakuNumber(matcher
								.group(1)) + ".");
			}
			iText.setText(text);
			return "<ParaStyle:バレット補足バレット>";
		}

		if (longStyle.equals("【メモ】【箇条書き・】【箇条書き・】【手順】【手順】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:バレット補足バレット>\t";
		}

		if (longStyle.compareTo("【メモ】【箇条書き・】【箇条書き・】【箇条書き・】【箇条書き・】") == 0) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			iText.setText(text);
			return "<ParaStyle:バレット補足バレット>";
		}

		if (longStyle.compareTo("【メモ】【箇条書き・】【箇条書き・】【箇条書き・】【箇条書き・】【本文】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:バレット補足バレット>\t";
		}

		if (longStyle.equals("【手順】【手順】") || longStyle.equals("【付録】【手順】【手順】")) {
			// 手順数字の置き換え
			String text = iText.getText();
			String sStepNumber = text.substring(0, text.indexOf("\t"));
			text = text.replaceFirst("^０", ""); // "０\t"は削除する
			Pattern pattern = Pattern.compile("^([０-９]+)");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				text = matcher
						.replaceFirst(zenkakuNumberToHankakuNumber(matcher
								.group(1)) + ".");
			}

			iText.setText(text);

			// 表組みにするか判断
			// 手順の開始
			String ret = "";

			// 手順の場合はこの行を表組みにするか検討する
			// System.out.println("この行を表組みにするか検討します。");
			m_nStepTableType = 0;
			if (iText.getText().matches(".*	【画面.*")) {
				// 画面あり
				m_nStepTableType |= 0x1;
				m_nStepTableWidth = 3;
				m_sStepCaption = iText.getText().substring(
						1 + iText.getText().indexOf("】《"));
				iText.setText(iText.getText().substring(0,
						iText.getText().indexOf("	【画面")));
			}
			if (iText.getText().equals("\t")
					|| iText.getText()
							.matches(
									"<CharStyle:step\\\\_number01><cOTFContAlt:0><cOTFeatureList:nalt\\\\,7>[0-9]+<cOTFContAlt:><cOTFeatureList:><CharStyle:>\\t")) {
				// "[０-９]+\\t")) {

				// 直後に表組みがある操作文
				m_nStepTableType |= 0x2;
				m_nStepTableWidth = 0;
				m_sStepCaption = iText.getText().substring(0,
						iText.getText().indexOf("\t"));
				if (m_sStepCaption.equals("")) {
					m_sStepCaption = " ";
				}
			}
			if (m_nStepTableType == 0) {
				m_nStepTableType = 0;
				m_nStepTableWidth = 0;
				m_sStepCaption = "";
			}

			if (m_nPrevStepTableType == 0x1) {
				// 画面あり手順の終了
				String tCellAttrTopInset = "5.669291338582678";
				if (m_sStepNumber.equals("１")) {
					tCellAttrTopInset = "0";
				}
				ret += "<CellEnd:><CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:"
						+ tCellAttrTopInset
						+ "><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>><CellEnd:><CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:"
						+ tCellAttrTopInset
						+ "><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>><ParaStyle:body00>"
						+ Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:-space\\_2mm>"
						+ Tx2x.getTaggedTextCRLF(m_bMac);
				ret += "<ParaStyle:table-cap01>" + m_sPrevStepCaption
						+ "<CellEnd:><RowEnd:>";
				m_sPrevStepCaption = "";

				if (m_nStepTableType != m_nPrevStepTableType) {
					// System.out
					// .println("列数:" + m_nStepTableWidth + "の表組みにしました。");
					ret += "<TableEnd:>" + Tx2x.getTaggedTextCRLF(m_bMac);
					m_nStepTableType = 0;
				}
			}

			m_sStepNumber = sStepNumber; // 反映する（遅延）
			// 出力する
			if (m_nStepTableType == 0) {
				// System.out.println("表組みにしません。");
			} else if (m_nStepTableType == 0x1) {
				// 画面ありなだけ。
				if (m_nStepTableType != m_nPrevStepTableType) {
					// TableStart
					// その前にtBeforeSpaceを決める
					String tBeforeSpace;
					if (m_sPrevLongStyle.equals("【節】【節】")) {
						tBeforeSpace = "1.4173228346456694";
					} else {
						tBeforeSpace = "0";
					}

					ret += "<ParaStyle:body00><TableStart:1,3:0:0<tCellDefaultCellType:Text><tBeforeSpace:"
							+ tBeforeSpace
							+ "><tAfterSpace:0>><ColStart:<tColAttrWidth:160.15748031496065>><ColStart:<tColAttrWidth:4.251968503937008>><ColStart:<tColAttrWidth:53.85826771653544>>";
				}
				// RowStart
				String tCellAttrTopInset = "5.669291338582678";
				if (m_sStepNumber.equals("１")) {
					tCellAttrTopInset = "0";
				}
				ret += "<RowStart:<tRowAttrHeight:5.669291338582678><tRowAttrMinRowSize:5.669291338582678>><CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:"
						+ tCellAttrTopInset
						+ "><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>>";
			} else if (m_nStepTableType == 0x2) {
				iText.setText("");

				m_nPrevStepTableType = m_nStepTableType;
				m_nPrevStepTableWidth = m_nStepTableWidth;
				m_sPrevStepCaption = m_sStepCaption;
				m_sPrevLongStyle = longStyle;
				return "";
			}

			ret += "<ParaStyle:リスト>";

			m_nPrevStepTableType = m_nStepTableType;
			m_nPrevStepTableWidth = m_nStepTableWidth;
			m_sPrevStepCaption = m_sStepCaption;
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.equals("【手順】【手順】【本文】【本文】")
				|| longStyle.equals("【手順】【手順】【画面】【画面】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:リスト補足>";
		}

		if (longStyle.equals("【手順】【手順】【箇条書き・】【箇条書き・】")) {
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:リスト補足バレット>";
		}

		if (longStyle.compareTo("【手順】【手順】【箇条書き・】【箇条書き・】【本文】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;

			String text = iText.getText();

			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);

			return "<ParaStyle:step-body01\\_a>\t";
		}

		if (longStyle.compareTo("【手順】【手順】【箇条書き・】【箇条書き・】【キー説明】【キー説明】") == 0) {
			m_sPrevLongStyle = longStyle;

			String text = iText.getText();
			text = text.replaceFirst("：", "：" + KOKOMADE_INDENT_CHAR);

			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);

			return "<ParaStyle:step-body01\\_a>\t";
		}

		if (longStyle.compareTo("【手順】【手順】【箇条書き・】【箇条書き・】【※】【※】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_02\\_kome>";
		}

		if (longStyle.compareTo("【手順】【手順】【箇条書き・】【箇条書き・】【箇条書き－】【箇条書き－】") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("\t", KOKOMADE_INDENT_CHAR);
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_a>\t";
		}

		if (longStyle.compareTo("【手順】【手順】【箇条書き・】【箇条書き・】【例：】【例：】") == 0) {
			String text = iText.getText();
			int c = text.indexOf("例：");
			if (c != -1) {
				text = "例：" + KOKOMADE_INDENT_CHAR + text.substring(c + 2);
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_a>\t";
		}

		if (longStyle.equals("【手順】【手順】【箇条書き・】【箇条書き・】【表】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_a>";
		}

		if (longStyle.compareTo("【手順】【手順】【箇条書き・】【箇条書き・】【表】【行】【セル】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02>";
		}
		if (longStyle.compareTo("【手順】【手順】【箇条書き・】【箇条書き・】【表】【行】【セル：ヘッダー】【本文】") == 0) {
			String text = iText.getText();
			m_sPrevLongStyle = longStyle;
			if (text.matches(".*【中央揃え】.*")) {
				iText.setText(text.replaceFirst("【中央揃え】", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body02>";
			}
		}
		if (longStyle.compareTo("【手順】【手順】【※】【※】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap02>";
		}

		if (longStyle.compareTo("【手順】【手順】【※0】【※0】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap02\\_a>";
		}

		if (longStyle.compareTo("【手順】【手順】【表】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01>";
		}

		if (longStyle.compareTo("【手順】【手順】【表】【行】【セル：ヘッダー】【本文】") == 0) {
			String text = iText.getText();
			m_sPrevLongStyle = longStyle;
			if (text.matches(".*【中央揃え】.*")) {
				iText.setText(text.replaceFirst("【中央揃え】", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body01>";
			}
		}

		if (longStyle.compareTo("【手順】【手順】【表】【行】【セル】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			// int c2 = text.indexOf("：");
			// if (c2 != -1) {
			// text = "<CharStyle:body-M>" + text.substring(0, c2)
			// + "<CharStyle:>" + text.substring(c2);
			// }
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			if (text.matches(".*【中央揃え】.*")) {
				iText.setText(text.replaceFirst("【中央揃え】", "")
						+ "<pTextAlignment:>");
				return "<ParaStyle:table-body02><pTextAlignment:Center>";
			} else {
				iText.setText(text);
				return "<ParaStyle:table-body02>";
			}
		}

		if (longStyle.compareTo("【手順】【手順】【表】【行】【セル】【例：】【例：】") == 0) {
			String text = iText.getText();
			int c = text.indexOf("例：");
			if (c != -1) {
				text = "例：" + KOKOMADE_INDENT_CHAR + text.substring(c + 2);
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02>";
		}

		if (longStyle.compareTo("【手順】【手順】【表】【行】【セル】【箇条書き・】【箇条書き・】") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>";
		}

		if (longStyle.compareTo("【手順】【手順】【表】【行】【セル】【箇条書き・】【箇条書き・】【本文】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>\t";
		}

		if (longStyle.compareTo("【手順】【手順】【表】【行】【セル】【箇条書き・】【箇条書き・】【※】【※】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap03>";
		}

		if (longStyle.compareTo("【手順】【手順】【表】【行】【セル】【箇条書き・】【箇条書き・】【参照】【参照】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>\t";
		}

		if (longStyle.compareTo("【手順】【手順】【表】【行】【セル】【参照】【参照】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>";
		}

		if (longStyle.compareTo("【手順】【手順】【表】【行】【セル】【※】【※】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.compareTo("【手順】【手順】【表】【行】【セル】【手順分岐】【手順分岐】") == 0) {
			iText.setText(iText.getText().substring(1)); // ■を削除するつもり
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body01-Bold01>";
		}

		if (longStyle.compareTo("【手順】【手順】【手順分岐】【手順分岐】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_01\\_M>";
		}

		if (longStyle.compareTo("【手順】【手順】【手順分岐】【手順分岐】【本文】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_02>";
		}

		if (longStyle.compareTo("【手順】【手順】【手順分岐】【手順分岐】【例：】【例：】") == 0) {
			String text = iText.getText();
			int c = text.indexOf("例：");
			if (c != -1) {
				text = "例：" + KOKOMADE_INDENT_CHAR + text.substring(c + 2);
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_02>";
		}

		if (longStyle.compareTo("【手順】【手順】【手順分岐】【手順分岐】【※】【※】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_02\\_kome>";
		}

		if (longStyle.compareTo("【手順】【手順】【手順分岐】【手順分岐】【箇条書き・】【箇条書き・】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_03\\_bullet>";
		}

		if (longStyle.compareTo("【手順】【手順】【手順分岐】【手順分岐】【箇条書き・】【箇条書き・】【本文】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_03\\_bullet>\t";
		}

		if (longStyle.compareTo("【表】【行】【セル】【手順】【手順】") == 0
				|| longStyle.compareTo("【手順】【手順】【表】【行】【セル】【手順】【手順】") == 0) {
			String ret;
			String text = iText.getText();
			if (text.matches("^０\t.*")) {
				text = text.replaceFirst("^０\t", "");
				ret = "<ParaStyle:table-body01-Bold02>";
			} else {
				text = text.replaceFirst("^１", "1.");
				text = text.replaceFirst("^２", "2.");
				text = text.replaceFirst("^３", "3.");
				text = text.replaceFirst("^４", "4.");
				text = text.replaceFirst("^５", "5.");
				text = text.replaceFirst("^６", "6.");
				text = text.replaceFirst("^７", "7.");
				text = text.replaceFirst("^８", "8.");
				text = text.replaceFirst("^９", "9.");
				ret = "<ParaStyle:table-body01-Bold01>";
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.compareTo("【手順】【手順】【表】【行】【セル】【手順】【手順】【本文】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_b>\t";
		}

		if (longStyle.compareTo("【手順】【手順】【キー説明】【キー説明】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01>";
		}

		if (longStyle.compareTo("【手順】【手順】【キー説明】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01>";
		}

		if (longStyle.compareTo("【手順分岐】【手順分岐】") == 0) {
			iText.setText("■\t" + iText.getText().substring(1));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02-Bold01\\_02>";
		}

		if (longStyle.compareTo("【付録】【手順分岐】【手順分岐】") == 0) {
			iText.setText("■\t" + iText.getText().substring(1));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-title02>";
		}

		if (longStyle.compareTo("【付録】【手順分岐】【手順分岐】【本文】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-title02>\t";
		}

		if (longStyle.compareTo("【付録】【手順分岐】【手順分岐】【※】【※】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle.compareTo("【手順】【手順】【①】【①】") == 0) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();

			int c1 = text.indexOf("\t");
			int c2 = text.indexOf("：");
			if (c1 != -1 && c2 != -1) {
				// ① xxxxx：abcdefghijklmnopqrstuvwxyz
				// "xxxxx"は太字になる
				text = text.substring(0, c1 + 1) + "<CharStyle:body-M>"
						+ text.substring(c1 + 1, c2) + "<CharStyle:>"
						+ text.substring(c2);
			} else if (text.matches("^.*[^。]$")) {
				// ① xxxxx （途中に：が無く「。」で終わっていない場合）
				// "xxxxx"は太字になる
				text = text.substring(0, c1 + 1) + "<CharStyle:body-M>"
						+ text.substring(c1 + 1) + "<CharStyle:>";
			}
			iText.setText(text);
			return "<ParaStyle:step-body01\\_b>";
		}

		if (longStyle.compareTo("【手順】【手順】【（M）①】【（M）①】") == 0) {
			String text = iText.getText();
			text = text.replaceAll("（M）", "");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_b>";
		}

		if (longStyle.equals("【手順】【手順】【①】【①】【本文】【本文】")
				|| longStyle.equals("【手順】【手順】【（M）①】【（M）①】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_b>\t";
		}

		if (longStyle.compareTo("【手順】【手順】【①】【①】【箇条書き・】【箇条書き・】") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("・\t", "•" + KOKOMADE_INDENT_CHAR);
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_b>\t";
		}

		if (longStyle.compareTo("【①】【①】") == 0) {
			String text = iText.getText();
			int c1 = text.indexOf("\t");
			int c2 = text.indexOf("：");
			if (c1 != -1 && c2 != -1) {
				text = text.substring(0, c1 + 1) + "<CharStyle:body-M>"
						+ text.substring(c1 + 1, c2) + "<CharStyle:>"
						+ text.substring(c2, c2 + 1) + KOKOMADE_INDENT_CHAR
						+ text.substring(c2 + 1);
			} else if (text.matches("^.*[^。]$")) {
				text = text.substring(0, c1 + 1) + "<CharStyle:body-M>"
						+ text.substring(c1 + 1) + "<CharStyle:>";
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.compareTo("【①】【①】【本文】【本文】") == 0
				|| longStyle.equals("【（M）①】【（M）①】【本文】【本文】")
				|| longStyle.equals("【（M）】【（M）】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>\t";
		}

		if (longStyle.compareTo("【①】【①】【箇条書き・】【箇条書き・】") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("・", "•");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02\\_a>";
		}

		if (longStyle.compareTo("【①】【①】【※】【※】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle.equals("【①】【①】【表】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.compareTo("【①】【①】【表】【行】【セル：ヘッダー】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body01>";
		}

		if (longStyle.compareTo("【①】【①】【表】【行】【セル】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			// String text = iText.getText();
			// int c2 = text.indexOf("：");
			// if (c2 != -1) {
			// text = "<CharStyle:body-M>" + text.substring(0, c2)
			// + "<CharStyle:>" + text.substring(c2);
			// }
			// iText.setText(text);
			return "<ParaStyle:table-body02>";
		}

		if (longStyle.equals("【（M）①】【（M）①】") || longStyle.equals("【（M）】【（M）】")) {
			String text = iText.getText();
			int c1 = text.indexOf("\t");
			if (c1 != -1) {
				text = text.substring(0, c1 + 1).replaceAll("（M）", "")
						+ "<CharStyle:body-M>" + text.substring(c1 + 1)
						+ "<CharStyle:>";
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}
		if (longStyle.compareTo("【キー説明】【キー説明】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body00>";
		}

		if (longStyle.equals("【箇条書き●】【箇条書き●】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.equals("【付録】【箇条書き●】【箇条書き●】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02-Bold01\\_01>";
		}

		if (longStyle.equals("【箇条書き●】【箇条書き●】【本文】【本文】")
				|| longStyle.equals("【付録】【箇条書き●】【箇条書き●】【本文】【本文】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>\t";
		}

		if (longStyle.compareTo("【1.】【1.】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.compareTo("【1.】【1.】【本文】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>\t";
		}

		if (longStyle.compareTo("【1.】【1.】【箇条書き・】【箇条書き・】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02\\_a>";
		}

		if (longStyle.compareTo("【1.】【1.】【箇条書き・】【箇条書き・】【本文】【本文】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02\\_a>\t";
		}

		if (longStyle.compareTo("【1.】【1.】【1】【1】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02\\_a>";
		}

		if (longStyle.compareTo("【1.】【1.】【表】") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.equals("【1.】【1.】【表】【行】【セル】【本文】")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*【中央揃え】.*")) {
				// 少し怪しいのでdummyにします。
				iText.setText(text.replaceFirst("【中央揃え】", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body02>";
			}
		}

		if (longStyle.equals("【1.】【1.】【表】【行】【セル：ヘッダー】【本文】")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*【中央揃え】.*")) {
				iText.setText(text.replaceFirst("【中央揃え】", ""));
				return "<ParaStyle:table-title01>";
			} else {
				return "<ParaStyle:table-body01>";
			}
		}

		if (longStyle.equals("【例：】【例：】")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02-Bold01\\_01>";
		}

		if (longStyle.equals("【Step 1】【Step 1】")) {
			m_sPrevLongStyle = longStyle;
			String text = "<CharStyle:ナンバーステップ>" + iText.getText();
			text = text.replaceFirst("\t", "<CharStyle:>\t");
			iText.setText(text);
			return "<ParaStyle:ステップ>";
		}

		if (longStyle.equals("【別紙タイトル】【別紙タイトル】")
				|| longStyle.equals("【安全上のご注意】【別紙タイトル】【別紙タイトル】")
				|| longStyle.equals("【付録】【別紙タイトル】【別紙タイトル】")
				|| longStyle.equals("【利用許諾】【別紙タイトル】【別紙タイトル】")) {
			m_sStepCaption = "";
			// 別紙xxx-xを読み取って、改ページの位置を調整する
			String text = iText.getText();
			text = text.replaceAll("■+", "");
			text = text.replaceFirst("別紙k?", "");
			if (m_bMac) {
				text = text.replaceFirst("(<2013>|-)[0-9-A-Z].*", "");
			} else {
				text = text.replaceFirst("-[0-9-A-Z].*", "");
			}
			text = text.replaceFirst("表1", "0000");
			text = text.replaceFirst("表2", "0000");
			text = text.replaceFirst("表3", "50000");
			text = text.replaceFirst("表4", "50000");

			Integer nPrefix = 0;
			String sPrefix = text.replaceFirst("[0-9]+$", "");
			if (sPrefix.length() > 0) {
				text = text.replaceFirst("[^0-9]+", "");
				nPrefix = m_cPrefixTable.get(sPrefix);
				if (nPrefix == null) {
					nPrefix = (m_cPrefixTable.size() + 1) * 1000;
					m_cPrefixTable.put(sPrefix, nPrefix);
				}
			}

			// ページ番号を含んでいた場合は改ページ処理をする
			int pageNum;
			String ret = "";
			try {
				pageNum = Integer.parseInt(text) / 2;
				pageNum *= 2;
				pageNum += nPrefix;
				if (/* m_nPrevPageNum != -1 && */m_nPrevPageNum != pageNum) {
					ret += "<ParaStyle:body00><cNextXChars:EvenPage>"
							+ Tx2x.getTaggedTextCRLF(m_bMac);
				}
				m_nPrevPageNum = pageNum;
			} catch (NumberFormatException e) {
				// パス
			}

			// 付録の場合は、（付録）と入れる
			if (longStyle.indexOf("【付録】") == 0) {
				text = iText.getText();
				Pattern pattern = Pattern.compile("(■+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find())
					text = text.replaceFirst("■+", matcher.group(1) + "（付録）");
				iText.setText(text);
			}

			// 利用許諾の場合は、（利用許諾）と入れる
			if (longStyle.indexOf("【利用許諾】") == 0) {
				text = iText.getText();
				Pattern pattern = Pattern.compile("(■+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find())
					text = text.replaceFirst("■+", matcher.group(1) + "（利用許諾）");
				iText.setText(text);
			}

			// Engの場合は、（Eng）と入れる
			if (longStyle.indexOf("【Eng】") == 0) {
				text = iText.getText();
				Pattern pattern = Pattern.compile("(■+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find())
					text = text.replaceFirst("■+", matcher.group(1) + "（Eng）");
				iText.setText(text);
			}

			m_sPrevLongStyle = longStyle;
			ret += "<ParaStyle:body00>";
			return ret;
		}

		// 以降、ダミースタイルの処理
		m_sPrevLongStyle = longStyle;
		return dummyStyle(longStyle);// + longStyle;
		// throw new IOException("Unknown Style:" + longStyle);
	}

	private String getEscapeText(String text) {
		String ret = text;
		ret = ret.replaceAll("\\\\", "\\\\\\\\");
		ret = ret.replaceAll(">", "\\\\>");
		ret = ret.replaceAll("<", "\\\\<");
		return ret;
	}

	private String dummyStyle(String longStyle) {
		String style = m_cDummyStyleHashTable.get(longStyle);
		if (style != null) {
			return style;
		}
		DecimalFormat df = new DecimalFormat();
		df.applyLocalizedPattern("0000");
		style = "<ParaStyle:dummy" + df.format(m_nDummyCounter) + ">";
		System.out.println(longStyle + "は、" + style + "として出力されました。");
		m_cDummyStyleHashTable.put(longStyle, style);
		m_nDummyCounter++;
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
}
