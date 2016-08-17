package tx2x;

import java.util.ArrayList;
import java.util.Iterator;

import tx2x.core.Style;
import tx2x.core.Style_BulletLike;
import tx2x.core.Style_NoteLike;
import tx2x.core.Style_Table;
import tx2x.core.Style_TableCell;
import tx2x.core.Style_TableCellHeader;
import tx2x.core.Style_TableCellHeaderRow;
import tx2x.core.Style_TableRow;

public class StyleManager {
	protected ArrayList<Style_BulletLike> m_cStyle_BulletLike_List;
	protected ArrayList<Style_NoteLike> m_cStyle_NoteLike_List;
	private ArrayList<Style_Table> m_cStyle_TableLike_List;
	private static Style m_cBodyStyle = new Style_BulletLike("【本文】", ".*", ".*");
	private static Style m_cRootStyle = new Style_BulletLike("【Root】", "", "");
	private ArrayList<Style> m_cStyleList;

	protected StyleManager() {
		defineStyles();
		packToStyleList();
	}

	protected void defineStyles() {
		// StyleManagerにStyleを登録する
		clearStyles();
		defineDefaultStyles();
	}

	protected void clearStyles() {
		m_cStyle_BulletLike_List = new ArrayList<Style_BulletLike>();
		m_cStyle_NoteLike_List = new ArrayList<Style_NoteLike>();
		m_cStyle_TableLike_List = new ArrayList<Style_Table>();
	}

	protected void defineDefaultStyles() {
		// 箇条書きの類似品（標準的なものを定義）
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【箇条書き・】", "^・\t.*", "^(?!^\t).*")); // 箇条書き「・」
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【箇条書き－】", "^－\t.*", "^[^－].*")); // 箇条書き「－」
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【箇条書き◎】", "^◎\t.*", "^[^◎].*")); // 箇条書き「◎」
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【箇条書き●】", "^●\t.*", "^[^●].*")); // 箇条書き「◎」
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【※】", "^※\t.*", "^[^※].*")); // ※
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【*】", "^\\*+\t.*", "^[^\\*].*")); // *
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【※0】", "^※[0-9]\t.*", "^[^※].*")); // ※[0-9]
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【手順】", "^[０-９]+[\\.．]\t.*", "^(?![０-９]+[\\.．]\t).*")); // 手順
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【①】", "^[①-⑳㉑-㉟]\t.*", "^(?!①-⑳㉑-㉟]\t).*")); // ①～⑳㉑～㉟
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【1.】", "^[0-9]+\\.\t.*", "^[^0-9].*")); // 1.
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【1】", "^[0-9]+\t.*", "^[^0-9].*")); // 1.
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【Step 1】", "^Step [0-9]+\t.*", "^(?!Step [0-9]+\t).*")); // Step
																													// 1.
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【箇条書き（用語）】", "^::.*", "^(?!::).*")); // 箇条書き「::」
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【＃】", "^＃.*", "^(?!＃).*"));

		// どちらかというと箇条書きの類似品
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【項】", "^【項】.*", ".*")); // 【項】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【項】", "^[0-9]+\\.[0-9]+\\.[0-9]+ .*", ".*")); // 【項】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【項2】", "^【項2】.*", ".*")); // 【項2】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【項下】", "^【項下】.*", ".*")); // 【項下】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【項下下】", "^【項下下】.*", ".*")); // 【項下下】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【■】", "^■[^■].*", "^[^■].*")); // 【■】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【□】", "^□[^□].*", "^[^□].*")); // 【□】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【節】", "^【節】.*", ".*")); // 【節】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【節2】", "^【節2】.*", ".*")); // 【節2】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【章】", "^【章】.*", ".*")); // 【章】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【章】", "^[0-9]+\\. .*", ".*")); // 【章】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【章サブ】", "^Hack #[0-9]+(-[0-9]+)?$", ".*")); // 【章サブ】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【編】", "^【編】.*", ".*")); // 【編】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【例：】", "^例：.*", ".*")); // 【例：】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【画面】", "^【(画面|画像).*】.*", ".*")); // 【画面】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【別紙タイトル】", "^■■■■■別紙.*", ".*"));
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【PL危険】", "^★危険★\t.*", ".*"));
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【PL警告】", "^★警告★\t.*", ".*"));
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【PL注意】", "^★注意★\t.*", ".*"));
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【HACK】", "^【HACK #[0-9]+】.*", ".*"));
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【画面】", "^<img .*>$", ".*")); // 【画面】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【――】", "^----\t.*", "^(?!----\t).*")); // 【――】

		// お知らせの類似品
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【メモ】", "^▼メモ.*", "▲.*")); // memo
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【ヒント】", "^▼ヒント.*", "▲.*")); // ヒント
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【重要】", "^▼重要.*", "▲.*")); // 重要
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【注意】", "^▼注意.*", "▲.*")); // 注意
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【補足】", "^▼補 *足.*", "▲.*")); // 補足
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【囲み】", "^▼囲み.*", "▲.*")); // 囲み
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【付録】", "^▼付録.*", "▲.*")); // 付録
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【利用許諾】", "^▼利用許諾.*", "▲.*")); // 利用許諾
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【編目次】", "^▼編目次.*", "▲.*")); // 編目次
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【目次】", "^▼目次.*", "▲.*")); // 編目次
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【索引】", "^▼索引.*", "▲.*")); // 索引
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【安全上のご注意】", "^▼安全上のご注意.*", "▲.*")); // 安全上のご注意
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【コード】", "^▼コード.*", "▲.*")); // コード
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【リンク】", "^▼リンク.*", "▲.*")); // リンク
		m_cStyle_NoteLike_List.add(new Style_NoteLike("【コマンド】", "^▼コマンド.*", "▲.*")); // コード

		// 表組関連
		m_cStyle_TableLike_List.add(new Style_Table()); // 表組み
		m_cStyle_TableLike_List.add(new Style_TableCell()); // セル
		m_cStyle_TableLike_List.add(new Style_TableCellHeader()); // セル：ヘッダー
		m_cStyle_TableLike_List.add(new Style_TableCellHeaderRow()); // セル：行ヘッダー
		m_cStyle_TableLike_List.add(new Style_TableRow()); // 行
	}

	public void packToStyleList() {
		m_cStyleList = new ArrayList<Style>();
		m_cStyleList.addAll(m_cStyle_BulletLike_List);
		m_cStyleList.addAll(m_cStyle_NoteLike_List);
		m_cStyleList.addAll(m_cStyle_TableLike_List);
		// 本文（最後に入れる）
		m_cStyleList.add(m_cBodyStyle); // 本文
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

	static public Style getBodyStyle() {
		return m_cBodyStyle;
	}

	static public Style getRootStyle() {
		return m_cRootStyle;
	}
}
