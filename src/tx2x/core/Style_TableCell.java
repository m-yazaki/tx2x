package tx2x.core;

import java.util.ArrayList;

public class Style_TableCell extends Style {

	public Style_TableCell() {
		super("【セル】", "^-----.*", "^(-----.*|=====.*|▲表?)");
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
