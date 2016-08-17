package tx2x.core;

import java.util.ArrayList;

public class Style_TableRow extends Style_Table {

	public Style_TableRow() {
		super("【行】", "^=====.*", "^(=====.*|▲表?)");
	}

	@Override
	public int compileLine(ControlText controlText,
			ArrayList<String> smallPartText, int startPos) {
		return startPos - 1; // 次は隠れたセル始まりのため、同じ行をもう一度読む
	}
}
