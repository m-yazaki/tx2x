package tx2x.core;

import java.util.ArrayList;

public class Style_TableCellHeaderRow extends Style_Table {

	public Style_TableCellHeaderRow() {
		// 内部処理用のスタイル
		super("【セル：行ヘッダー】", "^-----dummy dummy-----$",
				"^-----dummy dummy-----$");
	}

	@Override
	public int compileLine(ControlText controlText,
			ArrayList<String> smallPartText, int startPos) {
		return startPos; // そのまま読み続ける（ここで処理することはない）
	}
}
