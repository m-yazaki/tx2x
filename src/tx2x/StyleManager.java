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
		// StyleManagerにStyleを登録セヨ
		m_cStyleList = new ArrayList<Style>();

		// 箇条書きの類似品
		m_cStyleList.add(new Style_BulletLike("【箇条書き・】", "^・\t.*", "^[^・].*")); // 箇条書き「・」
		m_cStyleList.add(new Style_BulletLike("【箇条書き－】", "^－\t.*", "^[^－].*")); // 箇条書き「－」
		m_cStyleList.add(new Style_BulletLike("【箇条書き◎】", "^◎\t.*", "^[^◎].*")); // 箇条書き「◎」
		m_cStyleList.add(new Style_BulletLike("【箇条書き●】", "^●\t.*", "^[^●].*")); // 箇条書き「◎」
		m_cStyleList.add(new Style_BulletLike("【※】", "^※\t.*", "^[^※].*")); // ※
		m_cStyleList.add(new Style_BulletLike("【*】", "^\\*+\t.*", "^[^\\*].*")); // *
		m_cStyleList
				.add(new Style_BulletLike("【※0】", "^※[0-9]\t.*", "^[^※].*")); // ※[0-9]
		m_cStyleList.add(new Style_BulletLike("【※・】", "^※・\t.*", "^[^※].*")); // ※・
		m_cStyleList.add(new Style_BulletLike("【手順】", "^[０-９]+\t.*",
				"^[^０-９].*")); // 手順
		m_cStyleList.add(new Style_BulletLike("【手順分岐】", "^■[^■].*", "^[^■].*")); // 手順分岐
		m_cStyleList.add(new Style_BulletLike("【①】",
				"^[①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳]\t.*", "[^①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳].*")); // ①～⑳
		m_cStyleList.add(new Style_BulletLike("【①：】",
				"^[①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳]：\t.*", "[^①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳].*")); // ①：～⑳：
		m_cStyleList.add(new Style_BulletLike("【（M）①】",
				"^（M）[①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳]\t.*", "(^（M）).*"));
		m_cStyleList.add(new Style_BulletLike("【（M）】", "^（M）\t.*", "(^（M）).*"));
		m_cStyleList.add(new Style_BulletLike("【キー説明】", "^【.*キー】(（.+）)?：.*",
				"^[^【]].*")); // キー説明。終わりはだいぶ適当…。
		m_cStyleList.add(new Style_BulletLike("【利用許諾契約（1）】", "^（[0-9]+）\t.*",
				"^[^（].*")); // 利用許諾契約
		m_cStyleList.add(new Style_BulletLike("【1.】", "^[0-9]+\\.\t.*",
				"^[^0-9].*")); // 1.
		m_cStyleList
				.add(new Style_BulletLike("【1】", "^[0-9]+\t.*", "^[^0-9].*")); // 1.
		m_cStyleList.add(new Style_BulletLike("【Step 1】", "^Step [0-9]+\t.*",
				"^(?!Step [0-9]\t).*")); // Step 1.

		// どちらかというと箇条書きの類似品
		m_cStyleList.add(new Style_BulletLike("【項】", "^【項】.*", ".*")); // 【項】
		m_cStyleList.add(new Style_BulletLike("【項2】", "^【項2】.*", ".*")); // 【項2】
		m_cStyleList.add(new Style_BulletLike("【項下】", "^【項下】.*", ".*")); // 【項下】
		m_cStyleList.add(new Style_BulletLike("【節】", "^【節】.*", ".*")); // 【節】
		m_cStyleList.add(new Style_BulletLike("【節2】", "^【節2】.*", ".*")); // 【節2】
		m_cStyleList.add(new Style_BulletLike("【章】", "^【章】.*", ".*")); // 【章】
		m_cStyleList.add(new Style_BulletLike("【章サブ】",
				"^Hack #[0-9]+(-[0-9]+)?$", ".*")); // 【章サブ】
		m_cStyleList.add(new Style_BulletLike("【編】", "^【編】.*", ".*")); // 【編】
		m_cStyleList.add(new Style_BulletLike("【例：】", "^例：.*", ".*")); // 【例：】
		m_cStyleList.add(new Style_BulletLike("【画面】", "^【画面.*】.*", ".*")); // 【画面】
		m_cStyleList.add(new Style_BulletLike("【参照】", "^▼P.[●0-9]+「.*」", ".*")); // 【参照】
		m_cStyleList.add(new Style_BulletLike("【以上】", "^－以　上－", ".*"));
		m_cStyleList.add(new Style_BulletLike("【別紙タイトル】", "^■■■■■別紙.*", ".*"));
		m_cStyleList.add(new Style_BulletLike("【PL危険】", "^★危険★\t.*", ".*"));
		m_cStyleList.add(new Style_BulletLike("【PL警告】", "^★警告★\t.*", ".*"));
		m_cStyleList.add(new Style_BulletLike("【PL注意】", "^★注意★\t.*", ".*"));
		m_cStyleList.add(new Style_BulletLike("【HACK】", "^【HACK #[0-9]+】.*",
				".*"));

		// お知らせの類似品
		m_cStyleList.add(new Style_NoteLike("【メモ】", "^▼メモ.*", "▲.*")); // memo
		m_cStyleList.add(new Style_NoteLike("【ヒント】", "^▼ヒント.*", "▲.*")); // ヒント
		m_cStyleList.add(new Style_NoteLike("【注意】", "^▼注意.*", "▲.*")); // ヒント
		m_cStyleList.add(new Style_NoteLike("【画面囲み】", "^▼画面囲み.*", "▲.*")); // 画面囲み
		m_cStyleList.add(new Style_NoteLike("【付録】", "^▼付録.*", "▲.*")); // 付録
		m_cStyleList.add(new Style_NoteLike("【利用許諾】", "^▼利用許諾.*", "▲.*")); // 利用許諾
		m_cStyleList.add(new Style_NoteLike("【Eng】", "^▼Eng.*", "▲.*")); // Eng
		m_cStyleList.add(new Style_NoteLike("【編目次】", "^▼編目次.*", "▲.*")); // 編目次
		m_cStyleList.add(new Style_NoteLike("【目次】", "^▼目次.*", "▲.*")); // 編目次
		m_cStyleList.add(new Style_NoteLike("【索引】", "^▼索引.*", "▲.*")); // 索引
		m_cStyleList.add(new Style_NoteLike("【安全上のご注意】", "^▼安全上のご注意.*", "▲.*")); // 安全上のご注意
		m_cStyleList.add(new Style_NoteLike("【コード】", "^▼コード.*", "▲.*")); // コード

		// 表組関連
		m_cStyleList.add(new Style_Table()); // 表組み
		m_cStyleList.add(new Style_TableCell()); // セル
		m_cStyleList.add(new Style_TableCellHeader()); // セル：ヘッダー
		m_cStyleList.add(new Style_TableRow()); // 行
	}

	public static StyleManager getInstance() {
		return instance;
	}

	/*
	 * スタイルの始まり行かどうかを確認するメソッド。 始まり行であった場合は、Styleを返却
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
	 * スタイルの終わり行かどうかを確認するメソッド。 終わり行であった場合は、Styleを返却
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
	 * スタイル名称からStyleを取得する
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
