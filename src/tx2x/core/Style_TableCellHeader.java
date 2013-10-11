package tx2x.core;

import java.util.ArrayList;

public class Style_TableCellHeader extends Style {

	public Style_TableCellHeader() {
		// 内部処理用のスタイル
		super("【セル：ヘッダー】", "^-----dummy dummy-----$", "^-----dummy dummy-----$");
	}

	@Override
	public boolean bTableLikeStyle() {
		return true;
	}

	@Override
	public int compileLine(ControlText controlText,
			ArrayList<String> smallPartText, int startPos) {
		return startPos; // そのまま読み続ける（ここで処理することはない）
	}
}
