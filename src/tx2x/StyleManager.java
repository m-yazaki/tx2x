package tx2x;

import java.util.ArrayList;
import java.util.Iterator;

import tx2x_core.Style;
import tx2x_core.Style_BulletLike;
import tx2x_core.Style_NoteLike;
import tx2x_core.Style_Table;
import tx2x_core.Style_TableCell;
import tx2x_core.Style_TableCellHeader;
import tx2x_core.Style_TableRow;

public class StyleManager {
	private static StyleManager instance = new StyleManager();
	private ArrayList<Style> m_cStyleList;

	private StyleManager() {
		// StyleManager‚ÉStyle‚ğ“o˜^ƒZƒˆ
		m_cStyleList = new ArrayList<Style>();

		// ‰Óğ‘‚«‚Ì—Ş—•i
		m_cStyleList.add(new Style_BulletLike("y‰Óğ‘‚«Ez", "^E\t.*", "^[^E].*")); // ‰Óğ‘‚«uEv
		m_cStyleList.add(new Style_BulletLike("y‰Óğ‘‚«|z", "^|\t.*", "^[^|].*")); // ‰Óğ‘‚«u|v
		m_cStyleList.add(new Style_BulletLike("y‰Óğ‘‚«z", "^\t.*", "^[^].*")); // ‰Óğ‘‚«uv
		m_cStyleList.add(new Style_BulletLike("y‰Óğ‘‚«œz", "^œ\t.*", "^[^œ].*")); // ‰Óğ‘‚«uv
		m_cStyleList.add(new Style_BulletLike("y¦z", "^¦\t.*", "^[^¦].*")); // ¦
		m_cStyleList.add(new Style_BulletLike("y*z", "^\\*+\t.*", "^[^\\*].*")); // *
		m_cStyleList
				.add(new Style_BulletLike("y¦0z", "^¦[0-9]\t.*", "^[^¦].*")); // ¦[0-9]
		m_cStyleList.add(new Style_BulletLike("y¦Ez", "^¦E\t.*", "^[^¦].*")); // ¦E
		m_cStyleList.add(new Style_BulletLike("yè‡z", "^[‚O-‚X]+\t.*",
				"^[^‚O-‚X].*")); // è‡
		m_cStyleList.add(new Style_BulletLike("yè‡•ªŠòz", "^¡[^¡].*", "^[^¡].*")); // è‡•ªŠò
		m_cStyleList.add(new Style_BulletLike("y‡@z",
				"^[‡@‡A‡B‡C‡D‡E‡F‡G‡H‡I‡J‡K‡L‡M‡N‡O‡P‡Q‡R‡S]\t.*", "[^‡@‡A‡B‡C‡D‡E‡F‡G‡H‡I‡J‡K‡L‡M‡N‡O‡P‡Q‡R‡S].*")); // ‡@`‡S
		m_cStyleList.add(new Style_BulletLike("y‡@Fz",
				"^[‡@‡A‡B‡C‡D‡E‡F‡G‡H‡I‡J‡K‡L‡M‡N‡O‡P‡Q‡R‡S]F\t.*", "[^‡@‡A‡B‡C‡D‡E‡F‡G‡H‡I‡J‡K‡L‡M‡N‡O‡P‡Q‡R‡S].*")); // ‡@F`‡SF
		m_cStyleList.add(new Style_BulletLike("yiMj‡@z",
				"^iMj[‡@‡A‡B‡C‡D‡E‡F‡G‡H‡I‡J‡K‡L‡M‡N‡O‡P‡Q‡R‡S]\t.*", "(^iMj).*"));
		m_cStyleList.add(new Style_BulletLike("yiMjz", "^iMj\t.*", "(^iMj).*"));
		m_cStyleList.add(new Style_BulletLike("yƒL[à–¾z", "^y.*ƒL[z(i.+j)?F.*",
				"^[^y]].*")); // ƒL[à–¾BI‚í‚è‚Í‚¾‚¢‚Ô“K“–cB
		m_cStyleList.add(new Style_BulletLike("y—˜—p‹–‘øŒ_–ñi1jz", "^i[0-9]+j\t.*",
				"^[^i].*")); // —˜—p‹–‘øŒ_–ñ
		m_cStyleList.add(new Style_BulletLike("y1.z", "^[0-9]+\\.\t.*",
				"^[^0-9].*")); // 1.
		m_cStyleList
				.add(new Style_BulletLike("y1z", "^[0-9]+\t.*", "^[^0-9].*")); // 1.
		m_cStyleList.add(new Style_BulletLike("yStep 1z", "^Step [0-9]+\t.*",
				"^(?!Step [0-9]\t).*")); // Step 1.

		// ‚Ç‚¿‚ç‚©‚Æ‚¢‚¤‚Æ‰Óğ‘‚«‚Ì—Ş—•i
		m_cStyleList.add(new Style_BulletLike("y€z", "^y€z.*", ".*")); // y€z
		m_cStyleList.add(new Style_BulletLike("y€2z", "^y€2z.*", ".*")); // y€2z
		m_cStyleList.add(new Style_BulletLike("y€‰ºz", "^y€‰ºz.*", ".*")); // y€‰ºz
		m_cStyleList.add(new Style_BulletLike("yßz", "^yßz.*", ".*")); // yßz
		m_cStyleList.add(new Style_BulletLike("yß2z", "^yß2z.*", ".*")); // yß2z
		m_cStyleList.add(new Style_BulletLike("yÍz", "^yÍz.*", ".*")); // yÍz
		m_cStyleList.add(new Style_BulletLike("y•Òz", "^y•Òz.*", ".*")); // y•Òz
		m_cStyleList.add(new Style_BulletLike("y—áFz", "^—áF.*", ".*")); // y—áFz
		m_cStyleList.add(new Style_BulletLike("y‰æ–Êz", "^y‰æ–Ê.*z.*", ".*")); // y‰æ–Êz
		m_cStyleList.add(new Style_BulletLike("yQÆz", "^¥P.[œ0-9]+u.*v", ".*")); // yQÆz
		m_cStyleList.add(new Style_BulletLike("yˆÈãz", "^|ˆÈ@ã|", ".*"));
		m_cStyleList.add(new Style_BulletLike("y•Ê†ƒ^ƒCƒgƒ‹z", "^¡¡¡¡¡•Ê†.*", ".*"));
		m_cStyleList.add(new Style_BulletLike("yŠëŒ¯z", "^šŠëŒ¯š\t.*", ".*"));
		m_cStyleList.add(new Style_BulletLike("yŒxz", "^šŒxš\t.*", ".*"));
		m_cStyleList.add(new Style_BulletLike("y’ˆÓz", "^š’ˆÓš\t.*", ".*"));

		// ‚¨’m‚ç‚¹‚Ì—Ş—•i
		m_cStyleList.add(new Style_NoteLike("yƒƒ‚z", "^¥ƒƒ‚.*", "£.*")); // memo
		m_cStyleList.add(new Style_NoteLike("y‰æ–ÊˆÍ‚İz", "^¥‰æ–ÊˆÍ‚İ.*", "£.*")); // ‰æ–ÊˆÍ‚İ
		m_cStyleList.add(new Style_NoteLike("y•t˜^z", "^¥•t˜^.*", "£.*")); // •t˜^
		m_cStyleList.add(new Style_NoteLike("y—˜—p‹–‘øz", "^¥—˜—p‹–‘ø.*", "£.*")); // —˜—p‹–‘ø
		m_cStyleList.add(new Style_NoteLike("yEngz", "^¥Eng.*", "£.*")); // Eng
		m_cStyleList.add(new Style_NoteLike("y•Ò–ÚŸz", "^¥•Ò–ÚŸ.*", "£.*")); // •Ò–ÚŸ
		m_cStyleList.add(new Style_NoteLike("y–ÚŸz", "^¥–ÚŸ.*", "£.*")); // •Ò–ÚŸ
		m_cStyleList.add(new Style_NoteLike("yõˆøz", "^¥õˆø.*", "£.*")); // õˆø
		m_cStyleList.add(new Style_NoteLike("yˆÀ‘Sã‚Ì‚²’ˆÓz", "^¥ˆÀ‘Sã‚Ì‚²’ˆÓ.*", "£.*")); // ˆÀ‘Sã‚Ì‚²’ˆÓ

		// •\‘gŠÖ˜A
		m_cStyleList.add(new Style_Table()); // •\‘g‚İ
		m_cStyleList.add(new Style_TableCell()); // ƒZƒ‹
		m_cStyleList.add(new Style_TableCellHeader()); // ƒZƒ‹Fƒwƒbƒ_[
		m_cStyleList.add(new Style_TableRow()); // s
	}

	public static StyleManager getInstance() {
		return instance;
	}

	/*
	 * ƒXƒ^ƒCƒ‹‚Ìn‚Ü‚ès‚©‚Ç‚¤‚©‚ğŠm”F‚·‚éƒƒ\ƒbƒhB n‚Ü‚ès‚Å‚ ‚Á‚½ê‡‚ÍAStyle‚ğ•Ô‹p
	 */
	public Style getMatchStyle_Start(String line) {
		Iterator<Style> it = m_cStyleList.iterator();
		while (it.hasNext()) {
			Style style = it.next();
			if (style.isMatch_Start(line)) {
				return style;
			}
		}
		return null;
	}

	/*
	 * ƒXƒ^ƒCƒ‹‚ÌI‚í‚ès‚©‚Ç‚¤‚©‚ğŠm”F‚·‚éƒƒ\ƒbƒhB I‚í‚ès‚Å‚ ‚Á‚½ê‡‚ÍAStyle‚ğ•Ô‹p
	 */
	public Style getMatchStyle_Last(String line) {
		Iterator<Style> it = m_cStyleList.iterator();
		while (it.hasNext()) {
			Style style = it.next();
			if (style.isMatch_Last(line)) {
				return style;
			}
		}
		return null;
	}

	/*
	 * ƒXƒ^ƒCƒ‹–¼Ì‚©‚çStyle‚ğæ“¾‚·‚é
	 */
	public Style getStyle(String styleName) {
		Iterator<Style> it = m_cStyleList.iterator();
		while (it.hasNext()) {
			Style style = it.next();
			if (style.getStyleName().equals(styleName)) {
				return style;
			}
		}
		return null;
	}
}
