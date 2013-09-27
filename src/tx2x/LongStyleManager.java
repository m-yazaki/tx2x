/**
 * LongStyleiy‚¨’m‚ç‚¹zy‰Óğ‘‚«zy‰Óğ‘‚«zy–{•¶zy–{•¶zj‚ğŒ³‚ÉAInDesignƒ^ƒO•t‚«ƒeƒLƒXƒg‚ğ¶¬‚·‚é
 * ‚Â‚¢‚Å‚É“Áê•¶š‚Ì’uŠ·‚às‚Á‚Ä‚¢‚Ü‚·B
 */
package tx2x;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tx2x_core.IntermediateText;
import tx2x_core.Style;

public class LongStyleManager {
	private static final String KOKOMADE_INDENT_CHAR = String.valueOf((char) 7); // ‚±‚±‚Ü‚ÅƒCƒ“ƒfƒ“ƒg•¶š
	LinkedList<Style> m_StyleLinkedList; // ƒXƒ^ƒCƒ‹î•ñ‚ğpush/pop‚·‚é
	String m_sPrevLongStyle; // ’¼‘O‚Ì’·‚¢ƒXƒ^ƒCƒ‹–¼
	private boolean m_bMac;

	// •Ê†”Ô†‚ª‚©‚í‚Á‚½‚Æ‚«‚Éƒy[ƒW‚ğØ‚è‘Ö‚¦‚é‚½‚ß‚Ì•Ï”
	private int m_nPrevPageNum;
	private Hashtable<String, Integer> m_cPrefixTable;

	// –¢’è‹`‚ÌƒXƒ^ƒCƒ‹idummy000j‚ğŠÇ—‚·‚é‚½‚ß‚Ì•Ï”
	private int m_nDummyCounter;
	private Hashtable<String, String> m_cDummyStyleHashTable;

	/**
	 * è‡•\‘g‚İ‚ğ§Œä‚·‚é‚½‚ß‚Ì•Ï”
	 */
	private int m_nStepTableWidth = 0;

	// type = 0x1: ‰æ–Ê‚ ‚è
	// type = 0x2: è‡”š’¼Œã‚É•\‚ ‚è
	int m_nStepTableType = 0;

	// type = 0x1‚Ì‚Æ‚«‚ÍA‰æ–ÊƒLƒƒƒvƒVƒ‡ƒ“‚ğ•Û
	// type = 0x2‚Ì‚Æ‚«‚ÍAƒXƒeƒbƒv”Ô†‚ğ•Û
	String m_sStepCaption = "";

	// ƒXƒeƒbƒv”Ô†‚ğ•Û
	String m_sStepNumber = "";

	int m_nPrevStepTableWidth = 0;

	// type = 0x1: ‰æ–Ê‚ ‚è
	// type = 0x2: è‡”š’¼Œã‚É•\‚ ‚è
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
			longStyle += "y–{•¶z";
		}

		// iText‚Ì–{•¶‚ğˆ—By‰Šú‘€ìzˆÈŠO‚Ì‘S’i—‹¤’Ê
		{
			String text = iText.getEscapeText();
			text = text.replaceAll("\\\\<CharStyle:([^>]*)\\\\>",
					"<CharStyle:$1>");
			// text = text.replaceAll("\\\\<CharStyle:\\\\>", "<CharStyle:>");
			if (m_bMac) {
				String sKeyFontName = "";
				if (m_sMaker.equals("“ŒÅ")) {
					sKeyFontName = "KeyFont";

					// “ŒÅƒXƒ}[ƒgƒtƒHƒ“
					text = text.replace("yƒz[ƒ€ƒ^ƒbƒ`ƒL[z", "<CharStyle:"
							+ sKeyFontName + ">A<CharStyle:>");
					text = text.replace("y“dŒ¹ƒ{ƒ^ƒ“z", "<CharStyle:" + sKeyFontName
							+ ">C<CharStyle:>");
					text = text.replace("y“dŒ¹ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">C<CharStyle:>");
					text = text.replace("yƒJƒƒ‰ƒ{ƒ^ƒ“z", "<CharStyle:"
							+ sKeyFontName + ">D<CharStyle:>");
					text = text.replace("yƒJƒƒ‰ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">D<CharStyle:>");
					text = text.replace("y‰¹—Êƒ{ƒ^ƒ“ãz", "<CharStyle:"
							+ sKeyFontName + ">E<CharStyle:>");
					text = text.replace("y‰¹—Êƒ{ƒ^ƒ“‰ºz", "<CharStyle:"
							+ sKeyFontName + ">F<CharStyle:>");
					text = text.replace("yƒTƒCƒhãƒL[z", "<CharStyle:"
							+ sKeyFontName + ">E<CharStyle:>");
					text = text.replace("yƒTƒCƒh‰ºƒL[z", "<CharStyle:"
							+ sKeyFontName + ">F<CharStyle:>");
					text = text.replace("y”­MƒL[z", "<CharStyle:" + sKeyFontName
							+ ">G<CharStyle:>");
					text = text.replace("y“d˜bƒL[z", "<CharStyle:" + sKeyFontName
							+ ">G<CharStyle:>");
					text = text.replace("yƒXƒ^[ƒgƒL[z", "<CharStyle:"
							+ sKeyFontName + ">H<CharStyle:>");
					text = text.replace("yƒz[ƒ€ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">A<CharStyle:>");
					text = text.replace("yƒƒjƒ…[ƒL[z", "<CharStyle:"
							+ sKeyFontName + ">M<CharStyle:>");
					text = text.replace("yƒoƒbƒNƒL[z", "<CharStyle:" + sKeyFontName
							+ ">R<CharStyle:>");
					// if (text.matches(".*yƒz[ƒ€ƒL[z.*")) {
					// IDTaggedTextGenerator4KDDI
					// .appendWarn("yƒz[ƒ€ƒL[z‚ÍB–†‚Å‚·Byƒz[ƒ€ƒ^ƒbƒ`ƒL[z‚Ü‚½‚Íyƒz[ƒ€^ƒ[ƒ‹ƒL[z‚ğg—p‚µ‚Ä‚­‚¾‚³‚¢BF"
					// + text);
					// }
					// E31T
					text = text.replace("yƒƒbƒNƒL[z", "<CharStyle:" + sKeyFontName
							+ ">I<CharStyle:>");
					text = text.replace("yƒNƒŠƒA^ƒƒ‚ƒL[z", "<CharStyle:"
							+ sKeyFontName + ">B<CharStyle:>");
					text = text.replace("yƒ[ƒ‹ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">L<CharStyle:>");
					text = text.replace("y•¶šƒL[z", "<CharStyle:" + sKeyFontName
							+ ">R<CharStyle:>");
					text = text.replace("yã‰º¶‰EƒL[z", "<CharStyle:"
							+ sKeyFontName + ">a<CharStyle:>");
					text = text.replace("yã‰ºƒL[z", "<CharStyle:" + sKeyFontName
							+ ">j<CharStyle:>");
					text = text.replace("y¶‰EƒL[z", "<CharStyle:" + sKeyFontName
							+ ">s<CharStyle:>");
					text = text.replace("yãƒL[z", "<CharStyle:" + sKeyFontName
							+ ">u<CharStyle:>");
					text = text.replace("y‰ºƒL[z", "<CharStyle:" + sKeyFontName
							+ ">d<CharStyle:>");
					text = text.replace("y¶ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">l<CharStyle:>");
					text = text.replace("y‰EƒL[z", "<CharStyle:" + sKeyFontName
							+ ">r<CharStyle:>");
					text = text.replace("yƒZƒ“ƒ^[ƒL[z", "<CharStyle:"
							+ sKeyFontName + ">c<CharStyle:>");
					text = text.replace("y˜A—æƒL[z", "<CharStyle:" + sKeyFontName
							+ ">&<CharStyle:>");
					text = text.replace("yƒAƒhƒŒƒX’ ƒL[z", "<CharStyle:"
							+ sKeyFontName + ">&<CharStyle:>");
					{ // y0ƒL[z`y9ƒL[z
						Pattern pattern = Pattern.compile("y([0-9])ƒL[z");
						Matcher matcher = pattern.matcher(text);
						while (matcher.find()) {
							text = text.replaceFirst("y[0-9]ƒL[z", "<CharStyle:"
									+ sKeyFontName + ">" + matcher.group(1)
									+ "<CharStyle:>");
							matcher = pattern.matcher(text);
						}
					}
					text = text.replace("y”ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">#<CharStyle:>");
					text = text.replace("y–ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">*<CharStyle:>");

				} else if (m_sMaker.equals("‹ƒZƒ‰")) {
					sKeyFontName = "KeyFont\\_BaseKey001";

					text = text.replace("y”ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">#<CharStyle:>");
					text = text.replace("yƒAƒvƒŠƒL[z", "<CharStyle:" + sKeyFontName
							+ ">%<CharStyle:>");
					text = text.replace("yƒAƒhƒŒƒX’ ƒL[z", "<CharStyle:"
							+ sKeyFontName + ">&<CharStyle:>");
					text = text.replace("yƒ}ƒi[ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">(<CharStyle:>");
					text = text.replace("yƒJƒƒ‰ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">)<CharStyle:>");
					text = text.replace("y–ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">*<CharStyle:>");
					{ // y0ƒL[z`y9ƒL[z
						Pattern pattern = Pattern.compile("y([0-9])ƒL[z");
						Matcher matcher = pattern.matcher(text);
						while (matcher.find()) {
							text = text.replaceFirst("y[0-9]ƒL[z", "<CharStyle:"
									+ sKeyFontName + ">" + matcher.group(1)
									+ "<CharStyle:>");
							matcher = pattern.matcher(text);
						}
					}
					text = text.replace("yƒNƒŠƒA^ƒƒ‚ƒL[z", "<CharStyle:"
							+ sKeyFontName + ">C<CharStyle:>");
					text = text.replace("yI˜bƒL[z", "<CharStyle:" + sKeyFontName
							+ ">F<CharStyle:>");
					text = text.replace("yƒ[ƒ‹ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">L<CharStyle:>");
					text = text.replace("y”­MƒL[z", "<CharStyle:" + sKeyFontName
							+ ">N<CharStyle:>");
					text = text.replace("yEZƒL[z", "<CharStyle:" + sKeyFontName
							+ ">R<CharStyle:>");
					text = text.replace("yã‰º¶‰EƒL[z", "<CharStyle:"
							+ sKeyFontName + ">a<CharStyle:>");
					text = text.replace("yƒTƒCƒhŒˆ’èƒL[z", "<CharStyle:"
							+ sKeyFontName + ">b<CharStyle:>");
					text = text.replace("yƒZƒ“ƒ^[ƒL[z", "<CharStyle:"
							+ sKeyFontName + ">c<CharStyle:>");
					text = text.replace("y‰ºƒL[z", "<CharStyle:" + sKeyFontName
							+ ">d<CharStyle:>");
					text = text.replace("yTVƒL[z", "<CharStyle:" + sKeyFontName
							+ ">e<CharStyle:>");
					text = text.replace("yƒTƒCƒh‰EƒL[z", "<CharStyle:"
							+ sKeyFontName + ">f<CharStyle:>");
					text = text.replace("yƒTƒCƒh¶ƒL[z", "<CharStyle:"
							+ sKeyFontName + ">g<CharStyle:>");
					text = text.replace("yƒTƒCƒh¶‰EƒL[z", "<CharStyle:"
							+ sKeyFontName + ">gf<CharStyle:>");
					text = text.replace("yã‰ºƒL[z", "<CharStyle:" + sKeyFontName
							+ ">j<CharStyle:>");
					text = text.replace("y¶ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">l<CharStyle:>");
					text = text.replace("y‰EƒL[z", "<CharStyle:" + sKeyFontName
							+ ">r<CharStyle:>");
					text = text.replace("y¶‰EƒL[z", "<CharStyle:" + sKeyFontName
							+ ">s<CharStyle:>");
					text = text.replace("yãƒL[z", "<CharStyle:" + sKeyFontName
							+ ">u<CharStyle:>");
					// yƒ}ƒ‹ƒ`ƒL[z‚ÆyƒNƒCƒbƒNƒL[z‚Í“¯‚¶ƒL[ƒtƒHƒ“ƒgiwj
					text = text.replace("yƒ}ƒ‹ƒ`ƒL[z", "<CharStyle:" + sKeyFontName
							+ ">w<CharStyle:>");
					text = text.replace("yƒNƒCƒbƒNƒL[z", "<CharStyle:"
							+ sKeyFontName + ">w<CharStyle:>");
					// KD47ˆÈ~
					text = text.replace("yBOOKƒL[z", "<CharStyle:"
							+ sKeyFontName + ">x<CharStyle:>");
					// KD48ˆÈ~
					text = text.replace("yc‰¡—DæØ‘ÖƒL[z", "<CharStyle:"
							+ sKeyFontName + ">m<CharStyle:>");
					text = text.replace("yƒNƒŠƒAƒL[z", "<CharStyle:" + sKeyFontName
							+ ">n<CharStyle:>");
					text = text.replace("yƒTƒCƒh“dŒ¹ƒL[z", "<CharStyle:"
							+ sKeyFontName + ">o<CharStyle:>");
					text = text.replace("yƒƒbƒNƒL[z", "<CharStyle:" + sKeyFontName
							+ ">p<CharStyle:>");
					if (text.matches(".*y“dŒ¹ƒL[z.*")) {
						Tx2x.appendWarn("y“dŒ¹ƒL[z‚ÍB–†‚Å‚·ByI˜bƒL[z‚Ü‚½‚ÍyƒTƒCƒh“dŒ¹ƒL[z‚ğg—p‚µ‚Ä‚­‚¾‚³‚¢BF"
								+ text);
					}
				}
				// text = text.replaceAll("-", "<2013>"); // ‘åä•v‚¶‚á‚È‚©‚Á‚½
				// text = text.replaceAll("(<[^>]+)<2013>([^>]+>)", "$1-$2"); //
				// ƒ^ƒO‚Ì’†‚Ì-‚ÍA<2013>‚Å‚Íƒ_ƒ

				if (text.matches(".*ƒL[z.*")) {
					Tx2x.appendWarn("•s–¾‚ÈƒL[‚ªŒ©‚Â‚©‚è‚Ü‚µ‚½BF" + text);
				}

				if (!Tx2xOptions.getInstance().getBoolean("InDesignCS5")) {
					// ‡@`‡S
					text = text.replace("‡@", "š›1š");
					text = text.replace("‡A", "š›2š");
					text = text.replace("‡B", "š›3š");
					text = text.replace("‡C", "š›4š");
					text = text.replace("‡D", "š›5š");
					text = text.replace("‡E", "š›6š");
					text = text.replace("‡F", "š›7š");
					text = text.replace("‡G", "š›8š");
					text = text.replace("‡H", "š›9š");
					text = text.replace("‡I", "š›10š");
					text = text.replace("‡J", "š›11š");
					text = text.replace("‡K", "š›12š");
					text = text.replace("‡L", "š›13š");
					text = text.replace("‡M", "š›14š");
					text = text.replace("‡N", "š›15š");
					text = text.replace("‡O", "š›16š");
					text = text.replace("‡P", "š›17š");
					text = text.replace("‡Q", "š›18š");
					text = text.replace("‡R", "š›19š");
					text = text.replace("‡S", "š›20š");
				}
			}

			{ // ¦0`¦999iŒ…”§ŒÀ–³‚µj
				Pattern pattern = Pattern.compile("š(¦[0-9]*)š");
				Matcher matcher = pattern.matcher(text);
				while (matcher.find()) {
					text = text.replaceFirst("š¦[0-9]*š",
							"<CharStyle:small-up>" + matcher.group(1)
									+ "<CharStyle:>");
					matcher = pattern.matcher(text);
				}
			}

			// šiRjš
			text = text.replace("šiRjš",
					"<CharStyle:small-up><00AE><CharStyle:>");
			// šiCjš
			text = text.replace("šiCjš", "<cOTFContAlt:0><00A9><cOTFContAlt:>");
			// šTMš
			text = text.replace("šTMš", "<cOTFContAlt:0><2122><cOTFContAlt:>");

			// `
			// text = text.replace("`", "<FF5E>");
			// text = text.replace("|", "<2212>");

			text = text.replace("¥P.", "<25B6>P.");
			text = text.replace(" ¥ ", " <25B6> ");

			{ // ‘€ìn
				Pattern pattern = Pattern.compile("‘€ì([‚O-‚X]+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find()) {
					text = matcher
							.replaceFirst("‘€ì<2009><CharStyle:step\\\\_number02><cOTFContAlt:0><cOTFeatureList:nalt\\\\,7>"
									+ zenkakuNumberToHankakuNumber(matcher
											.group(1))
									+ "<cOTFContAlt:><cOTFeatureList:><cOTFContAlt:0><2009><cOTFContAlt:><CharStyle:>");
				}
			}

			text = text.replace("š‚±‚±‚Ü‚ÅƒCƒ“ƒfƒ“ƒgš", KOKOMADE_INDENT_CHAR);

			iText.setText(text);
		}

		// •W€“I‚Èƒ`ƒFƒbƒNi‚»‚ê‚¼‚ê“Æ—§‚µ‚Ä‚¢‚é‚Ì‚Å‡•s“¯j

		if (longStyle.equals("yÍzyÍz") || longStyle.equals("y•t˜^zyÍzyÍz")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:‘åŒ©o‚µ>";
		}

		if (longStyle.equals("yßzyßz")) {
			iText.setText(iText.getText().substring(3)); // yßz‚ğíœ‚·‚é‚Â‚à‚è
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:¬Œ©o‚µ>";
		}

		if (longStyle.equals("y€zy€z") || longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy€zy€z")
				|| longStyle.equals("y•t˜^zy€zy€z")) {
			String ret = "";
			iText.setText(iText.getText().substring(3)); // yßz‚ğíœ‚·‚é‚Â‚à‚è
			// if (iText.getText().equals("ƒtƒHƒgƒTƒCƒY‚ğİ’è‚·‚é"))
			// System.out.println("!");
			if (m_sPrevLongStyle.equals("ymemozymemoz£")) {
				ret += "<ParaStyle:head04\\_01>";
			} else {
				ret += "<ParaStyle:head04>";
			}
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.equals("y€2zy€2z")) {
			String ret = "";
			iText.setText(iText.getText().substring(4)); // yß2z‚ğíœ‚·‚é‚Â‚à‚è
			ret += "<ParaStyle:head04\\_2line>";
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.equals("y€‰ºzy€‰ºz")
				|| longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy€‰ºzy€‰ºz")
				|| longStyle.equals("y•t˜^zy€‰ºzy€‰ºz")
				|| longStyle.equals("y•t˜^zy—˜—p‹–‘øzy€‰ºzy€‰ºz")) {
			String ret = "";
			iText.setText(iText.getText().substring(4)); // y€‰ºz‚ğíœ‚·‚é‚Â‚à‚è
			if (m_sPrevLongStyle.equals("yè‡zyè‡zy–{•¶zy–{•¶z"))
				ret = "<ParaStyle:head05\\_01>¡	";
			else
				ret = "<ParaStyle:head05>¡	";
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.equals("y–{•¶zy–{•¶z")) {
			String text = iText.getText();
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:–{•¶>";
		}

		if (longStyle.equals("y–{•¶zy–{•¶zy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01>";
		}

		if (longStyle.equals("y—˜—p‹–‘øzy–{•¶z")
				|| longStyle.equals("y•t˜^zy—˜—p‹–‘øzy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03>";
		}

		if (longStyle.equals("y—˜—p‹–‘øzyè‡•ªŠòzyè‡•ªŠòz")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03-Bold01>";
		}

		if (longStyle.compareTo("yˆÀ‘Sã‚Ì‚²’ˆÓzyè‡•ªŠòzyè‡•ªŠòz") == 0) {
			iText.setText("¡\t" + iText.getText().substring(1));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02-Bold01>";
		}

		if (longStyle.equals("y—˜—p‹–‘øzy—˜—p‹–‘øŒ_–ñi1jzy—˜—p‹–‘øŒ_–ñi1jz")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03\\_b>";
		}

		if (longStyle.equals("y—˜—p‹–‘øzyˆÈãzyˆÈãz")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03-center01>";
		}

		if (longStyle.equals("y•t˜^zy—˜—p‹–‘øzy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")) {
			String text = iText.getText();
			text = text.replaceFirst("E", "<2022>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03\\_a>";
		}

		if (longStyle.equals("y•t˜^zy—˜—p‹–‘øzy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03\\_a>\t";
		}

		if (longStyle.equals("y•t˜^zy—˜—p‹–‘øzy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy¦zy¦z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle.equals("y•t˜^zyEngzy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body\\_Eng00>";
		}

		if (longStyle.equals("y•t˜^zyEngzy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")) {
			String text = iText.getText();
			text = text.replaceFirst("E", "<2022>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body\\_Eng01\\_01>";
		}

		if (longStyle.equals("y•t˜^zyEngzy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body\\_Eng01\\_01>\t";
		}

		if (longStyle.equals("y•t˜^zyEngzy•\zyszyƒZƒ‹zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body\\_Eng00>";
		}

		if (longStyle.equals("y–ÚŸzy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:contents-body01>";
		}

		if (longStyle.equals("y–ÚŸzy–ÚŸzy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:contents-body02>";
		}

		if (longStyle.equals("y–ÚŸzy–ÚŸzy–{•¶zy–{•¶zy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:contents-body03>";
		}

		if (longStyle.equals("y–ÚŸzy–ÚŸzy–{•¶zy–{•¶zy–{•¶zy–{•¶zy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:contents-body04>";
		}

		if (longStyle.equals("y•Ò–ÚŸzy–{•¶z") || longStyle.equals("y•t˜^zy•Ò–ÚŸzy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:HeadContents01>";
		}

		if (longStyle.equals("y•Ò–ÚŸzy•Ò–ÚŸzy–{•¶zy–{•¶z")
				|| longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•Ò–ÚŸzy•Ò–ÚŸzy–{•¶zy–{•¶z")
				|| longStyle.equals("y•t˜^zy•Ò–ÚŸzy•Ò–ÚŸzy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:HeadContents02>";
		}

		if (longStyle.equals("y•t˜^zyõˆøzy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:index-body01>";
		}

		if (longStyle.equals("y•t˜^zyõˆøzyõˆøzy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:index-body02>";
		}

		if (longStyle.equals("y‰æ–Êzy‰æ–Êz")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body00>";
		}

		if (longStyle.equals("y‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")
				|| longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")
				|| longStyle.equals("y•t˜^zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")) {
			String text = iText.getText();
			text = text.replaceFirst("E", "<2022>");
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01>";
		}

		if (longStyle.equals("y‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy•\z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01>";
		}

		if (longStyle.equals("y‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy•\zyszyƒZƒ‹zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*y’†‰›‘µ‚¦z.*")) {
				iText.setText(text.replaceFirst("y’†‰›‘µ‚¦z", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body02>";
			}
		}

		if (longStyle.equals("y‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy•\zyszyƒZƒ‹Fƒwƒbƒ_[zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*y’†‰›‘µ‚¦z.*")) {
				iText.setText(text.replaceFirst("y’†‰›‘µ‚¦z", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body02>";
			}
		}

		if (longStyle.equals("y‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy–{•¶zy–{•¶z")
				|| longStyle.equals("y•t˜^zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy–{•¶zy–{•¶z")) {
			String text = iText.getText();
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01>\t";
		}

		if (longStyle.equals("y‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")
				|| longStyle.equals("y‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy‰Óğ‘‚«|zy‰Óğ‘‚«|z")
				|| longStyle.equals("y•t˜^zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy‰Óğ‘‚«|zy‰Óğ‘‚«|z")) {
			String text = iText.getText();
			text = text.replaceFirst("|", "-");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01\\_a>";
		}

		if (longStyle.equals("y‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy¦zy¦z")
				|| longStyle.equals("y•t˜^zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy¦zy¦z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap03>";
		}

		if (longStyle.equals("y‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy¦zy¦zy–{•¶zy–{•¶z")
				|| longStyle.equals("y•t˜^zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy¦zy¦zy–{•¶zy–{•¶zy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap03>\t";
		}

		if (longStyle.equals("y*zy*z") || longStyle.equals("y•t˜^zy*zy*z")
				|| longStyle.equals("y•t˜^zyEngzy*zy*z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01\\_a>";
		}

		if (longStyle.equals("y¦zy¦z") || longStyle.equals("y•t˜^zy¦zy¦z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.equals("y•t˜^zy¦Ezy¦Ez")) {
			iText.setText(iText.getText().substring(1)); // ¦‚ğíœ‚·‚é‚Â‚à‚è
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.equals("y¦zy¦zy–{•¶zy–{•¶z")
				|| longStyle.equals("y•t˜^zy¦zy¦zy–{•¶zy–{•¶z")
				|| longStyle.equals("y•t˜^zy¦Ezy¦Ezy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>\t";
		}

		if (longStyle.equals("y¦zy¦zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")
				|| longStyle.equals("y•t˜^zy¦zy¦zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle.equals("y¦0zy¦0z")
				|| longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy¦0zy¦0z")
				|| longStyle.equals("y•t˜^zy¦0zy¦0z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01\\_a>";
		}

		if (longStyle.equals("y¦0zy¦0zy–{•¶zy–{•¶z")
				|| longStyle.equals("y•t˜^zy¦0zy¦0zy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01\\_a>\t";
		}

		if (longStyle.equals("y•\z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:•\\\:•\>";
		}

		if (longStyle.equals("y•\zyszyƒZƒ‹Fƒwƒbƒ_[zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			iText.setText(iText.getText().replace("yƒwƒbƒ_[z", ""));
			return "<ParaStyle:•\\\:•\ƒwƒbƒ_[>";
		}

		if (longStyle.equals("y•t˜^zy•\zyszyƒZƒ‹Fƒwƒbƒ_[zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*y’†‰›‘µ‚¦z.*")) {
				iText.setText(text.replaceFirst("y’†‰›‘µ‚¦z", ""));
				return "<ParaStyle:table-title02>";
			} else {
				return "<ParaStyle:table-body03>";
			}
		}

		if (longStyle.equals("y•\zyszyƒZƒ‹zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:•\\\:•\–{•¶>";
		}

		if (longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*y’†‰›‘µ‚¦z.*")) {
				iText.setText(text.replaceFirst("y’†‰›‘µ‚¦z", ""));
				return "<ParaStyle:table-body03-center01>";
			} else {
				return "<ParaStyle:table-body04>";
			}
		}

		if (longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹Fƒwƒbƒ_[zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*y’†‰›‘µ‚¦z.*")) {
				iText.setText(text.replaceFirst("y’†‰›‘µ‚¦z", ""));
				return "<ParaStyle:table-title02>";
			} else {
				return "<ParaStyle:table-body03>";
			}
		}

		if (longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>";
		}

		if (longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy1.zy1.z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>";
		}

		if (longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy1.zy1.zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a-tab01>";
		}

		if (longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy‰Óğ‘‚«œzy‰Óğ‘‚«œz")) {
			iText.setText(iText.getText().replaceFirst("œ",
					"<CharStyle:body_color01>œ<CharStyle:>")
					+ ""); // œ‚ÉF‚ğ•t‚¯‚é
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>";
		}

		if (longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy‰Óğ‘‚«œzy‰Óğ‘‚«œzy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>\t";
		}

		if (longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy‰Óğ‘‚«œzy‰Óğ‘‚«œzy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a-tab01>";
		}

		if (longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy‰Óğ‘‚«œzy‰Óğ‘‚«œzy‰Óğ‘‚«|zy‰Óğ‘‚«|z")) {
			m_sPrevLongStyle = longStyle;
			iText.setText(iText.getText().replaceFirst("|", "<2212>") + "");
			return "<ParaStyle:table-body04\\_a-tab01>";
		}

		if (longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy‰Óğ‘‚«œzy‰Óğ‘‚«œzy¦zy¦z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle
				.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy‰Óğ‘‚«œzy‰Óğ‘‚«œzy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a-tab01>\t";
		}

		if (longStyle
				.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy‰Óğ‘‚«œzy‰Óğ‘‚«œzy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy‰Óğ‘‚«|zy‰Óğ‘‚«|z")) {
			m_sPrevLongStyle = longStyle;
			iText.setText(iText.getText().replaceFirst("\t",
					KOKOMADE_INDENT_CHAR));
			return "<ParaStyle:table-body04\\_a-tab01>\t";
		}

		if (longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy‰Óğ‘‚«œzy‰Óğ‘‚«œzy1.zy1.z")) {
			iText.setText(iText.getText().replaceFirst("\t", ""));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a-tab01>";
		}

		if (longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•\zyszyƒZƒ‹zy¦zy¦z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.equals("y•t˜^zy•\zyszyƒZƒ‹zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*y’†‰›‘µ‚¦z.*")) {
				iText.setText(text.replaceFirst("y’†‰›‘µ‚¦z", ""));
				return "<ParaStyle:table-body03-center01>";
			} else {
				return "<ParaStyle:table-body03>";
			}
		}

		if (longStyle.compareTo("y•t˜^zy•\zyszyƒZƒ‹zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("E", "<2022>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body03\\_a>";
		}

		if (longStyle.compareTo("y•t˜^zy•\zyszyƒZƒ‹zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy–{•¶zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body03\\_a>\t";
		}

		if (longStyle.compareTo("y•t˜^zy•\zyszyƒZƒ‹zy¦zy¦z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>\t";
		}

		if (longStyle.compareTo("y•\zyszyƒZƒ‹zyQÆzyQÆz") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>";
		}

		if (longStyle.equals("y•\zyszyƒZƒ‹zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			text = text.replaceFirst("E", "<2022>");
			iText.setText(text);
			return "<ParaStyle:•\\\:•\ƒoƒŒƒbƒg>";
		}

		if (longStyle.equals("y•\zyszyƒZƒ‹zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>\t";
		}

		if (longStyle.compareTo("y•\zyszyƒZƒ‹zy¦zy¦z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.compareTo("y•\zyszyƒZƒ‹zyè‡•ªŠòzyè‡•ªŠòz") == 0) {
			iText.setText("¡\t" + iText.getText().substring(1));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body01-Bold01>";
		}

		if (longStyle.compareTo("y‰æ–ÊˆÍ‚İzy‰æ–Êzy‰æ–Êz") == 0) {
			m_sPrevLongStyle = longStyle;
			// iText.setText(iText.getText()
			// + IDTaggedTextGenerator4KDDI.getCRLF(m_bMac)
			// + "<ParaStyle:space01>");
			return "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>";
		}

		if (longStyle.compareTo("y‰æ–ÊˆÍ‚İzy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-cap01>";
		}
		if (longStyle.equals("yƒƒ‚zyƒƒ‚z")) {
			if (iText.getText().equals("£")) {
				iText.setText("");
				m_sPrevLongStyle = longStyle + "£";
				return "<CellEnd:><RowEnd:><TableEnd:>";
			} else {
				iText.setText("");
				m_sPrevLongStyle = longStyle + "¥";

				// FIXME: ‰üs‚µ‚È‚¢‚æ‚¤‚É“`‚¦‚½‚¢
				return "<ParaStyle:–{•¶><TableStyle:ƒƒ‚><TableStart:1,1:0:0<tCellDefaultCellType:Text>><ColStart:<tColAttrWidth:238.52362204733453>><RowStart:<tRowAttrHeight:167.89844595841532><tRowAttrMinRowSize:167.89844595841532>><CellStyle:ƒƒ‚><StylePriority:1><CellStart:1,1>";
			}
		}

		if (longStyle.equals("yƒƒ‚zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			text = text.replaceFirst("E", "<2022>");
			iText.setText(text);
			return "<ParaStyle:ƒoƒŒƒbƒg>";
		}

		if (longStyle.equals("yƒƒ‚zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezyè‡zyè‡z")) {
			m_sPrevLongStyle = longStyle;
			// è‡”š‚Ì’u‚«Š·‚¦
			String text = iText.getText();
			text = text.replaceFirst("^‚O", ""); // "‚O\t"‚Ííœ‚·‚é
			Pattern pattern = Pattern.compile("^([‚O-‚X]+)");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				text = matcher
						.replaceFirst(zenkakuNumberToHankakuNumber(matcher
								.group(1)) + ".");
			}
			iText.setText(text);
			return "<ParaStyle:ƒoƒŒƒbƒg•â‘«ƒoƒŒƒbƒg>";
		}

		if (longStyle.equals("yƒƒ‚zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezyè‡zyè‡zy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:ƒoƒŒƒbƒg•â‘«ƒoƒŒƒbƒg>\t";
		}

		if (longStyle.compareTo("yƒƒ‚zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez") == 0) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			text = text.replaceFirst("E", "<2022>");
			iText.setText(text);
			return "<ParaStyle:ƒoƒŒƒbƒg•â‘«ƒoƒŒƒbƒg>";
		}

		if (longStyle.compareTo("yƒƒ‚zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy–{•¶zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:ƒoƒŒƒbƒg•â‘«ƒoƒŒƒbƒg>\t";
		}

		if (longStyle.equals("yè‡zyè‡z") || longStyle.equals("y•t˜^zyè‡zyè‡z")) {
			// è‡”š‚Ì’u‚«Š·‚¦
			String text = iText.getText();
			String sStepNumber = text.substring(0, text.indexOf("\t"));
			text = text.replaceFirst("^‚O", ""); // "‚O\t"‚Ííœ‚·‚é
			Pattern pattern = Pattern.compile("^([‚O-‚X]+)");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				text = matcher
						.replaceFirst(zenkakuNumberToHankakuNumber(matcher
								.group(1)) + ".");
			}

			iText.setText(text);

			// •\‘g‚İ‚É‚·‚é‚©”»’f
			// è‡‚ÌŠJn
			String ret = "";

			// è‡‚Ìê‡‚Í‚±‚Ìs‚ğ•\‘g‚İ‚É‚·‚é‚©ŒŸ“¢‚·‚é
			// System.out.println("‚±‚Ìs‚ğ•\‘g‚İ‚É‚·‚é‚©ŒŸ“¢‚µ‚Ü‚·B");
			m_nStepTableType = 0;
			if (iText.getText().matches(".*	y‰æ–Ê.*")) {
				// ‰æ–Ê‚ ‚è
				m_nStepTableType |= 0x1;
				m_nStepTableWidth = 3;
				m_sStepCaption = iText.getText().substring(
						1 + iText.getText().indexOf("zs"));
				iText.setText(iText.getText().substring(0,
						iText.getText().indexOf("	y‰æ–Ê")));
			}
			if (iText.getText().equals("\t")
					|| iText.getText()
							.matches(
									"<CharStyle:step\\\\_number01><cOTFContAlt:0><cOTFeatureList:nalt\\\\,7>[0-9]+<cOTFContAlt:><cOTFeatureList:><CharStyle:>\\t")) {
				// "[‚O-‚X]+\\t")) {

				// ’¼Œã‚É•\‘g‚İ‚ª‚ ‚é‘€ì•¶
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
				// ‰æ–Ê‚ ‚èè‡‚ÌI—¹
				String tCellAttrTopInset = "5.669291338582678";
				if (m_sStepNumber.equals("‚P")) {
					tCellAttrTopInset = "0";
				}
				ret += "<CellEnd:><CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:"
						+ tCellAttrTopInset
						+ "><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>><CellEnd:><CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:"
						+ tCellAttrTopInset
						+ "><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>><ParaStyle:body00>"
						+ Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:-space\\_2mm>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:table-cap01>" + m_sPrevStepCaption
						+ "<CellEnd:><RowEnd:>";
				m_sPrevStepCaption = "";

				if (m_nStepTableType != m_nPrevStepTableType) {
					// System.out
					// .println("—ñ”:" + m_nStepTableWidth + "‚Ì•\‘g‚İ‚É‚µ‚Ü‚µ‚½B");
					ret += "<TableEnd:>" + Tx2x.getCRLF(m_bMac);
					m_nStepTableType = 0;
				}
			}

			m_sStepNumber = sStepNumber; // ”½‰f‚·‚éi’x‰„j
			// o—Í‚·‚é
			if (m_nStepTableType == 0) {
				// System.out.println("•\‘g‚İ‚É‚µ‚Ü‚¹‚ñB");
			} else if (m_nStepTableType == 0x1) {
				// ‰æ–Ê‚ ‚è‚È‚¾‚¯B
				if (m_nStepTableType != m_nPrevStepTableType) {
					// TableStart
					// ‚»‚Ì‘O‚ÉtBeforeSpace‚ğŒˆ‚ß‚é
					String tBeforeSpace;
					if (m_sPrevLongStyle.equals("yßzyßz")) {
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
				if (m_sStepNumber.equals("‚P")) {
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

			ret += "<ParaStyle:ƒŠƒXƒg>";

			m_nPrevStepTableType = m_nStepTableType;
			m_nPrevStepTableWidth = m_nStepTableWidth;
			m_sPrevStepCaption = m_sStepCaption;
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.equals("yè‡zyè‡zy–{•¶zy–{•¶z")
				|| longStyle.equals("yè‡zyè‡zy‰æ–Êzy‰æ–Êz")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:ƒŠƒXƒg•â‘«>";
		}

		if (longStyle.equals("yè‡zyè‡zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez")) {
			String text = iText.getText();
			text = text.replaceFirst("E", "<2022>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:ƒŠƒXƒg•â‘«ƒoƒŒƒbƒg>";
		}

		if (longStyle.compareTo("yè‡zyè‡zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy–{•¶zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;

			String text = iText.getText();

			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);

			return "<ParaStyle:step-body01\\_a>\t";
		}

		if (longStyle.compareTo("yè‡zyè‡zy‰Óğ‘‚«Ezy‰Óğ‘‚«EzyƒL[à–¾zyƒL[à–¾z") == 0) {
			m_sPrevLongStyle = longStyle;

			String text = iText.getText();
			text = text.replaceFirst("F", "F" + KOKOMADE_INDENT_CHAR);

			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);

			return "<ParaStyle:step-body01\\_a>\t";
		}

		if (longStyle.compareTo("yè‡zyè‡zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy¦zy¦z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_02\\_kome>";
		}

		if (longStyle.compareTo("yè‡zyè‡zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy‰Óğ‘‚«|zy‰Óğ‘‚«|z") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("\t", KOKOMADE_INDENT_CHAR);
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_a>\t";
		}

		if (longStyle.compareTo("yè‡zyè‡zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy—áFzy—áFz") == 0) {
			String text = iText.getText();
			int c = text.indexOf("—áF");
			if (c != -1) {
				text = "—áF" + KOKOMADE_INDENT_CHAR + text.substring(c + 2);
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_a>\t";
		}

		if (longStyle.equals("yè‡zyè‡zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy•\z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_a>";
		}

		if (longStyle.compareTo("yè‡zyè‡zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy•\zyszyƒZƒ‹zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02>";
		}
		if (longStyle.compareTo("yè‡zyè‡zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy•\zyszyƒZƒ‹Fƒwƒbƒ_[zy–{•¶z") == 0) {
			String text = iText.getText();
			m_sPrevLongStyle = longStyle;
			if (text.matches(".*y’†‰›‘µ‚¦z.*")) {
				iText.setText(text.replaceFirst("y’†‰›‘µ‚¦z", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body02>";
			}
		}
		if (longStyle.compareTo("yè‡zyè‡zy¦zy¦z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap02>";
		}

		if (longStyle.compareTo("yè‡zyè‡zy¦0zy¦0z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap02\\_a>";
		}

		if (longStyle.compareTo("yè‡zyè‡zy•\z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01>";
		}

		if (longStyle.compareTo("yè‡zyè‡zy•\zyszyƒZƒ‹Fƒwƒbƒ_[zy–{•¶z") == 0) {
			String text = iText.getText();
			m_sPrevLongStyle = longStyle;
			if (text.matches(".*y’†‰›‘µ‚¦z.*")) {
				iText.setText(text.replaceFirst("y’†‰›‘µ‚¦z", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body01>";
			}
		}

		if (longStyle.compareTo("yè‡zyè‡zy•\zyszyƒZƒ‹zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			// int c2 = text.indexOf("F");
			// if (c2 != -1) {
			// text = "<CharStyle:body-M>" + text.substring(0, c2)
			// + "<CharStyle:>" + text.substring(c2);
			// }
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			if (text.matches(".*y’†‰›‘µ‚¦z.*")) {
				iText.setText(text.replaceFirst("y’†‰›‘µ‚¦z", "")
						+ "<pTextAlignment:>");
				return "<ParaStyle:table-body02><pTextAlignment:Center>";
			} else {
				iText.setText(text);
				return "<ParaStyle:table-body02>";
			}
		}

		if (longStyle.compareTo("yè‡zyè‡zy•\zyszyƒZƒ‹zy—áFzy—áFz") == 0) {
			String text = iText.getText();
			int c = text.indexOf("—áF");
			if (c != -1) {
				text = "—áF" + KOKOMADE_INDENT_CHAR + text.substring(c + 2);
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02>";
		}

		if (longStyle.compareTo("yè‡zyè‡zy•\zyszyƒZƒ‹zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("E", "<2022>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>";
		}

		if (longStyle.compareTo("yè‡zyè‡zy•\zyszyƒZƒ‹zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy–{•¶zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>\t";
		}

		if (longStyle.compareTo("yè‡zyè‡zy•\zyszyƒZƒ‹zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy¦zy¦z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap03>";
		}

		if (longStyle.compareTo("yè‡zyè‡zy•\zyszyƒZƒ‹zy‰Óğ‘‚«Ezy‰Óğ‘‚«EzyQÆzyQÆz") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>\t";
		}

		if (longStyle.compareTo("yè‡zyè‡zy•\zyszyƒZƒ‹zyQÆzyQÆz") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>";
		}

		if (longStyle.compareTo("yè‡zyè‡zy•\zyszyƒZƒ‹zy¦zy¦z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.compareTo("yè‡zyè‡zy•\zyszyƒZƒ‹zyè‡•ªŠòzyè‡•ªŠòz") == 0) {
			iText.setText(iText.getText().substring(1)); // ¡‚ğíœ‚·‚é‚Â‚à‚è
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body01-Bold01>";
		}

		if (longStyle.compareTo("yè‡zyè‡zyè‡•ªŠòzyè‡•ªŠòz") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_01\\_M>";
		}

		if (longStyle.compareTo("yè‡zyè‡zyè‡•ªŠòzyè‡•ªŠòzy–{•¶zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_02>";
		}

		if (longStyle.compareTo("yè‡zyè‡zyè‡•ªŠòzyè‡•ªŠòzy—áFzy—áFz") == 0) {
			String text = iText.getText();
			int c = text.indexOf("—áF");
			if (c != -1) {
				text = "—áF" + KOKOMADE_INDENT_CHAR + text.substring(c + 2);
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_02>";
		}

		if (longStyle.compareTo("yè‡zyè‡zyè‡•ªŠòzyè‡•ªŠòzy¦zy¦z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_02\\_kome>";
		}

		if (longStyle.compareTo("yè‡zyè‡zyè‡•ªŠòzyè‡•ªŠòzy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_03\\_bullet>";
		}

		if (longStyle.compareTo("yè‡zyè‡zyè‡•ªŠòzyè‡•ªŠòzy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy–{•¶zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_03\\_bullet>\t";
		}

		if (longStyle.compareTo("y•\zyszyƒZƒ‹zyè‡zyè‡z") == 0
				|| longStyle.compareTo("yè‡zyè‡zy•\zyszyƒZƒ‹zyè‡zyè‡z") == 0) {
			String ret;
			String text = iText.getText();
			if (text.matches("^‚O\t.*")) {
				text = text.replaceFirst("^‚O\t", "");
				ret = "<ParaStyle:table-body01-Bold02>";
			} else {
				text = text.replaceFirst("^‚P", "1.");
				text = text.replaceFirst("^‚Q", "2.");
				text = text.replaceFirst("^‚R", "3.");
				text = text.replaceFirst("^‚S", "4.");
				text = text.replaceFirst("^‚T", "5.");
				text = text.replaceFirst("^‚U", "6.");
				text = text.replaceFirst("^‚V", "7.");
				text = text.replaceFirst("^‚W", "8.");
				text = text.replaceFirst("^‚X", "9.");
				ret = "<ParaStyle:table-body01-Bold01>";
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.compareTo("yè‡zyè‡zy•\zyszyƒZƒ‹zyè‡zyè‡zy–{•¶zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_b>\t";
		}

		if (longStyle.compareTo("yè‡zyè‡zyƒL[à–¾zyƒL[à–¾z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01>";
		}

		if (longStyle.compareTo("yè‡zyè‡zyƒL[à–¾zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01>";
		}

		if (longStyle.compareTo("yè‡•ªŠòzyè‡•ªŠòz") == 0) {
			iText.setText("¡\t" + iText.getText().substring(1));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02-Bold01\\_02>";
		}

		if (longStyle.compareTo("y•t˜^zyè‡•ªŠòzyè‡•ªŠòz") == 0) {
			iText.setText("¡\t" + iText.getText().substring(1));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-title02>";
		}

		if (longStyle.compareTo("y•t˜^zyè‡•ªŠòzyè‡•ªŠòzy–{•¶zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-title02>\t";
		}

		if (longStyle.compareTo("y•t˜^zyè‡•ªŠòzyè‡•ªŠòzy¦zy¦z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle.compareTo("yè‡zyè‡zy‡@zy‡@z") == 0) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();

			int c1 = text.indexOf("\t");
			int c2 = text.indexOf("F");
			if (c1 != -1 && c2 != -1) {
				// ‡@ xxxxxFabcdefghijklmnopqrstuvwxyz
				// "xxxxx"‚Í‘¾š‚É‚È‚é
				text = text.substring(0, c1 + 1) + "<CharStyle:body-M>"
						+ text.substring(c1 + 1, c2) + "<CharStyle:>"
						+ text.substring(c2);
			} else if (text.matches("^.*[^B]$")) {
				// ‡@ xxxxx i“r’†‚ÉF‚ª–³‚­uBv‚ÅI‚í‚Á‚Ä‚¢‚È‚¢ê‡j
				// "xxxxx"‚Í‘¾š‚É‚È‚é
				text = text.substring(0, c1 + 1) + "<CharStyle:body-M>"
						+ text.substring(c1 + 1) + "<CharStyle:>";
			}
			iText.setText(text);
			return "<ParaStyle:step-body01\\_b>";
		}

		if (longStyle.compareTo("yè‡zyè‡zyiMj‡@zyiMj‡@z") == 0) {
			String text = iText.getText();
			text = text.replaceAll("iMj", "");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_b>";
		}

		if (longStyle.equals("yè‡zyè‡zy‡@zy‡@zy–{•¶zy–{•¶z")
				|| longStyle.equals("yè‡zyè‡zyiMj‡@zyiMj‡@zy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_b>\t";
		}

		if (longStyle.compareTo("yè‡zyè‡zy‡@zy‡@zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("E\t", "<2022>" + KOKOMADE_INDENT_CHAR);
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_b>\t";
		}

		if (longStyle.compareTo("y‡@zy‡@z") == 0) {
			String text = iText.getText();
			int c1 = text.indexOf("\t");
			int c2 = text.indexOf("F");
			if (c1 != -1 && c2 != -1) {
				text = text.substring(0, c1 + 1) + "<CharStyle:body-M>"
						+ text.substring(c1 + 1, c2) + "<CharStyle:>"
						+ text.substring(c2, c2 + 1) + KOKOMADE_INDENT_CHAR
						+ text.substring(c2 + 1);
			} else if (text.matches("^.*[^B]$")) {
				text = text.substring(0, c1 + 1) + "<CharStyle:body-M>"
						+ text.substring(c1 + 1) + "<CharStyle:>";
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.compareTo("y‡@zy‡@zy–{•¶zy–{•¶z") == 0
				|| longStyle.equals("yiMj‡@zyiMj‡@zy–{•¶zy–{•¶z")
				|| longStyle.equals("yiMjzyiMjzy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>\t";
		}

		if (longStyle.compareTo("y‡@zy‡@zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("E", "<2022>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02\\_a>";
		}

		if (longStyle.compareTo("y‡@zy‡@zy¦zy¦z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle.equals("y‡@zy‡@zy•\z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.compareTo("y‡@zy‡@zy•\zyszyƒZƒ‹Fƒwƒbƒ_[zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body01>";
		}

		if (longStyle.compareTo("y‡@zy‡@zy•\zyszyƒZƒ‹zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			// String text = iText.getText();
			// int c2 = text.indexOf("F");
			// if (c2 != -1) {
			// text = "<CharStyle:body-M>" + text.substring(0, c2)
			// + "<CharStyle:>" + text.substring(c2);
			// }
			// iText.setText(text);
			return "<ParaStyle:table-body02>";
		}

		if (longStyle.equals("yiMj‡@zyiMj‡@z") || longStyle.equals("yiMjzyiMjz")) {
			String text = iText.getText();
			int c1 = text.indexOf("\t");
			if (c1 != -1) {
				text = text.substring(0, c1 + 1).replaceAll("iMj", "")
						+ "<CharStyle:body-M>" + text.substring(c1 + 1)
						+ "<CharStyle:>";
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}
		if (longStyle.compareTo("yƒL[à–¾zyƒL[à–¾z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body00>";
		}

		if (longStyle.equals("y‰Óğ‘‚«œzy‰Óğ‘‚«œz")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.equals("y•t˜^zy‰Óğ‘‚«œzy‰Óğ‘‚«œz")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02-Bold01\\_01>";
		}

		if (longStyle.equals("y‰Óğ‘‚«œzy‰Óğ‘‚«œzy–{•¶zy–{•¶z")
				|| longStyle.equals("y•t˜^zy‰Óğ‘‚«œzy‰Óğ‘‚«œzy–{•¶zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>\t";
		}

		if (longStyle.compareTo("y1.zy1.z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.compareTo("y1.zy1.zy–{•¶zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>\t";
		}

		if (longStyle.compareTo("y1.zy1.zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ez") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02\\_a>";
		}

		if (longStyle.compareTo("y1.zy1.zy‰Óğ‘‚«Ezy‰Óğ‘‚«Ezy–{•¶zy–{•¶z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02\\_a>\t";
		}

		if (longStyle.compareTo("y1.zy1.zy1zy1z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02\\_a>";
		}

		if (longStyle.compareTo("y1.zy1.zy•\z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.equals("y1.zy1.zy•\zyszyƒZƒ‹zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*y’†‰›‘µ‚¦z.*")) {
				// ­‚µ‰ö‚µ‚¢‚Ì‚Ådummy‚É‚µ‚Ü‚·B
				iText.setText(text.replaceFirst("y’†‰›‘µ‚¦z", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body02>";
			}
		}

		if (longStyle.equals("y1.zy1.zy•\zyszyƒZƒ‹Fƒwƒbƒ_[zy–{•¶z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*y’†‰›‘µ‚¦z.*")) {
				iText.setText(text.replaceFirst("y’†‰›‘µ‚¦z", ""));
				return "<ParaStyle:table-title01>";
			} else {
				return "<ParaStyle:table-body01>";
			}
		}

		if (longStyle.equals("y—áFzy—áFz")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02-Bold01\\_01>";
		}

		if (longStyle.equals("yStep 1zyStep 1z")) {
			m_sPrevLongStyle = longStyle;
			String text = "<CharStyle:ƒiƒ“ƒo[ƒXƒeƒbƒv>" + iText.getText();
			text = text.replaceFirst("\t", "<CharStyle:>\t");
			iText.setText(text);
			return "<ParaStyle:ƒXƒeƒbƒv>";
		}

		if (longStyle.equals("y•Ê†ƒ^ƒCƒgƒ‹zy•Ê†ƒ^ƒCƒgƒ‹z")
				|| longStyle.equals("yˆÀ‘Sã‚Ì‚²’ˆÓzy•Ê†ƒ^ƒCƒgƒ‹zy•Ê†ƒ^ƒCƒgƒ‹z")
				|| longStyle.equals("y•t˜^zy•Ê†ƒ^ƒCƒgƒ‹zy•Ê†ƒ^ƒCƒgƒ‹z")
				|| longStyle.equals("y—˜—p‹–‘øzy•Ê†ƒ^ƒCƒgƒ‹zy•Ê†ƒ^ƒCƒgƒ‹z")) {
			m_sStepCaption = "";
			// •Ê†xxx-x‚ğ“Ç‚İæ‚Á‚ÄA‰üƒy[ƒW‚ÌˆÊ’u‚ğ’²®‚·‚é
			String text = iText.getText();
			text = text.replaceAll("¡+", "");
			text = text.replaceFirst("•Ê†k?", "");
			if (m_bMac) {
				text = text.replaceFirst("(<2013>|-)[0-9-A-Z].*", "");
			} else {
				text = text.replaceFirst("-[0-9-A-Z].*", "");
			}
			text = text.replaceFirst("•\1", "0000");
			text = text.replaceFirst("•\2", "0000");
			text = text.replaceFirst("•\3", "50000");
			text = text.replaceFirst("•\4", "50000");

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

			// ƒy[ƒW”Ô†‚ğŠÜ‚ñ‚Å‚¢‚½ê‡‚Í‰üƒy[ƒWˆ—‚ğ‚·‚é
			int pageNum;
			String ret = "";
			try {
				pageNum = Integer.parseInt(text) / 2;
				pageNum *= 2;
				pageNum += nPrefix;
				if (/* m_nPrevPageNum != -1 && */m_nPrevPageNum != pageNum) {
					ret += "<ParaStyle:body00><cNextXChars:EvenPage>"
							+ Tx2x.getCRLF(m_bMac);
				}
				m_nPrevPageNum = pageNum;
			} catch (NumberFormatException e) {
				// ƒpƒX
			}

			// •t˜^‚Ìê‡‚ÍAi•t˜^j‚Æ“ü‚ê‚é
			if (longStyle.indexOf("y•t˜^z") == 0) {
				text = iText.getText();
				Pattern pattern = Pattern.compile("(¡+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find())
					text = text.replaceFirst("¡+", matcher.group(1) + "i•t˜^j");
				iText.setText(text);
			}

			// —˜—p‹–‘ø‚Ìê‡‚ÍAi—˜—p‹–‘øj‚Æ“ü‚ê‚é
			if (longStyle.indexOf("y—˜—p‹–‘øz") == 0) {
				text = iText.getText();
				Pattern pattern = Pattern.compile("(¡+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find())
					text = text.replaceFirst("¡+", matcher.group(1) + "i—˜—p‹–‘øj");
				iText.setText(text);
			}

			// Eng‚Ìê‡‚ÍAiEngj‚Æ“ü‚ê‚é
			if (longStyle.indexOf("yEngz") == 0) {
				text = iText.getText();
				Pattern pattern = Pattern.compile("(¡+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find())
					text = text.replaceFirst("¡+", matcher.group(1) + "iEngj");
				iText.setText(text);
			}

			m_sPrevLongStyle = longStyle;
			ret += "<ParaStyle:body00>";
			return ret;
		}

		// ˆÈ~Aƒ_ƒ~[ƒXƒ^ƒCƒ‹‚Ìˆ—
		m_sPrevLongStyle = longStyle;
		return dummyStyle(longStyle);// + longStyle;
		// throw new IOException("Unknown Style:" + longStyle);
	}

	private String dummyStyle(String longStyle) {
		String style = m_cDummyStyleHashTable.get(longStyle);
		if (style != null) {
			return style;
		}
		DecimalFormat df = new DecimalFormat();
		df.applyLocalizedPattern("0000");
		style = "<ParaStyle:dummy" + df.format(m_nDummyCounter) + ">";
		System.out.println(longStyle + "‚ÍA" + style + "‚Æ‚µ‚Äo—Í‚³‚ê‚Ü‚µ‚½B");
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

		// longStyle‚Ìæ“¾
		while (it2.hasNext()) {
			Style r2 = it2.next();
			if (r2 == null) {
				longStyle += "y–{•¶z";
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
			return ""; // ƒXƒ^ƒCƒ‹‚È‚µ
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
			if (c >= '‚O' && c <= '‚X') {
				sb.setCharAt(i, (char) (c - '‚O' + '0'));
			}
		}
		return sb.toString();
	}
}
